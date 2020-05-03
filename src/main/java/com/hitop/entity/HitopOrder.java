package com.hitop.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import org.springframework.stereotype.Component;
import lombok.Data;

@Data
@Entity // This tells Hibernate to make a table out of this class
@Component
public class HitopOrder {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;
    private String name;
    private String email;
//    private String address;
//    private String city;
//    private String state;
//    private String zip;
//    private String country;
    
    // HD wallet public key shown to user
    private String btcPublicKey;
    
//    private String btcTransaction;
    private Double btcRate;
    private Double btcUsdAmount;
    
//    private Integer status;
}
