# hitopbtc

to build project, use command:

```
$ mvn clean install
```

then execute spring boot jar using command:

```
$ java -jar target/hitop-0.1.0.jar
```

webserver on localhost:8080 will be started and both web-app & bitcoin testnet logs will echo to console.  


bitcoinj and spvchain files: 

```
monitor-service-testnet
monitor-service-testnet.spvchain
monitor-service-testnet.wallet
```

will be created and once spvchain load is completed, console will display the following:

```
Chain download 96% done with 5086 blocks to go, block date 2019-07-30T03:57:44Z
2019-09-02 16:01:38.743  INFO 12905 --- [ioClientManager] o.b.c.listeners.DownloadProgressTracker  : Chain download 100% done with 0 blocks to go, block date 2019-09-02T07:35:47Z
2019-09-02 16:01:39.286  INFO 12905 --- [eerGroup Thread] c.PeerGroup$ChainDownloadSpeedCalculator : End of sync detected.
2019-09-02 16:01:42.399  INFO 12905 --- [           main] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat started on port(s): 8080 (http) with context path ''
```

indicating application is ready for use (on bitcoin testnet).  any bitcoin payment made (to payment page HD wallet address) will be registered on the bitcoin network, then displayed in the console.  

> note: a unique bitcoin address is generated for each QR code displayed on the payment page. each of these addresses is a child address of the parent HD wallet address, such that all payments are accumulated in the parent HD wallet.
