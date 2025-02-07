package info.abelian.sdk.wallet;

import info.abelian.sdk.common.Bytes;
import info.abelian.sdk.common.Struct;

public class UnsignedRawTx extends Struct {

  public Bytes data;

  public String[] signerAccountIDs;

  public UnsignedRawTx(Bytes data, String[] signerAccountIDs) {
    this.data = data;
    this.signerAccountIDs = signerAccountIDs;
  }
}
