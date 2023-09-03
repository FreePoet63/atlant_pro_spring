package com.atlant.project.integration;

import com.atlant.project.domain.Specialty;
import com.atlant.project.repository.SpecialtyRepository;
import com.atlant.project.wrapper.PageableResponse;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.*;

import java.util.List;
import java.util.Optional;

import static com.atlant.project.util.SpecialtyCreator.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SpecialtyControllerIntegration {
    @Autowired
    @Qualifier(value = "testRestTemplateRoleUser")
    private TestRestTemplate testRestTemplateRoleUser;

    @Autowired
    @Qualifier("testRestTemplateRoleAdmin")
    private TestRestTemplate testRestTemplateRoleAdmin;

    @MockBean
    private SpecialtyRepository specialtyRepository;

    @Lazy
    @TestConfiguration
    static class Config {
        @Bean(name = "testRestTemplateRoleUser")
        public TestRestTemplate testRestTemplateRoleUserCreator(@Value("${local.server.port}") int port) {
            RestTemplateBuilder restTemplateBuilder = new RestTemplateBuilder()
                    .rootUri("http://localhost:" + port)
                    .basicAuthentication("Olesya", "1982");
            return new TestRestTemplate(restTemplateBuilder);
        }

        @Bean(name = "testRestTemplateRoleAdmin")
        public TestRestTemplate testRestTemplateRoleAdminCreator(@Value("${local.server.port}") int port) {
            RestTemplateBuilder restTemplateBuilder = new RestTemplateBuilder()
                    .rootUri("http://localhost:" + port)
                    .basicAuthentication("Nata", "1963");
            return new TestRestTemplate(restTemplateBuilder);
        }
    }

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
    }


    @Test
    @DisplayName("listAll returns a pageable list of specialty when successful")
    public void listAll_ReturnListOfSpecialtyInsidePageObject_WhenSuccessful() {
        String expectedName = createValidSpecialty().getName();
        Page<Specialty> specialtyPage = testRestTemplateRoleUser.exchange("/specialists", HttpMethod.GET,
                null, new ParameterizedTypeReference<PageableResponse<Specialty>>() {}).getBody();
        Assertions.assertThat(specialtyPage).isNotNull();
        Assertions.assertThat(specialtyPage.toList()).isNotEmpty();
        Assertions.assertThat(specialtyPage.toList().get(0).getName()).isEqualTo(expectedName);
    }

    @Test
    @DisplayName("findById returns an specialty when successful")
    public void findById_ReturnListOfSpecialtyInsidePageObject_WhenSuccessful() {
        Long expectedId = createValidSpecialty().getId();
        Specialty spec = testRestTemplateRoleUser.getForObject("/specialists/1", Specialty.class);
        Assertions.assertThat(spec).isNotNull();
        Assertions.assertThat(spec.getId()).isNotNull();
        Assertions.assertThat(spec.getId()).isEqualTo(expectedId);
    }

    @Test
    @DisplayName("findByName returns a list of specialty when successful")
    public void findByName_ReturnListOfSpecialty_WhenSuccessful() {
        String expectedName = createValidSpecialty().getName();
        List<Specialty> specialtyList = testRestTemplateRoleUser.exchange("/specialists/find?name='QA Fullstack'",
                HttpMethod.GET, null, new ParameterizedTypeReference<List<Specialty>>() {}).getBody();
        Assertions.assertThat(specialtyList).isNotNull();
        Assertions.assertThat(specialtyList).isNotEmpty();
        Assertions.assertThat(specialtyList.get(0).getName()).isEqualTo(expectedName);
    }

    @Test
    @DisplayName("save creates an specialty when successful")
    public void save_CreateSpecialty_WhenSuccessful() {
        Long expectedId = createValidSpecialty().getId();
        Specialty specToBeSaved = createSpecialtyToBeSaved();
        Specialty spec = testRestTemplateRoleUser.exchange("/specialists", HttpMethod.POST,
                createJsonHttpEntity(specToBeSaved), Specialty.class).getBody();
        Assertions.assertThat(spec).isNotNull();
        Assertions.assertThat(spec.getId()).isNotNull();
        Assertions.assertThat(spec.getId()).isEqualTo(expectedId);
    }

    @Test
    @DisplayName("delete removes the specialty when successful")
    public void delete_Return403_WhenUserIsNotAdmin() {
        ResponseEntity<Void> responseEntity = testRestTemplateRoleAdmin.exchange("/specialists/admin/1", HttpMethod.DELETE,
                null, Void.class);
        Assertions.assertThat(responseEntity).isNotNull();
        Assertions.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        Assertions.assertThat(responseEntity.getBody()).isNull();
    }

    @Test
    @DisplayName("delete returns forbidden when user does not have the role admin")
    public void delete_RemovesSpecialty_WhenSuccessful() {
        ResponseEntity<Void> responseEntity = testRestTemplateRoleUser.exchange("/specialists/admin/1", HttpMethod.DELETE,
                null, Void.class);
        Assertions.assertThat(responseEntity).isNotNull();
        Assertions.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    @DisplayName("update save updated specialty when successful")
    public void update_SaveUpdateSpecialty_WhenSuccessful() {
        Specialty spec = createValidSpecialty();
        ResponseEntity<Void> responseEntity = testRestTemplateRoleUser.exchange("/specialists", HttpMethod.PUT,
                createJsonHttpEntity(spec), Void.class);
        Assertions.assertThat(responseEntity).isNotNull();
        Assertions.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        Assertions.assertThat(responseEntity.getBody()).isNull();
    }

    private HttpEntity<Specialty> createJsonHttpEntity(Specialty specialty) {
        return new HttpEntity<>(specialty, createJsoHeader());
    }

    private static HttpHeaders createJsoHeader() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }
}
