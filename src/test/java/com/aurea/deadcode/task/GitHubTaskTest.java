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
import static org.mockito.Mockito.mock;

/**
 * Created by ekonovalov on 08.03.2017.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource(locations = "classpath:test.properties")
public class GitHubTaskTest {

    @Rule
    public TemporaryFolder temp = new TemporaryFolder();
    @Autowired
    private GitHubRepositoryService gitHubRepositoryService;
    @Autowired
    private OccurrenceService occurrenceService;

    @Test
    public void cloneRepository() throws Exception {
        GitHubTask task = new GitHubTask(
                gitHubRepositoryService,
                occurrenceService,
                mock(ScitoolsTask.class)
        );

        GitHubRepository repo = new GitHubRepository();
        repo.setUrl("https://github.com/waine/patterns.git");
        repo.getLanguages().add(Language.Java);
        Long id = gitHubRepositoryService.save(repo);

        File root = temp.getRoot();
        task.setRepositoryPath(temp.getRoot().getAbsolutePath());

        task.cloneRepository(id);

        File path = new File(root.getAbsolutePath() + "/" + id + "/repository");
        assertTrue(path.exists());
        assertTrue(path.list().length > 0);
    }

}
