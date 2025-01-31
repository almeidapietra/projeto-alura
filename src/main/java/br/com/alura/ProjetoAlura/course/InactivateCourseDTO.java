package br.com.alura.ProjetoAlura.course;

import jakarta.validation.constraints.NotBlank;

public class InactivateCourseDTO {

    @NotBlank
    private String inactivationReason;

    public String getInactivationReason() {
        return inactivationReason;
    }

    public void setInactivationReason(String inactivationReason) {
        this.inactivationReason = inactivationReason;
    }
}
