package com.atlant.project.exception;

import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class ValidationExceptionDetails extends ExceptionDetails{
    private String fields;
    private String fieldsMessage;
}
