package com.aurea.deadcode.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by ekonovalov on 08.03.2017.
 */
@ApiModel(value = "GitHubRepository", description = "GitHub repository")
@Data
@Entity
@Table(name = "REPOSITORY", indexes = {
        @Index(columnList = "url", name = "idx_url")
})
public class GitHubRepository {

    @ApiModelProperty(hidden = true)
    @Id
    @GeneratedValue()
    private Long id;

    @Column(unique = true, nullable = false)
    private String url;

    @ElementCollection(fetch = FetchType.EAGER)
    @Column(nullable = false)
    private List<Language> languages = new ArrayList<>();

    @JsonIgnore
    @Column
    private String path;

    @ApiModelProperty(hidden = true)
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Status status = Status.NEW;

    @ApiModelProperty(hidden = true)
    @Column
    @Enumerated(EnumType.STRING)
    private Task task;

    @JsonIgnore
    @Column
    private String host;

    @JsonIgnore
    @Column
    private String pid;

    @ApiModelProperty(hidden = true)
    @Column(nullable = false)
    private Date created = new Date();

    @ApiModelProperty(hidden = true)
    @Column(nullable = false)
    private Date updated = new Date();

}
