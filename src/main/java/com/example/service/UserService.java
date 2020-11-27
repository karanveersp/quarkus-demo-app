package com.example.service;

import com.example.model.User;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class UserService {
    @Inject
    Validator validator;

    @Inject
    EntityManager em;

    public List<User> getUsers() {
        return em.createNamedQuery("Users.findAll", User.class).getResultList();
    }

    public boolean isUniqueUsername(String username) {
        try {
            em.createNamedQuery("Users.findByName", User.class)
                .setParameter("username", username)
                .getSingleResult();
            return false;  // found
        }
        catch (NoResultException notFound) {
            return true;
        }
    }

    public void create(User u) {
        em.persist(u);
    }

    public String validateUser(User u) {
        var violations = validator.validate(u);
        List<String> messages = new ArrayList<>(Collections.emptyList());
        if (!violations.isEmpty()) {
            messages.addAll(violations.stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.toList()));
        }
        if (!isUniqueUsername(u.getUsername())) {
            messages.add("Username already exists");
        }
        return messages.isEmpty() ? null : String.join(", ", messages);
    }
}
