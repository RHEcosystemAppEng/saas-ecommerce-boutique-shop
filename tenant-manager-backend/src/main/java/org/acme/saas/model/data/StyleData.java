package org.acme.saas.model.data;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StyleData {
    private String tenantKey;
    private String headingText;
    private String headingColor;
    private String ribbonColor;
}
