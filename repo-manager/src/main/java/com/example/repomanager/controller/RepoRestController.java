package com.example.repomanager.controller;

import com.example.repomanager.model.dto.RepoDto;
import com.example.repomanager.model.repo.Repo;
import com.example.repomanager.repositories.RepoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static com.example.repomanager.config.SecurityRoles.SCOPE_ROLE_REPO_READ;
import static com.example.repomanager.config.SecurityRoles.SCOPE_ROLE_REPO_STORE;

@Slf4j
@RequestMapping(RootRestController.ROOT_URL + "/repos")
@RestController
@RequiredArgsConstructor
public class RepoRestController extends RootRestController {

    private final RepoRepository repoRepository;

    @PostMapping(value = "", consumes = "application/json")
    @PreAuthorize("hasAnyAuthority('" + SCOPE_ROLE_REPO_STORE + "')")
    public RepoDto addRepo(@RequestBody RepoDto dto) {
        Repo repo = new Repo();
        repo.setName(dto.getName());
        repo.setDescription(dto.getDescription());
        repo.setCreatedBy("rest-api");
        repo.setCreatedDate(Instant.now());
        return repoRepository.save(repo).toDto();
    }

    @GetMapping(value = "", produces = "application/json")
    @PreAuthorize("hasAnyAuthority('" + SCOPE_ROLE_REPO_READ + "')")
    public List<RepoDto> getRepos() {
        return repoRepository.findAll().stream().map(Repo::toDto).toList();
    }

    @DeleteMapping(value = "{id}", produces = "application/json")
    public void deleteRepo(@PathVariable String id) {
        repoRepository.deleteById(id);
    }
}
