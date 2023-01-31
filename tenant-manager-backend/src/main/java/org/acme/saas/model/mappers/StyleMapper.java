package org.acme.saas.model.mappers;

import org.acme.saas.model.Request;
import org.acme.saas.model.Style;
import org.acme.saas.model.draft.RequestDraft;
import org.acme.saas.model.draft.StyleDraft;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface StyleMapper {
    StyleMapper INSTANCE = Mappers.getMapper(StyleMapper.class);

    Style styleDraftToStyle(StyleDraft draft);

    StyleDraft styleToStyleDraft(Style request);
}
