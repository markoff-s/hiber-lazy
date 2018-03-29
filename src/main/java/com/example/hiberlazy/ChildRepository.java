package com.example.hiberlazy;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChildRepository extends JpaRepository<Child, Integer> {

    Child findByChildName(String name);
}

