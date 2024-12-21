package info.abelian.sdk.go;

import com.sun.jna.Library;
import com.sun.jna.Pointer;

// Functions exported by the Go library
public interface GoLibrary extends Library {
  public Pointer GenerateSafeCryptoSeed(Object... params);

  public Pointer GenerateCryptoKeysAndAddressByRootSeeds(Object... params);

  public Pointer GetAbelAddressFromCryptoAddress(Object... params);

  public Pointer GetCryptoAddressFromAbelAddress(Object... params);

  public Pointer GetShortAbelAddressFromAbelAddress(Object... params);

  public Pointer CoinReceiveFromTxOutData(Object... params);

  public Pointer GenerateRawTxRequest(Object... params);

  public Pointer GenerateRawTxData(Object... params);

  public Pointer GenerateCoinSerialNumber(Object... params);
}
