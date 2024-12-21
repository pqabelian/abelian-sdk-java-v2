package info.abelian.sdk.common;

import com.google.protobuf.ByteString;
import info.abelian.sdk.go.GoProxy;
import info.abelian.sdk.proto.Core.GetShortAbelAddressFromAbelAddressArgs;
import info.abelian.sdk.proto.Core.GetShortAbelAddressFromAbelAddressResult;

public class CryptoAddress extends Address {

    public CryptoAddress(byte[] data) {
        super(data);
    }

    public CryptoAddress(String hex) {
        super(hex);
    }

    @Override
    protected void initialize() {
        super.initialize();
    }

    @Override
    protected Integer[] getExpectedLength() {
        return new Integer[]{10696, 10826, 198};
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
