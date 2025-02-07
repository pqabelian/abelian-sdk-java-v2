package info.abelian.sdk.common;

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
    public static class EntropySeed extends CryptoSeed {

        public static final int LENGTH = 32;

        public EntropySeed(byte[] data) {
            super(data);
        }

        public EntropySeed(Bytes key) {
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

    public static class SpendSecretRootSeed extends CryptoSeed {


        public SpendSecretRootSeed(byte[] data) {
            super(data);
        }

        public SpendSecretRootSeed(Bytes key) {
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

    public static class SerialNoSecretRootSeed extends CryptoSeed {


        public SerialNoSecretRootSeed(byte[] data) {
            super(data);
        }

        public SerialNoSecretRootSeed(Bytes key) {
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

    public static class ViewSecretRootSeed extends CryptoSeed {


        public ViewSecretRootSeed(byte[] data) {
            super(data);
        }

        public ViewSecretRootSeed(Bytes key) {
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
    public static class PublicRandRootSeed extends CryptoSeed {
        public PublicRandRootSeed(byte[] data) {
            super(data);
        }

        public PublicRandRootSeed(Bytes key) {
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
