package com.riwi.Hospital.infrastructure.persistence;

import com.riwi.Hospital.domain.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByDocument(String document);

    boolean existsByDocument(String document);
}
