package com.aurea.deadcode.task;

import com.aurea.deadcode.model.GitRepository;
import com.aurea.deadcode.model.Language;
import com.aurea.deadcode.service.GitRepositoryService;
import com.aurea.deadcode.service.OccurrenceService;
import org.junit.Ignore;
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
    private GitRepositoryService gitRepositoryService;
    @Autowired
    private OccurrenceService occurrenceService;

    @Test
    @Ignore
    public void find() throws Exception {
        DeadCodeTask task = new DeadCodeTask(gitRepositoryService, occurrenceService);
        File rootRepositoryPath = temp.getRoot();
        task.setRepositoryPath(rootRepositoryPath.getAbsolutePath());
        task.setTempPath(rootRepositoryPath.getAbsolutePath());

        GitRepository repo = new GitRepository();
        repo.setUrl("url");
        repo.getLanguages().add(Language.Java);
        gitRepositoryService.save(repo);

        File repositoryPath = new File(rootRepositoryPath + "/" + repo.getId());
        repositoryPath.mkdirs();
        repo.setPath(repositoryPath.getAbsolutePath());
        gitRepositoryService.save(repo);
        File db = new File(repo.getPath() + "/db.udb");
        db.createNewFile();

        Utils.copyDatabase("/test.udb", db);

        task.find(repo);

        assertTrue(occurrenceService.getByRepositoryId(repo.getId()).size() > 0);
    }

}
