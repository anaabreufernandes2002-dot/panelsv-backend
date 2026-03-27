package com.panelsv.backend.repository;

import com.panelsv.backend.model.Job;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SuggestionRepository extends JpaRepository<Job, Long> {

    @Query("SELECT DISTINCT j.contractor " +
            "FROM Job j " +
            "WHERE j.contractor IS NOT NULL AND j.contractor <> '' " +
            "AND LOWER(j.contractor) LIKE LOWER(CONCAT(:prefix, '%')) " +
            "ORDER BY j.contractor ASC")
    List<String> findContractorsByPrefix(@Param("prefix") String prefix);

    @Query("SELECT DISTINCT j.material " +
            "FROM Job j " +
            "WHERE j.material IS NOT NULL AND j.material <> '' " +
            "AND LOWER(j.material) LIKE LOWER(CONCAT(:prefix, '%')) " +
            "ORDER BY j.material ASC")
    List<String> findMaterialsByPrefix(@Param("prefix") String prefix);
}
