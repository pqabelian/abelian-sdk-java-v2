package info.abelian.sdk.wallet;

import java.util.ArrayList;
import java.util.AbstractMap;

import com.google.protobuf.ByteString;

import info.abelian.sdk.common.AbelException;
import info.abelian.sdk.common.Bytes;
import info.abelian.sdk.proto.Core.BlockDescMessage;
import info.abelian.sdk.rpc.AbecRPCClient;
import info.abelian.sdk.rpc.BlockInfo;
import info.abelian.sdk.rpc.ChainInfo;
import info.abelian.sdk.rpc.TxInfo;
import info.abelian.sdk.rpc.TxVout;

public class ChainViewer extends Wallet {

    private static final int RING_SIZE = 3;

    private long cachedLatestHeight = -1;

    private long lastUpdateTimestamp = 0;

    private int requiredConfirmations = 1;

    public ChainViewer(AbecRPCClient client) {
        super(client);
    }

    public int getRequiredConfirmations() {
        return requiredConfirmations;
    }

    public void setRequiredConfirmations(int requiredConfirmations) {
        this.requiredConfirmations = requiredConfirmations;
    }

    public void updateLatestHeight() {
        LOG.debug("Updating latest height.");
        ChainInfo chainInfo = client.getChainInfo();
        if (chainInfo == null) {
            LOG.debug("Failed to update chain height.");
            return;
        }
        if (chainInfo.height < cachedLatestHeight) {
            LOG.warn("Updated chain height is lower than cached height.");
        }
        cachedLatestHeight = chainInfo.height;
        lastUpdateTimestamp = System.currentTimeMillis() / 1000;
    }

    public long getLatestHeight() {
        long currentTimestamp = System.currentTimeMillis() / 1000;
        if (cachedLatestHeight < 0 || currentTimestamp - lastUpdateTimestamp > 10) {
            updateLatestHeight();
        } else {
            LOG.debug("Using cached latest height: {}.", cachedLatestHeight);
        }
        return cachedLatestHeight;
    }

    public long getLatestSafeHeight() {
        long latestConfirmedHeight = getLatestHeight() - requiredConfirmations;
        return latestConfirmedHeight - (latestConfirmedHeight + 1) % RING_SIZE;
    }

    public BlockInfo getSafeBlockInfo(long height) {
        if (height > getLatestSafeHeight()) {
            updateLatestHeight();
        }

        if (height > cachedLatestHeight) {
            LOG.warn("Block height ({}) is beyond the latest height ({}).", height, cachedLatestHeight);
            return null;
        } else if (height > getLatestSafeHeight()) {
            LOG.warn("Block height ({}) is not safe yet.", height);
            return null;
        } else {
            return client.getBlockInfo(height);
        }
    }

    public AbstractMap.SimpleEntry<Coin, AbstractMap.SimpleEntry<String, ViewAccount>>[]
    getCoins(Bytes txid) throws AbelException {
        return createCoins(txid, -1);
    }

    public AbstractMap.SimpleEntry<Coin, AbstractMap.SimpleEntry<String, ViewAccount>>
    getCoin(CoinID coinID) throws AbelException {
        AbstractMap.SimpleEntry<Coin, AbstractMap.SimpleEntry<String, ViewAccount>>[]
                coins = createCoins(coinID.txid, coinID.index);
        if (coins.length == 0) {
            LOG.error("Failed to find coin for coin id: {}.", coinID);
            return null;
        } else if (coins.length == 1) {
            return coins[0];
        } else {
            LOG.error("Found more than one coin for coin id: {}.", coinID);
            return null;
        }
    }

    private AbstractMap.SimpleEntry<Coin, AbstractMap.SimpleEntry<String, ViewAccount>>[]
    createCoins(Bytes txid, int voutIndex) throws AbelException {
        TxInfo txInfo = getAbecRPCClient().getTxInfo(txid);
        if (txInfo == null) {
            LOG.error("Failed to get tx info for txid: {}.", txid.toHex());
            return null;
        }

        BlockInfo blockInfo = getAbecRPCClient().getBlockInfo(txInfo.blockid);
        if (blockInfo == null) {
            LOG.error("Failed to get block info for block hash: {}.", txInfo.blockid.toHex());
            return null;
        }

        ArrayList<AbstractMap.SimpleEntry<Coin, AbstractMap.SimpleEntry<String, ViewAccount>>> entries = new ArrayList<>();
        for (int i = 0; i < txInfo.vouts.length; i++) {
            if (voutIndex < 0 || voutIndex == i) {
                AbstractMap.SimpleEntry<Coin, AbstractMap.SimpleEntry<String, ViewAccount>> entry =
                        createCoin(txInfo.blockid, blockInfo.height, txInfo.version, txid, i, txInfo.vouts[i]);
                if (entry != null) {
                    entries.add(entry);
                }
            }
        }

        return entries.toArray(new AbstractMap.SimpleEntry[0]);
    }

    private AbstractMap.SimpleEntry<Coin, AbstractMap.SimpleEntry<String, ViewAccount>>
    createCoin(Bytes blockid, long blockHeight, int txVersion, Bytes txid, int voutIndex, TxVout vout) throws AbelException {
        AbstractMap.SimpleEntry<Coin, AbstractMap.SimpleEntry<String, ViewAccount>> coinAndOwnedViewAccount;
        try {
            coinAndOwnedViewAccount = coinReceive(blockid, blockHeight, txVersion, txid, voutIndex, vout.script);
            if (coinAndOwnedViewAccount == null || coinAndOwnedViewAccount.getKey() == null) {
                return null;
            }
        } catch (AbelException e) {
            LOG.error("Failed to decode coin value: {}.", e.getMessage());
            return null;
        }

        Coin coin = coinAndOwnedViewAccount.getKey();
        // Generate coin serial number.
        long[] ringBlockHeights = getRingBlockHeights(coin.blockHeight);
        ArrayList<BlockDescMessage> blockDescMessages = new ArrayList<>();
        for (long ringBlockHeight : ringBlockHeights) {
            Bytes blockBinData = getAbecRPCClient().getBlockBytes(ringBlockHeight);
            if (blockBinData == null) {
                LOG.error("Failed to get ring block data at height: {}.", ringBlockHeight);
                return null;
            }
            BlockDescMessage.Builder blockDescMessageBuilder = BlockDescMessage.newBuilder();
            blockDescMessageBuilder.setBinData(ByteString.copyFrom(blockBinData.getData()));
            blockDescMessageBuilder.setHeight(ringBlockHeight);
            blockDescMessages.add(blockDescMessageBuilder.build());
        }

        AbstractMap.SimpleEntry<String, ViewAccount> ownedViewAccount = coinAndOwnedViewAccount.getValue();
        Bytes serialNumber = null;
        try {
            BlockDescMessage[] arr = new BlockDescMessage[blockDescMessages.size()];
            serialNumber = ownedViewAccount.getValue().genCoinSerialNumber(coin.id, blockDescMessages.toArray(arr));
        } catch (AbelException e) {
            LOG.error("Failed to calculate coin serial number: {}.", e.getMessage());
            return null;
        }
        coin.serialNumber = serialNumber;

        return coinAndOwnedViewAccount;
    }
}