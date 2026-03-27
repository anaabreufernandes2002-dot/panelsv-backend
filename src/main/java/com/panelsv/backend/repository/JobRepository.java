package com.panelsv.backend.repository;

import com.panelsv.backend.model.Job;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JobRepository extends JpaRepository<Job, Long> {

    // ativos = completed = false
    List<Job> findAllByCompletedFalseOrderByInstallDateAscIdAsc();

    // concluídos = completed = true
    List<Job> findAllByCompletedTrueOrderByInstallDateAscIdAsc();
}
