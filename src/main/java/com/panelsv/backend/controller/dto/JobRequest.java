package com.panelsv.backend.controller.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;

public class JobRequest {

    private String contractor;
    private String customer;
    private String material;
    private String stage;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate installDate; // ✅ agora é LocalDate

    private String notes;
    private String seller;

    public String getContractor() {
        return contractor;
    }

    public void setContractor(String contractor) {
        this.contractor = contractor;
    }

    public String getCustomer() {
        return customer;
    }

    public void setCustomer(String customer) {
        this.customer = customer;
    }

    public String getMaterial() {
        return material;
    }

    public void setMaterial(String material) {
        this.material = material;
    }

    public String getStage() {
        return stage;
    }

    public void setStage(String stage) {
        this.stage = stage;
    }

    public LocalDate getInstallDate() {
        return installDate;
    }

    public void setInstallDate(LocalDate installDate) {
        this.installDate = installDate;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getSeller() {
        return seller;
    }

    public void setSeller(String seller) {
        this.seller = seller;
    }
}