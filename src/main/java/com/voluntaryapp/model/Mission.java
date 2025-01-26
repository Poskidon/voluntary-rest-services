package com.voluntaryapp.model;

public class Mission {
    private int id;
    private int requesterId;
    private Integer volunteerId;
    private String title;
    private String description;
    private String status;
    private String createdAt;

    // Constructeurs
    public Mission() {}

    public Mission(int requesterId, String title, String description) {
        this.requesterId = requesterId;
        this.title = title;
        this.description = description;
        this.status = "PENDING";
    }

    // Getters et Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getRequesterId() { return requesterId; }
    public void setRequesterId(int requesterId) { this.requesterId = requesterId; }

    public Integer getVolunteerId() { return volunteerId; }
    public void setVolunteerId(Integer volunteerId) { this.volunteerId = volunteerId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}