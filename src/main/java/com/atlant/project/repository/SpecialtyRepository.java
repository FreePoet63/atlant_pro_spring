package com.atlant.project.repository;

import com.atlant.project.domain.Specialty;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SpecialtyRepository extends JpaRepository<Specialty, Long> {
    List<Specialty> findByName(String name);
}
