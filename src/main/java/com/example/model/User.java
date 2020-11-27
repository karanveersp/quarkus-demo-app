package com.example.model;

import org.checkerframework.common.aliasing.qual.Unique;

import javax.persistence.*;
import javax.validation.ConstraintViolation;
import javax.validation.constraints.*;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(name = "user")
@NamedQueries({
    @NamedQuery(name = "Users.findAll", query = "SELECT u FROM User u"),
    @NamedQuery(name = "Users.findByName", query = "SELECT u FROM User u WHERE u.username = :username")
})
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Unique
    private Long id;

    @Column(unique = true)
    @NotBlank(message = "Username cannot be blank")
    private String username;

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
