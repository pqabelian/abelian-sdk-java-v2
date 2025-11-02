package info.abelian.sdk.wallet;

import com.google.protobuf.ByteString;
import info.abelian.sdk.common.Bytes;
import info.abelian.sdk.common.PrivacyLevel;
import info.abelian.sdk.common.AbelException;

import static info.abelian.sdk.common.Crypto.mnemonicsToPublicRandRootSeed;
import static info.abelian.sdk.common.Crypto.entropySeedToPublicRandRootSeed;
import static info.abelian.sdk.common.Crypto.generateKeysAndAddress;
import static info.abelian.sdk.common.Crypto.sequenceNoToPublicRand;
import static info.abelian.sdk.common.Crypto.getAbelAddressFromCryptoAddress;


import info.abelian.sdk.common.CryptoSeed.EntropySeed;
import info.abelian.sdk.common.CryptoSeed.SpendSecretRootSeed;
import info.abelian.sdk.common.CryptoSeed.SerialNoSecretRootSeed;
import info.abelian.sdk.common.CryptoSeed.ViewSecretRootSeed;
import info.abelian.sdk.common.CryptoSeed.DetectorRootKey;
import info.abelian.sdk.common.CryptoSeed.PublicRandRootSeed;

public class SeqAccount extends Account {
    final private PublicRandRootSeed publicRandRootSeed;

    public SeqAccount(int chainID, PrivacyLevel privacyLevel, String[] mnemonics) throws AbelException {
        super(chainID, privacyLevel, mnemonics);
        this.publicRandRootSeed = new PublicRandRootSeed(
                new PublicRandRootSeed(mnemonicsToPublicRandRootSeed(mnemonics))
        );
    }

    public SeqAccount(int chainID, PrivacyLevel privacyLevel, EntropySeed entropySeed) throws AbelException {
        super(chainID, privacyLevel, entropySeed);
        this.publicRandRootSeed = new PublicRandRootSeed(
                new PublicRandRootSeed(entropySeedToPublicRandRootSeed(entropySeed))
        );
    }

    public SeqAccount(int chainID, PrivacyLevel privacyLevel,
                      SpendSecretRootSeed spendSecretRootSeed,
                      SerialNoSecretRootSeed serialNoSecretRootSeed,
                      ViewSecretRootSeed viewKeyRootSeed,
                      DetectorRootKey detectorRootKey,
                      PublicRandRootSeed publicRandRootSeed) {
        super(chainID, privacyLevel, spendSecretRootSeed, serialNoSecretRootSeed, viewKeyRootSeed, detectorRootKey);
        this.publicRandRootSeed = publicRandRootSeed;
    }

    public PublicRandRootSeed getPublicRandRootSeed() {
        return publicRandRootSeed;
    }

    public AbelAddress generateAbelAddress(int seqNo) throws AbelException {
            ByteString cryptoAddress = generateKeysAndAddress(
                    viewAccount.privacyLevel,
                    new SpendSecretRootSeed(super.getSpendSecretRootSeed().getData()),
                    (viewAccount.privacyLevel == PrivacyLevel.PSEUDO_PRIVATE|| viewAccount.privacyLevel == PrivacyLevel.PSEUDO_CT_PRIVATE) ?
                            null:
                            new SerialNoSecretRootSeed(viewAccount.serialNoSecretRootSeed.getData()),
                    viewAccount.privacyLevel == PrivacyLevel.PSEUDO_PRIVATE ?
                            null:
                            new ViewSecretRootSeed(viewAccount.viewKeyRootSeed.getData()),
                    new DetectorRootKey(viewAccount.detectorRootKey.getData()),
                    new Bytes(sequenceNoToPublicRand(publicRandRootSeed,seqNo).getData())).getCryptoAddress();

            return new AbelAddress(getAbelAddressFromCryptoAddress(
                    viewAccount.chainID,
                    new Bytes(cryptoAddress.toByteArray())
            ).getData());
    }

    public static SeqAccount loadSeqAccount(int chainID, PrivacyLevel privacyLevel,
                                            EntropySeed entropySeed) throws AbelException {
        return new SeqAccount(chainID, privacyLevel, entropySeed);
    }

    public static SeqAccount loadSeqAccount(int chainID, PrivacyLevel privacyLevel,
                                            String[] mnemonics) throws AbelException {
        return new SeqAccount(chainID, privacyLevel, mnemonics);
    }

    public static SeqAccount loadSeqAccount(int chainID, PrivacyLevel privacyLevel,
                                            SpendSecretRootSeed spendSecretRootSeed,
                                            SerialNoSecretRootSeed serialNoSecretRootSeed,
                                            ViewSecretRootSeed viewKeyRootSeed,
                                            DetectorRootKey detectorRootKey,
                                            PublicRandRootSeed publicRandRootSeed){
        return new SeqAccount(chainID, privacyLevel,
                spendSecretRootSeed,
                serialNoSecretRootSeed,
                viewKeyRootSeed,
                detectorRootKey,
                publicRandRootSeed);
    }
}
