package com.aurea.deadcode.controller.impl;

import com.aurea.deadcode.model.DeadCodeOccurrence;
import com.aurea.deadcode.model.GitHubRepository;
import com.aurea.deadcode.model.Language;
import com.aurea.deadcode.model.Status;
import com.aurea.deadcode.service.GitHubRepositoryService;
import com.aurea.deadcode.service.OccurrenceService;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by ekonovalov on 13.03.2017.
 */
@RunWith(SpringRunner.class)
@ComponentScan(basePackages = {"com.aurea.deadcode"})
@AutoConfigureDataJpa
@TestPropertySource(locations = "classpath:test.properties")
@Ignore
public class BaseControllerImplTest {

    private static final String testUrl = "http://localhost:8080/";

    @Autowired
    protected GitHubRepositoryService gitHubRepositoryService;
    @Autowired
    protected OccurrenceService occurrenceService;
    @Autowired
    protected MockMvc mockMvc;

    @Before
    public void before() {
        GitHubRepository repo = new GitHubRepository();
        repo.setUrl(testUrl);
        repo.getLanguages().add(Language.Java);
        Long id = gitHubRepositoryService.save(repo);
        assertNotNull(id);
        repo = gitHubRepositoryService.getById(id);
        assertNotNull(repo);
        assertEquals(testUrl, repo.getUrl());
        assertEquals(Status.NEW, repo.getStatus());
        assertNotNull(repo.getCreated());
        assertNotNull(repo.getUpdated());

        List<DeadCodeOccurrence> batch = new ArrayList<>();
        batch.add(new DeadCodeOccurrence());
        batch.get(0).setName("name1");
        batch.get(0).setRepository(repo);
        batch.add(new DeadCodeOccurrence());
        batch.get(1).setName("name2");
        batch.get(1).setRepository(repo);
        occurrenceService.saveBatch(batch);
    }

    @After
    public void after() {
        List<GitHubRepository> repos = gitHubRepositoryService.getAll();
        for (GitHubRepository repo : repos) {
            occurrenceService.deleteByRepositoryId(repo.getId());
            gitHubRepositoryService.getById(repo.getId());
        }
    }

}
