package org.acme.saas.model.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class SummaryData {

    private String tier;
    private long totalSubscriptions;
    private long aggregatedShoppers;
}
