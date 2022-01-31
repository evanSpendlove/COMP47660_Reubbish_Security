package com.reubbishsecurity.CovidVaccinations.authentication.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Setter
@Getter
@Entity
@Table(name = "roles")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(length = 60)
    private String name;

    public Role(String name) {
        this.name = name;
    }

    public Role() {}

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "Role{" + name + "}";
    }
}