package com.tms.dto;

import java.util.List;

public class LabelsRequest {
    private List<String> labels;

    public LabelsRequest() {
    }

    public LabelsRequest(List<String> labels) {
        this.labels = labels;
    }

    public List<String> getLabels() {
        return labels;
    }

    public void setLabels(List<String> labels) {
        this.labels = labels;
    }
}

