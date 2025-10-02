package com.memento.server.common.exception;

import static com.memento.server.common.error.ErrorCodes.*;
import static org.springframework.http.HttpStatus.PAYLOAD_TOO_LARGE;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

import com.memento.server.common.error.ErrorResponse;
import com.memento.server.common.error.FieldError;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorResponse> handleMethodArgumentNotValid(MethodArgumentNotValidException e) {
		log.warn("MethodArgumentNotValidException 발생: {}", e.getMessage(), e);
		List<FieldError> errors = e.getBindingResult().getFieldErrors().stream()
			.map(error -> FieldError.of(error.getField(), error.getDefaultMessage()))
			.toList();

		return ResponseEntity.badRequest()
			.body(ErrorResponse.of(INVALID_INPUT_VALUE, errors));
	}

	@ExceptionHandler(BindException.class)
	public ResponseEntity<ErrorResponse> handleBindException(BindException e) {
		log.warn("BindException 발생: {}", e.getMessage(), e);
		List<FieldError> errors = e.getBindingResult().getFieldErrors().stream()
			.map(error -> FieldError.of(error.getField(), error.getDefaultMessage()))
			.toList();

		return ResponseEntity.badRequest()
			.body(ErrorResponse.of(INVALID_INPUT_VALUE, errors));
	}

	@ExceptionHandler(ConstraintViolationException.class)
	public ResponseEntity<ErrorResponse> handleConstraintViolation(ConstraintViolationException e) {
		log.warn("ConstraintViolationException 발생: {}", e.getMessage(), e);
		List<FieldError> errors = e.getConstraintViolations().stream()
			.map(cv -> {
				String propertyPath = cv.getPropertyPath().toString();
				String fieldName = extractFieldName(propertyPath);
				return FieldError.of(fieldName, cv.getMessage());
			})
			.toList();

		return ResponseEntity.badRequest()
			.body(ErrorResponse.of(INVALID_INPUT_VALUE, errors));
	}

	private String extractFieldName(String propertyPath) {
		if (propertyPath.contains(".")) {
			return propertyPath.substring(propertyPath.lastIndexOf(".") + 1);
		}
		return propertyPath;
	}

	@ExceptionHandler(MissingServletRequestPartException.class)
	public ResponseEntity<ErrorResponse> handleMissingServletRequestPart(MissingServletRequestPartException e) {
		log.warn("MissingServletRequestPartException 발생: {}", e.getMessage(), e);
		String field = e.getRequestPartName();
		String message = String.format("%s은(는) 필수입니다.", field);
		return ResponseEntity.badRequest()
			.body(ErrorResponse.of(MISSING_REQUEST_PART, List.of(FieldError.of(field, message))));
	}

	@ExceptionHandler(HandlerMethodValidationException.class)
	public ResponseEntity<ErrorResponse> handleHandlerMethodValidation(HandlerMethodValidationException e) {
		log.warn("HandlerMethodValidationException 발생: {}", e.getMessage(), e);
		List<FieldError> errors = e.getAllErrors().stream()
			.map(error -> {
				if (error instanceof org.springframework.validation.FieldError fieldError) {
					return FieldError.of(fieldError.getField(), fieldError.getDefaultMessage());
				}
				return FieldError.of("global", error.getDefaultMessage());
			})
			.toList();

		return ResponseEntity.badRequest().body(ErrorResponse.of(INVALID_INPUT_VALUE, errors));
	}

	@ExceptionHandler(MultipartException.class)
	public ResponseEntity<ErrorResponse> handleMultipartException(MultipartException e) {
		log.warn("MultipartException 발생: {}", e.getMessage(), e);
		return ResponseEntity.status(PAYLOAD_TOO_LARGE)
			.body(ErrorResponse.of(MULTIPART_TOO_LARGE));
	}

	@ExceptionHandler(MementoException.class)
	public ResponseEntity<ErrorResponse> handleMementoException(MementoException e) {
		log.warn("MementoException 발생: {} - {}", e.getErrorCode().getCode(), e.getErrorCode().getMessage(), e);
		return ResponseEntity.status(e.getErrorCode().getStatus())
			.body(ErrorResponse.of(e.getErrorCode()));
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponse> handleException(Exception e) {
		log.error("Unhandled Exception 발생: {}", e.getMessage(), e);
		return ResponseEntity.internalServerError()
			.body(ErrorResponse.of(INTERNAL_SERVER_ERROR));
	}

	@ExceptionHandler({HttpClientErrorException.class, RestClientResponseException.class})
	public ResponseEntity<ErrorResponse> handleRestClientErrors(RestClientResponseException e) {
		// 카카오 토큰 교환 등 외부 API 4xx 오류를 적절히 매핑하여 500으로 포장되지 않게 함
		String body = e.getResponseBodyAsString();
		log.warn("External API client error: status={}, body={}", e.getStatusCode(), body);
		// Kakao invalid_grant(code 재사용/만료) 등은 400으로 내려 사용자 재시도를 유도
		return ResponseEntity.badRequest().body(ErrorResponse.of(INVALID_INPUT_VALUE));
	}
}
