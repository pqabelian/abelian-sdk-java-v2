package info.abelian.sdk.wallet;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import com.google.protobuf.ByteString;

import info.abelian.sdk.common.Bytes;
import info.abelian.sdk.common.AbelBase;
import info.abelian.sdk.common.AbelException;
import info.abelian.sdk.common.AbelAddress;
import info.abelian.sdk.go.GoProxy;

import info.abelian.sdk.proto.Core.GenerateSafeCryptoSeedArgs;
import info.abelian.sdk.proto.Core.GenerateSafeCryptoSeedResult;
import info.abelian.sdk.proto.Core.GenerateCryptoKeysAndAddressByRootSeedsArgs;
import info.abelian.sdk.proto.Core.GetAbelAddressFromCryptoAddressArgs;
import info.abelian.sdk.proto.Core.BlockDescMessage;

import info.abelian.sdk.wallet.CryptoSeed.SerialNoKeyRootSeed;
import info.abelian.sdk.wallet.CryptoSeed.SpendKeyRootSeed;
import info.abelian.sdk.wallet.CryptoSeed.ViewKeyRootSeed;
import info.abelian.sdk.wallet.CryptoSeed.DetectorRootKey;


public class Account extends AbelBase {
    private static final Map<String, Map<String, Account>> allChainsBuiltinAccounts = new HashMap<>();

    public static Map<String, Account> getBuiltinAccounts(String chainName) {
        if (!allChainsBuiltinAccounts.containsKey(chainName)) {
            loadBuiltinAccounts(chainName);
        }
        return allChainsBuiltinAccounts.get(chainName);
    }

    private static void loadBuiltinAccounts(String chainName) {
        Map<String, Account> accounts = new HashMap<>();
        Properties accountConf = getConf(String.format("%s.account.", chainName));
        for (String key : accountConf.stringPropertyNames()) {
            if (key.endsWith(".rootSeeds")) {
                String accountName = key.substring(0, key.length() - ".rootSeeds".length());
                Bytes bytes = new Bytes(Bytes.fromHex(accountConf.getProperty(key)));
                Account account = new Account(bytes);
                account.viewAccount.id=Integer.parseInt(accountName, 10);
                accounts.put(accountName, account);
            }
        }
        allChainsBuiltinAccounts.put(chainName, accounts);
    }

    protected ViewAccount viewAccount;

    private SpendKeyRootSeed spendKeyRootSeed;

    public Account(int id,int chainID, PrivacyLevel privacyLevel,
                   SpendKeyRootSeed spendKeyRootSeed,
                   SerialNoKeyRootSeed serialNoKeyRootSeed,
                   ViewKeyRootSeed viewKeyRootSeed,
                   DetectorRootKey detectorRootKey) {
        this.viewAccount = new ViewAccount(id,chainID, privacyLevel, serialNoKeyRootSeed, viewKeyRootSeed, detectorRootKey);
        this.spendKeyRootSeed = spendKeyRootSeed;
    }

    protected Account(ViewAccount viewAccount, SpendKeyRootSeed spendKeyRootSeed) {
        this.viewAccount = new ViewAccount(viewAccount.id,viewAccount.chainID, viewAccount.privacyLevel,
                viewAccount.serialNoKeyRootSeed,
                viewAccount.viewKeyRootSeed,
                viewAccount.detectorRootKey);
        this.spendKeyRootSeed = spendKeyRootSeed;
    }

    protected Account(Bytes bytes) {
        if (bytes == null) {
            return;
        }
        if (bytes.getLength() != 2 + CryptoSeed.LENGTH * 2
                && bytes.getLength() != 2 + CryptoSeed.LENGTH * 4) {
            return;
        }
        int chainID = bytes.getByte(0);
        PrivacyLevel privacyLevel = PrivacyLevel.values()[bytes.getByte(1)];
        int offset = 2;

        this.spendKeyRootSeed = new SpendKeyRootSeed(bytes.getSubBytes(offset, CryptoSeed.LENGTH));
        offset += CryptoSeed.LENGTH;


        SerialNoKeyRootSeed serialNoKeyRootSeed = null;
        ViewKeyRootSeed viewKeyRootSeed = null;
        if (bytes.getLength() == 2 + CryptoSeed.LENGTH * 4) {
            serialNoKeyRootSeed = new SerialNoKeyRootSeed(bytes.getSubBytes(offset, CryptoSeed.LENGTH));
            offset += CryptoSeed.LENGTH;
            viewKeyRootSeed = new ViewKeyRootSeed(bytes.getSubBytes(offset, CryptoSeed.LENGTH));
            ;
            offset += CryptoSeed.LENGTH;
        }
        DetectorRootKey detectorRootKey = new DetectorRootKey(bytes.getSubBytes(offset, CryptoSeed.LENGTH));
        offset += CryptoSeed.LENGTH;

        this.viewAccount = new ViewAccount(-1,chainID, privacyLevel,
                serialNoKeyRootSeed,
                viewKeyRootSeed,
                detectorRootKey);
    }

    public ViewAccount getViewAccount() {
        return viewAccount;
    }

    public SpendKeyRootSeed getSpendKeyRootSeed() {
        return spendKeyRootSeed;
    }

    public int getId() {
        return viewAccount.id;
    }
    public int getChainID() {
        return viewAccount.chainID;
    }

    public PrivacyLevel getPrivacyLevel() {
        return viewAccount.privacyLevel;
    }

    public SerialNoKeyRootSeed getSerialNoKeyRootSeed() {
        return viewAccount.serialNoKeyRootSeed;
    }

    public ViewKeyRootSeed getViewKeyRootSeed() {
        return viewAccount.viewKeyRootSeed;
    }

    public DetectorRootKey getDetectorRootKey() {
        return viewAccount.detectorRootKey;
    }

    public Bytes toBytes() {
        if (viewAccount.privacyLevel != PrivacyLevel.FULLY_PRIVATE && viewAccount.privacyLevel != PrivacyLevel.PSEUDO_PRIVATE) {
            return null;
        }
        byte[] result = new byte[1 + 1 + CryptoSeed.LENGTH * 4];
        result[0] = (byte) viewAccount.chainID;
        result[1] = (byte) viewAccount.privacyLevel.ordinal();
        int offset = 2;
        System.arraycopy(spendKeyRootSeed.getData(), 0, result, offset, spendKeyRootSeed.getLength());
        offset += spendKeyRootSeed.getLength();
        if (viewAccount.privacyLevel == PrivacyLevel.FULLY_PRIVATE) {
            System.arraycopy(viewAccount.serialNoKeyRootSeed.getData(), 0, result, offset, viewAccount.serialNoKeyRootSeed.getLength());
            offset += viewAccount.serialNoKeyRootSeed.getLength();
            System.arraycopy(viewAccount.viewKeyRootSeed.getData(), 0, result, offset, viewAccount.viewKeyRootSeed.getLength());
            offset += viewAccount.viewKeyRootSeed.getLength();
        }
        System.arraycopy(viewAccount.detectorRootKey.getData(), 0, result, offset, viewAccount.detectorRootKey.getLength());
        offset += viewAccount.detectorRootKey.getLength();
        return new Bytes(result).getSubBytes(0, offset);
    }

    public AbelAddress generateAbelAddress() throws AbelException {
        try {
            GenerateCryptoKeysAndAddressByRootSeedsArgs args = GenerateCryptoKeysAndAddressByRootSeedsArgs.newBuilder()
                    .setPrivacyLevel(viewAccount.privacyLevel.ordinal())
                    .setSpendKeyRootSeed(ByteString.copyFrom(spendKeyRootSeed.getData()))
                    .setSerialNoKeyRootSeed(ByteString.copyFrom(viewAccount.serialNoKeyRootSeed.getData()))
                    .setViewKeyRootSeed(ByteString.copyFrom(viewAccount.viewKeyRootSeed.getData()))
                    .setDetectorRootKey(ByteString.copyFrom(viewAccount.detectorRootKey.getData()))
                    .build();
            ByteString cryptoAddress = getGoProxy().goGenerateCryptoKeysAndAddressByRootSeeds(args).getCryptoAddress();

            GetAbelAddressFromCryptoAddressArgs xargs = GetAbelAddressFromCryptoAddressArgs.newBuilder()
                    .setChainID(viewAccount.chainID)
                    .setCryptoAddress(cryptoAddress)
                    .build();
            return new AbelAddress(getGoProxy().goGetAbelAddressFromCryptoAddress(xargs).getAbelAddress().toByteArray());
        } catch (Exception e) {
            throw new AbelException(e);
        }
    }

    public Coin coinReceive(Bytes blockHash, long blockHeight, int txVersion, Bytes txid, int index, Bytes txOutData) throws AbelException {
        try {
            return viewAccount.coinReceive(blockHash, blockHeight, txVersion, txid, index, txOutData);
        } catch (Exception e) {
            throw new AbelException(e);
        }
    }

    public Bytes genCoinSerialNumber(CoinID coinID, BlockDescMessage[] blockDescs) throws AbelException {
        try {
            return viewAccount.genCoinSerialNumber(coinID, blockDescs);
        } catch (Exception e) {
            throw new AbelException(e);
        }
    }


    public static Account generateAccount(int chainID, PrivacyLevel privacyLevel) throws AbelException {
        try {
            GenerateSafeCryptoSeedResult cryptoSeed = getGoProxy().goGenerateSafeCryptoSeed(
                    GenerateSafeCryptoSeedArgs.newBuilder().
                            setPrivacyLevel(privacyLevel.ordinal())
                            .build()
            );
            SpendKeyRootSeed spendKeyRootSeed = new SpendKeyRootSeed(cryptoSeed.getSpendKeyRootSeed().toByteArray());
            SerialNoKeyRootSeed serialNoKeyRootSeed = new SerialNoKeyRootSeed(cryptoSeed.getSerialNoKeyRootSeed().toByteArray());
            ViewKeyRootSeed viewKeyRootSeed = new ViewKeyRootSeed(cryptoSeed.getViewKeyRootSeed().toByteArray());
            DetectorRootKey detectorRootKey = new DetectorRootKey(cryptoSeed.getDetectorRootKey().toByteArray());

            return new Account(-1,chainID, privacyLevel,
                    spendKeyRootSeed,
                    serialNoKeyRootSeed,
                    viewKeyRootSeed,
                    detectorRootKey);
        } catch (GoProxy.AbelGoException e) {
            throw new AbelException(e);
        }
    }
}