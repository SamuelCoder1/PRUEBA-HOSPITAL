package com.riwi.Hospital.domain.ports.service;

import com.riwi.Hospital.application.dtos.requests.UserWithoutId;
import com.riwi.Hospital.application.services.generic.ReadByDocument;
import com.riwi.Hospital.application.services.generic.Register;
import com.riwi.Hospital.domain.entities.User;

public interface IUserService extends
        Register<User, UserWithoutId>,
        ReadByDocument<User, String> {
}
