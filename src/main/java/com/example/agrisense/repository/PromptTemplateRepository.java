package com.example.agrisense.repository;

import com.example.agrisense.entity.PromptTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface PromptTemplateRepository extends JpaRepository<PromptTemplate, Long> {
    Optional<PromptTemplate> findByIsDefaultTrue();
    Optional<PromptTemplate> findByName(String name);
}
