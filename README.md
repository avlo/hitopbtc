# hitopbtc

to build project, use command:

```
$ mvn clean install
```

then execute spring boot jar using command:

```
$ java -jar target/hitop-0.1.0.jar
```

console will display application startup logs as well as bitcoinj setup logs.  files: 

```
monitor-service-testnet
monitor-service-testnet.spvchain
monitor-service-testnet.wallet
```

will be created which are used by bitcoinj for spvchain and wallet state.  once spvchain load is completed (100% displayed), the application is ready for use (on bitcoin testnet).  

any testnet bitcoin payment sent to payment page HD wallet address is registered on the bitcoin network, then displayed in the console.  

> note: a unique HD wallet address is generated for each QR code displayed. each of these addresses is a child address of the parent HD wallet address, such that all child address payments are accumulated in the parent HD wallet address.
