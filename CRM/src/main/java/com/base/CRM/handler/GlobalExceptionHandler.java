package com.base.CRM.handler;

import com.base.CRM.exception.AlreadyExistsException;
import com.base.CRM.exception.IncorrectCnpjException;
import com.base.CRM.exception.LeadAlreadyExistsException;
import com.base.CRM.exception.NegociacaoClosedException;
import com.base.CRM.exception.StageChangeException;
import com.base.CRM.exception.NotFoundException;
import com.base.CRM.exception.RestErrorClass;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(org.springframework.security.access.AccessDeniedException.class)
  public ResponseEntity<RestErrorClass> handleDenied(RuntimeException ex) {
    return ResponseEntity.status(HttpStatus.FORBIDDEN)
        .body(new RestErrorClass(HttpStatus.FORBIDDEN, ex.getMessage()));
  }

  @ExceptionHandler({IllegalArgumentException.class,
      org.springframework.http.converter.HttpMessageNotReadableException.class})
  public ResponseEntity<RestErrorClass> handleBadInput(Exception ex) {
    String message = ex instanceof IllegalArgumentException ? ex.getMessage() : "Confira os campos enviados.";
    return ResponseEntity.badRequest().body(new RestErrorClass(HttpStatus.BAD_REQUEST, message));
  }

  @ExceptionHandler(org.springframework.orm.ObjectOptimisticLockingFailureException.class)
  public ResponseEntity<RestErrorClass> handleConcurrentChange(Exception ex) {
    return ResponseEntity.status(HttpStatus.CONFLICT)
        .body(new RestErrorClass(HttpStatus.CONFLICT, "O registro foi atualizado por outra operação. Atualize a tela."));
  }

  @ExceptionHandler(org.springframework.dao.DataIntegrityViolationException.class)
  public ResponseEntity<RestErrorClass> handleConstraint(Exception ex) {
    return ResponseEntity.status(HttpStatus.CONFLICT)
        .body(new RestErrorClass(HttpStatus.CONFLICT, "Já existe um registro com estes dados ou um campo excede o tamanho permitido."));
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<Map<String, String>> handleValidationExceptions(
      MethodArgumentNotValidException ex) {
    Map<String, String> errors = new HashMap<>();

    ex.getBindingResult().getAllErrors().forEach((error) -> {
      String fieldName = ((FieldError) error).getField();
      String errorMessage = error.getDefaultMessage();
      errors.put(fieldName, errorMessage);
    });
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
  }

  @ExceptionHandler(NotFoundException.class)
  public ResponseEntity<RestErrorClass> handleNotFoundException(NotFoundException ex) {
    RestErrorClass restErrorClass = new RestErrorClass(HttpStatus.NOT_FOUND, ex.getMessage());
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(restErrorClass);
  }

  @ExceptionHandler(AuthenticationException.class)
  public ResponseEntity<RestErrorClass> handleAuthenticationException(
      AuthenticationException ex) {
    RestErrorClass restErrorClass = new RestErrorClass(HttpStatus.UNAUTHORIZED, "Credenciais inválidas ou sessão expirada.");
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(restErrorClass);
  }

  @ExceptionHandler({AlreadyExistsException.class, NegociacaoClosedException.class, StageChangeException.class,
      com.base.CRM.exception.UserAlreadyRegistered.class,
      com.base.CRM.exception.StatusProximoPassoCantReturnException.class,
      com.base.CRM.exception.ProximoPassoAlreadyConcludeException.class})
  public ResponseEntity<RestErrorClass> handleConflict(RuntimeException ex) {
    return ResponseEntity.status(HttpStatus.CONFLICT)
        .body(new RestErrorClass(HttpStatus.CONFLICT, ex.getMessage()));
  }

  @ExceptionHandler(com.base.CRM.exception.NextContactDateCantBeAfterTodayException.class)
  public ResponseEntity<RestErrorClass> handleInvalidContactDate(RuntimeException ex) {
    return ResponseEntity.badRequest().body(new RestErrorClass(HttpStatus.BAD_REQUEST, ex.getMessage()));
  }

  @ExceptionHandler(IncorrectCnpjException.class)
  public ResponseEntity<RestErrorClass> handleInvalidCnpj(IncorrectCnpjException ex) {
    return ResponseEntity.badRequest()
        .body(new RestErrorClass(HttpStatus.BAD_REQUEST, ex.getMessage()));
  }

  @ExceptionHandler(com.base.CRM.exception.EmpresaAlreadyExistsException.class)
  public ResponseEntity<?> handleCompanyDuplicate(com.base.CRM.exception.EmpresaAlreadyExistsException ex) {
    return ResponseEntity.status(HttpStatus.CONFLICT).body(
        Map.of("message", ex.getMessage(), "empresaId", ex.getLeadId()));
  }

  @ExceptionHandler(LeadAlreadyExistsException.class)
  public ResponseEntity<?> handleLeadAlreadyExistsException(
      LeadAlreadyExistsException ex
  ) {
    return ResponseEntity.status(HttpStatus.CONFLICT).body(
        Map.of("message", ex.getMessage(), "LeadId", ex.getLeadId())
    );
  }


}
