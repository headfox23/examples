package com.example.repomanager.repositories;

import com.example.repomanager.model.repo.Repo;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface RepoRepository extends MongoRepository<Repo, String> {
}
