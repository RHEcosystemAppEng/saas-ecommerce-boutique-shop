package org.acme.saas.service;

import io.quarkus.hibernate.reactive.panache.common.runtime.ReactiveTransactional;
import io.smallrye.mutiny.Uni;
import org.acme.saas.model.Request;
import org.acme.saas.model.data.RequestChangeData;
import org.acme.saas.model.draft.RequestDraft;
import org.acme.saas.model.mappers.RequestMapper;
import org.acme.saas.repository.RequestRepository;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;

@ApplicationScoped
public class RequestService {

    @Inject
    RequestRepository repository;

    @ReactiveTransactional
    public Uni<Request> createNewRequest(RequestDraft requestDraft) {
        Request request = RequestMapper.INSTANCE.requestDraftToRequest(requestDraft);
        return repository.persist(request);
    }

    @ReactiveTransactional
    public Uni<List<RequestChangeData>> getRequestChangeData() {
        return repository.getRequestChangeData();
    }
}
