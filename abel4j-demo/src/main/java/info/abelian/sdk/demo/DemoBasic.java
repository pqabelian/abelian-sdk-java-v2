package info.abelian.sdk.demo;

import info.abelian.sdk.common.*;
import info.abelian.sdk.common.CryptoSeed.EntropySeed;
import info.abelian.sdk.common.CryptoSeed.SpendSecretRootSeed;
import info.abelian.sdk.common.CryptoSeed.SerialNoSecretRootSeed;
import info.abelian.sdk.common.CryptoSeed.ViewSecretRootSeed;
import info.abelian.sdk.common.CryptoSeed.DetectorRootKey;
import info.abelian.sdk.proto.Core.GenerateCryptoKeysAndAddressByRootSeedsResult;
import info.abelian.sdk.rpc.AbecRPCClient;
import info.abelian.sdk.rpc.AbecRPCClient.JsonResponse;
import info.abelian.sdk.rpc.BlockInfo;
import info.abelian.sdk.rpc.ChainInfo;
import info.abelian.sdk.rpc.TxInfo;
import info.abelian.sdk.rpc.TxVin;
import info.abelian.sdk.rpc.TxVout;
import info.abelian.sdk.wallet.AbelAddress;

public class DemoBasic {

  // Demo Crypto.
  public static void demoCrypto(String[] args) throws Exception {
    System.out.println("\n==> Generate entropy seed.");
    EntropySeed entropySeed = Crypto.generateEntropySeed();
    System.out.println("    EntropySeed = " + Utils.summary(entropySeed));

    System.out.println("\n==> Convert entropy seed to mnemonics.");
    String[] mnemonics = Crypto.entropySeedToMnemonics(entropySeed);
    System.out.println("    Mnemonics = " + String.join(" ", mnemonics));

    System.out.println("\n==> Derive crypto seeds from entropy seed.");
    Bytes[] cryptoSeeds = Crypto.entropySeedToCryptoSeeds(entropySeed);
    System.out.println("    SpendSecretRootSeed = " + Utils.summary(cryptoSeeds[0]));
    System.out.println("    SerialNoSecretRootSeed = " + Utils.summary(cryptoSeeds[1]));
    System.out.println("    ViewSecretRootSeed = " + Utils.summary(cryptoSeeds[2]));
    System.out.println("    DetectorRootKey = " + Utils.summary(cryptoSeeds[3]));

    System.out.println("\n    Generate a FULLY-PRIVATE crypto address from above crypto seeds:");
    GenerateCryptoKeysAndAddressByRootSeedsResult ckaa = Crypto.generateKeysAndAddress(
            PrivacyLevel.FULLY_PRIVATE,
             new SpendSecretRootSeed(cryptoSeeds[0]),
             new SerialNoSecretRootSeed(cryptoSeeds[1]),
             new ViewSecretRootSeed(cryptoSeeds[2]),
             new DetectorRootKey(cryptoSeeds[3])
    );
    System.out.println("    PrivacyLevel = " + PrivacyLevel.FULLY_PRIVATE);
    System.out.println("    CryptoAddress[0] = " + Utils.summary(ckaa.getCryptoAddress()));
    Bytes abelAddress = Crypto.getAbelAddressFromCryptoAddress(1,new Bytes(ckaa.getCryptoAddress().toByteArray()));
    System.out.println("    AbelAddress[0] = " + Utils.summary(abelAddress));
    AbelAddress abelAddressFromString = new AbelAddress(abelAddress.toHex());
    System.out.println("    AbelAddressFromString[0] = " + Utils.summary(abelAddressFromString));
    Bytes shortAddress = Crypto.getShortAbelAddressFromAbelAddress(abelAddress);
    System.out.println("    ShortAddress[0] = " + Utils.summary(shortAddress));

    System.out.println("\n    Generate a PSEUDO-PRIVATE crypto address (from the same seeds):");
    ckaa = Crypto.generateKeysAndAddress(
            PrivacyLevel.PSEUDO_PRIVATE,
            new SpendSecretRootSeed(cryptoSeeds[0]),
            new DetectorRootKey(cryptoSeeds[3])
    );
    System.out.println("    PrivacyLevel = " + PrivacyLevel.PSEUDO_PRIVATE);
    System.out.println("    CryptoAddress[1] = " + Utils.summary(ckaa.getCryptoAddress()));
    abelAddress = Crypto.getAbelAddressFromCryptoAddress(1,new Bytes(ckaa.getCryptoAddress().toByteArray()));
    System.out.println("    AbelAddress[1] = " + Utils.summary(abelAddress));
    abelAddressFromString = new AbelAddress(abelAddress.toHex());
    System.out.println("    AbelAddressFromString[1] = " + Utils.summary(abelAddressFromString));
    shortAddress = Crypto.getShortAbelAddressFromAbelAddress(abelAddress);
    System.out.println("    ShortAddress[1] = " + Utils.summary(shortAddress));

      System.out.println("\n    Generate a PSEUDO-CT-PRIVATE crypto address from above crypto seeds:");
      ckaa = Crypto.generateKeysAndAddress(
              PrivacyLevel.PSEUDO_CT_PRIVATE,
              new SpendSecretRootSeed(cryptoSeeds[0]),
              new ViewSecretRootSeed(cryptoSeeds[2]),
              new DetectorRootKey(cryptoSeeds[3])
      );
      System.out.println("    PrivacyLevel = " + PrivacyLevel.PSEUDO_CT_PRIVATE);
      System.out.println("    CryptoAddress[2] = " + Utils.summary(ckaa.getCryptoAddress()));
      abelAddress = Crypto.getAbelAddressFromCryptoAddress(2,new Bytes(ckaa.getCryptoAddress().toByteArray()));
      System.out.println("    AbelAddress[2] = " + Utils.summary(abelAddress));
      abelAddressFromString = new AbelAddress(abelAddress.toHex());
      System.out.println("    abelAddressFromString[2] = " + Utils.summary(abelAddressFromString));
      shortAddress = Crypto.getShortAbelAddressFromAbelAddress(abelAddress);
      System.out.println("    ShortAddress[2] = " + Utils.summary(shortAddress));
  }

  // Demo AbecRPCClient.
  public static void demoAbecRPCClient(String[] args) throws Exception {
    AbecRPCClient client = Demo.getAbecRPCClient();

    class DemoTasks {
      void doAnyRPCCall(String method, Object... params) throws Exception {
        JsonResponse resp = client.call(method, params);
        System.out.printf("Request: method: %s, params: %s\n", method, Utils.summary(params));
        System.out.printf("Response: %s\n", resp);
        if (resp.hasError()) {
          System.out.println("    ❌ This is a failed call.");
        } else {
          System.out.println("    ✅ This is a successful call.");
        }
        System.out.printf("    Error: %s\n", resp.getError());
        System.out.printf("    Result: %s\n", resp.getResultAsString());
      }
    }

    DemoTasks tasks = new DemoTasks();

    System.out.println("\n==> Call any RPC method using client.call(method, ...params).");

    System.out.println("\n--> Call without parameters.");
    tasks.doAnyRPCCall("getinfo");

    System.out.println("\n--> Call with parameters.");
    tasks.doAnyRPCCall("getblockhash", 0);

    System.out.println("\n--> Call with wrong method name.");
    tasks.doAnyRPCCall("getnothing");

    System.out.println("\n--> Call with wrong parameters.");
    tasks.doAnyRPCCall("getinfo", 0, "0x0000000");

    System.out.printf("\n==> Call builtin member methods of %s.\n", AbecRPCClient.class.getSimpleName());
    System.out.println("\n--> Get chain info by client.getChainInfo().");
    ChainInfo ci = client.getChainInfo();
    if (ci != null) {
      System.out.println("ChainInfo: " + ci);
    } else {
      System.out.println("❌ Failed to get chain info.");
    }

    long blockHeight = 835;
    System.out.printf("\n==> Get block hash by client.getBlockHash(height=%d).\n", blockHeight);
    Bytes blockHash = client.getBlockHash(blockHeight);
    if (blockHash != null) {
      System.out.println("BlockHash: " + blockHash);
    } else {
      System.out.println("❌ Failed to get block hash.");
    }

    System.out.printf("\n==> Get block info by client.getBlockInfo(hash=%s).\n", blockHash);
    BlockInfo bi = client.getBlockInfo(blockHash);
    if (bi != null) {
      System.out.println("BlockInfo: " + bi);
    } else {
      System.out.println("❌ Failed to get block info.");
    }

    Bytes txid = bi.txHashes[bi.txHashes.length - 1];
    System.out.printf("\n==> Get tx info by client.getTxInfo(txid=%s).\n", blockHash);
    TxInfo ti = client.getTxInfo(txid);
    if (ti != null) {
      TxInfo tiSummary = new TxInfo() {{
        this.version=ti.version;
        this.txid = ti.txid;
        this.time = ti.time;
        this.blockid = ti.blockid;
        this.blockTime = ti.blockTime;
        this.vins = new TxVin[0];
        this.vouts = new TxVout[0];
        this.confirmations = ti.confirmations;
      }};
      System.out.println("TxInfo (without inputs and outputs): " + tiSummary);
      System.out.println("Inputs:");
      for (int i = 0; i < ti.vins.length; i++) {
        System.out.printf("  vins[%d]:\n", i);
        System.out.printf("    serialnumber: %s\n", ti.vins[i].serialNumber);
      }
      System.out.println("Outputs");
      for (int i = 0; i < ti.vouts.length; i++) {
        System.out.printf("  vouts[%d]:\n", i);
        System.out.printf("    script: %s\n", ti.vouts[i].script);
      }
    } else {
      System.out.println("❌ Failed to get tx info.");
    }
  }
}
