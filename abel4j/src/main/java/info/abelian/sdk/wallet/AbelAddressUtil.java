package info.abelian.sdk.wallet;

public class AbelAddressUtil {

    public static final int SHORT_ADDRESS_HEX_LENGTH_V1 = 132;
    public static final int SHORT_ADDRESS_HEX_LENGTH_V2 = 136;

    public static String getStandardShortAddress(String shortAddress) {
        if (shortAddress.startsWith("0x")) {
            shortAddress = shortAddress.substring(2);
        }

        shortAddress = shortAddress.toLowerCase();

        int len = shortAddress.length();
        if (len != SHORT_ADDRESS_HEX_LENGTH_V1 && len != SHORT_ADDRESS_HEX_LENGTH_V2) {
            throw new IllegalArgumentException("Invalid short address length: " + len);
        }

        if (!shortAddress.startsWith("abe")) {
            throw new IllegalArgumentException("Invalid short address prefix: " + shortAddress.substring(0, 4));
        }

        return shortAddress;
    }

    /* 通过 shortAddress 计算 fingerprint
     * @param shortAddress
     * @return
     */
    public static String getFingerprint(String shortAddress) {
        if (shortAddress.length() == SHORT_ADDRESS_HEX_LENGTH_V1) {
            return getStandardShortAddress(shortAddress).substring(4, 4 + 32 * 2);
        }else if (shortAddress.length() == SHORT_ADDRESS_HEX_LENGTH_V2) {
            return getStandardShortAddress(shortAddress).substring(8, 8 + 32 * 2);
        }else{
            throw new IllegalArgumentException("Invalid short address length: " + shortAddress.length());
        }
    }

    /*判断一个fingerprint是否属于一个shortAddress，（他俩是一一对应关系）
            *
    @param
    shortAddress
     *
    @param
    fingerprint
     *@return
             */

    public static boolean hasFingerprint(String shortAddress, String fingerprint) {
        return getFingerprint(shortAddress).equals(fingerprint);
    }
}
