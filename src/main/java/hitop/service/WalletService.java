package hitop.service;

import java.io.File;
import org.bitcoinj.core.LegacyAddress;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.kits.WalletAppKit;
import org.bitcoinj.params.TestNet3Params;
import org.bitcoinj.utils.BriefLogFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WalletService {
  final Logger logger = LoggerFactory.getLogger(WalletService.class);

  private WalletAppKit kit;
  private NetworkParameters params;

  private static final String BITCOIN_DIGIT_FORMAT = "%.9f";
  private static final String FILE_PREFIX = "monitor-service-testnet";
  private static final String QR_URL = "https://chart.googleapis.com/chart?chs=250x250&cht=qr&chl=bitcoin:%s?amount=%s";
  
  @Autowired
  RateService btcRateService;
  
  @Autowired
  CoinService btcWalletCoinsReceivedService;

  public WalletService() throws Exception {
    // This line makes the log output more compact and easily read, especially when using the JDK log adapter.
    BriefLogFormatter.init();

    // Figure out which network we should connect to. Each one gets its own set of files.
    //    if (args.length == 1 && args[1].equals("mainnet")) {
    //      params = MainNetParams.get();
    //      filePrefix = "monitor-service-mainnet";
    //    } else {
    params = TestNet3Params.get();
    //    }

    // Start up a basic app using a class that automates some boilerplate.
    kit = new WalletAppKit(params, new File("."), FILE_PREFIX) {
      @Override
      protected void onSetupCompleted() {
        // Don't make the user wait for confirmations for now, as the intention is they're sending it
        // their own money!
        kit.wallet().allowSpendingUnconfirmedTransactions();
      }
    };

    // Download the block chain and wait until it's done.
    kit.startAsync();
    kit.awaitRunning();
  }

  public void monitorReceiveEvent() throws Exception {
    // We want to know when we receive money.
    kit.wallet().addCoinsReceivedEventListener(btcWalletCoinsReceivedService);

    //    try {
    //      Thread.sleep(Long.MAX_VALUE);
    //    } catch (InterruptedException ignored) {}
  }

  public String qrCodeApi() {
    String sendToAddress = LegacyAddress.fromKey(params, kit.wallet().currentReceiveKey()).toString();
    String btcValue = String.format(BITCOIN_DIGIT_FORMAT, btcRateService.getUsdtoBtc(btcRateService.getBtcRate()));
    String qrCodeApi = String.format(QR_URL, sendToAddress, btcValue);

    logger.info("Send coins to: {} ", sendToAddress);
    logger.info("QR-Code URL: {}", qrCodeApi);
    logger.info("Waiting for coins to arrive. Press Ctrl-C to quit.");

    return qrCodeApi;
  }

}
