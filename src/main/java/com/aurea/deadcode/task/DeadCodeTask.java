package com.aurea.deadcode.task;

import com.aurea.deadcode.model.GitHubRepository;
import com.aurea.deadcode.model.Status;
import com.aurea.deadcode.model.Task;
import com.aurea.deadcode.service.GitHubRepositoryService;
import com.aurea.deadcode.service.OccurrenceService;
import com.aurea.deadcode.task.algo.DeadCode;
import com.scitools.understand.Database;
import com.scitools.understand.Understand;
import com.scitools.understand.UnderstandException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.Date;

/**
 * Created by ekonovalov on 10.03.2017.
 */
@Slf4j
@Component
public class DeadCodeTask {

    private final GitHubRepositoryService gitHubRepositoryService;
    private final OccurrenceService occurrenceService;

    @Value("${repository.path}")
    private String repositoryPath;

    @Value("${temp.path}")
    private String tempPath;

    @Autowired
    public DeadCodeTask(GitHubRepositoryService gitHubRepositoryService,
                        OccurrenceService occurrenceService) {

        this.gitHubRepositoryService = gitHubRepositoryService;
        this.occurrenceService = occurrenceService;
    }

    @Async("findDeadCodeTaskExecutor")
    public void find(GitHubRepository repo) {
        log.info("Find dead code occurrencies id = " + repo.getId());

        repo.setTask(Task.FIND);
        gitHubRepositoryService.save(repo);

        File tempDb;
        try {
            new File(tempPath).mkdirs();
            tempDb = new File(tempPath + "/deadcode_" + repo.getId() + "_" + System.currentTimeMillis() + ".udb");
            Utils.copyDatabase(new File(repositoryPath + "/" + repo.getId() + "/db.udb"), tempDb);
        } catch (IOException e) {
            logError(repo, "Cannot create temp database file", e);
            return;
        }

        log.info("Database copied id = " + repo.getId());

        Database db;
        try {
            db = Understand.open(tempDb.getAbsolutePath());
        } catch (UnderstandException e) {
            logError(repo, "Cannot open database", e);
            return;
        }

        log.info("Database opened id = " + repo.getId());

        try {
            new DeadCode(db).find(batch -> occurrenceService.saveBatch(Converter.convert(batch, repo, repositoryPath)));
            db.close();
            tempDb.delete();
        } catch (Exception e) {
            logError(repo, "Cannot retrieve data", e);
            throw e;
        }

        repo.setStatus(Status.COMPLETED);
        repo.setTask(null);
        repo.setUpdated(new Date());
        gitHubRepositoryService.save(repo);

        log.info("Find dead code occurrencies completed successfully id = " + repo.getId());
    }

    private void logError(GitHubRepository repo, String error, Exception e) {
        repo.setStatus(Status.FAILED);
        gitHubRepositoryService.save(repo);
        log.error(error + " repoId = " + repo.getId(), e);
    }

    public void setRepositoryPath(String repositoryPath) {
        this.repositoryPath = repositoryPath;
    }

    public void setTempPath(String tempPath) {
        this.tempPath = tempPath;
    }

}
