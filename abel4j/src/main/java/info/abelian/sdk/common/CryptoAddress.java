package info.abelian.sdk.common;

public class CryptoAddress extends Address {

    private CoinAddress coinAddress;

    public CryptoAddress(byte[] data) {
        super(data);
    }

    public CryptoAddress(String hex) {
        super(hex);
    }

    @Override
    protected void initialize() {
        super.initialize();
        try {
            coinAddress = new CoinAddress(
                    Crypto.getCoinAddressFromCryptoAddress(new Bytes(getData()))
                            .getData()
            );
        }catch (Exception e) {
            throw new IllegalArgumentException("Invalid Crypto address.", e);
        }
    }

    @Override
    protected Integer[] getExpectedLength() {
        return new Integer[]{10696, 10826, 198, 1386};
    }

    @Override
    public int getChainID() {
        return -1;
    }

    @Override
    public Fingerprint getFingerprint() {
        try{
            return coinAddress.getFingerprint();
        }catch (Exception e){
            return null;
        }
    }
}
