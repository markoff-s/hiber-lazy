package com.example.hiberlazy;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ParentRepository extends JpaRepository<Parent, Long> {

    Parent findByName(String name);

    @Query("SELECT p FROM Parent p JOIN FETCH p.children WHERE p.name = :name")
    Parent findByNameFetchJoin(@Param("name") String name);

    @Query("SELECT p FROM Parent p JOIN FETCH p.children")
    List<Parent> findAllFetchJoin();
}