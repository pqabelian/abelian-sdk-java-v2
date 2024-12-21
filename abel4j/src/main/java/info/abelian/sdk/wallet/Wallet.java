package info.abelian.sdk.wallet;

import java.util.HashMap;
import java.util.Map;

import info.abelian.sdk.common.AbelBase;
import info.abelian.sdk.common.AbelException;
import info.abelian.sdk.common.Bytes;
import info.abelian.sdk.rpc.AbecRPCClient;

public abstract class Wallet extends AbelBase {

    protected AbecRPCClient client;

    protected HashMap<String, ViewAccount> viewAccounts = new HashMap<>();
    protected HashMap<String, Account> accounts = new HashMap<>();

    public Wallet(AbecRPCClient client) {
        this.client = client;
    }

    public AbecRPCClient getAbecRPCClient() {
        return client;
    }

    public void addViewAccount(String key, ViewAccount viewAccount) {
        viewAccounts.put(key, viewAccount);
    }


    public void addAccount(String key, Account account) {
        accounts.put(key, account);
        viewAccounts.put(key, account.getViewAccount());
    }

    public ViewAccount getOwnedViewAccount(Coin coin) throws AbelException {
        try {
            for (Map.Entry<String, ViewAccount> entry : viewAccounts.entrySet()) {
                ViewAccount viewAccount = entry.getValue();
                Coin _coin = viewAccount.coinReceive(coin.blockid, coin.blockHeight, coin.txVersion, coin.id.txid, coin.id.index, coin.txOutData);
                if (_coin != null) {
                    return viewAccount;
                }
            }
            return null;
        } catch (Exception e) {
            throw new AbelException(e);
        }
    }

    public Account getSignerAccount(int accountID) {
        return accounts.get(Integer.toString(accountID));
    }

    public Coin coinReceive(Bytes blockid, long blockHeight, int txVersion, Bytes txid, int index, Bytes txOutData) throws AbelException {
        try {
            for (Map.Entry<String, ViewAccount> entry : viewAccounts.entrySet()) {
                ViewAccount viewAccount = entry.getValue();
                Coin coin = viewAccount.coinReceive(blockid, blockHeight, txVersion, txid, index, txOutData);
                if (coin != null) {
                    return coin;
                }
            }
            return null;
        } catch (Exception e) {
            throw new AbelException(e);
        }
    }
}
