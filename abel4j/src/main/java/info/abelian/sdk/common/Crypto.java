package info.abelian.sdk.common;

import com.google.protobuf.ByteString;

import info.abelian.sdk.common.CryptoSeed.*;


import info.abelian.sdk.proto.Core;
import info.abelian.sdk.proto.Core.GenerateEntropySeedArgs;
import info.abelian.sdk.proto.Core.GenerateEntropySeedResult;

import info.abelian.sdk.proto.Core.EntropySeedToMnemonicsArgs;
import info.abelian.sdk.proto.Core.EntropySeedToMnemonicsResult;

import info.abelian.sdk.proto.Core.MnemonicsToEntropySeedArgs;
import info.abelian.sdk.proto.Core.MnemonicsToEntropySeedResult;

import info.abelian.sdk.proto.Core.EntropySeedToCryptoSeedArgs;
import info.abelian.sdk.proto.Core.EntropySeedToCryptoSeedResult;

import info.abelian.sdk.proto.Core.MnemonicsToCryptoSeedArgs;
import info.abelian.sdk.proto.Core.MnemonicsToCryptoSeedResult;

import info.abelian.sdk.proto.Core.EntropySeedToPublicRandRootSeedArgs;
import info.abelian.sdk.proto.Core.EntropySeedToPublicRandRootSeedResult;

import info.abelian.sdk.proto.Core.MnemonicsToPublicRandRootSeedArgs;
import info.abelian.sdk.proto.Core.MnemonicsToPublicRandRootSeedResult;

import info.abelian.sdk.proto.Core.GenerateSafeCryptoSeedArgs;
import info.abelian.sdk.proto.Core.GenerateSafeCryptoSeedResult;

import info.abelian.sdk.proto.Core.SequenceNoToPublicRandArgs;
import info.abelian.sdk.proto.Core.SequenceNoToPublicRandResult;

import info.abelian.sdk.proto.Core.GenerateCryptoKeysAndAddressByRootSeedsFromPublicRandArgs;
import info.abelian.sdk.proto.Core.GenerateCryptoKeysAndAddressByRootSeedsFromPublicRandResult;

import info.abelian.sdk.proto.Core.GenerateCryptoKeysAndAddressByRootSeedsArgs;
import info.abelian.sdk.proto.Core.GenerateCryptoKeysAndAddressByRootSeedsResult;

import info.abelian.sdk.proto.Core.GetCoinAddressFromCryptoAddressArgs;

import info.abelian.sdk.proto.Core.GetAbelAddressFromCryptoAddressArgs;

import info.abelian.sdk.proto.Core.GetCryptoAddressFromAbelAddressArgs;

import info.abelian.sdk.proto.Core.GetShortAbelAddressFromAbelAddressArgs;


import info.abelian.sdk.proto.Core.CoinReceiveFromTxOutDataArgs;
import info.abelian.sdk.proto.Core.CoinReceiveFromTxOutDataResult;

import info.abelian.sdk.proto.Core.GenerateCoinSerialNumberArgs;
import info.abelian.sdk.proto.Core.GenerateCoinSerialNumberResult;

import info.abelian.sdk.proto.Core.GetFingerprintFromCoinAddressArgs;
import info.abelian.sdk.proto.Core.GetFingerprintFromCoinAddressResult;

import info.abelian.sdk.proto.Core.DecodeCoinAddressFromSerializedTxOutDataArgs;
import info.abelian.sdk.proto.Core.DecodeCoinAddressFromSerializedTxOutDataResult;

import java.util.Arrays;


public class Crypto extends AbelBase {
    public static EntropySeed generateEntropySeed() throws AbelException {
        try {
            GenerateEntropySeedArgs args = GenerateEntropySeedArgs.newBuilder()
                    .build();
            GenerateEntropySeedResult result = getGoProxy().goGenerateEntropySeed(args);
            return new EntropySeed(result.getEntropySeed().toByteArray());
        } catch (Exception e) {
            throw new AbelException(e);
        }
    }

    public static String[] entropySeedToMnemonics(EntropySeed entropySeed) throws AbelException {
        try {
            EntropySeedToMnemonicsArgs args = EntropySeedToMnemonicsArgs.newBuilder()
                    .setEntropySeed(ByteString.copyFrom(entropySeed.getData()))
                    .build();
            EntropySeedToMnemonicsResult result = getGoProxy().goEntropySeedToMnemonics(args);

            String[] mnemonics = new String[result.getMnemonicCount()];
            for (int i = 0; i < result.getMnemonicCount(); i++) {
                mnemonics[i] = result.getMnemonic(i);
            }
            return mnemonics;
        } catch (Exception e) {
            throw new AbelException(e);
        }
    }

    public static EntropySeed mnemonicsToEntropySeed(String[] mnemonics) throws AbelException {
        try {
            MnemonicsToEntropySeedArgs.Builder builder = MnemonicsToEntropySeedArgs.newBuilder();
            for (String mnemonic : mnemonics) {
                builder.addMnemonic(mnemonic);
            }
            MnemonicsToEntropySeedResult result = getGoProxy().goMnemonicsToEntropySeed(builder.build());
            return new EntropySeed(result.getEntropySeed().toByteArray());
        } catch (Exception e) {
            throw new AbelException(e);
        }
    }

    public static Bytes[] entropySeedToCryptoSeeds(EntropySeed entropySeed) throws AbelException {
        try {
            EntropySeedToCryptoSeedArgs args = EntropySeedToCryptoSeedArgs.newBuilder()
                    .setEntropySeed(ByteString.copyFrom(entropySeed.getData()))
                    .build();

            EntropySeedToCryptoSeedResult result = getGoProxy().goEntropySeedToCryptoSeed(args);
            return new Bytes[]{
                    new SpendSecretRootSeed(result.getSpendKeyRootSeed().toByteArray()),
                    new SerialNoSecretRootSeed(result.getSerialNoKeyRootSeed().toByteArray()),
                    new ViewSecretRootSeed(result.getViewKeyRootSeed().toByteArray()),
                    new DetectorRootKey(result.getDetectorRootKey().toByteArray()),
            };
        } catch (Exception e) {
            throw new AbelException(e);
        }
    }

    public static Bytes[] mnemonicsToCryptoSeed(String[] mnemonics) throws AbelException {
        try {
            MnemonicsToCryptoSeedArgs.Builder builder = MnemonicsToCryptoSeedArgs.newBuilder();
            for (String mnemonic : mnemonics) {
                builder.addMnemonic(mnemonic);
            }

            MnemonicsToCryptoSeedResult result = getGoProxy().goMnemonicsToCryptoSeed(builder.build());
            return new Bytes[]{
                    new SpendSecretRootSeed(result.getSpendKeyRootSeed().toByteArray()),
                    new SerialNoSecretRootSeed(result.getSerialNoKeyRootSeed().toByteArray()),
                    new ViewSecretRootSeed(result.getViewKeyRootSeed().toByteArray()),
                    new DetectorRootKey(result.getDetectorRootKey().toByteArray()),
            };
        } catch (Exception e) {
            throw new AbelException(e);
        }
    }

    public static PublicRandRootSeed entropySeedToPublicRandRootSeed(EntropySeed entropySeed) throws AbelException {
        try {

            EntropySeedToPublicRandRootSeedArgs args = EntropySeedToPublicRandRootSeedArgs.newBuilder()
                    .setEntropySeed(ByteString.copyFrom(entropySeed.getData()))
                    .build();

            EntropySeedToPublicRandRootSeedResult result = getGoProxy().goEntropySeedToPublicRandRootSeed(args);
            return new PublicRandRootSeed(result.getPublicRandRootSeed().toByteArray());
        } catch (Exception e) {
            throw new AbelException(e);
        }
    }

    public static PublicRandRootSeed mnemonicsToPublicRandRootSeed(String[] mnemonics) throws AbelException {
        try {
            MnemonicsToPublicRandRootSeedArgs.Builder builder = MnemonicsToPublicRandRootSeedArgs.newBuilder();
            for (int i = 0; i < mnemonics.length; i++) {
                builder.setMnemonic(i, mnemonics[i]);
            }

            MnemonicsToPublicRandRootSeedResult result = getGoProxy().goMnemonicsToPublicRandRootSeed(builder.build());
            return new PublicRandRootSeed(result.getPublicRandRootSeed().toByteArray());
        } catch (Exception e) {
            throw new AbelException(e);
        }
    }

    public static Bytes[] generateCryptoSeeds(PrivacyLevel privacyLevel) throws AbelException {
        try {
            GenerateSafeCryptoSeedArgs args = GenerateSafeCryptoSeedArgs.newBuilder()
                    .setPrivacyLevel(privacyLevel.getValue())
                    .build();
            GenerateSafeCryptoSeedResult result = getGoProxy().goGenerateSafeCryptoSeed(args);
            return new Bytes[]{
                    new SpendSecretRootSeed(result.getSpendKeyRootSeed().toByteArray()),
                    new SerialNoSecretRootSeed(result.getSerialNoKeyRootSeed().toByteArray()),
                    new ViewSecretRootSeed(result.getViewKeyRootSeed().toByteArray()),
                    new DetectorRootKey(result.getDetectorRootKey().toByteArray()),
            };
        } catch (Exception e) {
            throw new AbelException(e);
        }
    }

    public static GenerateCryptoKeysAndAddressByRootSeedsResult generateKeysAndAddress(
            PrivacyLevel privacyLevel,
            SpendSecretRootSeed spendSecretRootSeed,
            SerialNoSecretRootSeed serialNoSecretRootSeed,
            ViewSecretRootSeed viewKeyRootSeed,
            DetectorRootKey detectorRootKey)
            throws AbelException {
        try {
            if (privacyLevel != PrivacyLevel.FULLY_PRIVATE) {
                throw new AbelException("Privacy level must be FULLY_PRIVATE");
            }
            GenerateCryptoKeysAndAddressByRootSeedsArgs args = GenerateCryptoKeysAndAddressByRootSeedsArgs.newBuilder()
                    .setPrivacyLevel(privacyLevel.getValue())
                    .setSpendKeyRootSeed(ByteString.copyFrom(spendSecretRootSeed.getData()))
                    .setSerialNoKeyRootSeed(ByteString.copyFrom(serialNoSecretRootSeed.getData()))
                    .setViewKeyRootSeed(ByteString.copyFrom(viewKeyRootSeed.getData()))
                    .setDetectorRootKey(ByteString.copyFrom(detectorRootKey.getData()))
                    .build();
            return getGoProxy().goGenerateCryptoKeysAndAddressByRootSeeds(args);
        } catch (Exception e) {
            throw new AbelException(e);
        }
    }

    public static GenerateCryptoKeysAndAddressByRootSeedsResult generateKeysAndAddress(
            PrivacyLevel privacyLevel,
            SpendSecretRootSeed spendSecretRootSeed,
            DetectorRootKey detectorRootKey)
            throws AbelException {
        try {
            if (privacyLevel != PrivacyLevel.PSEUDO_PRIVATE) {
                throw new AbelException("Privacy level must be PSEUDO_PRIVATE");
            }
            GenerateCryptoKeysAndAddressByRootSeedsArgs args = GenerateCryptoKeysAndAddressByRootSeedsArgs.newBuilder()
                    .setPrivacyLevel(privacyLevel.getValue())
                    .setSpendKeyRootSeed(ByteString.copyFrom(spendSecretRootSeed.getData()))
                    .setDetectorRootKey(ByteString.copyFrom(detectorRootKey.getData()))
                    .build();
            return getGoProxy().goGenerateCryptoKeysAndAddressByRootSeeds(args);
        } catch (Exception e) {
            throw new AbelException(e);
        }
    }

    public static Bytes sequenceNoToPublicRand(PublicRandRootSeed publicRandRootSeed, Integer seqNo) throws AbelException {
        try {
            SequenceNoToPublicRandArgs args = SequenceNoToPublicRandArgs.newBuilder()
                    .setPublicRandRootSeed(ByteString.copyFrom(publicRandRootSeed.getData()))
                    .setSequenceNo(seqNo)
                    .build();
            SequenceNoToPublicRandResult result = getGoProxy().goSequenceNoToPublicRand(args);
            return new Bytes(result.getPublicRand().toByteArray());
        } catch (Exception e) {
            throw new AbelException(e);
        }
    }

    public static GenerateCryptoKeysAndAddressByRootSeedsFromPublicRandResult generateKeysAndAddress(
            PrivacyLevel privacyLevel,
            SpendSecretRootSeed spendSecretRootSeed,
            SerialNoSecretRootSeed serialNoSecretRootSeed,
            ViewSecretRootSeed viewKeyRootSeed,
            DetectorRootKey detectorRootKey,
            Bytes publicRand)
            throws AbelException {
        try {
            GenerateCryptoKeysAndAddressByRootSeedsFromPublicRandArgs.Builder builder = GenerateCryptoKeysAndAddressByRootSeedsFromPublicRandArgs.newBuilder();
            builder.setPrivacyLevel(privacyLevel.getValue());
            builder.setSpendKeyRootSeed(ByteString.copyFrom(spendSecretRootSeed.getData()));
            if (privacyLevel != PrivacyLevel.PSEUDO_PRIVATE) {
                builder.setSerialNoKeyRootSeed(ByteString.copyFrom(serialNoSecretRootSeed.getData()));
                builder.setViewKeyRootSeed(ByteString.copyFrom(viewKeyRootSeed.getData()));
            }
            builder.setDetectorRootKey(ByteString.copyFrom(detectorRootKey.getData()));
            builder.setPublicRand(ByteString.copyFrom(publicRand.getData()));
            return getGoProxy().goGenerateCryptoKeysAndAddressByRootSeedsFromPublicRand(builder.build());
        } catch (Exception e) {
            throw new AbelException(e);
        }
    }

    public static Bytes getCoinAddressFromCryptoAddress(
            Bytes cryptoAddress) throws AbelException {
        try {
            GetCoinAddressFromCryptoAddressArgs args = GetCoinAddressFromCryptoAddressArgs.newBuilder()
                    .setCryptoAddress(ByteString.copyFrom(cryptoAddress.getData()))
                    .build();

            return new Bytes(getGoProxy().goGetCoinAddressFromCryptoAddress(args).getCoinAddress().toByteArray());
        } catch (Exception e) {
            throw new AbelException(e);
        }
    }

    public static Bytes getAbelAddressFromCryptoAddress(Integer chainID, Bytes cryptoAddress) throws AbelException {
        try {
            GetAbelAddressFromCryptoAddressArgs args = GetAbelAddressFromCryptoAddressArgs.newBuilder()
                    .setChainID(chainID)
                    .setCryptoAddress(ByteString.copyFrom(cryptoAddress.getData()))
                    .build();
            return new Bytes(getGoProxy().goGetAbelAddressFromCryptoAddress(args).getAbelAddress().toByteArray());
        } catch (Exception e) {
            throw new AbelException(e);
        }
    }

    public static Bytes getCryptoAddressFromAbelAddress(Bytes abelAddress) throws AbelException {
        try {
            GetCryptoAddressFromAbelAddressArgs args = GetCryptoAddressFromAbelAddressArgs.newBuilder()
                    .setAbelAddress(ByteString.copyFrom(abelAddress.getData()))
                    .build();
            return new Bytes(getGoProxy().goGetCryptoAddressFromAbelAddress(args).getCryptoAddress().toByteArray());
        } catch (Exception e) {
            throw new AbelException(e);
        }
    }

    public static Bytes getShortAbelAddressFromAbelAddress(Bytes abelAddress) throws AbelException {
        try {
            GetShortAbelAddressFromAbelAddressArgs args = GetShortAbelAddressFromAbelAddressArgs.newBuilder()
                    .setAbelAddress(ByteString.copyFrom(abelAddress.getData()))
                    .build();
            return new Bytes(getGoProxy().goGetShortAbelAddressFromAbelAddress(args).getShortAbelAddress().toByteArray());
        } catch (Exception e) {
            throw new AbelException(e);
        }
    }

    public static CoinReceiveFromTxOutDataResult coinReceiveFromTxOutData(
            DetectorRootKey detectorRootKey,
            ViewSecretRootSeed viewKeyRootSeed,
            PrivacyLevel privacyLevel,
            Integer txVersion,
            Bytes txOutData) throws AbelException {
        try {
            CoinReceiveFromTxOutDataArgs.Builder builder = CoinReceiveFromTxOutDataArgs.newBuilder();
            builder.setCoinDetectorRootKey(ByteString.copyFrom(detectorRootKey.getData()));
            if (privacyLevel == PrivacyLevel.FULLY_PRIVATE) {
                builder.setCoinViewSecretRootSeed(ByteString.copyFrom(viewKeyRootSeed.getData()));
            }
            builder.setAccountPrivacyLevel(privacyLevel.getValue());
            builder.setTxVersion(txVersion);
            builder.setTxOutData(ByteString.copyFrom(txOutData.getData()));

            return getGoProxy().goCoinReceiveFromTxOutData(builder.build());
        } catch (Exception e) {
            throw new AbelException(e);
        }
    }


    public static Bytes generateCoinSerialNumber(
            SerialNoSecretRootSeed serialNoSecretRootSeed,
            Bytes txid,
            Integer index,
            Core.BlockDescMessage[] blockDescs) throws AbelException {
        try {
            GenerateCoinSerialNumberArgs.Builder builder = GenerateCoinSerialNumberArgs.newBuilder();
            if (serialNoSecretRootSeed != null) {
                builder.setSerialNoSecretRootSeed(ByteString.copyFrom(serialNoSecretRootSeed.getData()));
            }
            builder.setTxid(ByteString.copyFrom(txid.getData()));
            builder.setIndex(index);
            builder.addAllRingBlockDescs(Arrays.asList(blockDescs));

            GenerateCoinSerialNumberResult result = getGoProxy().goGenerateCoinSerialNumber(builder.build());
            return new Bytes(result.getSerialNumber().toByteArray());
        } catch (Exception e) {
            throw new AbelException(e);
        }
    }

    public static Bytes getFingerprintFromCoinAddress(
            Bytes coinAddress) throws AbelException {
        try {
            GetFingerprintFromCoinAddressArgs.Builder builder = GetFingerprintFromCoinAddressArgs.newBuilder();
            builder.setCoinAddress(ByteString.copyFrom(coinAddress.getData()));


            GetFingerprintFromCoinAddressResult result = getGoProxy().goGetFingerprintFromCoinAddress(builder.build());
            return new Bytes(result.getFingerprint().toByteArray());
        } catch (Exception e) {
            throw new AbelException(e);
        }
    }

    public static Bytes decodeCoinAddressFromSerializedTxOutData(
            Integer txVersion,
            Bytes txOutData) throws AbelException {
        try {
            DecodeCoinAddressFromSerializedTxOutDataArgs.Builder builder = DecodeCoinAddressFromSerializedTxOutDataArgs.newBuilder();
            builder.setTxVersion(txVersion);
            builder.setSerializedTxOutData(ByteString.copyFrom(txOutData.getData()));


            DecodeCoinAddressFromSerializedTxOutDataResult result = getGoProxy().goDecodeCoinAddressFromSerializedTxOutData(builder.build());
            return new Bytes(result.getCoinAddress().toByteArray());
        } catch (Exception e) {
            throw new AbelException(e);
        }
    }

}
