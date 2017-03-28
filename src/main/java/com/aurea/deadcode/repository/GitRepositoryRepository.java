package com.aurea.deadcode.repository;

import com.aurea.deadcode.model.GitRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.persistence.LockModeType;

/**
 * Created by ekonovalov on 08.03.2017.
 */
@Repository
public interface GitRepositoryRepository extends JpaRepository<GitRepository, Long> {

    GitRepository findByUrl(String url);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    GitRepository findById(Long id);

}
