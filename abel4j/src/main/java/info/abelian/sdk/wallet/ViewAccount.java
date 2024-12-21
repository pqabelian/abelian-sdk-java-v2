package info.abelian.sdk.wallet;

import com.google.protobuf.ByteString;
import info.abelian.sdk.common.AbelBase;
import info.abelian.sdk.common.AbelException;
import info.abelian.sdk.common.Bytes;

import info.abelian.sdk.proto.Core.CoinReceiveFromTxOutDataArgs;
import info.abelian.sdk.proto.Core.CoinReceiveFromTxOutDataResult;
import info.abelian.sdk.proto.Core.BlockDescMessage;
import info.abelian.sdk.proto.Core.GenerateCoinSerialNumberArgs;
import info.abelian.sdk.proto.Core.GenerateCoinSerialNumberResult;

import info.abelian.sdk.wallet.CryptoSeed.DetectorRootKey;
import info.abelian.sdk.wallet.CryptoSeed.SerialNoKeyRootSeed;
import info.abelian.sdk.wallet.CryptoSeed.ViewKeyRootSeed;

import java.util.Arrays;

public class ViewAccount extends AbelBase {
    protected int id;
    protected int chainID;

    protected PrivacyLevel privacyLevel;


    protected SerialNoKeyRootSeed serialNoKeyRootSeed;

    protected ViewKeyRootSeed viewKeyRootSeed;

    protected DetectorRootKey detectorRootKey;

    public ViewAccount(int accountID,int chainID, PrivacyLevel privacyLevel,
                          SerialNoKeyRootSeed serialNoKeyRootSeed,
                          ViewKeyRootSeed viewKeyRootSeed,
                          DetectorRootKey detectorRootKey) {
        this.id=accountID;
        this.chainID = chainID;
        this.privacyLevel = privacyLevel;
        this.serialNoKeyRootSeed = serialNoKeyRootSeed;
        this.viewKeyRootSeed = viewKeyRootSeed;
        this.detectorRootKey = detectorRootKey;
    }

    public int getId() {
        return id;
    }
    public int getChainID() {
        return chainID;
    }

    public PrivacyLevel getPrivacyLevel() {
        return privacyLevel;
    }

    public SerialNoKeyRootSeed getSerialNoKeyRootSeed() {
        return serialNoKeyRootSeed;
    }

    public ViewKeyRootSeed getViewKeyRootSeed() {
        return viewKeyRootSeed;
    }

    public DetectorRootKey getDetectorRootKey() {
        return detectorRootKey;
    }


    public Bytes toBytes() {
        if (privacyLevel != PrivacyLevel.FULLY_PRIVATE && privacyLevel != PrivacyLevel.PSEUDO_PRIVATE) {
            return null;
        }
        byte[] result = new byte[1 + 1 + CryptoSeed.LENGTH * 3];
        result[0] = (byte) chainID;
        result[1] = (byte) privacyLevel.ordinal();
        int offset = 2;
        if (privacyLevel == PrivacyLevel.FULLY_PRIVATE) {
            System.arraycopy(serialNoKeyRootSeed.getData(), 0, result, offset, serialNoKeyRootSeed.getLength());
            offset += serialNoKeyRootSeed.getLength();
            System.arraycopy(viewKeyRootSeed.getData(), 0, result, offset, viewKeyRootSeed.getLength());
            offset += viewKeyRootSeed.getLength();
        }
        System.arraycopy(detectorRootKey.getData(), 0, result, offset, detectorRootKey.getLength());
        offset += detectorRootKey.getLength();
        return new Bytes(result).getSubBytes(0, offset);
    }

    public Coin coinReceive(Bytes blockHash, long blockHeight, int txVersion, Bytes txid, int index, Bytes txOutData) throws AbelException {
        try {
            CoinReceiveFromTxOutDataArgs args = CoinReceiveFromTxOutDataArgs.newBuilder()
                    .setCoinDetectorRootKey(ByteString.copyFrom(detectorRootKey.getData()))
                    .setCoinViewSecretRootSeed(ByteString.copyFrom(viewKeyRootSeed.getData()))
                    .setAccountPrivacyLevel(privacyLevel.ordinal())
                    .setTxVersion(txVersion)
                    .setTxOutData(ByteString.copyFrom(txOutData.getData()))
                    .build();
            CoinReceiveFromTxOutDataResult result = getGoProxy().goCoinReceiveFromTxOutData(args);
            if (!result.getSuccess()) {
                return null;
            }

            return new Coin(txVersion, new CoinID(txid, index), result.getCoinValue(), txOutData, blockHash, blockHeight, null);
        } catch (Exception e) {
            throw new AbelException(e);
        }
    }

    public Bytes genCoinSerialNumber(CoinID coinID, BlockDescMessage[] blockDescs) throws AbelException {
        try {
            GenerateCoinSerialNumberArgs args = GenerateCoinSerialNumberArgs.newBuilder()
                    .setSerialNoSecretRootSeed(ByteString.copyFrom(serialNoKeyRootSeed.getData()))
                    .setTxid(ByteString.copyFrom(coinID.txid.getData()))
                    .setIndex(coinID.index)
                    .addAllRingBlockDescs(Arrays.asList(blockDescs))
                    .build();
            GenerateCoinSerialNumberResult result = getGoProxy().goGenerateCoinSerialNumber(args);
            return new Bytes(result.getSerialNumber().toByteArray());
        } catch (Exception e) {
            throw new AbelException(e);
        }
    }
}