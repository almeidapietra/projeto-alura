package br.com.alura.ProjetoAlura.course;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Enumerated(EnumType.STRING)
    private Status status = Status.ACTIVE;

    private LocalDateTime inactivatedAt;
    private String name;

    @Column(unique = true)
    private String code;

    private String instructor;
    private String description;

    @Deprecated
    public Course() {
    }

    public Course(String name, String code, String instructor, String description) {
        this.name = name;
        this.code = code;
        this.instructor = instructor;
        this.description = description;
        this.inactivatedAt = null;
    }

    public String getName() {
        return name;
    }

    public String getCode() {
        return code;
    }

    public String getInstructor() {
        return instructor;
    }

    public String getDescription() {
        return description;
    }

    public Status getStatus() {
        return status;
    }

    public LocalDateTime getInactivatedAt() {
        return inactivatedAt;
    }

    public void inactivated() {
        this.status = Status.INACTIVE;
        this.inactivatedAt = LocalDateTime.now();
    }
}