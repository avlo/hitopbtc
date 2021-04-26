package com.hitop.service.iota;

import org.iota.jota.IotaAPI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("prod")
public class IotaProdNetworkParameters implements IotaNetworkParameters {
  private final static Logger log = LoggerFactory.getLogger(IotaProdNetworkParameters.class);
  @Override
  public IotaAPI getIotaAPI() {
    return new IotaAPI.Builder()
    .protocol("https")
    .host("nodes.prod.thetangle.org")
    .port(443)
    .build();
  }
}
