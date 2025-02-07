package info.abelian.sdk.demo.persist;

import java.sql.SQLException;
import java.util.List;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.DatabaseTable;
import com.j256.ormlite.table.TableUtils;

import info.abelian.sdk.common.Bytes;

import info.abelian.sdk.common.CryptoSeed.SpendSecretRootSeed;
import info.abelian.sdk.common.CryptoSeed.SerialNoSecretRootSeed;
import info.abelian.sdk.common.CryptoSeed.ViewSecretRootSeed;
import info.abelian.sdk.common.CryptoSeed.DetectorRootKey;
import info.abelian.sdk.common.CryptoSeed.PublicRandRootSeed;

import info.abelian.sdk.wallet.Account;
import info.abelian.sdk.wallet.SeqAccount;
import info.abelian.sdk.common.PrivacyLevel;


public class SignerAccountTable {

    @DatabaseTable(tableName = "signer_account")
    public static class SignerAccountRow {
        @DatabaseField(id = true)
        public String accountID;

        @DatabaseField
        public int chainID;

        @DatabaseField
        public int privacyLevel;

        @DatabaseField
        public String spendSecretRootSeed;
        @DatabaseField

        public String serialNoSecretRootSeed;
        @DatabaseField
        public String viewKeyRootSeed;
        @DatabaseField
        public String detectorRootKey;
        @DatabaseField
        public String publicRandRootSeed;

        public SignerAccountRow() {
        }

        public SignerAccountRow(String accountID, int chainID, PrivacyLevel privacyLevel,
                                String spendSecretRootSeed,
                                String serialNoSecretRootSeed,
                                String viewKeyRootSeed,
                                String detectorRootKey) {
            this.accountID = accountID;
            this.chainID = chainID;
            this.privacyLevel = privacyLevel.getValue();
            this.spendSecretRootSeed = spendSecretRootSeed;
            if (privacyLevel != PrivacyLevel.PSEUDO_PRIVATE) {
                this.serialNoSecretRootSeed = serialNoSecretRootSeed;
                this.viewKeyRootSeed = viewKeyRootSeed;
            }
            this.detectorRootKey = detectorRootKey;
        }

        public SignerAccountRow(String accountID, int chainID, PrivacyLevel privacyLevel,
                                String spendSecretRootSeed,
                                String serialNoSecretRootSeed,
                                String viewKeyRootSeed,
                                String detectorRootKey,
                                String publicRandRootSeed) {
            this.accountID = accountID;
            this.chainID = chainID;
            this.privacyLevel = privacyLevel.getValue();
            this.spendSecretRootSeed = spendSecretRootSeed;
            if (privacyLevel != PrivacyLevel.PSEUDO_PRIVATE) {
                this.serialNoSecretRootSeed = serialNoSecretRootSeed;
                this.viewKeyRootSeed = viewKeyRootSeed;
            }
            this.detectorRootKey = detectorRootKey;
            this.publicRandRootSeed = publicRandRootSeed;
        }
    }

    private Dao<SignerAccountRow, Integer> dao;

    public SignerAccountTable(ConnectionSource connectionSource) throws SQLException {
        dao = DaoManager.createDao(connectionSource, SignerAccountRow.class);
        TableUtils.createTableIfNotExists(connectionSource, SignerAccountRow.class);
    }

    public long getCount() throws SQLException {
        return dao.countOf();
    }

    public void addAccountIfNotExists(String accountID, Account account) throws SQLException {
        PrivacyLevel privacyLevel = account.getPrivacyLevel();

        if (account instanceof SeqAccount) {
            dao.createIfNotExists(new SignerAccountRow(accountID,
                    account.getChainID(),
                    privacyLevel,
                    account.getSpendSecretRootSeed().toHex(),
                    privacyLevel == PrivacyLevel.PSEUDO_PRIVATE ? null : account.getSerialNoSecretRootSeed().toHex(),
                    privacyLevel == PrivacyLevel.PSEUDO_PRIVATE ? null : account.getViewKeyRootSeed().toHex(),
                    account.getDetectorRootKey().toHex(),
                    ((SeqAccount) account).getPublicRandRootSeed().toHex()));
        } else {
            dao.createIfNotExists(new SignerAccountRow(accountID,
                    account.getChainID(),
                    privacyLevel,
                    account.getSpendSecretRootSeed().toHex(),
                    privacyLevel == PrivacyLevel.PSEUDO_PRIVATE ? null : account.getSerialNoSecretRootSeed().toHex(),
                    privacyLevel == PrivacyLevel.PSEUDO_PRIVATE ? null : account.getViewKeyRootSeed().toHex(),
                    account.getDetectorRootKey().toHex()));
        }
    }

    public Account[] getAllSignerAccounts() throws SQLException {
        SignerAccountTable.SignerAccountRow[] rows = dao.queryForAll().toArray(new SignerAccountTable.SignerAccountRow[0]);
        Account[] accounts = new Account[rows.length];
        for (int i = 0; i < rows.length; i++) {
            SignerAccountTable.SignerAccountRow row = rows[i];
            PrivacyLevel privacyLevel = PrivacyLevel.fromValue(row.privacyLevel);

            if (row.publicRandRootSeed == null) {
                accounts[i] = new Account(row.chainID, privacyLevel,
                        new SpendSecretRootSeed(Bytes.fromHex(row.spendSecretRootSeed)),
                        privacyLevel == PrivacyLevel.PSEUDO_PRIVATE ? null : new SerialNoSecretRootSeed(Bytes.fromHex(row.serialNoSecretRootSeed)),
                        privacyLevel == PrivacyLevel.PSEUDO_PRIVATE ? null : new ViewSecretRootSeed(Bytes.fromHex(row.viewKeyRootSeed)),
                        new DetectorRootKey(Bytes.fromHex(row.detectorRootKey)));
            } else {
                accounts[i] = new SeqAccount(row.chainID, privacyLevel,
                        new SpendSecretRootSeed(Bytes.fromHex(row.spendSecretRootSeed)),
                        privacyLevel == PrivacyLevel.PSEUDO_PRIVATE ? null : new SerialNoSecretRootSeed(Bytes.fromHex(row.serialNoSecretRootSeed)),
                        privacyLevel == PrivacyLevel.PSEUDO_PRIVATE ? null : new ViewSecretRootSeed(Bytes.fromHex(row.viewKeyRootSeed)),
                        new DetectorRootKey(Bytes.fromHex(row.detectorRootKey)),
                        new PublicRandRootSeed(Bytes.fromHex(row.publicRandRootSeed)));
            }

        }
        return accounts;
    }

    public Account getAccount(String accountID) throws SQLException {
        List<SignerAccountRow> rows = dao.queryForEq("accountID", accountID);
        if (rows== null) {
            return null;
        }
        SignerAccountRow row = rows.get(0);
        if (row== null) {
            return null;
        }
        PrivacyLevel privacyLevel = PrivacyLevel.fromValue(row.privacyLevel);

        if (row.publicRandRootSeed == null) {
            return new Account(row.chainID, privacyLevel,
                    new SpendSecretRootSeed(Bytes.fromHex(row.spendSecretRootSeed)),
                    privacyLevel == PrivacyLevel.PSEUDO_PRIVATE ? null : new SerialNoSecretRootSeed(Bytes.fromHex(row.serialNoSecretRootSeed)),
                    privacyLevel == PrivacyLevel.PSEUDO_PRIVATE ? null : new ViewSecretRootSeed(Bytes.fromHex(row.viewKeyRootSeed)),
                    new DetectorRootKey(Bytes.fromHex(row.detectorRootKey)));
        } else {
            return new SeqAccount(row.chainID, privacyLevel,
                    new SpendSecretRootSeed(Bytes.fromHex(row.spendSecretRootSeed)),
                    privacyLevel == PrivacyLevel.PSEUDO_PRIVATE ? null : new SerialNoSecretRootSeed(Bytes.fromHex(row.serialNoSecretRootSeed)),
                    privacyLevel == PrivacyLevel.PSEUDO_PRIVATE ? null : new ViewSecretRootSeed(Bytes.fromHex(row.viewKeyRootSeed)),
                    new DetectorRootKey(Bytes.fromHex(row.detectorRootKey)),
                    new PublicRandRootSeed(Bytes.fromHex(row.publicRandRootSeed)));
        }
    }
}
