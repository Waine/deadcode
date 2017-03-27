package com.aurea.deadcode.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

/**
 * Created by ekonovalov on 27.03.2017.
 */
@Data
@NoArgsConstructor
@Entity
@Table(name = "PAGE_LABEL", indexes = {
        @Index(columnList = "repositoryId", name = "idx_repositoryId"),
        @Index(columnList = "expression", name = "idx_expression"),
        @Index(columnList = "pageLimit", name = "idx_pageLimit"),
})
public class PageLabel {

    @Id
    @GeneratedValue()
    private Long id;

    @Column(nullable = false)
    private Long repositoryId;

    @Column(nullable = false)
    private String expression;

    @Column(name = "pageLimit", nullable = false)
    private Integer limit;

    @Column(nullable = false)
    private Integer page;

    @Column(nullable = false)
    private Long occurrenceId;

    @Column
    private Integer totalElements = -1;

    @Column
    private Integer totalPages = -1;

    public PageLabel(Long repositoryId, String expression, Integer limit, Integer page, Long occurrenceId) {
        this.repositoryId = repositoryId;
        this.expression = expression;
        this.limit = limit;
        this.page = page;
        this.occurrenceId = occurrenceId;
    }

}
