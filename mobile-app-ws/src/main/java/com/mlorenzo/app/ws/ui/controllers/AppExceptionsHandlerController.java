package com.mlorenzo.app.ws.ui.controllers;

import java.util.Date;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.mlorenzo.app.ws.exceptions.UserServiceException;
import com.mlorenzo.app.ws.ui.models.responses.ErrorMessage;

@ControllerAdvice
public class AppExceptionsHandlerController {
	
	@ExceptionHandler(value = { UserServiceException.class, NullPointerException.class })
	public ResponseEntity<ErrorMessage> handleSpecificExceptions(Exception ex) {
		// Hay excepciones que no tienen detalles, o descripciones, de los errores ocurridos
		String errorMessageDescription = ex.getLocalizedMessage() != null ? ex.getLocalizedMessage() : ex.toString();
		ErrorMessage errorMessage = new ErrorMessage(new Date(), errorMessageDescription);
		return new ResponseEntity<>(errorMessage, HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorMessage> handleAnyException(Exception ex) {
		// Hay excepciones que no tienen detalles, o descripciones, de los errores ocurridos
		String errorMessageDescription = ex.getLocalizedMessage() != null ? ex.getLocalizedMessage() : ex.toString();
		ErrorMessage errorMessage = new ErrorMessage(new Date(), errorMessageDescription);
		return new ResponseEntity<>(errorMessage, HttpStatus.INTERNAL_SERVER_ERROR);
	}
}
