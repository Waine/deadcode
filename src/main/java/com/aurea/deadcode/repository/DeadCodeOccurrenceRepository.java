package com.aurea.deadcode.repository;

import com.aurea.deadcode.model.DeadCodeOccurrence;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Stream;

/**
 * Created by ekonovalov on 10.03.2017.
 */
@Repository
public interface DeadCodeOccurrenceRepository extends JpaRepository<DeadCodeOccurrence, Long> {

    List<DeadCodeOccurrence> findByRepositoryId(@Param("repositoryId") Long repositoryId);

    @Query("FROM DeadCodeOccurrence o WHERE o.repository.id = :repositoryId ORDER BY id")
    Page<DeadCodeOccurrence> findPagedByRepositoryId(@Param("repositoryId") Long repositoryId, Pageable pageable);

    @Query("FROM DeadCodeOccurrence o WHERE o.repository.id = :repositoryId AND o.id >= :startId ORDER BY id")
    Stream<DeadCodeOccurrence> findStreamedByRepositoryId(@Param("repositoryId") Long repositoryId, @Param("startId") Long startId);

    @Modifying
    @Query("DELETE FROM DeadCodeOccurrence o WHERE o.repository.id = :repositoryId")
    void deleteByRepositoryId(@Param("repositoryId") Long repositoryId);

}
