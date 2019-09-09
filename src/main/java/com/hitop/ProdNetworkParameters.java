package com.hitop;

import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.params.MainNetParams;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("prod")
public class ProdNetworkParameters implements com.hitop.NetworkParameters {
  @Override
  public NetworkParameters getNetworkParameters() {
    return MainNetParams.get();
  }
}
