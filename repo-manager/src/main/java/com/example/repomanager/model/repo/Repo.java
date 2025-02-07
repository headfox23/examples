package com.example.repomanager.model.repo;

import com.example.repomanager.model.dto.RepoDto;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.UUID;

@Data
@Document(collection = "Repos")
public class Repo {
    @Id
    private String id = UUID.randomUUID().toString();
    private String name;
    private String description;
    private String createdBy;
    private Instant createdDate;

    private String lastUpdatedBy;
    private Instant lastUpdatedDate;

    public RepoDto toDto() {
        RepoDto dto = new RepoDto();
        dto.setId(id);
        dto.setName(name);
        dto.setDescription(description);
        return dto;
    }
}
