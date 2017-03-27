package com.aurea.deadcode.service.impl;

import com.aurea.deadcode.model.*;
import com.aurea.deadcode.repository.PageLabelRepository;
import com.aurea.deadcode.service.GitHubRepositoryService;
import com.aurea.deadcode.service.OccurrenceService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by ekonovalov on 10.03.2017.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource(locations = "classpath:test.properties")
public class ServiceImplTest {

    @Autowired
    private GitHubRepositoryService gitHubRepositoryService;
    @Autowired
    private OccurrenceService occurrenceService;

    @Autowired
    private PageLabelRepository pageLabelRepository;

    @Before
    public void before() {
        GitHubRepository repo = new GitHubRepository();
        repo.setUrl("url");
        repo.getLanguages().add(Language.Java);
        Long id = gitHubRepositoryService.save(repo);
        assertNotNull(id);
        repo = gitHubRepositoryService.getById(id);
        assertNotNull(repo);
        assertEquals("url", repo.getUrl());
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
            gitHubRepositoryService.delete(repo);
        }
    }

    @Test
    public void getById() throws Exception {
        List<GitHubRepository> repos = gitHubRepositoryService.getAll();
        Long id = repos.get(0).getId();
        GitHubRepository repo = gitHubRepositoryService.getById(id);
        assertNotNull(repo);
    }

    @Test
    public void getByUrl() throws Exception {
        List<GitHubRepository> repos = gitHubRepositoryService.getAll();
        String url = repos.get(0).getUrl();
        GitHubRepository repo = gitHubRepositoryService.getByUrl(url);
        assertNotNull(repo);
    }

    @Test
    public void save() throws Exception {
        List<GitHubRepository> repos = gitHubRepositoryService.getAll();
        Long id = repos.get(0).getId();
        GitHubRepository repo = gitHubRepositoryService.getById(id);
        assertEquals(Status.NEW, repo.getStatus());
        repo.setStatus(Status.PROCESSING);
        gitHubRepositoryService.save(repo);
        repo = gitHubRepositoryService.getById(id);
        assertEquals(Status.PROCESSING, repo.getStatus());
    }

    @Test
    public void delete() throws Exception {
        GitHubRepository repo = new GitHubRepository();
        repo.setUrl("url2");
        repo.getLanguages().add(Language.Java);
        Long id = gitHubRepositoryService.save(repo);
        assertNotNull(id);
        gitHubRepositoryService.delete(repo);
        repo = gitHubRepositoryService.getById(id);
        assertNull(repo);
    }

    @Test
    public void getAll() throws Exception {
        List<GitHubRepository> repos = gitHubRepositoryService.getAll();
        assertTrue(repos.size() > 0);
    }

    @Test
    public void getAllByRepositoryId() throws Exception {
        List<GitHubRepository> repos = gitHubRepositoryService.getAll();
        Long id = repos.get(0).getId();
        List<DeadCodeOccurrence> occurrencies = occurrenceService.getByRepositoryId(id);
        assertTrue(occurrencies.size() > 0);
    }

    @Test
    public void getPagedByRepositoryId() throws Exception {
        List<GitHubRepository> repos = gitHubRepositoryService.getAll();
        Long id = repos.get(0).getId();
        GitHubRepository repo = gitHubRepositoryService.getById(id);
        List<DeadCodeOccurrence> batch = new ArrayList<>();
        for (int i = 0; i < 72; i++) {
            DeadCodeOccurrence o = new DeadCodeOccurrence();
            o.setName("name" + i);
            o.setRepository(repo);
            batch.add(o);
        }
        occurrenceService.saveBatch(batch);

        assertEquals(74, occurrenceService.getByRepositoryId(id).size());

        Page<DeadCodeOccurrence> page = occurrenceService.getByRepositoryId(id, 25, 0);
        assertEquals(25, page.getNumberOfElements());
        assertEquals(74, page.getTotalElements());
        assertEquals(3, page.getTotalPages());
        assertTrue(page.hasNext());
        assertFalse(page.hasPrevious());
        page = occurrenceService.getByRepositoryId(id, 25, 1);
        assertEquals(25, page.getNumberOfElements());
        assertEquals(74, page.getTotalElements());
        assertEquals(3, page.getTotalPages());
        assertTrue(page.hasNext());
        assertTrue(page.hasPrevious());
        page = occurrenceService.getByRepositoryId(id, 25, 2);
        assertEquals(24, page.getNumberOfElements());
        assertEquals(74, page.getTotalElements());
        assertEquals(3, page.getTotalPages());
        assertFalse(page.hasNext());
        assertTrue(page.hasPrevious());
    }

    @Test
    public void getFilteredByRepositoryId() throws Exception {
        List<GitHubRepository> repos = gitHubRepositoryService.getAll();
        Long id = repos.get(0).getId();
        GitHubRepository repo = gitHubRepositoryService.getById(id);
        List<DeadCodeOccurrence> batch = new ArrayList<>();
        for (int i = 0; i < 74; i++) {
            DeadCodeOccurrence o = new DeadCodeOccurrence();
            o.setName("filter" + i);
            o.setRepository(repo);
            batch.add(o);
        }
        occurrenceService.saveBatch(batch);

        assertEquals(76, occurrenceService.getByRepositoryId(id).size());

        Page<DeadCodeOccurrence> page = occurrenceService.getByRepositoryId(id, 25, 0, "name.contains(\"filter\")");
        assertEquals(25, page.getNumberOfElements());
        assertEquals(-1, page.getTotalElements());
        assertEquals(-1, page.getTotalPages());
        assertTrue(page.hasNext());
        assertFalse(page.hasPrevious());
        page = occurrenceService.getByRepositoryId(id, 25, 1, "name.contains(\"filter\")");
        assertEquals(25, page.getNumberOfElements());
        assertEquals(-1, page.getTotalElements());
        assertEquals(-1, page.getTotalPages());
        assertTrue(page.hasNext());
        assertTrue(page.hasPrevious());
        page = occurrenceService.getByRepositoryId(id, 25, 2, "name.contains(\"filter\")");
        assertEquals(24, page.getNumberOfElements());
        assertEquals(74, page.getTotalElements());
        assertEquals(3, page.getTotalPages());
        assertFalse(page.hasNext());
        assertTrue(page.hasPrevious());
    }

    @Test
    public void getFilteredByRepositoryIdThirdPage() throws Exception {
        List<GitHubRepository> repos = gitHubRepositoryService.getAll();
        Long id = repos.get(0).getId();
        GitHubRepository repo = gitHubRepositoryService.getById(id);
        List<DeadCodeOccurrence> batch = new ArrayList<>();
        for (int i = 0; i < 70; i++) {
            DeadCodeOccurrence o = new DeadCodeOccurrence();
            o.setName("filter" + i);
            o.setRepository(repo);
            batch.add(o);
        }
        occurrenceService.saveBatch(batch);

        assertEquals(72, occurrenceService.getByRepositoryId(id).size());

        Page<DeadCodeOccurrence> page = occurrenceService.getByRepositoryId(id, 20, 2, "name.contains(\"filter\")");
        assertEquals(20, page.getNumberOfElements());
        assertEquals(-1, page.getTotalElements());
        assertEquals(-1, page.getTotalPages());
        assertTrue(page.hasNext());
        assertTrue(page.hasPrevious());

        List<PageLabel> labels = pageLabelRepository.findByRepositoryIdAndExpressionAndLimitOrderByPage(id, "name.contains(\"filter\")", 20);

        assertEquals(3, labels.size());
    }

    @Test
    public void deleteByRepositoryId() throws Exception {
        List<GitHubRepository> repos = gitHubRepositoryService.getAll();
        Long id = repos.get(0).getId();
        List<DeadCodeOccurrence> occurrencies = occurrenceService.getByRepositoryId(id);
        assertTrue(occurrencies.size() > 0);
        occurrenceService.deleteByRepositoryId(id);
        occurrencies = occurrenceService.getByRepositoryId(id);
        assertTrue(occurrencies.size() == 0);

        gitHubRepositoryService.delete(gitHubRepositoryService.getById(id));
    }

}