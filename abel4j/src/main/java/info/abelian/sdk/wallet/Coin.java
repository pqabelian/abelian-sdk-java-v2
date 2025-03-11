package info.abelian.sdk.wallet;

import info.abelian.sdk.common.Bytes;
import info.abelian.sdk.common.Struct;

public class Coin extends Struct {
  public int txVersion;
  public CoinID id;
  public long blockHeight;
  public Bytes blockid;
  public Bytes fingerprint;
  public long value;
  public Bytes serialNumber;
  public Bytes txOutData;
  public Bytes ringid;
  public int ringIndex;

  public Coin(int txVersion,CoinID id,Bytes fingerprint, long value,
              Bytes txOutData, Bytes blockid, long blockHeight, Bytes serialNumber) {
    this.txVersion = txVersion;
    this.id = id;
    this.fingerprint=fingerprint;
    this.value = value;
    this.txOutData = txOutData;
    this.blockid = blockid;
    this.blockHeight = blockHeight;
    this.serialNumber = serialNumber;
  }
  public void updateRingInfo(Bytes ringid, int ringIndex) {
    this.ringid = ringid;
    this.ringIndex = ringIndex;
  }

  public String toString() {
    return String.format("COIN(id=%s, height=%d, blockid=%s, fingerprint=%s value=%d, script=%s)",
            id, blockHeight,blockid,
            fingerprint,value, txOutData);
  }
}