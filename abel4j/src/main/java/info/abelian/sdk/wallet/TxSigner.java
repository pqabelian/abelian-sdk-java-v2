package info.abelian.sdk.wallet;

import com.google.protobuf.ByteString;

import info.abelian.sdk.common.AbelException;
import info.abelian.sdk.common.Bytes;
import info.abelian.sdk.proto.Core.GenerateRawTxDataArgs;
import info.abelian.sdk.proto.Core.GenerateRawTxDataResult;

import java.util.Map;

public class TxSigner extends Wallet {

    private int accountsChainID = -1;

    public TxSigner(Account[] accounts) {
        super(null);
        for (Account account : accounts) {
            addAccount(Integer.toString(account.getId()), account);
        }
    }

    public int getAccountsChainID() {
        if (accounts.isEmpty()) {
            LOG.warn("Cannot determine accounts chain ID as there is no account in this wallet.");
        }
        for (Map.Entry < String, Account > entry: accounts.entrySet()) {
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
                    new Bytes(result.getTxid().toByteArray()));
        } catch (Exception e) {
            throw new AbelException(e);
        }
    }

    protected GenerateRawTxDataArgs buildGoFuncArgs(UnsignedRawTx unsignedRawTx) throws AbelException {
        try {
            GenerateRawTxDataArgs.Builder builder = GenerateRawTxDataArgs.newBuilder();
            builder.setSerializedTxRequest(ByteString.copyFrom(unsignedRawTx.data.getData()));

            for (int accountID : unsignedRawTx.signerAccountIDs) {
                Account signerAccount = getSignerAccount(accountID);
                builder.addPrivacyLevels(signerAccount.getPrivacyLevel().ordinal());
                builder.addSpendKeyRootSeeds(ByteString.copyFrom(signerAccount.getSpendKeyRootSeed().getData()));
                builder.addSerialNoKeyRootSeeds(ByteString.copyFrom(signerAccount.getSerialNoKeyRootSeed().getData()));
                builder.addViewKeyRootSeeds(ByteString.copyFrom(signerAccount.getViewKeyRootSeed().getData()));
                builder.addDetectorRootKeys(ByteString.copyFrom(signerAccount.getDetectorRootKey().getData()));
            }
            return builder.build();
        } catch (Exception e) {
            throw new AbelException(e);
        }
    }
}
