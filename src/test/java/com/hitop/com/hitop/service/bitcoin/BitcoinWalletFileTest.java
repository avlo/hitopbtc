package com.hitop.service.bitcoin;

/*
 *  Copyright 2020 Nick Avlonitis
 *
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */    

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import java.io.File;
import java.io.IOException;
import org.bitcoinj.wallet.Wallet;
import org.junit.Before;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

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
