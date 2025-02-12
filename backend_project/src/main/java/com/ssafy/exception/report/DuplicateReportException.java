package com.ssafy.exception.report;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class DuplicateReportException extends RuntimeException {
    public DuplicateReportException(String message) {
        super(message);
    }
}
