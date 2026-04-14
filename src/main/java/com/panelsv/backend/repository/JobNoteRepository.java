package com.panelsv.backend.repository;

import com.panelsv.backend.model.JobNote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface JobNoteRepository extends JpaRepository<JobNote, Long> {

    List<JobNote> findByJobIdOrderByCreatedAtDesc(Long jobId);

    @Modifying
    @Query("delete from JobNote n where n.job.id = :jobId")
    void deleteByJobId(@Param("jobId") Long jobId);
}