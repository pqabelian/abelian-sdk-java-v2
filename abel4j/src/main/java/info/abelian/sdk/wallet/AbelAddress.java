package info.abelian.sdk.wallet;

import com.google.protobuf.ByteString;
import info.abelian.sdk.common.*;
import info.abelian.sdk.proto.Core;


public class AbelAddress extends Address {

    private CryptoAddress cryptoAddress;

    private ShortAbelAddress shortAddress;

    public AbelAddress(byte[] data) {
        super(data);
    }

    public AbelAddress(String hex) {
        super(hex);
    }

    @Override
    protected void initialize() {
        super.initialize();
        try {
            cryptoAddress = new CryptoAddress(
                    Crypto.getCryptoAddressFromAbelAddress(new Bytes(getData()))
                            .getData()
            );
            shortAddress = new ShortAbelAddress(
                    Crypto.getShortAbelAddressFromAbelAddress(new Bytes(getData()))
                            .getData()
            );
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid Abel address.", e);
        }
    }

    @Override
    public int getChainID() {
        return getByte(0);
    }

    @Override
    protected Integer[] getExpectedLength() {
        return new Integer[]{10729, 10859, 231};
    }

    @Override
    public Fingerprint getFingerprint() {
        return shortAddress.getFingerprint();
    }

    public Bytes getCryptoAddress() {
        return cryptoAddress;
    }

    public ShortAbelAddress getShortAddress() {
        return shortAddress;
    }
}
