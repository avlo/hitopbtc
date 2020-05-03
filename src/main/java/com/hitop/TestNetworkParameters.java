package com.hitop;

import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.params.TestNet3Params;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("test")
public class TestNetworkParameters implements com.hitop.NetworkParameters {
  final Logger logger = LoggerFactory.getLogger(TestNetworkParameters.class);
  @Override
  public NetworkParameters getNetworkParameters() {
    logger.info("using {} network.", TestNet3Params.ID_TESTNET);
    return TestNet3Params.get();
  }
}
