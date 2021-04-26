package com.hitop.service.iota;

import org.iota.jota.IotaAPI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("test")
public class IotaTestNetworkParameters implements IotaNetworkParameters {
  private final static Logger log = LoggerFactory.getLogger(IotaTestNetworkParameters.class);
  
  private final String network;
  
  public IotaTestNetworkParameters(final @Value("${iota.network}") String network) {
    this.network = network;
    log.info("using network: {}", this.network);
  }
  
  @Override
  public IotaAPI getIotaAPI() {
    return new IotaAPI.Builder()
    .protocol("https")
    .host(network)
    .port(443)
    .build();
  }
}
