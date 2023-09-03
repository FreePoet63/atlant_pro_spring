package com.atlant.project.util;

import com.atlant.project.domain.Specialty;
import com.atlant.project.exception.ResourceNotFoundException;
import com.atlant.project.repository.SpecialtyRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class DataUtil {
    public String formatLocalDateTimeToDatabase(LocalDateTime dateTime) {
        return DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(dateTime);
    }

    public Specialty findSpecOrThrowNotFound(Long id, SpecialtyRepository repository) {
        return repository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Specialty Not Found"));
    }
}
