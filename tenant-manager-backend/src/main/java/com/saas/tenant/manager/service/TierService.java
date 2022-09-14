package com.saas.tenant.manager.service;

import org.springframework.stereotype.Service;

@Service
public class TierService {

    public double calculatePrice(String tier, int avgNumOfShoppers, int peakNumOfShoppers, int hours) {
        switch (tier) {
            case "free":
                return 0.0;
            case "silver" :
                return (avgNumOfShoppers/100.0)*10;
            case "gold" :
                return (avgNumOfShoppers/100.0)*20;
        }

        return 0.0;
    }
}
