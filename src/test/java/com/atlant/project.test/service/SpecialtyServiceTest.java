package com.atlant.project.service;

import com.atlant.project.domain.Specialty;
import com.atlant.project.exception.ResourceNotFoundException;
import com.atlant.project.repository.SpecialtyRepository;
import com.atlant.project.util.DataUtil;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.springframework.data.domain.*;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.*;

import static com.atlant.project.util.SpecialtyCreator.*;

@ExtendWith(SpringExtension.class)
class SpecialtyServiceTest {
    @InjectMocks
    private SpecialtyService specialtyService;
    @Mock
    private SpecialtyRepository specialtyRepository;
    @Mock
    private DataUtil utilMock;

    @BeforeEach
    public void setUp() {
        PageImpl<Specialty> specialtyPage = new PageImpl<>(List.of(createValidSpecialty()));
        BDDMockito.when(specialtyRepository.findAll(ArgumentMatchers.any(PageRequest.class)))
                .thenReturn(specialtyPage);
        BDDMockito.when(specialtyRepository.findById(ArgumentMatchers.anyLong()))
                .thenReturn(Optional.of(createValidSpecialty()));
        BDDMockito.when(specialtyRepository.findByName(ArgumentMatchers.anyString()))
                .thenReturn(List.of(createValidSpecialty()));
        BDDMockito.when(specialtyRepository.save(createSpecialtyToBeSaved()))
                .thenReturn(createValidSpecialty());
        BDDMockito.doNothing().when(specialtyRepository).delete(ArgumentMatchers.any(Specialty.class));
        BDDMockito.when(specialtyRepository.save(createValidSpecialty()))
                .thenReturn(createValidUpdatedSpecialty());
        BDDMockito.when(utilMock.findSpecOrThrowNotFound(ArgumentMatchers.anyLong(),
                        ArgumentMatchers.any(SpecialtyRepository.class)))
                .thenReturn(createValidSpecialty());
    }

    @Test
    @DisplayName("listAll returns a pageable list of specialty when successful")
    public void listAll_ReturnListOfSpecialtyInsidePageObject_WhenSuccessful() {
        String expectedName = createValidSpecialty().getName();
        Page<Specialty> specialtyPage = specialtyService.listAll(PageRequest.of(1,1));
        Assertions.assertThat(specialtyPage).isNotNull();
        Assertions.assertThat(specialtyPage.toList()).isNotEmpty();
        Assertions.assertThat(specialtyPage.toList().get(0).getName()).isEqualTo(expectedName);
    }

    @Test
    @DisplayName("findById returns an specialty when successful")
    public void findById_ReturnListOfSpecialtyInsidePageObject_WhenSuccessful() {
        Long expectedId = createValidSpecialty().getId();
        Specialty spec = specialtyService.findById(1L);
        Assertions.assertThat(spec).isNotNull();
        Assertions.assertThat(spec.getId()).isNotNull();
        Assertions.assertThat(spec.getId()).isEqualTo(expectedId);
    }

    @Test
    @DisplayName("findByName returns a pageable list of specialty when successful")
    public void findByName_ReturnListOfSpecialtyInsidePageObject_WhenSuccessful() {
        String expectedName = createValidSpecialty().getName();
        List<Specialty> specialtyList = specialtyService.findByName("DBS");
        Assertions.assertThat(specialtyList).isNotNull();
        Assertions.assertThat(specialtyList).isNotEmpty();
        Assertions.assertThat(specialtyList.get(0).getName()).isEqualTo(expectedName);
    }

    @Test
    @DisplayName("save creates an specialty when successful")
    public void save_CreateSpecialty_WhenSuccessful() {
        Long expectedId = createValidSpecialty().getId();
        Specialty specToBeSaved = createSpecialtyToBeSaved();
        Specialty spec = specialtyService.save(specToBeSaved);
        Assertions.assertThat(spec).isNotNull();
        Assertions.assertThat(spec.getId()).isNotNull();
        Assertions.assertThat(spec.getId()).isEqualTo(expectedId);
    }

    @Test
    @DisplayName("delete removes the specialty when successful")
    public void delete_RemovesSpecialty_WhenSuccessful() {
        Assertions.assertThatCode(() -> specialtyService.delete(1L))
                .doesNotThrowAnyException();

    }

    @Test
    @DisplayName("delete throw ResourceNotFoundException when the specialty does not exist")
    public void delete_ThrowResourceNotFoundException_WhenTheSpecialtyDoesNotExist() {
        BDDMockito.when(utilMock.findSpecOrThrowNotFound(ArgumentMatchers.anyLong(),
                        ArgumentMatchers.any(SpecialtyRepository.class)))
                .thenThrow(new ResourceNotFoundException("Specialty Not Found"));
        Assertions.assertThatExceptionOfType(ResourceNotFoundException.class)
                .isThrownBy(() -> specialtyService.delete(1L));

    }

    @Test
    @DisplayName("save updating update specialty when successful")
    public void save_SaveUpdateSpecialty_WhenSuccessful() {
        Specialty validUpdateSpecialty = createValidUpdatedSpecialty();
        String expectedName = validUpdateSpecialty.getName();
        Specialty spec = specialtyService.save(createValidSpecialty());
        Assertions.assertThat(spec).isNotNull();
        Assertions.assertThat(spec.getId()).isNotNull();
        Assertions.assertThat(spec.getName()).isEqualTo(expectedName);
    }
}