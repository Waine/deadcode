package com.aurea.deadcode.service.impl;

import com.aurea.deadcode.model.GitHubRepository;
import com.aurea.deadcode.model.Status;
import com.aurea.deadcode.repository.GitHubRepositoryRepository;
import com.aurea.deadcode.service.GitHubRepositoryService;
import com.aurea.deadcode.service.OccurrenceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

/**
 * Created by ekonovalov on 08.03.2017.
 */
@Slf4j
@Service
public class GitHubRepositoryServiceImpl implements GitHubRepositoryService {

    private final GitHubRepositoryRepository gitHubRepositoryRepository;
    private final OccurrenceService occurrenceService;

    private String pid;
    private String host;

    @Autowired
    public GitHubRepositoryServiceImpl(
            GitHubRepositoryRepository gitHubRepositoryRepository,
            OccurrenceService occurrenceService
    ) {
        this.gitHubRepositoryRepository = gitHubRepositoryRepository;
        this.occurrenceService = occurrenceService;

        pid = System.getProperty("PID");
        try {
            host = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            log.error("Cannot determine localhost", e);
        }
        log.debug("System host = " + host + ", PID = " + pid);
    }

    @Override
    public GitHubRepository getById(Long id) {
        return gitHubRepositoryRepository.findOne(id);
    }

    @Override
    public GitHubRepository getByUrl(String url) {
        return gitHubRepositoryRepository.findByUrl(url.trim().toLowerCase());
    }

    @Override
    @Transactional
    public GitHubRepository lockForProcessing(Long id) {
        GitHubRepository repo = gitHubRepositoryRepository.lock(id);
        if (repo.getStatus() == Status.PROCESSING) {
            throw new CannotAcquireLockException("Cannot lock repo id = " + id + ". Already in processing state.");
        }
        repo.setStatus(Status.PROCESSING);
        repo.setHost(host);
        repo.setPid(pid);
        gitHubRepositoryRepository.save(repo);

        return repo;
    }

    @Override
    @Transactional
    public Long save(GitHubRepository repo) {
        return gitHubRepositoryRepository.save(repo).getId();
    }

    @Override
    @Transactional
    public void delete(GitHubRepository repo) {
        occurrenceService.deleteByRepositoryId(repo.getId());
        gitHubRepositoryRepository.delete(repo);
    }

    @Override
    public List<GitHubRepository> getAll() {
        return gitHubRepositoryRepository.findAll();
    }

}
