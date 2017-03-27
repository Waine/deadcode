package com.aurea.deadcode.controller.impl;

import com.aurea.deadcode.model.GitHubRepository;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


/**
 * Created by ekonovalov on 13.03.2017.
 */
@RunWith(SpringRunner.class)
@WebMvcTest(controllers = GitHubRepositoryControllerImpl.class)
public class GitHubRepositoryControllerImplTest extends BaseControllerImplTest {

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
        List<GitHubRepository> repos = gitHubRepositoryService.getAll();
        assertTrue(repos.size() > 0);
        String id = String.valueOf(repos.get(0).getId());
        this.mockMvc.perform(MockMvcRequestBuilders.patch("/repos/" + id).content("{\"name\":\"pull\"}"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE));
    }

    @Test
    public void get() throws Exception {
        List<GitHubRepository> repos = gitHubRepositoryService.getAll();
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
        List<GitHubRepository> repos = gitHubRepositoryService.getAll();
        assertTrue(repos.size() > 0);
        String id = String.valueOf(repos.get(0).getId());
        this.mockMvc.perform(MockMvcRequestBuilders.delete("/repos/" + id))
                .andExpect(status().isAccepted())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE));
    }

    @Test
    public void listDeadCode() throws Exception {
        List<GitHubRepository> repos = gitHubRepositoryService.getAll();
        assertTrue(repos.size() > 0);
        String id = String.valueOf(repos.get(0).getId());
        this.mockMvc.perform(MockMvcRequestBuilders.get("/repos/" + id + "/unused_code/"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE));
    }
}
