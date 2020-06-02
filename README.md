# HiTopBPG (HiTop Bitcoin Payment Gateway)

### overview
HiTopBPG is a completely self-contained Bitcoin Payment Gateway web-application and framework. It uses no custodial or third party wallet/blockchain services (excluding exchange rate conversion and QR code generation). It has been specifically built with all business logic, bitcoin transaction logic & wallet custodianship completely within the application itself.

### motivation
after having been denied payment gateway services by paypal and others and not finding an existing, open-source, free, self-custodial bitcoin transaction/wallet service with the features i wanted, i decided to build my own.  it's intended to be a simple (two page) webapplication with bitcoin transaction engine and user-custodian'd wallet underneath.  

### tools used
as of this writing: 
- JAVA 1.9
- Spring 2.1.8
- BitcoinJ 0.15.2
- MySql 8.0.11

### requirements
uses 3rd party service to fetch current exchange rate.  currently configured (via application.properties) to use bitpay.com.  after downloading bitpay security certificate, run command:

```
$ keytool -import -trustcacerts -file </location/of/downloaded/bitpay.cer> -alias <any_alias> -keystore </location/of/java/lib/security/cacerts>
```
to install certificate into your application.
### build instructions
to build web-app, run command:
```
$ mvn clean install
```

or build and execute directly using command:

```
$ mvn spring-boot:run
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
Chain download 100% done with 0 blocks to go, block date 2019-09-02T07:35:47Z
End of sync detected.
TomcatWebServer  : Tomcat started on port(s): 8080 (http) with context path ''
```

indicating application is ready for use (by default, on bitcoin testnet).  any bitcoin payment made (to payment page HD wallet address) will be registered on the bitcoin network then displayed both on server console and in browser window.

> note: a unique bitcoin address is generated for each QR code displayed on the payment page. each of these addresses is a child address of the parent HD wallet address, such that all payments are accumulated in the parent HD wallet.

### eclipse/intellij developers

HiTopBPG uses project lombok annotations.  run the following command from project root prior to IDE startup to properly configure either IDE for lombok usage.

```
$ java -jar lib/lombok.jar
```

and follow steps as indicated

### ethos
- open source to download, modify, customize, fork and deploy as you like.
- encourage usage and contribution by others in the community, new features & PR's (pull requests) very much welcome and appreciated.
- seeking those interested to help grow, improve framework  in the usual/celebrated open source (GPL 2.0) spirit.

### application state
##### current functionality
- as of this writing, HiTopBPG currently can complete a full bitcoin payment transaction on the bitcoin test network.  - configuration exists to run on bitcoin main network, but hasn't been run there (yet).
- uses HD wallet with newly generated child keys for each order
- currency abstraction layer.  currently supports bitcoin, but API/interface exists for (any) currency extension.
- graphic images completely customizable via application.properties file
##### known limitations / bugs
- for ease of user testing, all but two entity bean fields (name and btcaddress) have been commented out.  users can uncomment remaining fields and include them in controller logic & html/thymeleaf template as needed
- no unit tests, bad developer... but they're coming (i know, TDD...)
- html/javascript/css ~50% culled to remove unneccary libs.  more cleanup there coming soon.
- no javadoc yet, but in the meanwhile hopefully sufficient OO design of understanding what's going on and where.
- non-show stopping TODO's annotated in various places throughout codebase. 
- Dockerfile / modular framework pending (currently, docker isn't necessary to run application as-is)
- various other "non-show-stopping" minutia, to be addressed moving forward