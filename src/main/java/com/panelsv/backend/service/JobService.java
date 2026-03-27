package com.panelsv.backend.service;

import com.panelsv.backend.controller.dto.JobRequest;
import com.panelsv.backend.model.Job;
import com.panelsv.backend.model.Stage;
import com.panelsv.backend.repository.JobRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class JobService {

    private final JobRepository repo;

    public JobService(JobRepository repo) {
        this.repo = repo;
    }

    private Stage parseStage(String value) {
        if (value == null || value.isBlank()) {
            return Stage.TO_BE_LOCATED;
        }
        return Stage.valueOf(value.toUpperCase().replace(' ', '_'));
    }

    // ============ LISTAGEM ============

    public List<Job> listActive() {
        return repo.findAllByCompletedFalseOrderByInstallDateAscIdAsc();
    }

    public List<Job> listCompleted() {
        return repo.findAllByCompletedTrueOrderByInstallDateAscIdAsc();
    }

    // ============ CRIAR ============

    @Transactional
    public Job create(JobRequest req) {
        Job j = new Job();
        j.setContractor(req.getContractor());
        j.setCustomer(req.getCustomer());
        j.setMaterial(req.getMaterial());
        j.setStage(parseStage(req.getStage()));
        j.setInstallDate(req.getInstallDate());
        j.setNotes(req.getNotes());
        j.setCompleted(false);

        // aqui gravamos quem criou o job (pode vir nulo se algo falhar no front)
        j.setSeller(req.getSeller());

        return repo.save(j);
    }

    // ============ EDITAR ============

    @Transactional
    public Job update(long id, JobRequest req) {
        Job j = repo.findById(id).orElseThrow();

        if (req.getContractor() != null) j.setContractor(req.getContractor());
        if (req.getCustomer() != null) j.setCustomer(req.getCustomer());
        if (req.getMaterial() != null) j.setMaterial(req.getMaterial());
        if (req.getStage() != null) j.setStage(parseStage(req.getStage()));
        if (req.getInstallDate() != null) j.setInstallDate(req.getInstallDate());
        if (req.getNotes() != null) j.setNotes(req.getNotes());

        // se algum dia quiser mudar o seller manualmente:
        if (req.getSeller() != null) j.setSeller(req.getSeller());

        return repo.save(j);
    }

    // ============ DONE / UNDO / DELETE ============

    @Transactional
    public void markDone(long id) {
        Job j = repo.findById(id).orElseThrow();
        j.setCompleted(true);
        repo.save(j);
    }

    @Transactional
    public void undoDone(long id) {
        Job j = repo.findById(id).orElseThrow();
        j.setCompleted(false);
        repo.save(j);
    }

    @Transactional
    public void delete(long id) {
        repo.deleteById(id);
    }
}
