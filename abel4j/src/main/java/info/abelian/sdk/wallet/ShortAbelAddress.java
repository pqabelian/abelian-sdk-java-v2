package info.abelian.sdk.wallet;

import info.abelian.sdk.common.Address;
import info.abelian.sdk.common.Bytes;
import info.abelian.sdk.common.Fingerprint;

public class ShortAbelAddress extends Address {
    public ShortAbelAddress(byte[] data) {
        super(data);
    }

    public ShortAbelAddress(String hex) {
        super(hex);
    }

    @Override
    protected void initialize() {
        super.initialize();
        String prefix = getSubBytes(0, 2).toHex().toLowerCase();
        if (!prefix.startsWith("abe")) {
            System.out.printf(toHex());
            throw new IllegalArgumentException("Short Abel address must start with 0xabe*.");
        }
    }

    @Override
    protected Integer[] getExpectedLength() {
        return new Integer[]{66, 68};
    }

    @Override
    public int getChainID() {
        return getByte(1) - 0xe1;
    }


    @Override
    public Fingerprint getFingerprint() {
        return new Fingerprint(getSubData(this.getData().length == 66 ? 2 : 4, 32));
    }

    public Bytes getAbelAddressChecksum() {
        return getSubBytes(34, 32);
    }
}
