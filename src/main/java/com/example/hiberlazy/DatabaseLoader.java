package com.example.hiberlazy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class DatabaseLoader implements CommandLineRunner {

    private final ParentRepository eagerParentRepository;
    private final ChildRepository childRepository;

    @Autowired
    public DatabaseLoader(ParentRepository parentRepository, ChildRepository childRepository) {
        this.eagerParentRepository = parentRepository;
        this.childRepository = childRepository;
    }

    @Override
    public void run(String... args) {

        Parent eagerDad = new Parent();
        eagerDad.setName("John");

        Parent lazyDad = new Parent();
        lazyDad.setName("Jane");

        List<Parent> parents = new ArrayList<>();
        parents.add(eagerDad);
        parents.add(lazyDad);
        eagerParentRepository.saveAll(parents);

        Child billy = new Child();
        billy.setChildName("Billy");
        billy.setParent(eagerDad);

        Child sally = new Child();
        sally.setChildName("Sally");
        sally.setParent(eagerDad);

        Child nick = new Child();
        nick.setChildName("Nick");
        nick.setParent(eagerDad);

        Set<Child> children = new HashSet<>();
        children.add(billy);
        children.add(sally);
        children.add(nick);
        childRepository.saveAll(children);

    }
}