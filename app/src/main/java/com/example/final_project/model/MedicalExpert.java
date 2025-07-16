package com.example.final_project.model;

public class MedicalExpert {
    private int id;
    private String name;
    private String specialty;
    // Thêm các trường khác nếu API trả về

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSpecialty() {
        return specialty;
    }

    public void setSpecialty(String specialty) {
        this.specialty = specialty;
    }
}