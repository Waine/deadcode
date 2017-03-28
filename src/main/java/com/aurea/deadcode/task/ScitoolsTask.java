package com.aurea.deadcode.task;

import com.aurea.deadcode.model.GitRepository;
import com.aurea.deadcode.model.Language;
import com.aurea.deadcode.model.Status;
import com.aurea.deadcode.model.Task;
import com.aurea.deadcode.service.GitRepositoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.stream.Collectors;

/**
 * Created by ekonovalov on 08.03.2017.
 */
@Slf4j
@Component
public class ScitoolsTask {

    private final GitRepositoryService gitRepositoryService;
    private final DeadCodeTask deadCodeTask;

    @Value("${repository.path}")
    private String repositoryPath;

    @Autowired
    public ScitoolsTask(GitRepositoryService gitRepositoryService, DeadCodeTask deadCodeTask) {
        this.gitRepositoryService = gitRepositoryService;
        this.deadCodeTask = deadCodeTask;
    }

    @Async
    public void createDatabase(GitRepository repo) {
        log.info("Start create database id = " + repo.getId());

        repo.setTask(Task.FILL);
        gitRepositoryService.save(repo);
        try {
            File db = new File(repositoryPath + "/" + repo.getId() + "/db.udb");
            if (db.exists()) {
                db.delete();
                db.createNewFile();
            }
            Utils.copyDatabase("/empty.udb", db);
        } catch (IOException e) {
            logError(repo, "Cannot copy empty database", e);
            return;
        }
        log.info("Database copied id = " + repo.getId());

        try {
            setLanguage(repo);
        } catch (IOException e) {
            logError(repo, "Cannot set programming language", e);
            return;
        }
        log.info("Programming language set id = " + repo.getId());

        try {
            addFiles(repo);
        } catch (IOException e) {
            logError(repo, "Cannot add files to database", e);
            return;
        }

        log.info("Files added id = " + repo.getId());

        repo.setTask(Task.ANALYZE);
        gitRepositoryService.save(repo);

        try {
            runAnalyzer(repo);
        } catch (IOException e) {
            logError(repo, "Cannot run analizer database", e);
            return;
        }
        log.info("Analyzer finished id = " + repo.getId());

        deadCodeTask.find(repo);
    }

    private void setLanguage(GitRepository repo) throws IOException {
        String output = Utils.runCommand(new File(repositoryPath + "/" + repo.getId()),
                "und settings -Languages " +
                        repo.getLanguages().stream().map(Language::toString).collect(Collectors.joining(" "))
                        + " db.udb");
        log.debug(output);
    }

    private void addFiles(GitRepository repo) throws IOException {
        String output = Utils.runCommand(new File(repositoryPath + "/" + repo.getId()),
                "und -db db.udb add \"" + repo.getPath() + "\"");
        log.debug(output);
    }

    private void runAnalyzer(GitRepository repo) throws IOException {
        String output = Utils.runCommand(new File(repositoryPath + "/" + repo.getId()),
                "und analyze db.udb");
        log.debug(output);
    }

    private void logError(GitRepository repo, String error, Exception e) {
        repo.setStatus(Status.FAILED);
        gitRepositoryService.save(repo);
        log.error(error + " repoId = " + repo.getId(), e);
    }

    public void setRepositoryPath(String repositoryPath) {
        this.repositoryPath = repositoryPath;
    }

}
