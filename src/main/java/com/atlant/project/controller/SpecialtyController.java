package com.atlant.project.controller;

import com.atlant.project.domain.Specialty;
import com.atlant.project.service.SpecialtyService;
import com.atlant.project.util.DataUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("specialists")
@Slf4j
@RequiredArgsConstructor
public class SpecialtyController {
    private final DataUtil dataUtil;
    private final SpecialtyService service;

    @GetMapping
    @Operation(summary = "List all specialists paginated and sorted",
    description = "To use pagination and sort and params ?page='number'&sort='field' to the url",
    tags = {"specialty"})
    public ResponseEntity<Page<Specialty>> listAll(Pageable pageable) {
        //log.info("Date formatted {}", dataUtil.formatLocalDateTimeToDatabase(LocalDateTime.now()));
        return ResponseEntity.ok(service.listAll(pageable));
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<Specialty> findById(@PathVariable Long id, @AuthenticationPrincipal UserDetails details) {
        log.info("User logged is {}", details);
        return ResponseEntity.ok(service.findById(id));
    }

    @GetMapping(path = "/find")
    public ResponseEntity<List<Specialty>> findByName(@RequestParam(value = "name") String name) {
        return ResponseEntity.ok(service.findByName(name));
    }

    @PostMapping
    public ResponseEntity<Specialty> save(@RequestBody @Valid Specialty specialty) {
        return ResponseEntity.ok(service.save(specialty));
    }

    @DeleteMapping(path = "/admin/{id}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Successful operation"),
            @ApiResponse(responseCode = "404", description = "Not found")
    })
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping
    public ResponseEntity<Void> update(@RequestBody Specialty specialty) {
        service.update(specialty);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
