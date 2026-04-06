package com.panelsv.backend.repository;

import com.panelsv.backend.model.JobNote;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JobNoteRepository extends JpaRepository<JobNote, Long> {

    List<JobNote> findByJobIdOrderByCreatedAtDesc(Long jobId);

}