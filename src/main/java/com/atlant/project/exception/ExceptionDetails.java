package com.atlant.project.exception;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Data
@SuperBuilder
public class ExceptionDetails {
    private String title;
    private int status;
    private String detail;
    private LocalDateTime time;
    private String developerMessage;
}
