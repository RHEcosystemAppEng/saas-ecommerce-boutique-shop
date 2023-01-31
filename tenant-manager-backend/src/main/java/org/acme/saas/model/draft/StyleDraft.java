package org.acme.saas.model.draft;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class StyleDraft {

    @NonNull
    private String tenantKey;
    private String headingText;
    private String headingColor;
    private String ribbonColor;
}
