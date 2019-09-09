package com.hitop;

import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.params.MainNetParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("prod")
public class ProdNetworkParameters implements com.hitop.NetworkParameters {
  final Logger logger = LoggerFactory.getLogger(ProdNetworkParameters.class);
  @Override
  public NetworkParameters getNetworkParameters() {
    logger.info("using {} network.", MainNetParams.ID_MAINNET);
    return MainNetParams.get();
  }
}
