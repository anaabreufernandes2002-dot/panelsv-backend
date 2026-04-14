package com.panelsv.backend.controller;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.panelsv.backend.controller.dto.JobRequest;
import com.panelsv.backend.model.Job;
import com.panelsv.backend.model.JobAttachment;
import com.panelsv.backend.repository.JobAttachmentRepository;
import com.panelsv.backend.repository.JobRepository;
import com.panelsv.backend.service.JobService;
import jakarta.transaction.Transactional;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/jobs")
public class JobController {

    private final JobService svc;
    private final JobRepository jobRepository;
    private final JobAttachmentRepository attachmentRepository;
    private final Cloudinary cloudinary;

    public JobController(JobService svc,
                         JobRepository jobRepository,
                         JobAttachmentRepository attachmentRepository,
                         Cloudinary cloudinary) {
        this.svc = svc;
        this.jobRepository = jobRepository;
        this.attachmentRepository = attachmentRepository;
        this.cloudinary = cloudinary;
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
    @Transactional
    public ResponseEntity<?> uploadAttachment(@PathVariable Long id,
                                              @RequestParam("file") MultipartFile file) throws IOException {

        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Job not found"));

        if (file == null || file.isEmpty()) {
            return ResponseEntity.badRequest().body("No file selected");
        }

        String contentType = file.getContentType();
        boolean allowed = "application/pdf".equals(contentType)
                || (contentType != null && contentType.startsWith("image/"));

        if (!allowed) {
            return ResponseEntity.badRequest().body("Only image or PDF files are allowed");
        }

        Map<?, ?> uploadResult = cloudinary.uploader().upload(
                file.getBytes(),
                ObjectUtils.asMap(
                        "resource_type", "auto",
                        "folder", "panelsv/jobs/" + id
                )
        );

        String secureUrl = (String) uploadResult.get("secure_url");
        String publicId = (String) uploadResult.get("public_id");

        JobAttachment attachment = new JobAttachment();
        attachment.setFileName(file.getOriginalFilename());
        attachment.setStoredName(publicId);
        attachment.setContentType(contentType);
        attachment.setFileUrl(secureUrl);
        attachment.setJob(job);

        JobAttachment saved = attachmentRepository.save(attachment);

        return ResponseEntity.ok(saved);
    }

    @DeleteMapping("/{jobId}/upload/{attachmentId}")
    @Transactional
    public ResponseEntity<?> deleteAttachment(@PathVariable Long jobId,
                                              @PathVariable Long attachmentId) throws IOException {

        JobAttachment attachment = attachmentRepository.findByIdAndJob_Id(attachmentId, jobId)
                .orElseThrow(() -> new RuntimeException("Attachment not found for this job"));

        String resourceType = resolveResourceType(attachment.getContentType());

        if (attachment.getStoredName() != null && !attachment.getStoredName().isBlank()) {
            cloudinary.uploader().destroy(
                    attachment.getStoredName(),
                    ObjectUtils.asMap(
                            "resource_type", resourceType,
                            "invalidate", true
                    )
            );
        }

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

    private String resolveResourceType(String contentType) {
        if (contentType != null && contentType.startsWith("image/")) {
            return "image";
        }
        return "raw";
    }
}