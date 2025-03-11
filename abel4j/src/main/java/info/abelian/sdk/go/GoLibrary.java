package info.abelian.sdk.go;

import com.sun.jna.Library;
import com.sun.jna.Pointer;

// Functions exported by the Go library
public interface GoLibrary extends Library {
    public Pointer GenerateEntropySeed(Object... params);

    public Pointer EntropySeedToMnemonics(Object... params);

    public Pointer MnemonicsToEntropySeed(Object... params);

    public Pointer EntropySeedToCryptoSeed(Object... params);

    public Pointer MnemonicsToCryptoSeed(Object... params);

    public Pointer EntropySeedToPublicRandRootSeed(Object... params);

    public Pointer MnemonicsToPublicRandRootSeed(Object... params);

    public Pointer GenerateSafeCryptoSeed(Object... params);

    public Pointer GenerateCryptoKeysAndAddressByRootSeeds(Object... params);

    public Pointer SequenceNoToPublicRand(Object... params);

    public Pointer GenerateCryptoKeysAndAddressByRootSeedsFromPublicRand(Object... params);

    public Pointer GetCoinAddressFromCryptoAddress(Object... params);

    public Pointer GetAbelAddressFromCryptoAddress(Object... params);

    public Pointer GetCryptoAddressFromAbelAddress(Object... params);

    public Pointer GetShortAbelAddressFromAbelAddress(Object... params);

    public Pointer CoinReceiveFromTxOutData(Object... params);

    public Pointer GenerateRawTxRequest(Object... params);

    public Pointer GenerateRawTxData(Object... params);

    public Pointer GenerateCoinSerialNumber(Object... params);

    public Pointer GetFingerprintFromCoinAddress(Object... params);

    public Pointer DecodeCoinAddressFromSerializedTxOutData(Object... params);
}
