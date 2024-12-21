package info.abelian.sdk.wallet;

import info.abelian.sdk.common.Bytes;

public abstract class CryptoSeed extends Bytes {
    public static final int LENGTH = 64;

    public CryptoSeed(byte[] data) {
        super(data);
        if (!isValid()) {
            throw new IllegalArgumentException("Invalid crypto seed.");
        }
    }

    public CryptoSeed(Bytes key) {
        this(key.getData());
    }

    protected abstract boolean isValid();

    protected abstract boolean isSecret();

    public String toString() {
        if (isSecret()) {
            return String.format("[%d bytes|********...********]", getLength());
        }
        return super.toString();
    }

    public static class SpendKeyRootSeed extends CryptoSeed {


        public SpendKeyRootSeed(byte[] data) {
            super(data);
        }

        public SpendKeyRootSeed(Bytes key) {
            super(key);
        }

        @Override
        protected boolean isValid() {
            return getLength() == LENGTH;
        }

        @Override
        protected boolean isSecret() {
            return true;
        }
    }

    public static class SerialNoKeyRootSeed extends CryptoSeed {


        public SerialNoKeyRootSeed(byte[] data) {
            super(data);
        }

        public SerialNoKeyRootSeed(Bytes key) {
            super(key);
        }

        @Override
        protected boolean isValid() {
            return getLength() ==0 || getLength() == LENGTH;
        }

        @Override
        protected boolean isSecret() {
            return false;
        }
    }

    public static class ViewKeyRootSeed extends CryptoSeed {


        public ViewKeyRootSeed(byte[] data) {
            super(data);
        }

        public ViewKeyRootSeed(Bytes key) {
            super(key);
        }

        @Override
        protected boolean isValid() {
            return getLength() ==0 ||  getLength() == LENGTH;
        }

        @Override
        protected boolean isSecret() {
            return false;
        }
    }

    public static class DetectorRootKey extends CryptoSeed {
        public DetectorRootKey(byte[] data) {
            super(data);
        }

        public DetectorRootKey(Bytes key) {
            super(key);
        }

        @Override
        protected boolean isValid() {
            return getLength() == LENGTH;
        }

        @Override
        protected boolean isSecret() {
            return false;
        }
    }
}
