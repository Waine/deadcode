package com.aurea.deadcode.service;

import com.aurea.deadcode.exception.MalformedExpressionException;
import com.aurea.deadcode.model.Occurrence;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * Created by ekonovalov on 10.03.2017.
 */
public interface OccurrenceService {

    List<Occurrence> getByRepositoryId(Long repositoryId);

    Page<Occurrence> getByRepositoryId(Long repositoryId, Integer limit, Integer page);

    Page<Occurrence> getByRepositoryId(Long repositoryId, Integer limit, Integer page, String filter) throws MalformedExpressionException;

    void deleteByRepositoryId(Long repositoryId);

    void saveBatch(List<Occurrence> occurrences);

}
