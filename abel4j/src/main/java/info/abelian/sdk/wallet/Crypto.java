package info.abelian.sdk.wallet;

import com.google.protobuf.ByteString;

import info.abelian.sdk.common.Bytes;
import info.abelian.sdk.common.AbelBase;
import info.abelian.sdk.common.AbelException;

import info.abelian.sdk.proto.Core.GenerateCryptoKeysAndAddressByRootSeedsArgs;
import info.abelian.sdk.proto.Core.GenerateCryptoKeysAndAddressByRootSeedsResult;
import info.abelian.sdk.proto.Core.GenerateSafeCryptoSeedArgs;
import info.abelian.sdk.proto.Core.GenerateSafeCryptoSeedResult;

public class Crypto extends AbelBase {

    public static Bytes[] generateSeed(PrivacyLevel privacyLevel) throws AbelException {
        try {
            GenerateSafeCryptoSeedArgs args = GenerateSafeCryptoSeedArgs.newBuilder()
                    .setPrivacyLevel(privacyLevel.ordinal())
                    .build();
            GenerateSafeCryptoSeedResult result = getGoProxy().goGenerateSafeCryptoSeed(args);
            return new Bytes[]{
                    new Bytes(result.getSpendKeyRootSeed().toByteArray()),
                    new Bytes(result.getSerialNoKeyRootSeed().toByteArray()),
                    new Bytes(result.getViewKeyRootSeed().toByteArray()),
                    new Bytes(result.getDetectorRootKey().toByteArray()),
            } ;
        } catch (Exception e) {
            throw new AbelException(e);
        }
    }

    public static GenerateCryptoKeysAndAddressByRootSeedsResult generateKeysAndAddress(
            PrivacyLevel privacyLevel,
            Bytes spendKeyRootSeed,
            Bytes serialNoKeyRootSeed,
            Bytes viewKeyRootSeed,
            Bytes detectorRootKey)
            throws AbelException {
        try {
            GenerateCryptoKeysAndAddressByRootSeedsArgs args = GenerateCryptoKeysAndAddressByRootSeedsArgs.newBuilder()
                    .setPrivacyLevel(privacyLevel.ordinal())
                    .setSpendKeyRootSeed(ByteString.copyFrom(spendKeyRootSeed.getData()))
                    .setSerialNoKeyRootSeed(ByteString.copyFrom(serialNoKeyRootSeed.getData()))
                    .setViewKeyRootSeed(ByteString.copyFrom(viewKeyRootSeed.getData()))
                    .setDetectorRootKey(ByteString.copyFrom(detectorRootKey.getData()))
                    .build();
            return getGoProxy().goGenerateCryptoKeysAndAddressByRootSeeds(args);
        } catch (Exception e) {
            throw new AbelException(e);
        }
    }
}
