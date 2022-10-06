package com.saas.tenant.manager.service;

import org.springframework.stereotype.Service;

@Service
public class TierService {

    public double calculatePrice(String tier, int avgNumOfShoppers, int peakNumOfShoppers, int hours) {
        switch (tier) {
            case "Free":
                return 0.0;
            case "Silver":
                return (avgNumOfShoppers / 100.0) * 10;
            case "Gold":
                return (avgNumOfShoppers / 100.0) * 20;
        }

        return 0.0;
    }

    public int[] calculateInstanceCount(String tier, int avgNumOfShoppers, int peakNumOfShoppers) {
        int min = avgNumOfShoppers / 50;
        int max = peakNumOfShoppers / 50;

        return new int[]{min, max};
    }
}
