package com.aurea.deadcode.task;

import com.aurea.deadcode.model.GitHubRepository;
import com.aurea.deadcode.model.Language;
import com.aurea.deadcode.service.GitHubRepositoryService;
import com.aurea.deadcode.service.OccurrenceService;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;

import static org.junit.Assert.assertTrue;

/**
 * Created by ekonovalov on 14.03.2017.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource(locations = "classpath:test.properties")
public class DeadCodeTaskTest {

    @Rule
    public TemporaryFolder temp = new TemporaryFolder();
    @Autowired
    private GitHubRepositoryService gitHubRepositoryService;
    @Autowired
    private OccurrenceService occurrenceService;

    @Test
    public void find() throws Exception {
        DeadCodeTask task = new DeadCodeTask(gitHubRepositoryService, occurrenceService);
        File rootRepositoryPath = temp.getRoot();
        task.setRepositoryPath(rootRepositoryPath.getAbsolutePath());
        task.setTempPath(rootRepositoryPath.getAbsolutePath());

        GitHubRepository repo = new GitHubRepository();
        repo.setUrl("url");
        repo.getLanguages().add(Language.Java);
        gitHubRepositoryService.save(repo);

        File repositoryPath = new File(rootRepositoryPath + "/" + repo.getId());
        repositoryPath.mkdirs();
        repo.setPath(repositoryPath.getAbsolutePath());
        gitHubRepositoryService.save(repo);
        File db = new File(repo.getPath() + "/db.udb");
        db.createNewFile();

        Utils.copyDatabase("/test.udb", db);

        task.find(repo);

        assertTrue(occurrenceService.getByRepositoryId(repo.getId()).size() > 0);
    }

}
