package hitop;

import java.io.File;
import java.io.IOException;
import org.bitcoinj.core.Coin;
import org.bitcoinj.core.Transaction;
import org.bitcoinj.core.TransactionConfidence;
import org.bitcoinj.wallet.Wallet;
import org.bitcoinj.wallet.listeners.WalletCoinsReceivedEventListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.MoreExecutors;

@Service
public class BtcWalletCoinsReceivedService implements WalletCoinsReceivedEventListener {

  private static final String FILE_PREFIX = "monitor-service-testnet";

  @Autowired
  WalletTransactionService walletTransactionService;
  
  @Override
  public void onCoinsReceived(Wallet wallet, Transaction tx, Coin prevBalance, Coin newBalance) {
    // Runs in the dedicated "user thread" (see bitcoinj docs for more info on this).
    //
    // The transaction "tx" can either be pending, or included into a block (we didn't see the broadcast).
    try {
      File file = new File(FILE_PREFIX);
      System.out.println(String.format("saving file [%s]...", file.toString()));
      wallet.saveToFile(file);
      System.out.println(String.format("[%s] saved.", file.toString()));
    } catch (IOException e) {
      System.out.println("SAVE FAILED");
      e.printStackTrace();
    }

    walletTransactionService.addNewOrder();

    Coin value = tx.getValueSentToMe(wallet);
    System.out.println("111111111111111");
    System.out.println("111111111111111");
    System.out.println("111111111111111");
    System.out.println("Received tx for " + value.toFriendlyString() + ": " + tx);
    System.out.println("new value: " + wallet.getBalance().getValue());
    System.out.println("---");
    System.out.println("coin prev balance : " + prevBalance.toFriendlyString());
    System.out.println("coin new balance : " + newBalance.toFriendlyString());
    // Wait until it's made it into the block chain (may run immediately if it's already there).
    //
    // For this dummy app of course, we could just forward the unconfirmed transaction. If it were
    // to be double spent, no harm done. Wallet.allowSpendingUnconfirmedTransactions() would have to
    // be called in onSetupCompleted() above. But we don't do that here to demonstrate the more common
    // case of waiting for a block.
    Futures.addCallback(tx.getConfidence().getDepthFuture(1), new FutureCallback<TransactionConfidence>() {
      @Override
      public void onSuccess(TransactionConfidence result) {
        System.out.println("2222222222222222");
        System.out.println("2222222222222222");
        System.out.println("2222222222222222");
        System.out.println("Confirmation received.");
      }

      @Override
      public void onFailure(Throwable t) {
        // This kind of future can't fail, just rethrow in case something weird happens.
        throw new RuntimeException(t);
      }
    }, MoreExecutors.directExecutor());
  }
}
