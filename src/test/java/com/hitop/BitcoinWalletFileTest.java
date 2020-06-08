package com.hitop.service.bitcoin;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import java.io.File;
import java.io.IOException;
import org.bitcoinj.wallet.Wallet;
import org.junit.Before;
import org.junit.Rule;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;

@RunWith(MockitoJUnitRunner.class)
class BitcoinWalletFileTest {
  private static final String FILE_NAME = "BitcoinWalletFileTest";
  
  @Mock
  Wallet walletMock;
  
  BitcoinWalletFile bitcoinWalletFile;
  
  @Before
  void before() {
    walletMock = mock(Wallet.class);
  }
  
  @BeforeEach
  void beforeEach() {
    bitcoinWalletFile = new BitcoinWalletFile(FILE_NAME);
    MockitoAnnotations.initMocks(this);
  }

  @Test
  void testSaveToFile() throws IOException {
    Mockito.doNothing().when(walletMock).saveToFile(Mockito.any(File.class));
    
    bitcoinWalletFile.saveToFile(walletMock);
  }

  @Test
  void testSaveToFileFailed() throws IOException {
    Mockito.doThrow(new Error()).when(walletMock).saveToFile(Mockito.any(File.class));

    Assertions.assertThrows(Error.class, () -> {
      bitcoinWalletFile.saveToFile(walletMock);
    });
  }

  @Test
  void testGetFilePrefix() {
    assertEquals(bitcoinWalletFile.getFilePrefix(), FILE_NAME);
  }
}
