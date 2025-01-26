package com.voluntaryapp.model;

public class User {
    private int id;
    private String email;
    private String password;
    private String name;
    private String role;
    private String address;
    private String phoneNumber;

    // Constructeurs
    public User() {}

    public User(String email, String password, String name, String role, String address, String phoneNumber) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.role = role;
        this.address = address;
        this.phoneNumber = phoneNumber;
    }

    // Getters et Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
}