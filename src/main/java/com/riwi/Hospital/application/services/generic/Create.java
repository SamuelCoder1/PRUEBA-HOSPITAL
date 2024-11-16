package com.riwi.Hospital.application.services.generic;

public interface Create<Entity, EntityRequest> {
    public Entity create(EntityRequest entityRequest);
}
