package com.aurea.deadcode.repository;

import com.aurea.deadcode.model.Antipattern;
import com.aurea.deadcode.model.Occurrence;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Stream;

/**
 * Created by ekonovalov on 10.03.2017.
 */
@Repository
public interface OccurrenceRepository extends JpaRepository<Occurrence, Long> {

    List<Occurrence> findByRepositoryIdAndAntipattern(Long repositoryId, Antipattern antipattern);

    Page<Occurrence> findByRepositoryIdAndAntipatternOrderById(Long repositoryId, Antipattern antipattern, Pageable pageable);

    Stream<Occurrence> findByRepositoryIdAndAntipatternAndIdGreaterThanEqualOrderById(Long repositoryId, Antipattern antipattern, Long id);

    void deleteByRepositoryId(Long repositoryId);

}
