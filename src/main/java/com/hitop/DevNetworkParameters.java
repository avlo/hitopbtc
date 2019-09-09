package com.hitop;

import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.params.TestNet3Params;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("dev")
public class DevNetworkParameters implements com.hitop.NetworkParameters {
  @Override
  public NetworkParameters getNetworkParameters() {
    return TestNet3Params.get();
  }
}
