package com.atlant.project.util;

import com.atlant.project.domain.Specialty;

public class SpecialtyCreator {
    public static Specialty createSpecialtyToBeSaved() {
        return Specialty
                .builder()
                .name("QA Fullstack")
                .build();
    }

    public static Specialty createValidSpecialty() {
        return Specialty
                .builder()
                .name("QA Fullstack")
                .id(1L)
                .build();
    }

    public static Specialty createValidUpdatedSpecialty() {
        return Specialty
                .builder()
                .name("QA Fullstack")
                .id(1L)
                .build();
    }
}
