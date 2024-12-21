package info.abelian.sdk.common;

public class CoinAddress extends Address {

  public CoinAddress(byte[] data) {
    super(data);
  }

  public CoinAddress(String hex) {
    super(hex);
  }

  @Override
  protected void initialize() {
    super.initialize();
  }

  @Override
  protected Integer[] getExpectedLength() {
    return new Integer[]{9504, 9633, 193};
  }

  @Override
  public int getChainID() {
    return -1;
  }

  @Override
  public Fingerprint getFingerprint() {
    return null;
  }
}
