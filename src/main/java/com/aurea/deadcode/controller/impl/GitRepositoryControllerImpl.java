package com.aurea.deadcode.controller.impl;

import com.aurea.deadcode.controller.GitRepositoryController;
import com.aurea.deadcode.exception.MalformedExpressionException;
import com.aurea.deadcode.model.*;
import com.aurea.deadcode.service.GitRepositoryService;
import com.aurea.deadcode.service.OccurrenceService;
import com.aurea.deadcode.task.GitTask;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.validator.routines.UrlValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ekonovalov on 13.03.2017.
 */
@Slf4j
@RestController
public class GitRepositoryControllerImpl implements GitRepositoryController {

    private final GitRepositoryService gitRepositoryService;
    private final OccurrenceService occurrenceService;
    private final GitTask gitTask;

    @Value("${occurrence.limit}")
    private Integer limit = 50;

    @Autowired
    public GitRepositoryControllerImpl(GitRepositoryService gitRepositoryService, OccurrenceService occurrenceService, GitTask gitTask) {
        this.gitRepositoryService = gitRepositoryService;
        this.occurrenceService = occurrenceService;
        this.gitTask = gitTask;
    }

    @Override
    public GitRepository add(@RequestBody GitRepository repo, HttpServletResponse response) {
        if (!UrlValidator.getInstance().isValid(repo.getUrl())) {
            badRequest("Malformed URL: " + repo.getUrl(), response);
            return null;
        }

        repo.setUrl(repo.getUrl().trim().toLowerCase());

        if (gitRepositoryService.getByUrl(repo.getUrl()) == null) {
            log.info("Create new repo");
            Long id = gitRepositoryService.save(repo);
            log.info("Repository saved with id = " + id);
            response.setStatus(HttpServletResponse.SC_ACCEPTED);
            gitTask.cloneRepository(id);
        } else {
            log.info("Repo is found url = " + repo.getUrl() + ", id = " + repo.getId());
            repo = gitRepositoryService.getByUrl(repo.getUrl());
        }

        if (repo.getStatus() == Status.NEW || repo.getStatus() == Status.PROCESSING) {
            response.setStatus(HttpServletResponse.SC_ACCEPTED);
        } else {
            response.setStatus(HttpServletResponse.SC_CREATED);
        }
        response.setHeader("Location", "/repo/" + repo.getId());

        return repo;
    }

    @Override
    public GitRepository update(@PathVariable Long id, @RequestBody Operation operation, HttpServletResponse response) {
        log.info("Update repo id = " + id);
        GitRepository repo = gitRepositoryService.getById(id);
        if (repo == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return null;
        }
        if (repo.getStatus() == Status.PROCESSING) {
            if (repo.getTask() == Task.PULL) {
                response.setStatus(HttpServletResponse.SC_ACCEPTED);
            } else {
                response.setStatus(HttpServletResponse.SC_CONFLICT);
            }
            return repo;
        }
        if (Operation.PULL.equals(operation.getName())) {
            response.setStatus(HttpServletResponse.SC_ACCEPTED);
            gitTask.pullRepository(id);
        } else {
            response.setStatus(HttpServletResponse.SC_NOT_IMPLEMENTED);
        }

        return repo;
    }

    @Override
    public GitRepository get(@PathVariable Long id, HttpServletResponse response) {
        log.info("Get repo id = " + id);
        GitRepository repo = gitRepositoryService.getById(id);
        if (repo == null) {
            log.info("Repo not found id = " + id);
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
        return gitRepositoryService.getById(id);
    }

    @Override
    public List<GitRepository> list() {
        log.info("List repos");
        return gitRepositoryService.getAll();
    }

    @Override
    public String delete(@PathVariable Long id, HttpServletResponse response) {
        log.info("Delete repo id = " + id);
        GitRepository repo = gitRepositoryService.getById(id);
        if (repo == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return "";
        }
        if (repo.getStatus() == Status.PROCESSING) {
            if (repo.getTask() == Task.DELETE) {
                response.setStatus(HttpServletResponse.SC_ACCEPTED);
            } else {
                response.setStatus(HttpServletResponse.SC_CONFLICT);
            }
            return "";
        }

        response.setStatus(HttpServletResponse.SC_ACCEPTED);
        gitTask.deleteRepository(id);

        return "";
    }

    @Override
    public List<Occurrence> list(@PathVariable Long id, Integer limit, Integer page, String filter,
                                 HttpServletRequest request, HttpServletResponse response) {

        log.info("List dead code occurrences for repo id = " + id);
        GitRepository repo = gitRepositoryService.getById(id);
        if (repo == null) {
            log.info("Repo not found id = " + id);
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return null;
        }

        if (limit == null) limit = this.limit;
        if (page == null) {
            page = 0;
        } else {
            page--;
        }

        Page<Occurrence> p;
        if (filter == null || filter.trim().length() == 0) {
            p = occurrenceService.getByRepositoryId(id, limit, page);
        } else {
            try {
                p = occurrenceService.getByRepositoryId(id, limit, page, filter);
            } catch (MalformedExpressionException e) {
                badRequest("Malformed expression: " + filter, response);
                return null;
            }
        }

        StringBuilder sb = new StringBuilder();
        if (p.hasNext()) {
            sb.append("<").append(request.getRequestURL()).append(limit != null ? "&limit=" + limit : "")
                    .append("&page=").append(page + 2).append(filter != null ? "&filter=" + filter : "")
                    .append(">; rel=\"next\"");
            if (p.getTotalPages() > 0) {
                if (sb.length() > 0) sb.append(",");
                sb.append("<").append(request.getRequestURL()).append(limit != null ? "&limit=" + limit : "")
                        .append("&page=").append(p.getTotalPages()).append(filter != null ? "&filter=" + filter : "")
                        .append(">; rel=\"last\"");
            }
        }
        if (p.hasPrevious()) {
            if (sb.length() > 0) sb.append(",");
            sb.append("<").append(request.getRequestURL()).append(limit != null ? "&limit=" + limit : "")
                    .append("&page=").append(page).append(filter != null ? "&filter=" + filter : "")
                    .append(">; rel=\"prev\",");
            sb.append("<").append(request.getRequestURL()).append(limit != null ? "&limit=" + limit : "")
                    .append("&page=1").append(filter != null ? "&filter=" + filter : "")
                    .append(">; rel=\"first\"");
        }

        response.setHeader("Link", sb.toString());

        List<Occurrence> result = new ArrayList<>();
        p.forEach(result::add);

        return result;
    }

    private void badRequest(String msg, HttpServletResponse response) {
        log.info(msg);
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        response.setHeader("Error", msg);
    }

}
