package org.acme.saas.service;

import io.quarkus.hibernate.reactive.panache.common.runtime.ReactiveTransactional;
import io.smallrye.mutiny.Uni;
import org.acme.saas.model.Request;
import org.acme.saas.model.data.RequestChangeData;
import org.acme.saas.model.draft.RequestDraft;
import org.acme.saas.model.mappers.RequestMapper;

import javax.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class RequestService {

    @ReactiveTransactional
    public Uni<Request> createNewRequest(RequestDraft requestDraft) {
        Request request = RequestMapper.INSTANCE.requestDraftToRequest(requestDraft);
        return request.persist();
    }

    @ReactiveTransactional
    public Uni<List<RequestChangeData>> getRequestChangeData() {
        return Request.getRequestChangeData();
    }
}
