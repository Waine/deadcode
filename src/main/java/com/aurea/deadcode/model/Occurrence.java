package com.aurea.deadcode.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.persistence.*;

/**
 * Created by ekonovalov on 10.03.2017.
 */
@ApiModel(value = "Occurrence", description = "Occurrence of dead code")
@Data
@Entity
@Table(name = "OCCURRENCE")
public class Occurrence {

    @Id
    @GeneratedValue()
    private Long id;

    @Column(nullable = false)
    private Antipattern antipattern = Antipattern.DEAD_CODE;

    @Column(nullable = false)
    private String name;

    @Column
    private String longName;

    @Column
    private String type;

    @Column
    private String kind;

    @Column
    private String file;

    @Column
    private int line;

    @Column
    private int column;

    @JsonIgnore
    @ManyToOne(optional = false)
    @JoinColumn(name = "repository_id")
    private GitHubRepository repository;

}
