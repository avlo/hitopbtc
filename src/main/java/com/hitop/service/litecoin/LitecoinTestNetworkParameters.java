package com.hitop.service.litecoin;

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

import org.litecoinj.core.NetworkParameters;
import org.litecoinj.params.TestNet3Params;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("test")
public class LitecoinTestNetworkParameters implements LitecoinNetworkParameters {
  private final static Logger log = LoggerFactory.getLogger(LitecoinTestNetworkParameters.class);
  @Override
  public NetworkParameters getNetworkParameters() {
    log.info("using {} network.", TestNet3Params.ID_TESTNET);
//  TODO: looks like TestNet4 needs to be used
//  or try adding below
//  104.236.211.206
//  66.178.182.35
//  to TestNet3Params
    return TestNet3Params.get();
  }
}
