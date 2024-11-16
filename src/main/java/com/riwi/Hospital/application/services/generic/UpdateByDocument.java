package com.riwi.Hospital.application.services.generic;

public interface UpdateByDocument<DOCUMENT, Entity, EntityRequest> {
    public Entity updateByDocument(DOCUMENT document, EntityRequest entity);
}
