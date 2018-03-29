package com.example.hiberlazy;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EagerParentRepository extends JpaRepository<Parent, Integer> {

    Parent findByParentName(String name);
}