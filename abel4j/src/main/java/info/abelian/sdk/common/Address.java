package info.abelian.sdk.common;

import java.util.Arrays;

public abstract class Address extends Bytes {

  public Address(byte[] data) {
    super(data);
    initialize();
  }

  public Address(String hex) {
    this(fromHex(hex));
  }

  protected void initialize() {
    Integer[] expectedLengths = getExpectedLength();
    if (expectedLengths!=null && expectedLengths.length != 0 && !Arrays.asList(expectedLengths).contains(getData().length)) {
      throw new IllegalArgumentException(this.getClass().getName() + " data must be " + Arrays.toString(expectedLengths) + " bytes long.");
    }
  }

  protected Integer[] getExpectedLength() {
    return null;
  }

  public abstract int getChainID();

  public abstract Fingerprint getFingerprint();
}