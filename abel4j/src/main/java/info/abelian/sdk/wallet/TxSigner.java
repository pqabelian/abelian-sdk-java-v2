package info.abelian.sdk.wallet;

import com.google.protobuf.ByteString;

import info.abelian.sdk.common.AbelException;
import info.abelian.sdk.common.Bytes;
import info.abelian.sdk.common.PrivacyLevel;
import info.abelian.sdk.proto.Core.GenerateRawTxDataArgs;
import info.abelian.sdk.proto.Core.GenerateRawTxDataResult;

import java.util.AbstractMap;
import java.util.Map;

public class TxSigner extends Wallet {

    private int accountsChainID = -1;

    public TxSigner(AbstractMap.SimpleEntry<String,Account>[] accounts) {
        super(null);
        for (AbstractMap.SimpleEntry<String, Account> account : accounts) {
            addAccount(account.getKey(), account.getValue());
        }
    }

    public int getAccountsChainID() {
        if (accounts.isEmpty()) {
            LOG.warn("Cannot determine accounts chain ID as there is no account in this wallet.");
        }
        for (Map.Entry<String, Account> entry : accounts.entrySet()) {
            if (accountsChainID == -1) {
                accountsChainID = entry.getValue().getChainID();
            }
            if (accountsChainID != entry.getValue().getChainID()) {
                return -1;
            }
        }
        return accountsChainID;
    }

    public SignedRawTx sign(UnsignedRawTx unsignedRawTx) throws AbelException {
        try {
            GenerateRawTxDataArgs args = buildGoFuncArgs(unsignedRawTx);
            GenerateRawTxDataResult result = getGoProxy().goGenerateRawTxData(args);
            return new SignedRawTx(new Bytes(result.getData().toByteArray()),
                    new Bytes(result.getTxid().toByteArray()).reverse().toHex());
        } catch (Exception e) {
            throw new AbelException(e);
        }
    }

    protected GenerateRawTxDataArgs buildGoFuncArgs(UnsignedRawTx unsignedRawTx) throws AbelException {
        try {
            GenerateRawTxDataArgs.Builder builder = GenerateRawTxDataArgs.newBuilder();
            builder.setSerializedTxRequest(ByteString.copyFrom(unsignedRawTx.data.getData()));

            for (String accountID : unsignedRawTx.signerAccountIDs) {
                Account signerAccount = getSignerAccount(accountID);
                if (signerAccount == null) {
                    throw new AbelException("Signer account " + accountID + " not found.");
                }
                PrivacyLevel accountPrivacyLevel = signerAccount.getPrivacyLevel();
                builder.addPrivacyLevels(accountPrivacyLevel.getValue());
                builder.addSpendKeyRootSeeds(ByteString.copyFrom(signerAccount.getSpendSecretRootSeed().getData()));
                builder.addSerialNoKeyRootSeeds(
                        (accountPrivacyLevel== PrivacyLevel.PSEUDO_PRIVATE || accountPrivacyLevel== PrivacyLevel.PSEUDO_CT_PRIVATE) ?
                                ByteString.empty()
                                : ByteString.copyFrom(signerAccount.getSerialNoSecretRootSeed().getData()));
                builder.addViewKeyRootSeeds(
                       accountPrivacyLevel== PrivacyLevel.PSEUDO_PRIVATE ? ByteString.empty()
                                : ByteString.copyFrom(signerAccount.getViewKeyRootSeed().getData()));
                builder.addDetectorRootKeys(ByteString.copyFrom(signerAccount.getDetectorRootKey().getData()));
            }
            return builder.build();
        } catch (Exception e) {
            throw new AbelException(e);
        }
    }
}
