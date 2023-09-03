package com.atlant.project.client;

import com.atlant.project.domain.Specialty;
import com.atlant.project.wrapper.PageableResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.client.RestTemplate;

@Slf4j
public class SpringClient {
    public static void main(String[] args) {
        System.out.println(new BCryptPasswordEncoder().encode("1982"));
        testGetWithRestTemplate();

        ResponseEntity<PageableResponse<Specialty>> exchangeSpecialtyList = new RestTemplate()
                .exchange("http://localhost:8080/specialists?sort=name,desc",
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<PageableResponse<Specialty>>() {});

        log.info("Specialty List {}", exchangeSpecialtyList.getBody());

        Specialty specialty = Specialty.builder().name("Java Developer").build();
        Specialty specSaved = new RestTemplate().postForObject("http://localhost:8080/specialists", specialty, Specialty.class);

        log.info("Spec saved id{}", specSaved.getId());

        Specialty securityEngineer = Specialty.builder().name("Security Engineer").build();
        Specialty securityEngineerSaved = new RestTemplate()
                .exchange("http://localhost:8080/specialists",
                        HttpMethod.POST,
                        new HttpEntity<>(securityEngineer, createJsoHeader()),
                        Specialty.class)
                .getBody();

        log.info("securityEngineer saved id: {}", securityEngineerSaved.getId());

        securityEngineerSaved.setName("QA Manual");
        ResponseEntity<Void> exchangeUpdates = new RestTemplate()
                .exchange("http://localhost:8080/specialists",
                        HttpMethod.PUT,
                        new HttpEntity<>(securityEngineerSaved, createJsoHeader()),
                        Void.class);

        log.info("QA Manual updates status: {}", exchangeUpdates.getStatusCode());

        ResponseEntity<Void> exchangeDeleted = new RestTemplate()
                .exchange("http://localhost:8080/specialists/{id}",
                        HttpMethod.DELETE,
                        null,
                        Void.class,
                        securityEngineerSaved.getId());

        log.info("QA Manual deleted status: {}", exchangeDeleted.getStatusCode());
    }

    private static HttpHeaders createJsoHeader() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    private static void testGetWithRestTemplate() {
        ResponseEntity<Specialty> specialtyResponseEntity = new RestTemplate()
                .getForEntity("http://localhost:8080/specialists/{id}", Specialty.class, 7);

        log.info("Response Entity {}", specialtyResponseEntity);
        log.info("Response Data {}", specialtyResponseEntity.getBody());

        Specialty spec = new RestTemplate()
                .getForObject("http://localhost:8080/specialists/{id}", Specialty.class, 7);

        log.info("Specialty {}", spec);
    }
}
