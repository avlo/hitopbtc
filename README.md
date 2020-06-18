# HiTopBPG (HiTop Bitcoin Payment Gateway)

### overview
HiTopBPG is a completely self-contained Bitcoin Payment Gateway framework and store-front web-application. It uses no custodial or third party wallet/blockchain services (excluding exchange rate conversion and QR code generation). It has been specifically built with all business logic, bitcoin transaction logic & wallet custodianship completely within the application itself.

### motivation
after having been denied payment gateway services by paypal and others and not finding an existing, open-source, free, self-custodial bitcoin transaction/wallet service with the features i wanted, i decided to build my own.  it's intended to be a simple (two page) store-front web-application with self-contained bitcoin transaction engine and user-custodian'd wallet.

### tools used
as of this writing: 
|tool|version|
|---:|---|
|JAVA OpenJDK|[1.9](https://openjdk.java.net/install/)|
|Spring Boot|[2.18](http:// "in pom.xml")|
|BitcoinJ|[0.15.7](http:// "in pom.xml")|
|MySql|[8.0.11](http:// "in docker-compose.yml")|
|Docker (optional)|19.03.11|
|Docker Compose (optional)|1.17.1|

### requirements
uses 3rd party service to fetch current exchange rate.  currently configured (via application.properties) to use bitpay.com.  after downloading bitpay security certificate, run command:

```
$ keytool -import -trustcacerts -file </location/of/downloaded/bitpay.cer> -alias <any_alias> -keystore </location/of/java/lib/security/cacerts>
```
to install certificate into your application.

### customization instructions
* in file `src/main/resources/application.properties`, update  ```productname``` value to be your product name.
* in file `src/main/resources/application.properties`, update  ```unit.price.usd``` value to be your product price in USD.
* save your index page image as `src/main/resources/public/images/index.jpg`
* save your order page image as `src/main/resources/public/images/order.jpg`
### development build instructions
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

### docker build (optional)
to build an HiTopBPG docker image, use command:
```
$ docker build  -t <image_name>:<tag_name> dir
```
or
```
$ docker build  -t <image_repo>/<image_name>:<tag_name> dir
```
and can be customized via `Dockerfile` in project root directory
### docker deployment (optional)
to deplay an HiTopBPG container (along with a separate mysql volume container for data storage), use command:
```
$ docker-compose up
```
environment variables can be configured via `docker-compose.yml` in project root directory
### intended audience
- open source to download, modify, customize, fork and deploy as you like.  licenced under Apache Software Foundation LICENSE-2.0
- i encourage usage and contribution by others in the community, new features & PR's (pull requests) very much welcome and appreciated.
- seeking those interested to help grow, improve framework in the usual/celebrated open source ASF/2.0 spirit.

### application use
once you've started HiTopBPG (either via `mvn spring-boot:run` or `docker-compose up`), open a web browser to `http://localhost:8080`

### application state
***note:  DO NOT SENT REAL BITCOIN TO THE HiTopBPG DEMO/TEST APPLICATION!!!  DOING SO WILL LOSE YOUR REAL BITCOIN!!!***

bitcoin ***TEST*** network demonstation version running at http://hitoplids.com:8080/ 

##### current functionality
- as of this writing, HiTopBPG currently can complete a full bitcoin payment transaction on the bitcoin test network.  - configuration exists to run on bitcoin main network, but hasn't been run there (yet).
- uses HD wallet with newly generated child key for each order
- currency abstraction layer.  currently supports bitcoin, but API/interface exists for (any) currency extension.
- graphic images completely customizable via application.properties file
##### known limitations / bugs
- for ease of user testing, all but two entity bean fields (name and btcaddress) have been commented out.  users can uncomment remaining fields and include them in controller logic & html/thymeleaf template as needed
- more unit tests coming (TDD... yes, yes, i know 💩)
- html/javascript/css ~50% culled to remove unneccary libs.  more cleanup there coming soon.
- no javadoc yet, but in the meanwhile (hopefully sufficient) OO design for developer ease of understanding what's going on and where.
- non-show stopping TODO's annotated in various places throughout codebase. 
- various other "non-show-stopping" minutia, to be addressed moving forward
##### next items on deck, in order:
1. unit tests / functional test
2. general cleanup
3. lightning network support
