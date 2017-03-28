package com.aurea.deadcode.service.impl;

import com.aurea.deadcode.model.GitRepository;
import com.aurea.deadcode.model.Status;
import com.aurea.deadcode.repository.GitRepositoryRepository;
import com.aurea.deadcode.service.GitRepositoryService;
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
public class GitRepositoryServiceImpl implements GitRepositoryService {

    private final GitRepositoryRepository gitRepositoryRepository;
    private final OccurrenceService occurrenceService;

    private String pid;
    private String host;

    @Autowired
    public GitRepositoryServiceImpl(
            GitRepositoryRepository gitRepositoryRepository,
            OccurrenceService occurrenceService
    ) {
        this.gitRepositoryRepository = gitRepositoryRepository;
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
    public GitRepository getById(Long id) {
        return gitRepositoryRepository.findOne(id);
    }

    @Override
    public GitRepository getByUrl(String url) {
        return gitRepositoryRepository.findByUrl(url.trim().toLowerCase());
    }

    @Override
    @Transactional
    public GitRepository lockForProcessing(Long id) {
        GitRepository repo = gitRepositoryRepository.findById(id);
        if (repo.getStatus() == Status.PROCESSING) {
            throw new CannotAcquireLockException("Cannot findById repo id = " + id + ". Already in processing state.");
        }
        repo.setStatus(Status.PROCESSING);
        repo.setHost(host);
        repo.setPid(pid);
        gitRepositoryRepository.save(repo);

        return repo;
    }

    @Override
    @Transactional
    public Long save(GitRepository repo) {
        return gitRepositoryRepository.save(repo).getId();
    }

    @Override
    @Transactional
    public void delete(GitRepository repo) {
        occurrenceService.deleteByRepositoryId(repo.getId());
        gitRepositoryRepository.delete(repo);
    }

    @Override
    public List<GitRepository> getAll() {
        return gitRepositoryRepository.findAll();
    }

}
