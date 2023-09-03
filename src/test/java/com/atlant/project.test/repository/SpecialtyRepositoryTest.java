package com.atlant.project.repository;

import com.atlant.project.domain.Specialty;
import jakarta.validation.ConstraintViolationException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;

import static com.atlant.project.util.SpecialtyCreator.createSpecialtyToBeSaved;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@DisplayName("Specialty Repository Tests")
//@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class SpecialtyRepositoryTest {
    @Autowired
    private SpecialtyRepository specialtyRepository;

    @Test
    @DisplayName("Save creates specialty when successful")
    public void save_PersistSpec_WhenSuccessful() {
        Specialty spec = createSpecialtyToBeSaved();

        Specialty saveSpec = this.specialtyRepository.save(spec);
        Assertions.assertThat(saveSpec.getId()).isNotNull();
        Assertions.assertThat(saveSpec.getName()).isNotNull();
        Assertions.assertThat(saveSpec.getName()).isEqualTo(spec.getName());
    }

    @Test
    @DisplayName("Save updated specialty when successful")
    public void save_UpdateSpec_WhenSuccessful() {
        Specialty spec = createSpecialtyToBeSaved();

        Specialty saveSpec = this.specialtyRepository.save(spec);
        saveSpec.setName("My Love IT");
        Specialty updatedSpec = this.specialtyRepository.save(saveSpec);

        Assertions.assertThat(saveSpec.getId()).isNotNull();
        Assertions.assertThat(saveSpec.getName()).isNotNull();
        Assertions.assertThat(saveSpec.getName()).isEqualTo(updatedSpec.getName());
    }

    @Test
    @DisplayName("Delete removes specialty when successful")
    public void delete_RemoveSpec_WhenSuccessful() {
        Specialty spec = createSpecialtyToBeSaved();

        Specialty saveSpec = this.specialtyRepository.save(spec);
        this.specialtyRepository.delete(spec);
        Optional<Specialty> specOptional = this.specialtyRepository.findById(saveSpec.getId());

        Assertions.assertThat(specOptional.isEmpty()).isTrue();
    }

    @Test
    @DisplayName("Find by name returns specialty when successful")
    public void findByName_ReturnSpec_WhenSuccessful() {
        Specialty spec = createSpecialtyToBeSaved();

        Specialty saveSpec = this.specialtyRepository.save(spec);
        String name = saveSpec.getName();
        List<Specialty> specialtyList = this.specialtyRepository.findByName(name);

        Assertions.assertThat(specialtyList).isNotEmpty();
        Assertions.assertThat(specialtyList).contains(saveSpec);
    }

    @Test
    @DisplayName("Find by name returns empty list when no specialty is found")
    public void findByName_ReturnEmptyList_WhenSpecialtyNotFound() {
        String name = "fake name";
        List<Specialty> specialtyList = this.specialtyRepository.findByName(name);
        Assertions.assertThat(specialtyList).isEmpty();
    }

    @Test
    @DisplayName("Save throw ConstraintViolationException when name is empty")
    public void save_ThrowConstraintViolationException_WhenNameIsEmpty() {
        Specialty spec = new Specialty();
        /*Assertions.assertThatThrownBy(() -> specialtyRepository.save(spec))
                .isInstanceOf(ConstraintViolationException.class);*/

        Assertions.assertThatExceptionOfType(ConstraintViolationException.class)
                .isThrownBy(() -> specialtyRepository.save(spec))
                .withMessageContaining("The name of this specialty cannot be empty");

    }
}