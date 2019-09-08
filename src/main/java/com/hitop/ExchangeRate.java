package com.hitop;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ExchangeRate {
  
    private String name;
    private String code;
    private Double rate;

    //  private String last;
//  private String buy;
//  private String sell;
//  private String symbol;
}


