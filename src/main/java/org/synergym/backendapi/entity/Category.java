package org.synergym.backendapi.entity;

import jakarta.persistence.*;
import lombok.Builder;

@Entity
@Table(name = "Categories")
public class Category extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private int id;

    @Column(nullable = false, unique = true, length = 30)
    private String name;

    @Builder
    public Category(String name) {
        this.name = name;
    }
}
