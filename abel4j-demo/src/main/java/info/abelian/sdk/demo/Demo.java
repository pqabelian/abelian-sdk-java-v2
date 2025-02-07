package info.abelian.sdk.demo;

import java.lang.reflect.Method;
import java.util.*;

import info.abelian.sdk.common.*;
import info.abelian.sdk.common.CryptoSeed.EntropySeed;
import info.abelian.sdk.demo.persist.WalletDB;
import info.abelian.sdk.demo.persist.WalletDB.ColdWalletDB;
import info.abelian.sdk.demo.persist.WalletDB.HotWalletDB;
import info.abelian.sdk.rpc.AbecRPCClient;
import info.abelian.sdk.wallet.*;

public class Demo extends AbelBase {

    private static Class<?>[] demoClasses = new Class[]{
            Demo.class,
            DemoBasic.class,
            DemoAccount.class,
            DemoWalletDB.class,
            DemoChainViewer.class,
            DemoTxBuilder.class,
            DemoTxSigner.class,
            DemoTxSubmitter.class,
            DemoTxTracker.class,
    };

    private static Method[] demoMethods = getDemoMethods();

    private static AbecRPCClient abecRPCClient;

    private static Map<String, Map<String, Account>> allChainsBuiltinAccounts;
    private static Map<String, Account> builtinAccounts;

    private static HotWalletDB hotWalletDB;

    private static ColdWalletDB coldWalletDB;

    public static void main(String[] args) throws Exception {
        // Suppress ORMLite log.
        com.j256.ormlite.logger.Logger.setGlobalLogLevel(com.j256.ormlite.logger.Level.OFF);

        // If no args, print usage to stderr.
        if (args.length == 0) {
            System.err.println("Usage: java -jar abec4j-demo.jar <demoName> [demoArgs]");
            // Print the list of demos.
            System.err.println("Available demos:");
            for (Method m : Demo.demoMethods) {
                System.err.println("  " + m.getName().substring(4));
            }
            System.exit(0);
        }

        // Get the first arg as the demo name.
        String demoName = args[0];

        // Get the rest of args as the demo args.
        String[] demoArgs = Arrays.copyOfRange(args, 1, args.length);

        // Print the demo name and the rest of args.
        System.out.println("demoName = " + demoName);
        System.out.println("demoArgs = " + Arrays.toString(demoArgs));
        System.out.println("default.chain = " + getDefaultChainName());

        // Get the demo method by name.
        Method demoMethod = getDemoMethod(demoName);

        // If the demo method is not found, print error to stderr and exit.
        if (demoMethod == null) {
            System.err.println("Demo not found: " + demoName);
            System.exit(1);
        }

        // Invoke the demo method.
        try {
            demoMethod.invoke(null, (Object) demoArgs);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        } finally {
            if (hotWalletDB != null) {
                hotWalletDB.close();
            }
        }

        // Exit with 0.
        System.exit(0);
    }

    // Get all demo methods whose name starts with "demo".
    private static Method[] getDemoMethods() {
        ArrayList<Method> demoMethods = new ArrayList<Method>();
        for (Class<?> c : Demo.demoClasses) {
            for (Method m : c.getDeclaredMethods()) {
                if (m.getName().startsWith("demo")) {
                    demoMethods.add(m);
                }
            }
        }
        return demoMethods.toArray(new Method[demoMethods.size()]);
    }

    // Get demo method by name.
    private static Method getDemoMethod(String demoName) {
        String methodName = "demo" + demoName;
        for (Method m : Demo.demoMethods) {
            if (m.getName().equals(methodName)) {
                return m;
            }
        }
        return null;
    }

    public static AbecRPCClient getAbecRPCClient() {
        if (abecRPCClient == null) {
            try {
                abecRPCClient = AbecRPCClient.getInstance();
            } catch (AbelException e) {
                throw new RuntimeException(e);
            }
        }
        return abecRPCClient;
    }

    private static void loadBuiltinAccounts(String chainName) throws AbelException {
        Map<String, Account> accounts = new HashMap<>();
        Properties accountConf = getConf(String.format("%s.account.", chainName));
        for (String key : accountConf.stringPropertyNames()) {
            if (key.endsWith(".entropyseed")) {
                String accountID = key.substring(0, key.length() - ".entropyseed".length());
                PrivacyLevel privacyLevel = PrivacyLevel.fromValue(Integer.parseInt(accountConf.getProperty(accountID + ".privacyLevel")));
                EntropySeed entropySeed = new EntropySeed(Bytes.fromHex(accountConf.getProperty(key)));

                // use seq account for demo
                Account account = new SeqAccount(getChainID(chainName), privacyLevel, entropySeed);
                accounts.put(accountID, account);
            }
        }

        allChainsBuiltinAccounts = new HashMap<>();
        allChainsBuiltinAccounts.put(chainName, accounts);
    }

    public static Map<String, Account> getBuiltinAccounts() throws AbelException {
        if (builtinAccounts == null) {
            String chainName = getDefaultChainName();
            if (allChainsBuiltinAccounts==null) {
                loadBuiltinAccounts(chainName);
            }
            return allChainsBuiltinAccounts.get(chainName);
        }
        return builtinAccounts;
    }

    public static HotWalletDB getHotWalletDB() {
        if (hotWalletDB == null) {
            hotWalletDB = WalletDB.openHotWalletDB();
        }
        return hotWalletDB;
    }

    public static ColdWalletDB getColdWalletDB() {
        if (coldWalletDB == null) {
            coldWalletDB = WalletDB.openColdWalletDB();
        }
        return coldWalletDB;
    }

    public static ChainViewer createChainViewer(boolean verbose) throws Exception {
        // Load all viewer accounts in the hot wallet database.
        HotWalletDB db = Demo.getHotWalletDB();
        AbstractMap.SimpleEntry<String,ViewAccount>[] viewerAccounts = db.getAllViewerAccounts();

        if (verbose) {
            for (int i = 0; i < viewerAccounts.length; i++) {
                System.out.printf("Account %s with detector root key %s.\n", viewerAccounts[i].getKey(),
                        viewerAccounts[i].getValue().getDetectorRootKey());
            }
        }

        // Create ChainViewer and return it.
        ChainViewer chainViewer = new ChainViewer(getAbecRPCClient());

        for (AbstractMap.SimpleEntry<String, ViewAccount> viewerAccount : viewerAccounts) {
            chainViewer.addViewAccount(viewerAccount.getKey(), viewerAccount.getValue());
        }
        return chainViewer;
    }
}