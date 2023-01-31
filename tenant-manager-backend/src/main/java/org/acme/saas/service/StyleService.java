package org.acme.saas.service;

import io.quarkus.hibernate.reactive.panache.common.runtime.ReactiveTransactional;
import io.smallrye.mutiny.Uni;
import org.acme.saas.model.Style;
import org.acme.saas.model.draft.StyleDraft;
import org.acme.saas.model.mappers.StyleMapper;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class StyleService {


    @ReactiveTransactional
    public Uni<Style> createNewStyle(StyleDraft styleDraft) {

        Style style = StyleMapper.INSTANCE.styleDraftToStyle(styleDraft);
        return style.persist();
    }
}
