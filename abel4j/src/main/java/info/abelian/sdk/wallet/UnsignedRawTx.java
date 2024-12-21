package info.abelian.sdk.wallet;

import info.abelian.sdk.common.Bytes;
import info.abelian.sdk.common.CoinAddress;
import info.abelian.sdk.common.Struct;

public class UnsignedRawTx extends Struct {
  
  public Bytes data;
  
  public int[] signerAccountIDs;

  public UnsignedRawTx(Bytes data, int[] signerAccountIDs) {
    this.data = data;
    this.signerAccountIDs = signerAccountIDs;
  }
}
