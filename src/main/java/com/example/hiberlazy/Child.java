package com.example.hiberlazy;

import javax.persistence.*;

@Entity
public class Child {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column
    private String childName;

    @ManyToOne // eager by default
    @JoinColumn(name = "PARENT_ID", updatable=false, nullable=false)
    private Parent parent;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getChildName() {
        return childName;
    }

    public void setChildName(String childName) {
        this.childName = childName;
    }

    public Parent getParent() {
        return parent;
    }

    public void setParent(Parent parent) {
        this.parent = parent;
    }
}

