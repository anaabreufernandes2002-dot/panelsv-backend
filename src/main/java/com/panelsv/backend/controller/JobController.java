package com.panelsv.backend.controller;

import com.panelsv.backend.controller.dto.JobRequest;
import com.panelsv.backend.model.Job;
import com.panelsv.backend.model.JobAttachment;
import com.panelsv.backend.repository.JobAttachmentRepository;
import com.panelsv.backend.repository.JobRepository;
import com.panelsv.backend.service.JobService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/jobs")
public class JobController {

    private final JobService svc;
    private final JobRepository jobRepository;
    private final JobAttachmentRepository attachmentRepository;

    @Value("${app.upload-dir}")
    private String uploadDir;

    public JobController(JobService svc,
                         JobRepository jobRepository,
                         JobAttachmentRepository attachmentRepository) {
        this.svc = svc;
        this.jobRepository = jobRepository;
        this.attachmentRepository = attachmentRepository;
    }

    @GetMapping(value = "/active", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Job> listActive() {
        return svc.listActive();
    }

    @GetMapping(value = "/completed", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Job> listCompleted() {
        return svc.listCompleted();
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Job> create(@RequestBody JobRequest req) {
        return ResponseEntity.ok(svc.create(req));
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Job> update(@PathVariable Long id,
                                      @RequestBody JobRequest req) {
        return ResponseEntity.ok(svc.update(id, req));
    }

    @PostMapping(value = "/{id}/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadAttachment(@PathVariable Long id,
                                              @RequestParam("file") MultipartFile file) throws IOException {

        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Job not found"));

        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("No file selected");
        }

        String contentType = file.getContentType();
        boolean allowed = "application/pdf".equals(contentType)
                || (contentType != null && contentType.startsWith("image/"));

        if (!allowed) {
            return ResponseEntity.badRequest().body("Only image or PDF files are allowed");
        }

        Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        String originalName = file.getOriginalFilename() == null ? "file" : file.getOriginalFilename();
        String safeOriginalName = originalName.replaceAll("[^a-zA-Z0-9\\.\\-_]", "_");
        String safeName = UUID.randomUUID() + "_" + safeOriginalName;

        Path filePath = uploadPath.resolve(safeName).normalize();
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        JobAttachment attachment = new JobAttachment();
        attachment.setFileName(originalName);
        attachment.setStoredName(safeName);
        attachment.setContentType(contentType);
        attachment.setJob(job);

        job.addAttachment(attachment);
        jobRepository.save(job);

        return ResponseEntity.ok(attachment);
    }

    @DeleteMapping("/{jobId}/upload/{attachmentId}")
    public ResponseEntity<?> deleteAttachment(@PathVariable Long jobId,
                                              @PathVariable Long attachmentId) throws IOException {

        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found"));

        JobAttachment attachment = attachmentRepository.findById(attachmentId)
                .orElseThrow(() -> new RuntimeException("Attachment not found"));

        if (attachment.getJob() == null || !attachment.getJob().getId().equals(job.getId())) {
            return ResponseEntity.badRequest().body("Attachment does not belong to this job");
        }

        Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
        Path filePath = uploadPath.resolve(attachment.getStoredName()).normalize();

        Files.deleteIfExists(filePath);

        job.removeAttachment(attachment);
        jobRepository.save(job);

        // apaga o registro no banco de forma explícita
        attachmentRepository.delete(attachment);

        return ResponseEntity.ok("Attachment deleted successfully");
    }

    @PutMapping("/{id}/done")
    public ResponseEntity<Void> markDone(@PathVariable Long id) {
        svc.markDone(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/undo")
    public ResponseEntity<Void> undoDone(@PathVariable Long id) {
        svc.undoDone(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        svc.delete(id);
        return ResponseEntity.noContent().build();
    }
}