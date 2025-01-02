package info.abelian.sdk.demo;

import java.util.Map;

import info.abelian.sdk.common.AbelAddress;
import info.abelian.sdk.common.AbelException;
import info.abelian.sdk.wallet.Account;
import info.abelian.sdk.wallet.PrivacyLevel;

public class DemoAccount {

    // Demo creating new accounts and loading existing accounts.
    public static void demoAccount(String[] args) throws Exception {
        int chainID = Demo.getDefaultChainID();

        System.out.println("\n==> Create accounts.");
        Account[] fullyPrivateAccounts = new Account[3];
        for (int i = 0; i < fullyPrivateAccounts.length; i++) {
            System.out.printf("\n--> FullyPrivateAccounts[%d]\n", i);
            fullyPrivateAccounts[i] = Account.generateAccount(chainID, PrivacyLevel.FULLY_PRIVATE);
            printAccountInfo(fullyPrivateAccounts[i]);

            AbelAddress abelAddress = fullyPrivateAccounts[i].generateAbelAddress();
            System.out.printf("\n--> Fully-private AbelAddress of FullyPrivateAccounts[%s]\n", abelAddress);

            AbelAddress anotherAbelAddress = fullyPrivateAccounts[i].generateAbelAddress();
            System.out.printf("\n--> another Fully-private AbelAddress of FullyPrivateAccounts[%s]\n", anotherAbelAddress);
        }

        Account[] pseudoPrivateAccounts = new Account[3];
        for (int i = 0; i < pseudoPrivateAccounts.length; i++) {
            System.out.printf("\n--> PseudoPrivateAccounts[%d]\n", i);
            pseudoPrivateAccounts[i] = Account.generateAccount(chainID, PrivacyLevel.PSEUDO_PRIVATE);
            printAccountInfo(pseudoPrivateAccounts[i]);

            AbelAddress abelAddress = pseudoPrivateAccounts[i].generateAbelAddress();
            System.out.printf("\n--> Pseudo-private AbelAddress of PseudoPrivateAccounts[%s]\n", abelAddress);

            AbelAddress anotherAbelAddress = pseudoPrivateAccounts[i].generateAbelAddress();
            System.out.printf("\n--> another Pseudo-private AbelAddress of PseudoPrivateAccounts[%s]\n", anotherAbelAddress);
        }

        System.out.println("\n==> Show builtin accounts.");
        Map<String, Account> builtinAccounts = Demo.getBuiltinAccounts();
        for (Map.Entry<String, Account> entry : builtinAccounts.entrySet()) {
            System.out.printf("\n--> Account[%s]\n", entry.getKey());
            printAccountInfo(entry.getValue());
        }
    }

    private static void printAccountInfo(Account account) throws AbelException {
        System.out.println("    SpendKeyRootSeed = " + account.getSpendKeyRootSeed());
        if (account.getPrivacyLevel() == PrivacyLevel.FULLY_PRIVATE) {
            System.out.println("    SerialNoKeyRootSeed = " + account.getSerialNoKeyRootSeed());
            System.out.println("    ViewerAccount = " + account.getViewKeyRootSeed());
        }
        System.out.println("    DetectorRootKey = " + account.getDetectorRootKey());
    }
}
