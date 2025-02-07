package info.abelian.sdk.demo.persist;

import java.sql.SQLException;
import java.util.AbstractMap;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.DatabaseTable;
import com.j256.ormlite.table.TableUtils;

import info.abelian.sdk.common.Bytes;
import info.abelian.sdk.wallet.ViewAccount;
import info.abelian.sdk.common.PrivacyLevel;
import info.abelian.sdk.common.CryptoSeed.SerialNoSecretRootSeed;
import info.abelian.sdk.common.CryptoSeed.ViewSecretRootSeed;
import info.abelian.sdk.common.CryptoSeed.DetectorRootKey;

public class ViewerAccountTable {

    @DatabaseTable(tableName = "viewer_account")
    public static class ViewerAccountRow {
        @DatabaseField(id = true)
        public String accountID;

        @DatabaseField
        public int chainID;

        @DatabaseField
        public int privacyLevel;

        @DatabaseField
        public String serialNoKeyRootSeed;
        @DatabaseField
        public String viewKeyRootSeed;
        @DatabaseField
        public String detectorRootKey;

        public ViewerAccountRow() {
        }

        public ViewerAccountRow(String accountID, int chainID, PrivacyLevel privacyLevel,
                                String serialNoKeyRootSeed,
                                String viewKeyRootSeed,
                                String detectorRootKey) {
            this.accountID = accountID;
            this.chainID = chainID;
            this.privacyLevel = privacyLevel.getValue();
            if (privacyLevel != PrivacyLevel.PSEUDO_PRIVATE) {
                this.serialNoKeyRootSeed = serialNoKeyRootSeed;
                this.viewKeyRootSeed = viewKeyRootSeed;
            }
            this.detectorRootKey = detectorRootKey;
        }
    }

    private Dao<ViewerAccountRow, Integer> dao;

    public ViewerAccountTable(ConnectionSource connectionSource) throws SQLException {
        dao = DaoManager.createDao(connectionSource, ViewerAccountRow.class);
        TableUtils.createTableIfNotExists(connectionSource, ViewerAccountRow.class);
    }

    public long getCount() throws SQLException {
        return dao.countOf();
    }

    public void addAccountIfNotExists(String accountID, ViewAccount viewAccount) throws SQLException {
        PrivacyLevel privacyLevel = viewAccount.getPrivacyLevel();
        dao.createIfNotExists(new ViewerAccountRow(accountID,
                viewAccount.getChainID(),
                privacyLevel,
                privacyLevel == PrivacyLevel.PSEUDO_PRIVATE ? null : viewAccount.getSerialNoKeyRootSeed().toHex(),
                privacyLevel == PrivacyLevel.PSEUDO_PRIVATE ? null : viewAccount.getViewKeyRootSeed().toHex(),
                viewAccount.getDetectorRootKey().toHex()));
    }

    public AbstractMap.SimpleEntry<String,ViewAccount>[] getAllViewerAccounts() throws SQLException {
        ViewerAccountRow[] rows = dao.queryForAll().toArray(new ViewerAccountRow[0]);
        AbstractMap.SimpleEntry<String,ViewAccount>[] viewAccounts = new AbstractMap.SimpleEntry[rows.length];
        for (int i = 0; i < rows.length; i++) {
            ViewerAccountRow row = rows[i];
            PrivacyLevel privacyLevel= PrivacyLevel.fromValue(row.privacyLevel);
            ViewAccount viewAccount = new ViewAccount(row.chainID, privacyLevel,
                    privacyLevel == PrivacyLevel.PSEUDO_PRIVATE ? null : new SerialNoSecretRootSeed(Bytes.fromHex(row.serialNoKeyRootSeed)),
                    privacyLevel == PrivacyLevel.PSEUDO_PRIVATE ? null : new ViewSecretRootSeed(Bytes.fromHex(row.viewKeyRootSeed)),
                    new DetectorRootKey(Bytes.fromHex(row.detectorRootKey)));
            viewAccounts[i] = new AbstractMap.SimpleEntry<>(row.accountID, viewAccount);
        }
        return viewAccounts;
    }
}
