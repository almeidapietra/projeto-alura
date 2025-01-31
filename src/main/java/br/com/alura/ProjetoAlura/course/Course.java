package br.com.alura.ProjetoAlura.course;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Enumerated(EnumType.STRING)
    private Status status = Status.ACTIVE;

    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    private LocalDateTime inactivatedAt;
    private String inactivationReason;
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
        this.inactivationReason = null;
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

    public String getInactivationReason() {
        return inactivationReason;
    }

    public void setInactivationReason(String inactivationReason) {
        this.inactivationReason = inactivationReason;
    }

    public void inactivated() {
        this.status = Status.INACTIVE;
        this.inactivatedAt = LocalDateTime.now();
        this.inactivationReason = inactivationReason;
    }
}