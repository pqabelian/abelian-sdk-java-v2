package info.abelian.sdk.demo.persist;

import java.sql.SQLException;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.DatabaseTable;
import com.j256.ormlite.table.TableUtils;

import info.abelian.sdk.common.Bytes;
import info.abelian.sdk.wallet.ViewAccount;
import info.abelian.sdk.wallet.PrivacyLevel;
import info.abelian.sdk.wallet.CryptoSeed.SerialNoKeyRootSeed;
import info.abelian.sdk.wallet.CryptoSeed.ViewKeyRootSeed;
import info.abelian.sdk.wallet.CryptoSeed.DetectorRootKey;

public class ViewerAccountTable {

    @DatabaseTable(tableName = "viewer_account")
    public static class ViewerAccountRow {
        @DatabaseField(generatedId = true)
        public int id;

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

        public ViewerAccountRow(int id,int chainID, PrivacyLevel privacyLevel,
                                String serialNoKeyRootSeed,
                                String viewKeyRootSeed,
                                String detectorRootKey) {
            this.id = id;
            this.chainID = chainID;
            this.privacyLevel = privacyLevel.ordinal();
            this.serialNoKeyRootSeed = serialNoKeyRootSeed;
            this.viewKeyRootSeed = viewKeyRootSeed;
            this.detectorRootKey = detectorRootKey;
        }

        public ViewerAccountRow(ViewAccount viewAccount) {
            this(viewAccount.getId(),viewAccount.getChainID(), viewAccount.getPrivacyLevel(),
                    viewAccount.getSerialNoKeyRootSeed().toHex(),
                    viewAccount.getViewKeyRootSeed().toHex(),
                    viewAccount.getDetectorRootKey().toHex());
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

    public void addAccountIfNotExists(ViewAccount viewAccount) throws SQLException {
        if(viewAccount.getPrivacyLevel() == PrivacyLevel.FULLY_PRIVATE){
            dao.createIfNotExists(new ViewerAccountRow(viewAccount));
        }else{
            dao.createIfNotExists(new ViewerAccountRow(viewAccount.getId(),viewAccount.getChainID(),viewAccount.getPrivacyLevel(),
                    "",
                    "",
                    viewAccount.getDetectorRootKey().toHex()));
        }
    }

    public ViewAccount[] getAllViewerAccounts() throws SQLException {
        ViewerAccountRow[] rows = dao.queryForAll().toArray(new ViewerAccountRow[0]);
        ViewAccount[] viewAccounts = new ViewAccount[rows.length];
        for (int i = 0; i < rows.length; i++) {
            ViewerAccountRow row = rows[i];
            viewAccounts[i] = new ViewAccount(row.id,row.chainID,PrivacyLevel.values()[row.privacyLevel],
                    new SerialNoKeyRootSeed(Bytes.fromHex(row.serialNoKeyRootSeed)),
                    new ViewKeyRootSeed(Bytes.fromHex(row.viewKeyRootSeed)),
                    new DetectorRootKey(Bytes.fromHex(row.detectorRootKey)));
        }
        return viewAccounts;
    }
}
