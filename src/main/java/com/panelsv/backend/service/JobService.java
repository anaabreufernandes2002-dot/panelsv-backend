package com.panelsv.backend.service;

import com.panelsv.backend.controller.dto.JobRequest;
import com.panelsv.backend.model.Job;
import com.panelsv.backend.model.Stage;
import com.panelsv.backend.repository.JobAttachmentRepository;
import com.panelsv.backend.repository.JobNoteRepository;
import com.panelsv.backend.repository.JobRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class JobService {

    private final JobRepository repo;
    private final JobAttachmentRepository jobAttachmentRepository;
    private final JobNoteRepository jobNoteRepository;

    public JobService(JobRepository repo,
                      JobAttachmentRepository jobAttachmentRepository,
                      JobNoteRepository jobNoteRepository) {
        this.repo = repo;
        this.jobAttachmentRepository = jobAttachmentRepository;
        this.jobNoteRepository = jobNoteRepository;
    }

    private Stage parseStage(String value) {
        if (value == null || value.isBlank()) {
            return Stage.TO_BE_LOCATED;
        }
        return Stage.valueOf(value.toUpperCase().replace(' ', '_'));
    }

    public List<Job> listActive() {
        return repo.findAllByCompletedFalseOrderByInstallDateAscIdAsc();
    }

    public List<Job> listCompleted() {
        return repo.findAllByCompletedTrueOrderByInstallDateAscIdAsc();
    }

    @Transactional
    public Job create(JobRequest req) {
        Job j = new Job();
        j.setContractor(req.getContractor());
        j.setCustomer(req.getCustomer());
        j.setMaterial(req.getMaterial());
        j.setStage(parseStage(req.getStage()));
        j.setInstallDate(req.getInstallDate());
        j.setNotes(req.getNotes());
        j.setSeller(req.getSeller());
        j.setCompleted(false);
        return repo.save(j);
    }

    @Transactional
    public Job update(Long id, JobRequest req) {
        Job j = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Job not found"));

        j.setContractor(req.getContractor());
        j.setCustomer(req.getCustomer());
        j.setMaterial(req.getMaterial());
        j.setStage(parseStage(req.getStage()));
        j.setInstallDate(req.getInstallDate());
        j.setNotes(req.getNotes());
        j.setSeller(req.getSeller());

        return repo.save(j);
    }

    @Transactional
    public void markDone(Long id) {
        Job j = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Job not found"));
        j.setCompleted(true);
        repo.save(j);
    }

    @Transactional
    public void undoDone(Long id) {
        Job j = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Job not found"));
        j.setCompleted(false);
        repo.save(j);
    }

    @Transactional
    public void delete(Long id) {
        Job job = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Job not found"));

        jobNoteRepository.deleteByJobId(id);
        jobAttachmentRepository.deleteByJobId(id);

        repo.delete(job);
    }
}