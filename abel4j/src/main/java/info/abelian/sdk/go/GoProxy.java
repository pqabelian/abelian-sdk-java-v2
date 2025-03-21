package info.abelian.sdk.go;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;

import com.google.protobuf.GeneratedMessage;
import com.sun.jna.Native;


import info.abelian.sdk.proto.Core.GenerateEntropySeedArgs;
import info.abelian.sdk.proto.Core.GenerateEntropySeedResult;

import info.abelian.sdk.proto.Core.EntropySeedToMnemonicsArgs;
import info.abelian.sdk.proto.Core.EntropySeedToMnemonicsResult;

import info.abelian.sdk.proto.Core.MnemonicsToEntropySeedArgs;
import info.abelian.sdk.proto.Core.MnemonicsToEntropySeedResult;

import info.abelian.sdk.proto.Core.EntropySeedToCryptoSeedArgs;
import info.abelian.sdk.proto.Core.EntropySeedToCryptoSeedResult;

import info.abelian.sdk.proto.Core.MnemonicsToCryptoSeedArgs;
import info.abelian.sdk.proto.Core.MnemonicsToCryptoSeedResult;

import info.abelian.sdk.proto.Core.EntropySeedToPublicRandRootSeedArgs;
import info.abelian.sdk.proto.Core.EntropySeedToPublicRandRootSeedResult;

import info.abelian.sdk.proto.Core.MnemonicsToPublicRandRootSeedArgs;
import info.abelian.sdk.proto.Core.MnemonicsToPublicRandRootSeedResult;


import info.abelian.sdk.proto.Core.GenerateSafeCryptoSeedArgs;
import info.abelian.sdk.proto.Core.GenerateSafeCryptoSeedResult;

import info.abelian.sdk.proto.Core.SequenceNoToPublicRandArgs;
import info.abelian.sdk.proto.Core.SequenceNoToPublicRandResult;

import info.abelian.sdk.proto.Core.GenerateCryptoKeysAndAddressByRootSeedsFromPublicRandArgs;
import info.abelian.sdk.proto.Core.GenerateCryptoKeysAndAddressByRootSeedsFromPublicRandResult;

import info.abelian.sdk.proto.Core.GenerateCryptoKeysAndAddressByRootSeedsArgs;
import info.abelian.sdk.proto.Core.GenerateCryptoKeysAndAddressByRootSeedsResult;

import info.abelian.sdk.proto.Core.GetCoinAddressFromCryptoAddressArgs;
import info.abelian.sdk.proto.Core.GetCoinAddressFromCryptoAddressResult;

import info.abelian.sdk.proto.Core.GetAbelAddressFromCryptoAddressArgs;
import info.abelian.sdk.proto.Core.GetAbelAddressFromCryptoAddressResult;

import info.abelian.sdk.proto.Core.GetCryptoAddressFromAbelAddressArgs;
import info.abelian.sdk.proto.Core.GetCryptoAddressFromAbelAddressResult;

import info.abelian.sdk.proto.Core.GetShortAbelAddressFromAbelAddressArgs;
import info.abelian.sdk.proto.Core.GetShortAbelAddressFromAbelAddressResult;


import info.abelian.sdk.proto.Core.CoinReceiveFromTxOutDataArgs;
import info.abelian.sdk.proto.Core.CoinReceiveFromTxOutDataResult;

import info.abelian.sdk.proto.Core.GenerateRawTxRequestArgs;
import info.abelian.sdk.proto.Core.GenerateRawTxRequestResult;

import info.abelian.sdk.proto.Core.GenerateRawTxDataArgs;
import info.abelian.sdk.proto.Core.GenerateRawTxDataResult;

import info.abelian.sdk.proto.Core.GenerateCoinSerialNumberArgs;
import info.abelian.sdk.proto.Core.GenerateCoinSerialNumberResult;

import info.abelian.sdk.proto.Core.GetFingerprintFromCoinAddressArgs;
import info.abelian.sdk.proto.Core.GetFingerprintFromCoinAddressResult;

import info.abelian.sdk.proto.Core.DecodeCoinAddressFromSerializedTxOutDataArgs;
import info.abelian.sdk.proto.Core.DecodeCoinAddressFromSerializedTxOutDataResult;

public class GoProxy {

    // Exceptions
    public static class AbelGoException extends Exception {
        public AbelGoException(Exception e) {
            super(e);
        }
    }

    // Constants
    private static final Map<String, String> GO_LIB_PATHS = new HashMap<String, String>() {
        {
            put("macos-x64", "/native/macos-x64/libabelsdk.2.dylib");
            put("macos-arm64", "/native/macos-arm64/libabelsdk.2.dylib");
            put("linux-x64", "/native/linux-x64/libabelsdk.so.2");
            put("linux-arm64", "/native/linux-arm64/libabelsdk.so.2");
        }
    };

    // Singleton
    private static GoProxy instance;

    public static GoProxy getInstance() {
        if (instance == null) {
            instance = new GoProxy();
        }
        return instance;
    }

    private GoLibrary goLib;

    private GoProxy() {
        String goLibPath = getGoLibPath();
        try {
            File libFile = Native.extractFromResourcePath(goLibPath, Native.class.getClassLoader());
            String fileName = goLibPath.substring(goLibPath.lastIndexOf('/') + 1);
            File tmpLibFile = new File(System.getProperty("java.io.tmpdir"), fileName);
            Files.copy(libFile.toPath(), tmpLibFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            this.goLib = (GoLibrary) Native.load(tmpLibFile.getAbsolutePath(), GoLibrary.class);
            tmpLibFile.delete();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String getGoLibPath() {
        String os = System.getProperty("os.name").toLowerCase();
        String arch = System.getProperty("os.arch").toLowerCase();

        if (os.contains("mac") || os.contains("darwin")) {
            os = "macos";
        } else if (os.contains("linux")) {
            os = "linux";
        } else {
            throw new RuntimeException("Unsupported OS: " + os);
        }

        if (arch.contains("x86_64") || arch.contains("amd64")) {
            arch = "x64";
        } else if (arch.contains("aarch64") || arch.contains("arm64")) {
            arch = "arm64";
        } else {
            throw new RuntimeException("Unsupported Arch: " + arch);
        }

        String goLibPath = GO_LIB_PATHS.get(os + "-" + arch);
        if (goLibPath == null) {
            throw new RuntimeException("Unsupported OS-Arch: " + os + "-" + arch);
        }
        return goLibPath;
    }

    public Method getGoFunc(String funcName) {
        Method goFunc = null;
        for (Method m : GoLibrary.class.getDeclaredMethods()) {
            if (m.getName().equals(funcName)) {
                goFunc = m;
                break;
            }
        }
        return goFunc;
    }

    public GoResponse callGoFunc(GoRequest req) throws AbelGoException {
        // Get Go func by name.
        Method goFunc = getGoFunc(req.funcName);

        // Create Go params.
        // NOTE: There might be new Memory objects create in Go params. They will be
        // closed by req.reclaimGoParams().
        Object[] goParams = req.createGoParams();

        // Call Go func with Go params.
        Object goRetVal;
        try {
            goRetVal = goFunc.invoke(goLib, new Object[]{goParams});
        } catch (Exception e) {
            throw new AbelGoException(e);
        }

        // Reclaim Go params and construct response.
        // NOTE: Any previously created Memory objects in goParams will be closed here.
        GoResponse resp = GoResponse.create(req, goRetVal, goParams);

        // Return Response.
        return resp;
    }

    public Object callProtoGoFunc(String goFuncName, GeneratedMessage args, Class<?> resultClass)
            throws AbelGoException {
        byte[] argsData = args.toByteArray();
        GoResponse resp = callGoFunc(new GoRequest(goFuncName, DataItemType.V_BYTE_BUFFER, new DataItem[]{
                new DataItem("argsData", DataItemType.BYTE_ARRAY, argsData),
        }));
        try {
            Method parseFrom = resultClass.getDeclaredMethod("parseFrom", byte[].class);
            return parseFrom.invoke(null, (Object) resp.getRetValAsByteArray());
        } catch (Exception e) {
            throw new AbelGoException(e);
        }
    }

    public GenerateEntropySeedResult goGenerateEntropySeed(
            GenerateEntropySeedArgs args) throws AbelGoException {
        return (GenerateEntropySeedResult) callProtoGoFunc("GenerateEntropySeed", args,
                GenerateEntropySeedResult.class);
    }

    public EntropySeedToMnemonicsResult goEntropySeedToMnemonics(
            EntropySeedToMnemonicsArgs args) throws AbelGoException {
        return (EntropySeedToMnemonicsResult) callProtoGoFunc("EntropySeedToMnemonics", args,
                EntropySeedToMnemonicsResult.class);
    }

    public MnemonicsToEntropySeedResult goMnemonicsToEntropySeed(
            MnemonicsToEntropySeedArgs args) throws AbelGoException {
        return (MnemonicsToEntropySeedResult) callProtoGoFunc("MnemonicsToEntropySeed", args,
                MnemonicsToEntropySeedResult.class);
    }

    public EntropySeedToCryptoSeedResult goEntropySeedToCryptoSeed(
            EntropySeedToCryptoSeedArgs args) throws AbelGoException {
        return (EntropySeedToCryptoSeedResult) callProtoGoFunc("EntropySeedToCryptoSeed", args,
                EntropySeedToCryptoSeedResult.class);
    }

    public MnemonicsToCryptoSeedResult goMnemonicsToCryptoSeed(
            MnemonicsToCryptoSeedArgs args) throws AbelGoException {
        return (MnemonicsToCryptoSeedResult) callProtoGoFunc("MnemonicsToCryptoSeed", args,
                MnemonicsToCryptoSeedResult.class);
    }

    public EntropySeedToPublicRandRootSeedResult goEntropySeedToPublicRandRootSeed(
            EntropySeedToPublicRandRootSeedArgs args) throws AbelGoException {
        return (EntropySeedToPublicRandRootSeedResult)
                callProtoGoFunc("EntropySeedToPublicRandRootSeed", args,
                        EntropySeedToPublicRandRootSeedResult.class);
    }

    public MnemonicsToPublicRandRootSeedResult goMnemonicsToPublicRandRootSeed(
            MnemonicsToPublicRandRootSeedArgs args) throws AbelGoException {
        return (MnemonicsToPublicRandRootSeedResult)
                callProtoGoFunc("MnemonicsToPublicRandRootSeed", args,
                        MnemonicsToPublicRandRootSeedResult.class);
    }

    public GenerateSafeCryptoSeedResult goGenerateSafeCryptoSeed(
            GenerateSafeCryptoSeedArgs args) throws AbelGoException {
        return (GenerateSafeCryptoSeedResult)
                callProtoGoFunc("GenerateSafeCryptoSeed", args,
                        GenerateSafeCryptoSeedResult.class);
    }

    public GenerateCryptoKeysAndAddressByRootSeedsResult goGenerateCryptoKeysAndAddressByRootSeeds(
            GenerateCryptoKeysAndAddressByRootSeedsArgs args) throws AbelGoException {
        return (GenerateCryptoKeysAndAddressByRootSeedsResult)
                callProtoGoFunc("GenerateCryptoKeysAndAddressByRootSeeds", args,
                        GenerateCryptoKeysAndAddressByRootSeedsResult.class);
    }

    public SequenceNoToPublicRandResult goSequenceNoToPublicRand(
            SequenceNoToPublicRandArgs args) throws AbelGoException {
        return (SequenceNoToPublicRandResult)
                callProtoGoFunc("SequenceNoToPublicRand", args,
                        SequenceNoToPublicRandResult.class);
    }

    public GenerateCryptoKeysAndAddressByRootSeedsFromPublicRandResult
    goGenerateCryptoKeysAndAddressByRootSeedsFromPublicRand(
            GenerateCryptoKeysAndAddressByRootSeedsFromPublicRandArgs args) throws AbelGoException {
        return (GenerateCryptoKeysAndAddressByRootSeedsFromPublicRandResult)
                callProtoGoFunc("GenerateCryptoKeysAndAddressByRootSeedsFromPublicRand", args,
                        GenerateCryptoKeysAndAddressByRootSeedsFromPublicRandResult.class);
    }

    public GetCoinAddressFromCryptoAddressResult goGetCoinAddressFromCryptoAddress(
            GetCoinAddressFromCryptoAddressArgs args) throws AbelGoException {
        return (GetCoinAddressFromCryptoAddressResult)
                callProtoGoFunc("GetCoinAddressFromCryptoAddress", args,
                        GetCoinAddressFromCryptoAddressResult.class);
    }


    public GetAbelAddressFromCryptoAddressResult goGetAbelAddressFromCryptoAddress(
            GetAbelAddressFromCryptoAddressArgs args) throws AbelGoException {
        return (GetAbelAddressFromCryptoAddressResult)
                callProtoGoFunc("GetAbelAddressFromCryptoAddress", args,
                        GetAbelAddressFromCryptoAddressResult.class);
    }

    public GetCryptoAddressFromAbelAddressResult goGetCryptoAddressFromAbelAddress(
            GetCryptoAddressFromAbelAddressArgs args) throws AbelGoException {
        return (GetCryptoAddressFromAbelAddressResult)
                callProtoGoFunc("GetCryptoAddressFromAbelAddress", args,
                        GetCryptoAddressFromAbelAddressResult.class);
    }

    public GetShortAbelAddressFromAbelAddressResult goGetShortAbelAddressFromAbelAddress(
            GetShortAbelAddressFromAbelAddressArgs args) throws AbelGoException {
        return (GetShortAbelAddressFromAbelAddressResult)
                callProtoGoFunc("GetShortAbelAddressFromAbelAddress", args,
                        GetShortAbelAddressFromAbelAddressResult.class);
    }


    public CoinReceiveFromTxOutDataResult goCoinReceiveFromTxOutData(
            CoinReceiveFromTxOutDataArgs args) throws AbelGoException {
        return (CoinReceiveFromTxOutDataResult)
                callProtoGoFunc("CoinReceiveFromTxOutData", args,
                        CoinReceiveFromTxOutDataResult.class);
    }

    public GenerateRawTxRequestResult goGenerateRawTxRequest(
            GenerateRawTxRequestArgs args) throws AbelGoException {
        return (GenerateRawTxRequestResult) callProtoGoFunc("GenerateRawTxRequest", args,
                GenerateRawTxRequestResult.class);
    }

    public GenerateRawTxDataResult goGenerateRawTxData(
            GenerateRawTxDataArgs args) throws AbelGoException {
        return (GenerateRawTxDataResult) callProtoGoFunc("GenerateRawTxData", args,
                GenerateRawTxDataResult.class);
    }

    public GenerateCoinSerialNumberResult goGenerateCoinSerialNumber(
            GenerateCoinSerialNumberArgs args) throws AbelGoException {
        return (GenerateCoinSerialNumberResult)
                callProtoGoFunc("GenerateCoinSerialNumber", args,
                        GenerateCoinSerialNumberResult.class);
    }

    public GetFingerprintFromCoinAddressResult goGetFingerprintFromCoinAddress(
            GetFingerprintFromCoinAddressArgs args) throws AbelGoException {
        return (GetFingerprintFromCoinAddressResult)
                callProtoGoFunc("GetFingerprintFromCoinAddress", args,
                        GetFingerprintFromCoinAddressResult.class);
    }

    public DecodeCoinAddressFromSerializedTxOutDataResult goDecodeCoinAddressFromSerializedTxOutData(
            DecodeCoinAddressFromSerializedTxOutDataArgs args) throws AbelGoException {
        return (DecodeCoinAddressFromSerializedTxOutDataResult)
                callProtoGoFunc("DecodeCoinAddressFromSerializedTxOutData", args,
                        DecodeCoinAddressFromSerializedTxOutDataResult.class);
    }

}
