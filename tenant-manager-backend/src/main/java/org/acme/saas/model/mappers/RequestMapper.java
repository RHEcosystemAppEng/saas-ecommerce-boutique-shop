package org.acme.saas.model.mappers;

import org.acme.saas.model.Request;
import org.acme.saas.model.draft.RequestDraft;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface RequestMapper {
    RequestMapper INSTANCE = Mappers.getMapper(RequestMapper.class);
    Request requestDraftToRequest(RequestDraft draft);
}
