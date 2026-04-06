package com.panelsv.backend.controller;

import com.panelsv.backend.controller.dto.JobNoteRequest;
import com.panelsv.backend.model.Job;
import com.panelsv.backend.model.JobNote;
import com.panelsv.backend.repository.JobNoteRepository;
import com.panelsv.backend.repository.JobRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/jobs")
public class JobNoteController {

    private final JobRepository jobRepository;
    private final JobNoteRepository jobNoteRepository;

    public JobNoteController(JobRepository jobRepository,
                             JobNoteRepository jobNoteRepository) {
        this.jobRepository = jobRepository;
        this.jobNoteRepository = jobNoteRepository;
    }

    @PostMapping("/{jobId}/notes")
    public JobNote addNote(@PathVariable Long jobId,
                           @RequestBody JobNoteRequest req) {

        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Job not found"));

        if (req.getMessage() == null || req.getMessage().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Message is required");
        }

        JobNote note = new JobNote();
        note.setJob(job);
        note.setAuthor(req.getAuthor() != null ? req.getAuthor().trim() : "");
        note.setMessage(req.getMessage().trim());
        note.setSendEmail(Boolean.TRUE.equals(req.getSendEmail()));

        return jobNoteRepository.save(note);
    }

    @GetMapping("/{jobId}/notes")
    public List<JobNote> listNotes(@PathVariable Long jobId) {
        if (!jobRepository.existsById(jobId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Job not found");
        }

        return jobNoteRepository.findByJobIdOrderByCreatedAtDesc(jobId);
    }
}