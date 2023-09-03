package com.atlant.project.service;

import com.atlant.project.domain.Specialty;
import com.atlant.project.repository.SpecialtyRepository;
import com.atlant.project.util.DataUtil;
import lombok.*;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class SpecialtyService {
    private final DataUtil dataUtil;
    private final SpecialtyRepository repository;

    public Page<Specialty> listAll(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public List<Specialty> findByName(String name) {
        return repository.findByName(name);
    }

    public Specialty findById(Long id) {
        return dataUtil.findSpecOrThrowNotFound(id, repository);
    }

    public Specialty save(Specialty specialty) {
        return repository.save(specialty);
    }

    public void delete(Long id) {
        repository.delete(dataUtil.findSpecOrThrowNotFound(id, repository));
    }

    public void update(Specialty specialty) {
        repository.save(specialty);
    }
}
