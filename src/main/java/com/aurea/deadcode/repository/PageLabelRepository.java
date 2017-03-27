package com.aurea.deadcode.repository;

import com.aurea.deadcode.model.PageLabel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Created by ekonovalov on 27.03.2017.
 */
public interface PageLabelRepository extends JpaRepository<PageLabel, Long> {

    List<PageLabel> findByRepositoryIdAndExpressionAndLimitOrderByPage(Long repositoryId, String expression, Integer limit);

    void deleteByRepositoryId(Long repositoryId);

}
