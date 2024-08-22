package com.example.authenticationauthorization.dto;

import lombok.Data;

@Data
public class PageRequestDTO {
    private int page;
    private int size;

    // Getters and setters
    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }
}
