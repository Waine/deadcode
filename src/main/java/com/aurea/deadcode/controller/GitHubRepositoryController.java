package com.aurea.deadcode.controller;

import com.aurea.deadcode.model.Occurrence;
import com.aurea.deadcode.model.GitHubRepository;
import com.aurea.deadcode.model.Operation;
import io.swagger.annotations.*;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * Created by ekonovalov on 09.03.2017.
 */
@Api(value = "/repos", description = "GitHub repositories", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
@RequestMapping("/repos")
public interface GitHubRepositoryController {

    @ApiOperation(value = "add", notes = "Available languages: Ada, Assembly, COBOL, Cpp, CSharp, Fortran, Java, Jovial, Pascal, Plm, Python, VHDL, Web")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "repo", value = "Repository", required = true, dataType = "GitHubRepository", paramType = "body"),
    })
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Created"),
            @ApiResponse(code = 202, message = "Accepted"),
            @ApiResponse(code = 400, message = "Bad Request")
    })
    @ResponseHeader(name = "Location", description = "Repository resource location")
    @RequestMapping(method = RequestMethod.POST, path = "/", produces = MediaType.APPLICATION_JSON_VALUE)
    GitHubRepository add(@RequestBody GitHubRepository repo, HttpServletResponse response);

    @ApiOperation(value = "update", notes = "Available operations: pull")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "Repository ID", required = true, dataType = "long", paramType = "path"),
            @ApiImplicitParam(name = "operation", value = "Operation to perform on repository", required = true, dataType = "Operation", paramType = "body")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 202, message = "Accepted"),
            @ApiResponse(code = 404, message = "Not Found"),
            @ApiResponse(code = 409, message = "Conflict"),
            @ApiResponse(code = 501, message = "Not Implemented"),
    })
    @RequestMapping(method = RequestMethod.PATCH, path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    GitHubRepository update(@PathVariable Long id, @RequestBody Operation operation, HttpServletResponse response);

    @ApiOperation(value = "get")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "Repository ID", required = true, dataType = "long", paramType = "path")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success", response = GitHubRepository.class),
            @ApiResponse(code = 404, message = "Not Found")
    })
    @RequestMapping(method = RequestMethod.GET, path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    GitHubRepository get(@PathVariable Long id, HttpServletResponse response);

    @ApiOperation(value = "list")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success", response = GitHubRepository.class, responseContainer = "List")
    })
    @RequestMapping(method = RequestMethod.GET, path = "/", produces = MediaType.APPLICATION_JSON_VALUE)
    List<GitHubRepository> list();

    @ApiOperation(value = "delete")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "Repository ID", required = true, dataType = "long", paramType = "path")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 202, message = "Accepted"),
            @ApiResponse(code = 404, message = "Not Found"),
            @ApiResponse(code = 409, message = "Conflict")
    })
    @RequestMapping(method = RequestMethod.DELETE, path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    String delete(@PathVariable Long id, HttpServletResponse response);

    @ApiOperation(value = "list", notes = "To get dead code occurrences for Java language apply following filter: !name.contains('serialVersionUID'). Default page size = 50.")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "Repository ID", required = true, dataType = "long", paramType = "path"),
            @ApiImplicitParam(name = "limit", value = "Page size", dataType = "Integer", paramType = "query"),
            @ApiImplicitParam(name = "page", value = "Page number", dataType = "Integer", paramType = "query"),
            @ApiImplicitParam(name = "filter", value = "Filter", dataType = "String", paramType = "query")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success", response = Occurrence.class, responseContainer = "List"),
            @ApiResponse(code = 404, message = "Bad Request"),
            @ApiResponse(code = 404, message = "Not Found")
    })
    @RequestMapping(method = RequestMethod.GET, path = "/{id}/unused_code/", produces = MediaType.APPLICATION_JSON_VALUE)
    List<Occurrence> list(
            @PathVariable Long id,
            @RequestParam(value = "limit", required = false) Integer limit,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "filter", required = false) String filter,
            HttpServletRequest request,
            HttpServletResponse response
    );

}
