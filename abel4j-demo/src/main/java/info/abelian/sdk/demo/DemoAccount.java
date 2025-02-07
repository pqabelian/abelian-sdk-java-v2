package info.abelian.sdk.demo;

import java.util.Map;

import info.abelian.sdk.common.AbelBase;
import info.abelian.sdk.common.CryptoSeed.EntropySeed;
import info.abelian.sdk.common.AbelException;

import info.abelian.sdk.common.PrivacyLevel;
import info.abelian.sdk.wallet.AbelAddress;
import info.abelian.sdk.wallet.ViewAccount;
import info.abelian.sdk.wallet.Account;
import info.abelian.sdk.wallet.SeqAccount;

public class DemoAccount {

    // Demo creating new accounts and loading built-in accounts.
    public static void demoAccount(String[] args) throws Exception {
        int chainID = Demo.getDefaultChainID();

        PrivacyLevel privacyLevel = PrivacyLevel.PSEUDO_PRIVATE;
        System.out.println("\n==> Create pseudo-private accounts from scratch.");
        Account[] accounts = new Account[3];
        for (int i = 0; i < accounts.length; i++) {
            System.out.printf("\n--> Account[%d]\n", i);
            accounts[i] = Account.generateAccount(chainID, privacyLevel);
            printAccountInfo(accounts[i]);
        }

        System.out.println("\n==> Load signer accounts from entropy seed.");
        EntropySeed[] entropySeeds = new EntropySeed[accounts.length];
        Account[] signerAccounts = new Account[accounts.length];
        for (int i = 0; i < accounts.length; i++) {
            System.out.printf("\n--> Account[%d]\n", i);
            entropySeeds[i] = Account.generateEntropySeed();
            signerAccounts[i] = Account.loadAccount(chainID, privacyLevel, entropySeeds[i]);
            printAccountInfo(signerAccounts[i]);
        }

        System.out.println("\n==> Load viewer accounts from serial number root seed, view key and address.");
        ViewAccount[] viewerAccounts = new ViewAccount[accounts.length];
        for (int i = 0; i < accounts.length; i++) {
            System.out.printf("\n--> ViewAccount[%d]\n", i);
            viewerAccounts[i] = ViewAccount.loadViewAccount(chainID,
                    accounts[i].getPrivacyLevel(),
                    accounts[i].getSerialNoSecretRootSeed(),
                    accounts[i].getViewKeyRootSeed(),
                    accounts[i].getDetectorRootKey());
            printAccountInfo(viewerAccounts[i]);
        }


        System.out.println("\n==> Load sequence accounts from entropy seed.");
        SeqAccount[] seqAccounts = new SeqAccount[accounts.length];
        for (int i = 0; i < accounts.length; i++) {
            seqAccounts[i] = SeqAccount.loadSeqAccount(chainID,
                    accounts[i].getPrivacyLevel(),
                    entropySeeds[i]);
            int seqNo = 0;
            System.out.printf("\n--> Generate abel address with sequence %d for SeqAccount[%d]\n", seqNo, i);
            AbelAddress abelAddress = seqAccounts[i].generateAbelAddress(seqNo);
            System.out.println("    Address = " + Utils.summary(abelAddress));

            System.out.printf("\n--> Generate abel address with sequence %d for SeqAccount[%d] again\n", seqNo, i);
            AbelAddress abelAddress2 = seqAccounts[i].generateAbelAddress(seqNo);
            System.out.println("    Address = " + Utils.summary(abelAddress2));
        }


        System.out.println("\n==> Export accounts generated from entropy seed.");
        String outputDir = AbelBase.getEnvPath("accounts");
        java.io.File dir = new java.io.File(outputDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        for (int i = 0; i < entropySeeds.length; i++) {
            String filePath = String.format("%s/chain-%d-account-%d.mnemonics", outputDir, chainID, i);

            java.io.File file = new java.io.File(filePath);
            if (file.exists()) {
                file.delete();
                file.createNewFile();
            }

            String[] mnemonics = Account.entropySeedToMnemonics(entropySeeds[i]);
            try (java.io.PrintWriter writer = new java.io.PrintWriter(filePath)) {
                writer.println(String.join(" ", mnemonics));
            }
        }
        System.out.printf("Successfully export all accounts to %s.\n", outputDir);

        System.out.println("\n==> Show builtin accounts.");
        Map<String, Account> builtinAccounts = Demo.getBuiltinAccounts();
        for (Map.Entry<String, Account> entry : builtinAccounts.entrySet()) {
            System.out.printf("\n--> Account[%s]\n", entry.getKey());
            printAccountInfo(entry.getValue());
        }
    }

    private static void printAccountInfo(Account account) throws AbelException {
        System.out.println("    SpendKeyRootSeed = " + account.getSpendSecretRootSeed());
        if (account.getPrivacyLevel() == PrivacyLevel.FULLY_PRIVATE) {
            System.out.println("    SerialNoKeyRootSeed = " + account.getSerialNoSecretRootSeed());
            System.out.println("    ViewerAccount = " + account.getViewKeyRootSeed());
        }
        System.out.println("    DetectorRootKey = " + account.getDetectorRootKey());
    }

    private static void printAccountInfo(ViewAccount viewAccount) throws AbelException {
        if (viewAccount.getPrivacyLevel() == PrivacyLevel.FULLY_PRIVATE) {
            System.out.println("    SerialNoKeyRootSeed = " + viewAccount.getSerialNoKeyRootSeed());
            System.out.println("    ViewerAccount = " + viewAccount.getViewKeyRootSeed());
        }
        System.out.println("    DetectorRootKey = " + viewAccount.getDetectorRootKey());
    }
}
