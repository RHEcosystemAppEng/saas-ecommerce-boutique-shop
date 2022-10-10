package org.acme.saas.model.mappers;

import org.acme.saas.model.Subscription;
import org.acme.saas.model.draft.SubscriptionDraft;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface SubscriptionMapper {
    SubscriptionMapper INSTANCE = Mappers.getMapper(SubscriptionMapper.class);
    Subscription subscriptionDraftToSubscription(SubscriptionDraft draft);

    SubscriptionDraft subscriptionToSubscriptionDraft(Subscription subscription);
}
