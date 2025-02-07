# Understanding the Abelian Java SDK by Demos
> [!NOTE]
> All demos described in this document are run on the testnet of the Abelian blockchain. 
> The testnet is a separate blockchain from the mainnet and is used for testing purposes only. 
> The built-in accounts used in the demos already has some ABELs and transactions on the testnet. 
> As such, you can run the demos without setting up any testnet accounts or sending any ABELs to the built-in accounts.

## 1. Prepare the environment

### 1.1. Get the demos

Download and decompress `abec4j-demo-x.y.z.zip` to get the demo directory `abel4j-demo`. Go to the demo directory and we will run all the demos from there.

```bash
unzip abel4j-demo-x.y.z.zip
cd abel4j-demo
```

### 1.2. Test Java environment

Run the following command to test if your Java environment is ready for the demos.

```bash
bin/abel4j-demo
```

The expected output should be like this:

```text
Usage: java -jar abec4j-demo.jar <demoName> [demoArgs]
Available demos:
  Crypto
  AbecRPCClient
  Account
  WalletDB
  GetSafeBlockInfo
  GetTxInfo
  GetCoins
  ScanCoins
  GenerateUnsignedRawTx
  GenerateSignedRawTx
  SubmitSignedRawTx
  TrackSpentCoins
```

As shown above, run `bin/abel4j-demo` without any arguments will list all the available demos.

### 1.3. Test native library

The Abelian SDK for Java depends on a native library `libabelsdk` built from the Abelian SDK for Go. The native library binaries for all supported platforms (`libabelsdk.so.x` for Linux,`libabelsdk.x.dylib` for macOS) are already included in the SDK jar file `abel4j-x.y.z.jar`.

As of the time of writing, the native library is built for the following platforms:
- Linux x86
- Linux arm64
- MacOS x86
- MacOS arm64 (Apple Silicon)

The SDK jar file contains all the above native libraries for the supported platforms. When you run the demos, the SDK will automatically load the native library compatible with your platform.

Run the following command to test if the native library included in the SDK jar file is compatible with your platform.

```bash
bin/abel4j-demo Crypto
```

The expected output should be like this:

```text
demoName = Crypto
demoArgs = []
default.chain = testnet

==> Generate entropy seed.
    EntropySeed = [32 bytes|0xEB88BEF48F563AE9CEC6431105CE7690B299CEF62A64898F2B235857FB2047D8]

==> Convert entropy seed to mnemonics.
    Mnemonics = twist echo rule burger glove insane deposit silent baby common oval cannon civil trash suffer erode maze junk silver radar leisure mother buyer air

==> Derive crypto seeds from entropy seed.
    SpendSecretRootSeed = [64 bytes|0x770464BF4DC11FF1DC03387333B1DB59...DE8054AEA4D3619DB36891C9927804C1]
    SerialNoSecretRootSeed = [64 bytes|0xBAEBC2674A990296CF7213906C166ACC...6901277101F75B86224925B45786154D]
    ViewSecretRootSeed = [64 bytes|0x4F068EB26398A6032E2450617C026849...BC2D591933144AD4E9E650CB22EF784F]
    DetectorRootKey = [64 bytes|0xDF5ACBDF235DCDE859F7051778A4DF15...B27D182ECD5927B3A41AAEC2BACB637A]

    Generate a FULLY-PRIVATE crypto address from above crypto seeds:
    PrivacyLevel = FULLY_PRIVATE
    CryptoAddress[0] = [10826 bytes|0x01000000010184E23839DA9E5DC776CC...2442F68FA1E44731AD1F4B4A5DEE5CE7]
    AbelAddress[0] = [10859 bytes|0x0101000000010184E23839DA9E5DC776...3397B48505D5E03D86074D3F8277B24D]
    ShortAddress[0] = [68 bytes|0xABE01105F5F27EBDE07B59D550DE2A97...733538575AB2562568BC0F1659BD95F3]

    Generate a PSEUDO-PRIVATE crypto address (from the same seeds):
    PrivacyLevel = PSEUDO_PRIVATE
    CryptoAddress[1] = [198 bytes|0x01000000020278386781BB4704733261...FD1A0190802D4DC8A8D7E8998CB2B1CB]
    AbelAddress[1] = [231 bytes|0x0101000000020278386781BB47047332...59F040764F3C7736E70946AB5A2ED5B9]
    ShortAddress[1] = [68 bytes|0xABE011069E00775D2CE0455774E0CC4D...A2189C40BA8CCD6C4E17C38CDC643CD9]
```

If the `Crypto` demo runs successfully, then the native library included in the SDK jar file is compatible with your platform.

Note that this demo is for testing purpose only. It invokes low-level cryptographic APIs of the native library which is unlikely to be used directly in your applications. Therefore, it doesn't matter if you don't understand the output here or its source code. The only important thing is whether the demo runs successfully.

### 1.3 Test the default Abec full node for the demos

The Abelian SDK for Java depends on an Abec full node to access the data on the Abelian blockchain. In the default settings, the demos will connect to an Abec full node maintained by the Abelian Foundation. It allows you to run the demos without setting up your own Abec full node.

Note that the default Abec full node is for testing purpose only. It is not stable and may be reset at any time. **For production use, you should always set up your own Abec full node for real-world applications.**

Run the following command to test if the default Abec full node is accessible from your machine.

```bash
bin/abel4j-demo AbecRPCClient
```

The expected output should be like this:

```text
demoName = AbecRPCClient
demoArgs = []
default.chain = testnet

==> Call any RPC method using client.call(method, ...params).

--> Call without parameters.
Request: method: getinfo, params: []
Response: {id: a02742ba-12c1-4898-8a00-260058aa4de8, result: {"version":1000000,"protocolversion":70002,"blocks":397324,"bestblockhash":"3fcc0ea06a454ac42ddd86b4a5021ef28b6f383c25847796e49ab149d5751783","worksum":"5168348353837609580890082993511122433868240042","timeoffset":0,"connections":2,"proxy":"","difficulty":2.177823504153896E40,"testnet":true,"relayfee":1.0E-6,"errors":"","nodetype":"SemiFullNode","witnessserviceheight":0,"netid":2}, error: null}
    ✅ This is a successful call.
    Error: null
    Result: {"version":1000000,"protocolversion":70002,"blocks":397324,"bestblockhash":"3fcc0ea06a454ac42ddd86b4a5021ef28b6f383c25847796e49ab149d5751783","worksum":"5168348353837609580890082993511122433868240042","timeoffset":0,"connections":2,"proxy":"","difficulty":2.177823504153896E40,"testnet":true,"relayfee":1.0E-6,"errors":"","nodetype":"SemiFullNode","witnessserviceheight":0,"netid":2}

--> Call with parameters.
Request: method: getblockhash, params: [1 items|0]
Response: {id: c7da63d6-1182-435b-9915-1a7bbc455c24, result: "eb143c8328e3131a4474ee1811d3c3a9f27e5102064148dc172966ccb50c2e2b", error: null}
    ✅ This is a successful call.
    Error: null
    Result: "eb143c8328e3131a4474ee1811d3c3a9f27e5102064148dc172966ccb50c2e2b"

--> Call with wrong method name.
Request: method: getnothing, params: []
Response: {id: d6c6541b-a6a8-42e7-b82e-4412d92d987e, result: null, error: (-32601, "Method not found")}
    ❌ This is a failed call.
    Error: (-32601, "Method not found")
    Result: null

--> Call with wrong parameters.
Request: method: getinfo, params: [2 items|0, [9 chars|0x0000000]]
Response: {id: 0df131fd-4a0d-42f0-8bb5-059aa99554cf, result: null, error: (-32602, "wrong number of params (expected 0, received 2)")}
    ❌ This is a failed call.
    Error: (-32602, "wrong number of params (expected 0, received 2)")
    Result: null

==> Call builtin member methods of AbecRPCClient.

--> Get chain info by client.getChainInfo().
ChainInfo: {"rpc":"https://testnet-rpc-00.abelian.info","height":397324,"difficulty":9223372036854775807}

==> Get block hash by client.getBlockHash(height=835).
BlockHash: [32 bytes|0x7AF4230AB3E7105C4067B0AEE93D20FBCBEC90A6F9DE08DB2E7AD14D8C77E730]

==> Get block info by client.getBlockInfo(hash=[32 bytes|0x7AF4230AB3E7105C4067B0AEE93D20FBCBEC90A6F9DE08DB2E7AD14D8C77E730]).
BlockInfo: {"height":835,"hash":{"data":"7AF4230AB3E7105C4067B0AEE93D20FBCBEC90A6F9DE08DB2E7AD14D8C77E730","length":32},"prevHash":{"data":"07340CEE74926B2AC5F9C3FA0FBC039CD3E26F8E8844C8902B4EAB04688475A5","length":32},"time":1707375349,"txHashes":[{"data":"4F5D4A6B922FC01E7584016BF3AB92421C7EC881837A18B4B787D03D708990B4","length":32}]}

==> Get tx info by client.getTxInfo(txid=[32 bytes|0x7AF4230AB3E7105C4067B0AEE93D20FBCBEC90A6F9DE08DB2E7AD14D8C77E730]).
TxInfo (without inputs and outputs): {"version":1,"txid":{"data":"4F5D4A6B922FC01E7584016BF3AB92421C7EC881837A18B4B787D03D708990B4","length":32},"time":1707375349,"blockid":{"data":"7AF4230AB3E7105C4067B0AEE93D20FBCBEC90A6F9DE08DB2E7AD14D8C77E730","length":32},"blockTime":1707375349,"vins":[],"vouts":[],"confirmations":396490,"coinSerialNumbers":[]}
Inputs:
  vins[0]:
    serialnumber: [64 bytes|0x00000000000000000000000000000000...00000000000000000000000000000000]
Outputs
  vouts[0]:
    script: [20469 bytes|0x01000000FDEE4F9DB62D1CEB38299C21...765AEF4A9875FD4A1231A1E3F1A7AF39]
```

If the `AbecRPCClient` demo runs successfully, then the default Abec full node is accessible from your machine.

> **Milestone**
> 
> If you have reached this point, congratulations! You have successfully installed the Abelian SDK for Java and tested if it is working properly. You can now learn how to use the SDK to develop your own applications. In the following section, we will omit some output of the demos for brevity. You can run the demos yourself to see the full output.

## 2. Understand account in Abelian SDK

### 2.1 Concepts

An account in the Abelian SDK (both for Java and Go) consists of the following components:

- **Spend Secret Root Seed**: A 64-byte *secret seed* to sign transactions and **should be treated as sensitive as the private**.

- **Serial Number Secret Root Seed**: A 64-byte *semi-secret seed* to decode which input coins are actually spent in a transaction.

- **View Secret Root Seed**: A 64-byte *semi-secret seed* to decode the amount of ABELs in the output coins belonging to the account.

- **Detector Root Key**: A 64-byte *semi-secret key* to decode the amount of ABELs in the output coins belonging to the account.

- **Privacy Level**: An important mechanism for controlling data public visibility for specified account, which can effectively protect transaction participants and hidden the coin value.
  Abelian has two levels of privacy, namely fully-private privacy level and pseudo-private privacy level.
  The coins with fully-private privacy level would hide the coin value in the transaction, and their transfers are not traceable, while the coins with pseudo-private privacy level would public the coin value in the transaction, and their transfers are traceable.


For integrating with existing wallets, such as Abelian Desktop Wallet Pro, the above root seeds and keys can be derived from somewhere, i.e., 
entropy seed, referred to as `Entropy Seed` in the low-level APIs of the Abelian SDK,
or its one alternative form, say mnemonics, referred to as `Mnemonics` in the low-level APIs of the Abelian SDK.

- **Entropy Seed**: A 32-byte random data used to derive secret info such as all above seeds and keys. 

- **Mnemonics**: A 24-word list used to derive secret info such as all above seeds and keys.

In order to receive coins, the account needs to provide an address to the outside:

- **Abel Address**: A 10859-byte or 231-byte *address* to receive ABELs. An account can also have many addresses.
The coins on 10859-byte *address* has the fully-private privacy, while coins on 231-byte *address* has the pseudo-private privacy.
We often referred to it as `Address` for short. It can be generated from the crypto seeds.

- **Short Address**: A 68-byte *address* to receive ABELs. It is calculated from the abel address and can be used as a shorter alternative to the lengthy abel address. Note that a short address must be registered on the Abelian Name Service (ANS) before it can be used to receive ABELs.

> **Note**
>
> **Serial Number Secret Root Seed** or **View Secret Root Seed** are called *semi-secret seed* because revealing them to untrusted parties will only compromise the privacy of the account. The funds in the account are safe as long as the **Spend Secret Root Seed** is not revealed.
>
> For trusted parties (e.g., an internal Abelian Super Node), the *semi-secret seeds* can be revealed to them to allow them to track the spent coins and the balance of the account. In this sense, they can be treated as **public keys**.
>
> Note that for **Pseudo-private** privacy level, **Serial Number Secret Root Seed** and **View Secret Root Seed** is idle.
>
> In the following description,  all accounts will be pseudo-private.
> For ease of use, `sequence account` is introduced to generate addresses in specified order, i.e., using the same sequence number would generate the same address.
> And more, an account with the **secret seed** (i.e., **Spend Secret Root Seed**) is called a `signer account`; an account without the **secret seed** is called a `viewer account`.


### 2.2. Create, import and export accounts

The `Account` demo shows how to create, import and export accounts:

```bash
bin/abel4j-demo Account
```

### 2.3. Built-in accounts

There are 10 built-in pseudo-private accounts that will be used in the following demos to illustrate the key functionalities required by a non-trivial application (e.g., an exchange wallet). 

### 2.4. Short address

# TODO short address with ANS

## 3. Separate cold data and hot data to support offline signing

### 3.1. Initialize wallet databases

The `WalletDB` demo shows how to initialize wallet databases:

```bash
bin/abel4j-demo WalletDB
```

If you haven't run other demos that may change the databases, the output should be like this:

```text
demoName = WalletDB
demoArgs = []
default.chain = testnet

==> Get table summary of the hot wallet db.
Count of accounts: 10
Count of coins   : 0
Count of txs     : 0

==> Get table summary of the cold wallet db.
Count of accounts: 10
```

The `WalletDB` demo creates two wallet databases in `sqlite3` format under `$HOME/.abel4j`: a *cold database* named `demo-testnet-cold-wallet.db` and a *hot database* named `demo-testnet-hot-wallet.db`. You may use any `sqlite3` GUI client or the `sqlite3` command to view the content of the databases.

### 3.2. Cold Database

The *cold database* stores all seeds and keys of the managed accounts for offline signing. It contains only one table `signer_account`:

```bash
sqlite3 $HOME/.abel4j/demo-testnet-cold-wallet.db
``` 

```sql
sqlite> .tables
signer_account
``` 

```sql
sqlite> .schema signer_account
CREATE TABLE `signer_account` (`accountID` VARCHAR , `chainID` INTEGER , `privacyLevel` INTEGER , `spendSecretRootSeed` VARCHAR , `serialNoSecretRootSeed` VARCHAR , `viewKeyRootSeed` VARCHAR , `detectorRootKey` VARCHAR , `publicRandRootSeed` VARCHAR , PRIMARY KEY (`accountID`) );
```

```sql
sqlite> select * from signer_account;
```

The truncated output of the above query is:

```text
1|2|2|00FADE3E...9538|||91A47A42...DF10|79C8BEBA...96EE
2|2|2|63EC7444...670D|||6A20EAEC...43A8|DDE9A1E2...41AB
3|2|2|65C570E9...0A62|||EE048A5F...7B79|2AD5BDBD...E1B2
4|2|2|1A42337E...67A6|||9CBC88F3...E81C|50CB3EE2...DCB9
5|2|2|1E18580A...CCED|||0AF134DC...0178|C3C9EC23...8C22
6|2|2|BB62BA88...2D71|||9B8F8C80...AD0F|454F0AB2...91AF
7|2|2|DAF3E721...853E|||2D9D6F7A...CF7C|5B00C813...B29A
8|2|2|66F27F34...DCB5|||23AAE0A4...D7FA|AB0ACDB3...8EB4
9|2|2|B38E5176...3324|||D920DED1...97DE|F0689922...F804
10|2|2|E31B79E0...5EFB|||901DEA8F...D877|4EECCF2C...D395
```

In the `signer_account` table, the `accountID` column serves as the account ID.

### 3.3. Hot Database

The *hot database* stores all the data **excluding secret keys** of the managed accounts for online operations such as scanning UTXOs, tracking spent coins, generating unsigned raw transactions, etc. It contains three tables `viewer_account`, `coin` and `tx`:

```bash
sqlite3 ~/.abel4j/demo-testnet-hot-wallet.db
``` 

```sql
sqlite> .tables
coin tx viewer_account
``` 

```sql
sqlite> .schema viewer_account
CREATE TABLE `viewer_account` (`accountID` VARCHAR , `chainID` INTEGER , `privacyLevel` INTEGER , `serialNoKeyRootSeed` VARCHAR , `viewKeyRootSeed` VARCHAR , `detectorRootKey` VARCHAR , PRIMARY KEY (`accountID`) );
```

```sql
sqlite> .schema coin
CREATE TABLE `coin` (`coinIDStr` VARCHAR , `blockHeight` BIGINT , `blockid` VARCHAR , `txVersion` INTEGER , `data` VARCHAR , `value` BIGINT , `ownerAccountID` VARCHAR , `snHex` VARCHAR , `isSpent` BOOLEAN , PRIMARY KEY (`coinIDStr`) );
```

```sql
.schema tx
CREATE TABLE `tx` (`txMd5` VARCHAR , `unsignedRawTxDataHex` VARCHAR , `signerAccountIDs` VARCHAR , `signedRawTxDataHex` VARCHAR , `txid` VARCHAR , `isSubmitted` BOOLEAN , PRIMARY KEY (`txMd5`) );
```

In the `view_account` table, the `accountID` column serves as the account ID and corresponds to the one in `signer_account`.

The `viewer_account` table stores all information but the **secret seed** of the managed accounts. 
The schema of the other two tables will be explained when they are used in the following demos.

## 4. Manage coins in the hot wallet

### 4.1. Search blocks for viewable coins

The `ScanCoins` demo shows how to scan for coins belonging to the accounts managed by the hot wallet:

```bash
bin/abel4j-demo ScanCoins 397470 397475
```

We choose the above range of block heights because we know in advance that they contain some transactions that send ABELs to the built-in accounts. 
The `ScanCoins` demo will scan the blocks in the specified range and store the coins belonging to the managed accounts in the hot wallet database.

Let's check the status of the hot wallet database after running the **ScanCoins** demo:

```bash
bin/abel4j-demo WalletDB
```

The output should be like this:

```text
demoName = WalletDB
demoArgs = []
default.chain = testnet

==> Get table summary of the hot wallet db.
Count of accounts: 10
Count of coins   : 9
Count of txs     : 0

==> Get table summary of the cold wallet db.
Count of accounts: 10
```

We can see that the `ScanCoins` demo has scanned 6 blocks and found 9 coin belonging to the managed accounts. Let's further check the content of the `coin` table:

```bash
sqlite3 ~/.abel4j/demo-testnet-hot-wallet.db
``` 

```sql
sqlite> select coinIDStr, ownerAccountID, value, blockHeight, isSpent from coin;
```

The output of the above query is (truncated):

```text
3B97B7D0...910A:0|7|999900000|397474|0
3B97B7D0...910A:1|6|999900000|397474|0
3B97B7D0...910A:2|4|999900000|397474|0
3B97B7D0...910A:3|9|999900000|397474|0
3B97B7D0...910A:5|2|999900000|397474|0
3B97B7D0...910A:6|8|999900000|397474|0
3B97B7D0...910A:7|1|999900000|397474|0
3B97B7D0...910A:8|0|999900000|397474|0
3B97B7D0...910A:9|5|999900000|397474|0
```

We define `Coin ID` as the combination of the `txid` and the `vout` index of a coin in hex format. The definition ensures that each coin has a unique ID and can be easily located by the ID.

To understand the meaning of the fields, let's take a closer look at the first coin:
it belongs to a built-in account (ID 7), has a value of 99.99 ABELs (999,900,000 Neutrinos), 
is at block 397474, and has not yet been detected as spent.

> **Note**
> 
> We can scan the same blocks multiple times without worrying about duplicate coins. The `ScanCoins` demo will only add new coins to the database and ignore the duplicate ones. You may verify this by running the following commands to see if the number of coins in the hot wallet database changes:

```bash
bin/abel4j-demo ScanCoins 397470 397475
bin/abel4j-demo WalletDB
```

### 4.2. Track spent coins

At this point, the **isSpent** field of all the coins in the hot wallet database is still `0`(`false`) because we haven't scanned the blocks that contain the transactions spending the coins. The `TrackSpentCoins` demo shows how to track the spent coins:

# TODO spent coin here

```bash
bin/abel4j-demo TrackSpentCoins 397700 
```

The output should be like this:

```text
demoName = TrackSpentCoins
demoArgs = [397700]
default.chain = testnet

==> Tracking spent coins in block 397700.

--> Tracking spent coins in tx [32 bytes|0x0B94F7EBE9D06CFAFD9A7E4894D183AFF27E8F0313826A40F4A27002C344580D].

--> Tracking spent coins in tx [32 bytes|0x2E956401AAB1CDD35D1FA415BB0370D114838A65A592A9CA9CED8D991AE11599].
💰 Found spent coin: id=3B97B7D0B2D26D254A546CFBBD312BBB9A88910FA23A24D5AC8D4F3A16D6910A:0, value=999900000.
💰 Found spent coin: id=3B97B7D0B2D26D254A546CFBBD312BBB9A88910FA23A24D5AC8D4F3A16D6910A:1, value=999900000.
```

We can see that there is 2 coin spent in block height 397700. Let's check the `coin` table again:

```bash
sqlite3 ~/.abel4j/demo-testnet-hot-wallet.db
``` 

```sql
sqlite> select coinIDStr, blockHeight, isSpent from coin;
```

The output of the above query is:

```text
3B97B7D0...910A:0|397474|1
3B97B7D0...910A:1|397474|1
3B97B7D0...910A:2|397474|0
3B97B7D0...910A:3|397474|0
3B97B7D0...910A:5|397474|0
3B97B7D0...910A:9|397474|0
3B97B7D0...910A:6|397474|0
3B97B7D0...910A:7|397474|0
3B97B7D0...910A:8|397474|0
```

We can see there is 2 coins previously found in block height `397474` are now marked as spent while the last (`isSpent`) field of the other coins remain unchanged.

Again, here we choose the `397700` as the tracking heights because we know in advance that there is a consumption transaction at that height.
Note that, for a regular transaction, there is usually a change output. More that in order to keep the demo running, 
we point the outputs of that transaction to the address of the built-in account as well.

Let's scan this block `397700` to get the change coin and then check the status of the hot wallet database:

```bash
bin/abel4j-demo ScanCoins 397700 397700
bin/abel4j-demo WalletDB
```

The output should be like this:

```text
demoName = WalletDB
demoArgs = []
default.chain = testnet

==> Get table summary of the hot wallet db.
Count of accounts: 10
Count of coins   : 14
Count of txs     : 0

==> Get table summary of the cold wallet db.
Count of accounts: 10
```

it's worth mentioning that, for a formal application, these two demo can be performed with the specified block at the same time, i.e., tracking spent coins in a block while scanning new coins in the same block.

## 5. Make a transaction

Making a transaction is non-trivial task. It may involve many steps:
- determining the outputs
- selecting coins to spend
- determining the transaction fee
- calculating the change
- generating the raw transaction
- signing the raw transaction
- broadcasting the signed transaction, etc.

Based on our previous work, the complete transaction generation is divided into three demos:
- firstly, the `GenerateUnsignedRawTx` demo show how to generate an unsigned transaction 
- then, the `GenerateSignedRawTx` demo show how to sign the unsigned transaction generated by the `GenerateUnsignedRawTx` demo
- lastly, the `SubmitSignedRawTx` demo shows how to broadcast the signed raw transaction generated by the `GenerateSignedRawTx` demo

Let's dive into the details of those demos now.

### 5.1. Generate an unsigned raw transaction

The `GenerateUnsignedRawTx` demo shows how to generate an unsigned raw transaction:

```bash
bin/abel4j-demo GenerateUnsignedRawTx
```

> **Note**
>
> Please record the value of `txMd5` in the output for later use.

It will generate an unsigned raw transaction with 2 inputs and 2 outputs. The input coins are selected from the coins in the hot wallet database where the **isSpent** field is `0`(`false`). The output coins are generated by the demo itself. Specifically, the receiver addresses are randomly chosen from the built-in accounts and the total output value is calculated by summing up the values of the input coins and subtracting the transaction fee from it.

The generated unsigned raw transaction will be saved in the `tx` table of the hot wallet database. Let's check the status of the hot wallet database after running the `GenerateUnsignedRawTx` demo:

```bash
bin/abel4j-demo WalletDB
```

The output should be like this:

```text
demoName = WalletDB
demoArgs = []
default.chain = testnet

==> Get table summary of the hot wallet db.
Count of accounts: 10
Count of coins   : 14
Count of txs     : 1

==> Get table summary of the cold wallet db.
Count of accounts: 10
```

Note that the count of records in the `tx` table is `1` now. Let's check the content of the `tx` table:

```bash
sqlite3 ~/.abel4j/demo-testnet-hot-wallet.db
``` 

```sql
sqlite> select txMd5, length(unsignedRawTxDataHex), length(signedRawTxDataHex), isSubmitted from tx;
```

The output of the above query is like this (you may see different values):

```text
0430B1D076C9671622F3378E7056A8C7|2356||0
```

If recorded the value of `txMd5` in the output of the `GenerateUnsignedRawTx` demo, 
we can verify that it is the same as the value in the `tx` table. 
We can also see that the value of `unsignedRawTxDataHex` is not empty and the value of `signedRawTxDataHex` is empty. This is because the signed raw transaction is not generated yet.

### 5.2. Sign the unsigned raw transaction

The `GenerateSignedRawTx` demo shows how to sign the unsigned raw transaction generated by the `GenerateUnsignedRawTx` demo:

```bash
bin/abel4j-demo GenerateSignedRawTx 0430B1D076C9671622F3378E7056A8C7
```

The output should be like this (it's unlikely to be the same as yours):

```text
demoName = GenerateSignedRawTx
demoArgs = [0430B1D076C9671622F3378E7056A8C7]
default.chain = testnet

==> Generating a signed raw tx with txMd5: 0430B1D076C9671622F3378E7056A8C7

==> Getting unsigned raw tx data from the hot wallet db.
Got unsigned raw tx: txMd5=0430B1D076C9671622F3378E7056A8C7, data=[1178 bytes|0x02EC983A8B54638AEDC1FEE2C5B9A7FD...C11B2561CCFE40A2913BFE40420F0000], signers=2.

==> Getting signer accounts from the cold wallet db.
Got 2 signer accounts:
Signer 5 with detector root key 0AF134DC637A7AFA7CC1F64B80DC98B770F093C0534914FF6C6F888E581F50F549EA6D9EF402C3567E5E288C45F5B30D103942A7FDF3B5133B4E43D13B4B0178
Signer 10 with detector root key 901DEA8FC5CF771F340BCBF446435A4696FD38E0BD52105F235B221EDCC33773B80B4B6311024F2BA3CF646925566D2207426A9B560E3E127FE340C8148ED877

==> Signing the unsigned raw tx.
Signed raw tx: txid=3C521B53FBE07F3CD5122CCE26814F498BDEBD1F7870FA0105715DB0F0F7E01B, data=[38349 bytes|0x020000000240B6295B1459E354365CD1...E3B522B0AD019CB7E40BD5760A640100].

==> Saving the signed raw tx to the hot wallet db.
```

We pass the `txMd5` as the argument to the demo. It will be used to find the unsigned raw transaction in the `tx` table. The demo will sign the transaction and save the signed data back to the `tx` table.

Let's check the status of the hot wallet database after running the `GenerateSignedRawTx` demo:

```bash
sqlite3 ~/.abel4j/demo-testnet-hot-wallet.db
``` 

```sql
sqlite> select txMd5, length(unsignedRawTxDataHex), length(signedRawTxDataHex), isSubmitted from tx;
```

Now the output of the above query is like this (you may see different values):

```text
0430B1D076C9671622F3378E7056A8C7|2356|76698|0
```

We can see that the value of `signedRawTxDataHex` is not empty now.

> **Note**
>
> The transaction was signed by the **Spend Secret Root Reed**, which stored in the cold wallet database, of the sender-accounts (who owns the input coins). 
> The  **Spend Secret Root Reed** should be never exposed to the public network. 
> This is how the cold wallet database protects the private keys from being stolen.

### 5.3. Broadcast the signed raw transaction

The last step is to broadcast the signed raw transaction to the Abelian network.
The `SubmitSignedRawTx` demo shows how to broadcast the signed raw transaction generated by the `GenerateSignedRawTx` demo:

```bash
bin/abel4j-demo SubmitSignedRawTx 0430B1D076C9671622F3378E7056A8C7
```

If the transaction is successfully submitted, please record the value of `txid` in the output and search it 
on the [Abelian Explorer for Mainnet](https://explorer.pqabelian.io) or [Abelian Explorer for Testnet](https://testnet-explorer.pqabelian.io/)
to see if it is confirmed. 
You may also check the `tx` table to see if the `isSubmitted` field of the transaction is set to `1`(`true`).

If the transaction fails to be submitted, please check the error message in the output. 
The most common error is that one or more coins in the inputs are spent before the transaction is submitted. 
If you know at which block height the coins are spent, you can run the `TrackSpentCoins` demo to update 
the `isSpent` field of the coins in the hot wallet database and try to make another unsigned raw transaction, 
sign it and submit it again.

## 6. Summary

In this document, we have learned how to install the Abelian SDK for Java, test if it is working properly, understand the concepts of account in the Abelian SDK, separate cold data and hot data to support offline signing, manage coins in the hot wallet and make a transaction with the crafted demos.

There are other demos in the SDK that are not covered in this document. You may run them yourself to see the results. 
You may also read the source code of the demos to learn how to use the SDK to develop your own applications.