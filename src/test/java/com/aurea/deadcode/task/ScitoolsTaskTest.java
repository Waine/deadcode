package com.aurea.deadcode.task;

import com.aurea.deadcode.service.GitHubRepositoryService;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Created by ekonovalov on 14.03.2017.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource(locations = "classpath:test.properties")
public class ScitoolsTaskTest {

    @Rule
    public TemporaryFolder temp = new TemporaryFolder();
    @Autowired
    private GitHubRepositoryService gitHubRepositoryService;

    @Test
    public void cloneRepository() throws Exception {
        ScitoolsTask task = new ScitoolsTask(gitHubRepositoryService, Mockito.mock(DeadCodeTask.class));
    }

}
