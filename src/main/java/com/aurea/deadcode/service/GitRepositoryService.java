package com.aurea.deadcode.service;

import com.aurea.deadcode.model.GitRepository;

import java.util.List;

/**
 * Created by ekonovalov on 08.03.2017.
 */
public interface GitRepositoryService {

    GitRepository getById(Long id);

    GitRepository getByUrl(String url);

    GitRepository lockForProcessing(Long id);

    Long save(GitRepository repo);

    void delete(GitRepository repo);

    List<GitRepository> getAll();

}
