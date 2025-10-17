package com.cartechindia.exception;

import com.cartechindia.dto.response.ApiResponse;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler(ResourceAlreadyExistsException.class)
    public ResponseEntity<Map<String, String>> handleResourceExists(ResourceAlreadyExistsException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("error", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(ExternalApiException.class)
    public ResponseEntity<Map<String, Object>> handleExternalApi(ExternalApiException ex) {
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(Map.of(
                "timestamp", LocalDateTime.now(),
                "status", 502,
                "error", "Bad Gateway",
                "message", ex.getMessage()
        ));
    }


    @ExceptionHandler(InvalidRequestException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidRequest(InvalidRequestException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                "timestamp", LocalDateTime.now(),
                "status", 400,
                "error", "Bad Request",
                "message", ex.getMessage()
        ));
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<String>> handleValidationErrors(MethodArgumentNotValidException ex) {
        String errorMessage = ex.getBindingResult().getAllErrors().get(0).getDefaultMessage();
        return ResponseEntity.badRequest()
                .body(new ApiResponse<>(HttpStatus.BAD_REQUEST.value(), errorMessage, null));
    }

    @ExceptionHandler(InvalidOtpException.class)
    public ResponseEntity<ApiResponse<String>> handleInvalidOtp(InvalidOtpException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST) // 400 → client provided wrong OTP
                .body(new ApiResponse<>(
                        HttpStatus.BAD_REQUEST.value(),
                        ex.getMessage(),
                        null
                ));
    }

    @ExceptionHandler(InactiveUserException.class)
    public ResponseEntity<ApiResponse<String>> handleInvalidOtp(InactiveUserException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST) // 400 → client provided wrong OTP
                .body(new ApiResponse<>(
                        HttpStatus.BAD_REQUEST.value(),
                        ex.getMessage(),
                        null
                ));
    }

    @ExceptionHandler(KycDocumentException.class)
    public ResponseEntity<ApiResponse<String>> handleKycDocument(KycDocumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST) // 400 → invalid/missing KYC docs
                .body(new ApiResponse<>(
                        HttpStatus.BAD_REQUEST.value(),
                        ex.getMessage(),
                        null
                ));
    }

    // Handle duplicate key / unique constraint violations
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponse<String>> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        String message = "Duplicate entry detected. The record already exists.";

        return ResponseEntity.status(HttpStatus.CONFLICT) // 409 Conflict
                .body(new ApiResponse<>(
                        HttpStatus.CONFLICT.value(),
                        message,
                        null
                ));
    }

    @ExceptionHandler(SmsSendException.class)
    public ResponseEntity<ApiResponse<String>> handleSmsSendException(SmsSendException ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR) // 500 → SMS sending failed
                .body(new ApiResponse<>(
                        HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        ex.getMessage(),
                        null
                ));
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ApiResponse<String>> handleUserAlreadyExists(UserAlreadyExistsException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT) // 409 → user already exists
                .body(new ApiResponse<>(
                        HttpStatus.CONFLICT.value(),
                        ex.getMessage(),
                        null
                ));
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<String>> handleResourceNotFound(ResourceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND) // 404 → resource not found
                .body(new ApiResponse<>(
                        HttpStatus.NOT_FOUND.value(),
                        ex.getMessage(),
                        null
                ));
    }

    @ExceptionHandler(OtpGenerationException.class)
    public ResponseEntity<ApiResponse<String>> handleOtpGeneration(OtpGenerationException ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR) // 500 → server failed to generate OTP
                .body(new ApiResponse<>(
                        HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        ex.getMessage(),
                        null
                ));
    }

    @ExceptionHandler(OtpException.class)
    public ResponseEntity<ApiResponse<String>> handleOtpException(OtpException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST) // 400 → general OTP error
                .body(new ApiResponse<>(
                        HttpStatus.BAD_REQUEST.value(),
                        ex.getMessage(),
                        null
                ));
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ApiResponse<String>> handleUserNotFound(UserNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiResponse<>(
                        HttpStatus.NOT_FOUND.value(),
                        ex.getMessage(),
                        null
                ));
    }

    @ExceptionHandler(EmailSendException.class)
    public ResponseEntity<ApiResponse<String>> handleEmailSend(EmailSendException ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        ex.getMessage(),
                        null));
    }

    @ExceptionHandler(InvalidBidException.class)
    public ResponseEntity<ApiResponse<String>> handleInvalidBid(InvalidBidException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponse<>(
                        HttpStatus.BAD_REQUEST.value(),
                        ex.getMessage(),
                        null
                ));
    }

    @ExceptionHandler(OtpExpiredException.class)
    public ResponseEntity<ApiResponse<String>> handleOtpExpired(OtpExpiredException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST) // or HttpStatus.GONE (410)
                .body(new ApiResponse<>(
                        HttpStatus.BAD_REQUEST.value(),
                        ex.getMessage(),
                        null
                ));
    }


    @ExceptionHandler(OtpAlreadyUsedException.class)
    public ResponseEntity<ApiResponse<String>> handleOtpAlreadyUsed(OtpAlreadyUsedException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)  // or HttpStatus.CONFLICT
                .body(new ApiResponse<>(
                        HttpStatus.BAD_REQUEST.value(),
                        ex.getMessage(),
                        null
                ));
    }



    @ExceptionHandler(InvalidRoleException.class)
    public ResponseEntity<ApiResponse<String>> handleInvalidRole(InvalidRoleException ex) {
        return ResponseEntity.badRequest()
                .body(new ApiResponse<>(HttpStatus.BAD_REQUEST.value(), ex.getMessage(), null));
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ApiResponse<String>> handleInvalidCredentials(InvalidCredentialsException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ApiResponse<>(HttpStatus.UNAUTHORIZED.value(), ex.getMessage(), null));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<String>> handleAccessDenied(AccessDeniedException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new ApiResponse<>(HttpStatus.FORBIDDEN.value(), ex.getMessage(), null));
    }

    @ExceptionHandler(BiddingNotActiveException.class)
    public ResponseEntity<ApiResponse<String>> handleBiddingNotActive(BiddingNotActiveException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponse<>(400, ex.getMessage(), null));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse<String>> handleRuntime(RuntimeException ex) {
        return ResponseEntity.badRequest()
                .body(new ApiResponse<>(HttpStatus.BAD_REQUEST.value(), ex.getMessage(), null));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<String>> handleException(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Something went wrong!", null));
    }

}
