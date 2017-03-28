package com.aurea.deadcode.controller.impl;

import com.aurea.deadcode.model.GitRepository;
import com.aurea.deadcode.model.Occurrence;
import com.aurea.deadcode.model.Language;
import com.aurea.deadcode.model.Status;
import com.aurea.deadcode.service.GitRepositoryService;
import com.aurea.deadcode.service.OccurrenceService;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


/**
 * Created by ekonovalov on 13.03.2017.
 */
@RunWith(SpringRunner.class)
@ComponentScan(basePackages = {"com.aurea.deadcode"})
@AutoConfigureDataJpa
@TestPropertySource(locations = "classpath:test.properties")
@WebMvcTest(controllers = GitRepositoryControllerImpl.class)
public class GitRepositoryControllerImplTest {

    private static final String testUrl = "http://localhost:8080/";

    @Autowired
    protected GitRepositoryService gitRepositoryService;
    @Autowired
    protected OccurrenceService occurrenceService;
    @Autowired
    protected MockMvc mockMvc;

    @Before
    public void before() {
        GitRepository repo = new GitRepository();
        repo.setUrl(testUrl);
        repo.getLanguages().add(Language.Java);
        Long id = gitRepositoryService.save(repo);
        assertNotNull(id);
        repo = gitRepositoryService.getById(id);
        assertNotNull(repo);
        assertEquals(testUrl, repo.getUrl());
        assertEquals(Status.NEW, repo.getStatus());
        assertNotNull(repo.getCreated());
        assertNotNull(repo.getUpdated());

        List<Occurrence> batch = new ArrayList<>();
        batch.add(new Occurrence());
        batch.get(0).setName("name1");
        batch.get(0).setRepository(repo);
        batch.add(new Occurrence());
        batch.get(1).setName("name2");
        batch.get(1).setRepository(repo);
        occurrenceService.saveBatch(batch);
    }

    @After
    public void after() {
        List<GitRepository> repos = gitRepositoryService.getAll();
        for (GitRepository repo : repos) {
            occurrenceService.deleteByRepositoryId(repo.getId());
            gitRepositoryService.delete(repo);
        }
    }
    @Test
    @Ignore
    public void add() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.post("/repos/")
                .content("{\"languages\": [\"Java\"],\"url\": \"https://github.com/Waine/deadcode\"}"))
                .andExpect(status().isAccepted())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE));
    }

    @Test
    @Ignore
    public void update() throws Exception {
        List<GitRepository> repos = gitRepositoryService.getAll();
        assertTrue(repos.size() > 0);
        String id = String.valueOf(repos.get(0).getId());
        this.mockMvc.perform(MockMvcRequestBuilders.patch("/repos/" + id).content("{\"name\":\"pull\"}"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE));
    }

    @Test
    public void get() throws Exception {
        List<GitRepository> repos = gitRepositoryService.getAll();
        assertTrue(repos.size() > 0);
        String id = String.valueOf(repos.get(0).getId());
        this.mockMvc.perform(MockMvcRequestBuilders.get("/repos/" + id))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE));
    }

    @Test
    public void list() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.get("/repos/"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE));
    }

    @Test
    public void delete() throws Exception {
        List<GitRepository> repos = gitRepositoryService.getAll();
        assertTrue(repos.size() > 0);
        String id = String.valueOf(repos.get(0).getId());
        this.mockMvc.perform(MockMvcRequestBuilders.delete("/repos/" + id))
                .andExpect(status().isAccepted())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE));
    }

    @Test
    public void listDeadCode() throws Exception {
        List<GitRepository> repos = gitRepositoryService.getAll();
        assertTrue(repos.size() > 0);
        String id = String.valueOf(repos.get(0).getId());
        this.mockMvc.perform(MockMvcRequestBuilders.get("/repos/" + id + "/unused_code/"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE));
    }

}
