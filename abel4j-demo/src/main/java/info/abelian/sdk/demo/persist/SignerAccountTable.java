package info.abelian.sdk.demo.persist;

import java.sql.SQLException;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.DatabaseTable;
import com.j256.ormlite.table.TableUtils;

import info.abelian.sdk.common.Bytes;
import info.abelian.sdk.wallet.Account;
import info.abelian.sdk.wallet.PrivacyLevel;
import info.abelian.sdk.wallet.CryptoSeed.SpendKeyRootSeed;
import info.abelian.sdk.wallet.CryptoSeed.SerialNoKeyRootSeed;
import info.abelian.sdk.wallet.CryptoSeed.ViewKeyRootSeed;
import info.abelian.sdk.wallet.CryptoSeed.DetectorRootKey;
import info.abelian.sdk.wallet.ViewAccount;

public class SignerAccountTable {
  
  @DatabaseTable(tableName = "signer_account")
  public static class SignerAccountRow {
    @DatabaseField(generatedId = true)
    public int id;

    @DatabaseField
    public int chainID;

    @DatabaseField
    public int privacyLevel;

    @DatabaseField
    public String spendKeyRootSeed;
    @DatabaseField

    public String serialNoKeyRootSeed;
    @DatabaseField
    public String viewKeyRootSeed;
    @DatabaseField
    public String detectorRootKey;

    public SignerAccountRow() {
    }

    public SignerAccountRow(int id,int chainID,PrivacyLevel privacyLevel,
                            String spendKeyRootSeed,
                            String serialNoKeyRootSeed,
                            String viewKeyRootSeed,
                            String detectorRootKey) {
      this.id=id;
      this.chainID = chainID;
      this.privacyLevel = privacyLevel.ordinal();
      this.spendKeyRootSeed = spendKeyRootSeed;
      this.serialNoKeyRootSeed = serialNoKeyRootSeed;
      this.viewKeyRootSeed = viewKeyRootSeed;
      this.detectorRootKey = detectorRootKey;
    }

    public SignerAccountRow(Account account) {
      this(account.getId(),account.getChainID(),
              account.getPrivacyLevel(),
              account.getSpendKeyRootSeed().toHex(),
              account.getSerialNoKeyRootSeed().toHex(),
              account.getViewKeyRootSeed().toHex(),
              account.getDetectorRootKey().toHex()
              );
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

  public void addAccountIfNotExists(Account account) throws SQLException {
    if (account.getPrivacyLevel() == PrivacyLevel.FULLY_PRIVATE) {
      dao.createIfNotExists(new SignerAccountRow(account));
    }else{
      ViewAccount viewAccount =  account.getViewAccount();
      dao.createIfNotExists(new SignerAccountRow(viewAccount.getId(),
              viewAccount.getChainID(),viewAccount.getPrivacyLevel(),
              account.getSpendKeyRootSeed().toHex(),
              "",
              "",
              viewAccount.getDetectorRootKey().toHex()));
    }
  }

  public Account[] getAllSignerAccounts() throws SQLException {
    SignerAccountTable.SignerAccountRow[] rows = dao.queryForAll().toArray(new SignerAccountTable.SignerAccountRow[0]);
    Account[] viewAccounts = new Account[rows.length];
    for (int i = 0; i < rows.length; i++) {
      SignerAccountTable.SignerAccountRow row = rows[i];
      viewAccounts[i] = new Account(row.id,row.chainID,PrivacyLevel.values()[row.privacyLevel],
              new SpendKeyRootSeed(Bytes.fromHex(row.spendKeyRootSeed)),
              new SerialNoKeyRootSeed(Bytes.fromHex(row.serialNoKeyRootSeed)),
              new ViewKeyRootSeed(Bytes.fromHex(row.viewKeyRootSeed)),
              new DetectorRootKey(Bytes.fromHex(row.detectorRootKey)));
    }
    return viewAccounts;
  }

  public Account getAccount(int accountID) throws SQLException {
    SignerAccountRow row = dao.queryForId(accountID);
    if (row == null) {
      return null;
    }
      return new Account(row.id,row.chainID,PrivacyLevel.values()[row.privacyLevel],
              new SpendKeyRootSeed(Bytes.fromHex(row.spendKeyRootSeed)),
              new SerialNoKeyRootSeed(Bytes.fromHex(row.serialNoKeyRootSeed)),
              new ViewKeyRootSeed(Bytes.fromHex(row.viewKeyRootSeed)),
              new DetectorRootKey(Bytes.fromHex(row.detectorRootKey))
      );
  }
}
