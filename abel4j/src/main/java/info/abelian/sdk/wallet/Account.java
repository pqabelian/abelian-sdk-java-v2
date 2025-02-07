package info.abelian.sdk.wallet;

import info.abelian.sdk.common.*;

import info.abelian.sdk.proto.Core.BlockDescMessage;
import info.abelian.sdk.proto.Core.GenerateCryptoKeysAndAddressByRootSeedsResult;

import info.abelian.sdk.common.CryptoSeed.EntropySeed;
import info.abelian.sdk.common.CryptoSeed.SpendSecretRootSeed;
import info.abelian.sdk.common.CryptoSeed.SerialNoSecretRootSeed;
import info.abelian.sdk.common.CryptoSeed.ViewSecretRootSeed;
import info.abelian.sdk.common.CryptoSeed.DetectorRootKey;

public class Account extends AbelBase {
    protected ViewAccount viewAccount;

    final private SpendSecretRootSeed spendSecretRootSeed;

    public Account(int chainID, PrivacyLevel privacyLevel, String[] mnemonics) throws AbelException {
        Bytes[] cryptoSeeds = Crypto.mnemonicsToCryptoSeed(mnemonics);

        this.viewAccount = new ViewAccount(chainID, privacyLevel,
                new SerialNoSecretRootSeed(cryptoSeeds[1]),
                new ViewSecretRootSeed(cryptoSeeds[2]),
                new DetectorRootKey(cryptoSeeds[3]));
        this.spendSecretRootSeed = new SpendSecretRootSeed(cryptoSeeds[0]);
    }

    public Account(int chainID, PrivacyLevel privacyLevel, EntropySeed entropySeed) throws AbelException {
        Bytes[] cryptoSeeds = Crypto.entropySeedToCryptoSeeds(entropySeed);

        this.viewAccount = new ViewAccount(chainID, privacyLevel,
                privacyLevel == null ? null : new SerialNoSecretRootSeed(cryptoSeeds[1]),
                privacyLevel == null ? null : new ViewSecretRootSeed(cryptoSeeds[2]),
                new DetectorRootKey(cryptoSeeds[3]));
        this.spendSecretRootSeed = new SpendSecretRootSeed(cryptoSeeds[0]);
    }

    public Account(int chainID, PrivacyLevel privacyLevel,
                   SpendSecretRootSeed spendSecretRootSeed,
                   SerialNoSecretRootSeed serialNoSecretRootSeed,
                   ViewSecretRootSeed viewKeyRootSeed,
                   DetectorRootKey detectorRootKey) {
        this.viewAccount = new ViewAccount(chainID, privacyLevel, serialNoSecretRootSeed, viewKeyRootSeed, detectorRootKey);
        this.spendSecretRootSeed = spendSecretRootSeed;
    }

    public ViewAccount getViewAccount() {
        return viewAccount;
    }

    public SpendSecretRootSeed getSpendSecretRootSeed() {
        return spendSecretRootSeed;
    }

    public int getChainID() {
        return viewAccount.chainID;
    }

    public PrivacyLevel getPrivacyLevel() {
        return viewAccount.privacyLevel;
    }

    public SerialNoSecretRootSeed getSerialNoSecretRootSeed() {
        return viewAccount.serialNoSecretRootSeed;
    }

    public ViewSecretRootSeed getViewKeyRootSeed() {
        return viewAccount.viewKeyRootSeed;
    }

    public DetectorRootKey getDetectorRootKey() {
        return viewAccount.detectorRootKey;
    }

    public AbelAddress generateAbelAddress() throws AbelException {
        GenerateCryptoKeysAndAddressByRootSeedsResult result = Crypto.generateKeysAndAddress(
                viewAccount.privacyLevel,
                new SpendSecretRootSeed(spendSecretRootSeed.getData()),
                new SerialNoSecretRootSeed(viewAccount.serialNoSecretRootSeed.getData()),
                new ViewSecretRootSeed(viewAccount.viewKeyRootSeed.getData()),
                new DetectorRootKey(viewAccount.detectorRootKey.getData()));

        return new AbelAddress(
                Crypto.getAbelAddressFromCryptoAddress(
                        viewAccount.chainID,
                        new CryptoAddress(result.getCryptoAddress().toByteArray())
                ).getData()
        );
    }

    public Coin coinReceive(Bytes blockHash, long blockHeight, int txVersion, Bytes txid, int index, Bytes txOutData) throws AbelException {
        return viewAccount.coinReceive(blockHash, blockHeight, txVersion, txid, index, txOutData);
    }

    public Bytes genCoinSerialNumber(CoinID coinID, BlockDescMessage[] blockDescs) throws AbelException {
        return viewAccount.genCoinSerialNumber(coinID, blockDescs);
    }

    public static EntropySeed generateEntropySeed() throws AbelException {
        return Crypto.generateEntropySeed();
    }

    public static String[] entropySeedToMnemonics(EntropySeed entropySeed) throws AbelException {
        return Crypto.entropySeedToMnemonics(entropySeed);
    }

    public static Account generateAccount(int chainID, PrivacyLevel privacyLevel) throws AbelException {
        Bytes[] cryptoSeeds = Crypto.generateCryptoSeeds(privacyLevel);
        SpendSecretRootSeed spendSecretRootSeed = new SpendSecretRootSeed(cryptoSeeds[0]);
        SerialNoSecretRootSeed serialNoSecretRootSeed = new SerialNoSecretRootSeed(cryptoSeeds[1]);
        ViewSecretRootSeed viewKeyRootSeed = new ViewSecretRootSeed(cryptoSeeds[2]);
        DetectorRootKey detectorRootKey = new DetectorRootKey(cryptoSeeds[3]);

        return new Account(chainID, privacyLevel,
                spendSecretRootSeed,
                serialNoSecretRootSeed,
                viewKeyRootSeed,
                detectorRootKey);
    }

    public static Account loadAccount(int chainID, PrivacyLevel privacyLevel, EntropySeed entropySeed) throws AbelException {
        return new Account(chainID, privacyLevel, entropySeed);
    }

    public static Account loadAccount(int chainID, PrivacyLevel privacyLevel, String[] mnemonics) throws AbelException {
        return new Account(chainID, privacyLevel, mnemonics);
    }

    public static Account loadAccount(int chainID, PrivacyLevel privacyLevel,
                                      SpendSecretRootSeed spendSecretRootSeed,
                                      SerialNoSecretRootSeed serialNoSecretRootSeed,
                                      ViewSecretRootSeed viewKeyRootSeed,
                                      DetectorRootKey detectorRootKey) {
        return new Account(chainID, privacyLevel, spendSecretRootSeed, serialNoSecretRootSeed, viewKeyRootSeed, detectorRootKey);
    }
}