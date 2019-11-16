package com.exception.handler;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.exception.model.CommonExceptionResponse;
import com.exception.model.ExceptionMessages;

/**
 * This class is responsible to handle centralized exception occurred in Web
 * Services.
 * 
 * @author akash
 *
 */
@ControllerAdvice
public class CommonExceptionHandler extends ResponseEntityExceptionHandler {
	private static final Logger LOG = LoggerFactory.getLogger(CommonExceptionHandler.class);

	/**
	 * This method is responsible to handle exception of type
	 * RecordNotFoundException and build response.
	 * 
	 * @param ex
	 * @return
	 */
	@ExceptionHandler(RecordNotFoundException.class)
	public ResponseEntity<Object> recordNotFoundExceptionHandler(Exception ex) {
		LOG.info("Calling recordNotFoundExceptionHandler() method");
		return buildExceptionResponse(ex, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	/**
	 * This method is responsible to handle Global exceptions for which handler
	 * method is not defined. It will build response for that also.
	 * 
	 * @param ex
	 * @return
	 */
	@ExceptionHandler(Exception.class)
	public ResponseEntity<Object> customExceptionHandler(Exception ex) {
		LOG.info("Calling customExceptionHandler() method");
		return buildExceptionResponse(ex, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	/**
	 * This method is responsible to handle validation over fields. It will build
	 * response with error validation messages.
	 */
	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {
		LOG.info("Calling handleMethodArgumentNotValid() method");
		List<String> error = new ArrayList<>();
		for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
			String errorMessage=String.join(" ", fieldError.getField(),fieldError.getDefaultMessage());
			error.add(errorMessage);
		}
		CommonExceptionResponse response = CommonExceptionResponse.builder()
				.message(ExceptionMessages.FIELD_VALIDATION_FAILED.getMessage()).error(error)
				.timestamp(LocalDateTime.now()).status(HttpStatus.PARTIAL_CONTENT).build();
		return new ResponseEntity<Object>(response, response.getStatus());
	}

	/**
	 * This method is used to build response object for all type of exception
	 * handler.
	 * 
	 * @param ex
	 * @return
	 */
	private ResponseEntity<Object> buildExceptionResponse(Exception ex, HttpStatus status) {
		LOG.info("Building Error response ::");
		CommonExceptionResponse response = CommonExceptionResponse.builder().message(ex.getMessage())
				.timestamp(LocalDateTime.now()).status(status).build();
		LOG.debug("Error Response Generated : " + response);
		return new ResponseEntity<Object>(response, response.getStatus());
	}

	@Override
	@ResponseStatus(value = HttpStatus.BAD_REQUEST)
	protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {
		LOG.info("Calling handleHttpMessageNotReadable() method");
		return buildExceptionResponse(ex, status);

	}

	@Override
	protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {
		LOG.info("Calling handleHttpRequestMethodNotSupported() method");
		return buildExceptionResponse(ex, status);
	}

}
