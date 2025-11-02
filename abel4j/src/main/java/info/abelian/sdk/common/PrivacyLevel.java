package info.abelian.sdk.common;

public enum PrivacyLevel {
    FULLY_PRIVATE_PRE(0),
    FULLY_PRIVATE(1),
    PSEUDO_PRIVATE(2),
    PSEUDO_CT_PRIVATE(3);

    private final int value;

    PrivacyLevel(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static PrivacyLevel fromValue(int value) {
        for (PrivacyLevel entry : PrivacyLevel.values()) {
            if (entry.getValue() == value) {
                return entry;
            }
        }
        throw new IllegalArgumentException("No PrivacyLevel for value: " + value);
    }
}
