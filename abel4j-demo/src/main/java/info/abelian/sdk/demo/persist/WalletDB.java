package info.abelian.sdk.demo.persist;

import java.sql.SQLException;
import java.util.AbstractMap;
import java.util.Map;

import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;

import info.abelian.sdk.common.AbelBase;
import info.abelian.sdk.common.AbelException;
import info.abelian.sdk.common.Bytes;
import info.abelian.sdk.demo.Demo;
import info.abelian.sdk.wallet.Account;
import info.abelian.sdk.wallet.ViewAccount;
import info.abelian.sdk.wallet.CoinID;
import info.abelian.sdk.wallet.Coin;
import info.abelian.sdk.wallet.UnsignedRawTx;
import info.abelian.sdk.wallet.SignedRawTx;

public abstract class WalletDB {

  public static HotWalletDB openHotWalletDB() {
    String dbName = String.format("demo-%s-hot-wallet.db", Demo.getDefaultChainName());
    String dbUrl = "jdbc:sqlite:" + AbelBase.getEnvPath(dbName);
    HotWalletDB db = new HotWalletDB(dbUrl);
    return db;
  }

  public static ColdWalletDB openColdWalletDB() {
    String dbName = String.format("demo-%s-cold-wallet.db", Demo.getDefaultChainName());
    String dbUrl = "jdbc:sqlite:" + AbelBase.getEnvPath(dbName);
    ColdWalletDB db = new ColdWalletDB(dbUrl);
    return db;
  }

  protected ConnectionSource connectionSource;

  protected WalletDB(String jdbcUrl) {
    try {
      connectionSource = new JdbcConnectionSource(jdbcUrl);
      initTables();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public void close() {
    try {
      connectionSource.close();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  protected abstract void initTables() throws SQLException, AbelException;

  public static class ColdWalletDB extends WalletDB {

    protected ColdWalletDB(String jdbcUrl) {
      super(jdbcUrl);
    }

    SignerAccountTable signerAccountTable;

    @Override
    protected void initTables() throws SQLException, AbelException {
      initSignerAccountTable();
    }
    
    private void initSignerAccountTable() throws SQLException, AbelException {
      signerAccountTable = new SignerAccountTable(connectionSource);
      // Save all builtin accounts as signer accounts to the table.
      for (Map.Entry<String,Account> entry: Demo.getBuiltinAccounts().entrySet()) {
        signerAccountTable.addAccountIfNotExists(entry.getKey(),entry.getValue());
      }
    }

    public long getCountOfSignerAccounts() throws SQLException {
      return signerAccountTable.getCount();
    }
    public Account[] getAllSignerAccounts() throws SQLException {
      return signerAccountTable.getAllSignerAccounts();
    }

    public AbstractMap.SimpleEntry<String,Account> getSignerAccount(String accountID) throws SQLException {
      Account account=  signerAccountTable.getAccount(accountID);
      return new AbstractMap.SimpleEntry<String,Account>(accountID,account);
    }
  }

  public static class HotWalletDB extends WalletDB {

    ViewerAccountTable viewerAccountTable;

    CoinTable coinTable;

    TxTable txTable;

    protected HotWalletDB(String jdbcUrl) {
      super(jdbcUrl);
    }

    @Override
    protected void initTables() throws SQLException, AbelException {
      initViewerAccountTable();
      initCoinTable();
      initTxTable();
    }

    private void initViewerAccountTable() throws SQLException, AbelException {
      viewerAccountTable = new ViewerAccountTable(connectionSource);
      // Save all builtin accounts as viewer accounts to the table.
      for (Map.Entry<String,Account> entry : Demo.getBuiltinAccounts().entrySet()) {
        viewerAccountTable.addAccountIfNotExists(entry.getKey(),entry.getValue().getViewAccount());
      }
    }

    private void initCoinTable() throws SQLException {
      coinTable = new CoinTable(connectionSource);
    }

    private void initTxTable() throws SQLException {
      txTable = new TxTable(connectionSource);
    }

    public long getCountOfViewerAccounts() throws SQLException {
      return viewerAccountTable.getCount();
    }

    public AbstractMap.SimpleEntry<String,ViewAccount>[] getAllViewerAccounts() throws SQLException {
      return viewerAccountTable.getAllViewerAccounts();
    }

    public long getCountOfCoins() throws SQLException {
      return coinTable.getCount();
    }

    public void addCoinIfNotExists(Coin coin,String ownerAccountID) throws SQLException {
      coinTable.addCoinIfNotExists(coin,ownerAccountID);
    }

    public Coin[] getAllUnspentCoins() throws SQLException {
      return coinTable.getAllUnspentCoins();
    }

    public long getCountOfTxs() throws SQLException {
      return txTable.getCount();
    }

    public String addUnsignedRawTxIfNotExists(UnsignedRawTx unsignedRawTx) throws SQLException {
      return txTable.addUnsignedRawTxIfNotExists(unsignedRawTx);
    }

    public Map<String, UnsignedRawTx> getUnsignedRawTxs(int limit) throws SQLException {
      return txTable.getUnsignedRawTxs(limit);
    }

    public UnsignedRawTx getUnsignedRawTx(String txMd5) throws SQLException {
      return txTable.getUnsignedRawTx(txMd5);
    }

    public void updateSignedRawTx(String txMd5, SignedRawTx signedRawTx) throws SQLException {
      txTable.updateSignedRawTx(txMd5, signedRawTx);
    }

    public SignedRawTx getSignedRawTx(String txMd5) throws SQLException {
      return txTable.getSignedRawTx(txMd5);
    }

    public void setSubmitted(String txMd5) throws SQLException {
      txTable.setSubmitted(txMd5);
    }

    public Coin getCoinBySerialNumber(Bytes serialNumber) throws SQLException {
      return coinTable.getCoinBySnHex(serialNumber.toHex());
    }

    public void markSpentCoin(CoinID coinID) throws SQLException {
      coinTable.setCoinSpentStatus(coinID.toString(), true);
    }
  }
}
