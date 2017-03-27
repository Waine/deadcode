package com.aurea.deadcode.task;

import com.aurea.deadcode.model.GitHubRepository;
import com.aurea.deadcode.model.Status;
import com.aurea.deadcode.model.Task;
import com.aurea.deadcode.service.GitHubRepositoryService;
import com.aurea.deadcode.service.OccurrenceService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Created by ekonovalov on 08.03.2017.
 */
@Slf4j
@Component
public class GitHubTask {

    private final GitHubRepositoryService gitHubRepositoryService;
    private final OccurrenceService occurrenceService;
    private final ScitoolsTask scitoolsTask;

    @Value("${repository.path}")
    private String repositoryPath;

    @Autowired
    public GitHubTask(GitHubRepositoryService gitHubRepositoryService,
                      OccurrenceService occurrenceService,
                      ScitoolsTask scitoolsTask) {

        this.gitHubRepositoryService = gitHubRepositoryService;
        this.occurrenceService = occurrenceService;
        this.scitoolsTask = scitoolsTask;
    }

    @Async
    public void cloneRepository(Long id) {
        log.info("Start cloning repository id = " + id);
        GitHubRepository repo;
        try {
            repo = gitHubRepositoryService.lockForProcessing(id);
        } catch (CannotAcquireLockException e) {
            log.info("Cannot acquire lock for repo id = " + id);
            return;
        }

        repo.setTask(Task.CLONE);
        gitHubRepositoryService.save(repo);

        File path = new File(repositoryPath + "/" + repo.getId() + "/repository");
        log.info("Repository path = " + path.getAbsolutePath());
        if (!path.mkdirs()) {
            logError(repo, "Cannot create folder", new FileNotFoundException("Cannot create folder"));
            return;
        }

        Git git = null;
        try {
            git = Git.cloneRepository().setURI(repo.getUrl()).setDirectory(path).call();
        } catch (GitAPIException e) {
            logError(repo, "Cannot clone repository", e);
            return;
        } catch (Exception e) {
            logError(repo, "Cannot clone repository", e);
            throw e;
        } finally {
            if (git != null) git.close();
        }

        repo.setPath(path.getAbsolutePath());
        gitHubRepositoryService.save(repo);

        log.info("Clone repository finished successfully id = " + repo.getId());

        scitoolsTask.createDatabase(repo);
    }

    @Async
    public void pullRepository(Long id) {
        log.info("Start pulling from the repository id = " + id);
        GitHubRepository repo;
        try {
            repo = gitHubRepositoryService.lockForProcessing(id);
        } catch (CannotAcquireLockException e) {
            log.info("Cannot acquire lock for repo id = " + id);
            return;
        }

        repo.setTask(Task.PULL);
        gitHubRepositoryService.save(repo);

        occurrenceService.deleteByRepositoryId(repo.getId());

        Git git = null;
        try {
            git = Git.open(new File(repo.getPath()));
            git.pull().call();
        } catch (GitAPIException | IOException e) {
            logError(repo, "Cannot pull repository", e);
            return;
        } catch (Exception e) {
            logError(repo, "Cannot pull repository", e);
            throw e;
        } finally {
            if (git != null) git.close();
        }

        log.info("Pull from the repository finished successfully id = " + repo.getId());

        scitoolsTask.createDatabase(repo);
    }

    @Async
    public void deleteRepository(Long id) {
        log.info("Start deleting repository id = " + id);
        GitHubRepository repo;
        try {
            repo = gitHubRepositoryService.lockForProcessing(id);
        } catch (CannotAcquireLockException e) {
            log.info("Cannot acquire lock for repo id = " + id);
            return;
        }
        repo.setTask(Task.DELETE);
        gitHubRepositoryService.save(repo);
        try {
            File path = new File(repositoryPath + "/" + repo.getId());
            log.debug("Repository path = " + path.getAbsolutePath());
            FileUtils.deleteDirectory(path);
            gitHubRepositoryService.delete(repo);
        } catch (IOException e) {
            logError(repo, "Cannot delete repository", e);
            return;
        } catch (Exception e) {
            logError(repo, "Cannot delete repository", e);
            throw e;
        }

        log.info("Repository deleted successfully id = " + repo.getId());
    }

    private void logError(GitHubRepository repo, String error, Exception e) {
        repo.setStatus(Status.FAILED);
        gitHubRepositoryService.save(repo);
        log.error(error + " repoId = " + repo.getId(), e);
    }

    public void setRepositoryPath(String repositoryPath) {
        this.repositoryPath = repositoryPath;
    }

}
