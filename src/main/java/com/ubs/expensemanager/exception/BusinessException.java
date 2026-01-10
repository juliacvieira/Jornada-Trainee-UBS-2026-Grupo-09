package com.ubs.expensemanager.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class BusinessException extends ResponseStatusException {
    
	// Create a serialVersionUID to follow the class request
	private static final long serialVersionUID = 1L;
	
	public BusinessException(String message) {
        super(HttpStatus.BAD_REQUEST, message);
    }
}
