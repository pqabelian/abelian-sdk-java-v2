package info.abelian.sdk.demo;

import java.util.AbstractMap;
import java.util.Map;

import info.abelian.sdk.demo.persist.WalletDB.ColdWalletDB;
import info.abelian.sdk.demo.persist.WalletDB.HotWalletDB;
import info.abelian.sdk.wallet.Account;
import info.abelian.sdk.wallet.TxSigner;
import info.abelian.sdk.wallet.UnsignedRawTx;
import info.abelian.sdk.wallet.SignedRawTx;

public class DemoTxSigner {

    public static void demoGenerateSignedRawTx(String[] args) throws Exception {
        // Usage: GenerateSignedRawTx [TX_MD5]
        String txMd5 = args.length > 0 ? args[0] : null;

        // STEP 0.
        System.out.printf("\n==> Generating a signed raw tx with txMd5: %s\n", txMd5 != null ? txMd5 : "ANY");
        HotWalletDB hotWalletDB = Demo.getHotWalletDB();
        ColdWalletDB coldWalletDB = Demo.getColdWalletDB();

        // STEP 1.
        System.out.printf("\n==> Getting unsigned raw tx data from the hot wallet db.\n");
        UnsignedRawTx unsignedRawTx = null;
        if (txMd5 != null) {
            unsignedRawTx = hotWalletDB.getUnsignedRawTx(txMd5);
        } else {
            Map<String, UnsignedRawTx> unsignedRawTxMap = hotWalletDB.getUnsignedRawTxs(1);
            if (!unsignedRawTxMap.isEmpty()) {
                txMd5 = unsignedRawTxMap.keySet().iterator().next();
                unsignedRawTx = unsignedRawTxMap.values().iterator().next();
            }
        }

        if (unsignedRawTx == null) {
            System.out.printf("No unsigned raw tx found.\n");
            return;
        }
        System.out.printf("Got unsigned raw tx: txMd5=%s, data=%s, signers=%d.\n", txMd5, unsignedRawTx.data,
                unsignedRawTx.signerAccountIDs.length);

        // STEP 2.
        System.out.printf("\n==> Getting signer accounts from the cold wallet db.\n");
        AbstractMap.SimpleEntry<String, Account>[] signerAccounts = new AbstractMap.SimpleEntry[unsignedRawTx.signerAccountIDs.length];
        for (int i = 0; i < signerAccounts.length; i++) {
            signerAccounts[i] = coldWalletDB.getSignerAccount(unsignedRawTx.signerAccountIDs[i]);
            if (signerAccounts[i] == null) {
                System.out.printf("Signer account not found: %s.\n", unsignedRawTx.signerAccountIDs[i]);
                return;
            }
        }
        System.out.printf("Got %d signer accounts:\n", signerAccounts.length);
        for (AbstractMap.SimpleEntry<String, Account> signerAccount : signerAccounts) {
            System.out.printf("Signer %s with detector root key %s\n", signerAccount.getKey(), signerAccount.getValue().getDetectorRootKey().toHex());
        }

        // STEP 3.
        System.out.printf("\n==> Signing the unsigned raw tx.\n");
        TxSigner txSigner = new TxSigner(signerAccounts);
        SignedRawTx signedRawTx = txSigner.sign(unsignedRawTx);
        System.out.printf("Signed raw tx: txid=%s, data=%s.\n", signedRawTx.txid, signedRawTx.data);

        // STEP 4.
        System.out.printf("\n==> Saving the signed raw tx to the hot wallet db.\n");
        hotWalletDB.updateSignedRawTx(txMd5, signedRawTx);
    }
}
