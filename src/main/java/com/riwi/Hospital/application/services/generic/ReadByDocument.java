package com.riwi.Hospital.application.services.generic;

import javax.swing.text.html.parser.Entity;
import java.util.Optional;

public interface ReadByDocument<Entity, DOCUMENT>{
    public Optional<Entity> readByDocument(DOCUMENT document);
}
