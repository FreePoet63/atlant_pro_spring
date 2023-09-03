package com.atlant.project.controller;

import com.atlant.project.domain.Specialty;
import com.atlant.project.service.SpecialtyService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.springframework.data.domain.*;
import org.springframework.http.*;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static com.atlant.project.util.SpecialtyCreator.*;

@ExtendWith(SpringExtension.class)
class SpecialtyControllerTest {
    @InjectMocks
    private SpecialtyController specialtyController;
    @Mock
    private SpecialtyService specialtyService;

    @BeforeEach
    public void setUp() {
        PageImpl<Specialty> specialtyPage = new PageImpl<>(List.of(createValidSpecialty()));
        BDDMockito.when(specialtyService.listAll(ArgumentMatchers.any()))
                .thenReturn(specialtyPage);
        BDDMockito.when(specialtyService.findById(ArgumentMatchers.anyLong()))
                .thenReturn(createValidSpecialty());
        BDDMockito.when(specialtyService.findByName(ArgumentMatchers.anyString()))
                .thenReturn(List.of(createValidSpecialty()));
        BDDMockito.when(specialtyService.save(createSpecialtyToBeSaved()))
                .thenReturn(createValidSpecialty());
        BDDMockito.doNothing().when(specialtyService).delete(ArgumentMatchers.anyLong());
        BDDMockito.when(specialtyService.save(createValidSpecialty()))
                .thenReturn(createValidUpdatedSpecialty());
    }

    @Test
    @DisplayName("listAll returns a pageable list of specialty when successful")
    public void listAll_ReturnListOfSpecialtyInsidePageObject_WhenSuccessful() {
        String expectedName = createValidSpecialty().getName();
        Page<Specialty> specialtyPage = specialtyController.listAll(null).getBody();
        Assertions.assertThat(specialtyPage).isNotNull();
        Assertions.assertThat(specialtyPage.toList()).isNotEmpty();
        Assertions.assertThat(specialtyPage.toList().get(0).getName()).isEqualTo(expectedName);
    }

    @Test
    @DisplayName("findById returns an specialty when successful")
    public void findById_ReturnListOfSpecialtyInsidePageObject_WhenSuccessful() {
        Long expectedId = createValidSpecialty().getId();
        Specialty spec = specialtyController.findById(1L, null).getBody();
        Assertions.assertThat(spec).isNotNull();
        Assertions.assertThat(spec.getId()).isNotNull();
        Assertions.assertThat(spec.getId()).isEqualTo(expectedId);
    }

    @Test
    @DisplayName("findByName returns a pageable list of specialty when successful")
    public void findByName_ReturnListOfSpecialtyInsidePageObject_WhenSuccessful() {
        String expectedName = createValidSpecialty().getName();
        List<Specialty> specialtyList = specialtyController.findByName("DBS").getBody();
        Assertions.assertThat(specialtyList).isNotNull();
        Assertions.assertThat(specialtyList).isNotEmpty();
        Assertions.assertThat(specialtyList.get(0).getName()).isEqualTo(expectedName);
    }

    @Test
    @DisplayName("save creates an specialty when successful")
    public void save_CreateSpecialty_WhenSuccessful() {
        Long expectedId = createValidSpecialty().getId();
        Specialty specToBeSaved = createSpecialtyToBeSaved();
        Specialty spec = specialtyController.save(specToBeSaved).getBody();
        Assertions.assertThat(spec).isNotNull();
        Assertions.assertThat(spec.getId()).isNotNull();
        Assertions.assertThat(spec.getId()).isEqualTo(expectedId);
    }

    @Test
    @DisplayName("delete removes the specialty when successful")
    public void delete_RemovesSpecialty_WhenSuccessful() {
        ResponseEntity<Void> responseEntity = specialtyController.delete(1L);
        Assertions.assertThat(responseEntity).isNotNull();
        Assertions.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        Assertions.assertThat(responseEntity.getBody()).isNull();
    }

    @Test
    @DisplayName("update save updated specialty when successful")
    public void update_SaveUpdateSpecialty_WhenSuccessful() {
        ResponseEntity<Void> responseEntity = specialtyController.update(createValidSpecialty());
        Assertions.assertThat(responseEntity).isNotNull();
        Assertions.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        Assertions.assertThat(responseEntity.getBody()).isNull();
    }
}