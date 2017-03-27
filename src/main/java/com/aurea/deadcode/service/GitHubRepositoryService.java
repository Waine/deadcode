package com.aurea.deadcode.service;

import com.aurea.deadcode.model.GitHubRepository;

import java.util.List;

/**
 * Created by ekonovalov on 08.03.2017.
 */
public interface GitHubRepositoryService {

    GitHubRepository getById(Long id);

    GitHubRepository getByUrl(String url);

    GitHubRepository lockForProcessing(Long id);

    Long save(GitHubRepository repo);

    void delete(GitHubRepository repo);

    List<GitHubRepository> getAll();

}
