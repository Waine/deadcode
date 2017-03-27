package com.aurea.deadcode.service;

import com.aurea.deadcode.exception.MalformedExpressionException;
import com.aurea.deadcode.model.DeadCodeOccurrence;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * Created by ekonovalov on 10.03.2017.
 */
public interface OccurrenceService {

    List<DeadCodeOccurrence> getByRepositoryId(Long repositoryId);

    Page<DeadCodeOccurrence> getByRepositoryId(Long repositoryId, Integer limit, Integer page);

    Page<DeadCodeOccurrence> getByRepositoryId(Long repositoryId, Integer limit, Integer page, String filter) throws MalformedExpressionException;

    void deleteByRepositoryId(Long repositoryId);

    void saveBatch(List<DeadCodeOccurrence> occurrences);

}
