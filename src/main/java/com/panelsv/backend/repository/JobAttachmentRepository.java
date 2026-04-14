package com.panelsv.backend.repository;

import com.panelsv.backend.model.JobAttachment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface JobAttachmentRepository extends JpaRepository<JobAttachment, Long> {

    @Modifying
    @Query("delete from JobAttachment a where a.job.id = :jobId")
    void deleteByJobId(@Param("jobId") Long jobId);

    Optional<JobAttachment> findByIdAndJob_Id(Long id, Long jobId);
}