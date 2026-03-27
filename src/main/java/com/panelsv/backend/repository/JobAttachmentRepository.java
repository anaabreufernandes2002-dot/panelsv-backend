package com.panelsv.backend.repository;

import com.panelsv.backend.model.JobAttachment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobAttachmentRepository extends JpaRepository<JobAttachment, Long> {
}