package com.tms.dto;

import java.util.List;

public class CategoriesRequest {
    private List<String> categories;

    public CategoriesRequest() {
    }

    public CategoriesRequest(List<String> categories) {
        this.categories = categories;
    }

    public List<String> getCategories() {
        return categories;
    }

    public void setCategories(List<String> categories) {
        this.categories = categories;
    }
}

