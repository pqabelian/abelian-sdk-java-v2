package info.abelian.sdk.wallet;

import info.abelian.sdk.common.*;

import info.abelian.sdk.proto.Core.CoinReceiveFromTxOutDataResult;
import info.abelian.sdk.proto.Core.BlockDescMessage;

import info.abelian.sdk.common.CryptoSeed.DetectorRootKey;
import info.abelian.sdk.common.CryptoSeed.SerialNoSecretRootSeed;
import info.abelian.sdk.common.CryptoSeed.ViewSecretRootSeed;

public class ViewAccount extends AbelBase {
    protected int chainID;

    protected PrivacyLevel privacyLevel;

    protected SerialNoSecretRootSeed serialNoSecretRootSeed;

    protected ViewSecretRootSeed viewKeyRootSeed;

    protected DetectorRootKey detectorRootKey;

    public ViewAccount(int chainID, PrivacyLevel privacyLevel,
                       SerialNoSecretRootSeed serialNoSecretRootSeed,
                       ViewSecretRootSeed viewKeyRootSeed,
                       DetectorRootKey detectorRootKey) {
        this.chainID = chainID;
        this.privacyLevel = privacyLevel;
        if (privacyLevel == PrivacyLevel.FULLY_PRIVATE) {
            this.serialNoSecretRootSeed = serialNoSecretRootSeed;
            this.viewKeyRootSeed = viewKeyRootSeed;
        }else if (privacyLevel == PrivacyLevel.PSEUDO_CT_PRIVATE){
            this.viewKeyRootSeed = viewKeyRootSeed;
        }
        this.detectorRootKey = detectorRootKey;
    }

    public int getChainID() {
        return chainID;
    }

    public PrivacyLevel getPrivacyLevel() {
        return privacyLevel;
    }

    public SerialNoSecretRootSeed getSerialNoKeyRootSeed() {
        return serialNoSecretRootSeed;
    }

    public ViewSecretRootSeed getViewKeyRootSeed() {
        return viewKeyRootSeed;
    }

    public DetectorRootKey getDetectorRootKey() {
        return detectorRootKey;
    }

    static public CoinAddress decodeCoinAddressFromSerializedTxOutData(int txVersion, Bytes txOutData) throws AbelException {
        Bytes coinAddressBytes = Crypto.decodeCoinAddressFromSerializedTxOutData(txVersion, txOutData);
        return new CoinAddress(coinAddressBytes.getData());
    }

    public Coin coinReceive(Bytes blockHash, long blockHeight, int txVersion, Bytes txid, int index, Bytes txOutData) throws AbelException {
        CoinReceiveFromTxOutDataResult result = Crypto.coinReceiveFromTxOutData(
                detectorRootKey,
                viewKeyRootSeed,
                privacyLevel,
                txVersion,
                txOutData);
        if (!result.getSuccess()) {
            return null;
        }
        Bytes coinAddressBytes = Crypto.decodeCoinAddressFromSerializedTxOutData(txVersion, txOutData);
        CoinAddress coinaddress = new CoinAddress(coinAddressBytes.getData());
        Fingerprint fingerprint = coinaddress.getFingerprint();
        return new Coin(txVersion, new CoinID(txid, index),fingerprint, result.getCoinValue(), txOutData, blockHash, blockHeight, null);
    }

    public Bytes genCoinSerialNumber(CoinID coinID, BlockDescMessage[] blockDescs) throws AbelException {
        SerialNoSecretRootSeed localSerialNoSecretRootSeed = serialNoSecretRootSeed;
        if (privacyLevel == PrivacyLevel.PSEUDO_PRIVATE || privacyLevel == PrivacyLevel.PSEUDO_CT_PRIVATE) {
            localSerialNoSecretRootSeed = null;
        }
        return Crypto.generateCoinSerialNumber(
                localSerialNoSecretRootSeed,
                coinID.txid,
                coinID.index,
                blockDescs
        );
    }

    public static ViewAccount loadViewAccount(int chainID, PrivacyLevel privacyLevel,
                                              SerialNoSecretRootSeed serialNoSecretRootSeed,
                                              ViewSecretRootSeed viewKeyRootSeed,
                                              DetectorRootKey detectorRootKey) {
        return new ViewAccount(chainID, privacyLevel, serialNoSecretRootSeed, viewKeyRootSeed, detectorRootKey);
    }
}