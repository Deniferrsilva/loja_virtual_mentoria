package jdev.mentoria.lojavirtual;

import java.sql.SQLException;
import java.util.List;

import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import jdev.mentoria.lojavirtual.dto.ObjetoErroDto;

@RestControllerAdvice
@ControllerAdvice
public class ControleExcecoes extends ResponseEntityExceptionHandler {
	
	
	public ResponseEntity<Object> handleExceptionCustomEntity (ExceptionMentoriaJava ex){
		
		ObjetoErroDto objetoErroDto = new ObjetoErroDto();
		
		objetoErroDto.setError(ex.getMessage());
		objetoErroDto.setCode(HttpStatus.OK.toString());
		
		return new ResponseEntity<Object>(objetoErroDto, HttpStatus.OK);
	}

    /* Captura exceções gerais do projeto */
    @ExceptionHandler({Exception.class, RuntimeException.class, Throwable.class})    
    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body, HttpHeaders headers,
    		HttpStatusCode statusCode, WebRequest request) {
    	ObjetoErroDto objetoErroDTO = new ObjetoErroDto();
        String msg = ex.getMessage();
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        
        if (ex instanceof MethodArgumentNotValidException) {
            List<ObjectError> list = ((MethodArgumentNotValidException) ex).getBindingResult().getAllErrors();
            msg = "";
            for (ObjectError objectError : list) {
                msg += objectError.getDefaultMessage() + "\n";
            }
        }
        
        objetoErroDTO.setError(msg);
        objetoErroDTO.setCode(status.value() + " ==> " + status.getReasonPhrase());
        
        ex.printStackTrace();
        
        
        return new ResponseEntity<>(objetoErroDTO, status);
    }
    
  

    /* Captura erro na parte de banco */
    @ExceptionHandler({DataIntegrityViolationException.class, 
            ConstraintViolationException.class, SQLException.class})
    public ResponseEntity<Object> handleExceptionDataIntegrity(Exception ex, WebRequest request) {
        
        ObjetoErroDto objetoErroDTO = new ObjetoErroDto();
        
        String msg = "";
        
        if (ex instanceof DataIntegrityViolationException) {
            msg = "Erro de integridade no banco: " +  ((DataIntegrityViolationException) ex).getCause().getCause().getMessage();
        } else if (ex instanceof ConstraintViolationException) {
            msg = "Erro de chave estrangeira: " + ((ConstraintViolationException) ex).getCause().getCause().getMessage();
        } else if (ex instanceof SQLException) {
            msg = "Erro de SQL do Banco: " + ((SQLException) ex).getCause().getCause().getMessage();
        } else {
            msg = ex.getMessage();
        }
        
        objetoErroDTO.setError(msg);
        objetoErroDTO.setCode(HttpStatus.INTERNAL_SERVER_ERROR.toString());
        
        ex.printStackTrace();
        
        return new ResponseEntity<>(objetoErroDTO, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    
    @Override
    protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException ex,
    		HttpHeaders headers, HttpStatusCode status, WebRequest request) {
    	
    	  String supportedMethods = (ex.getSupportedMethods() != null) ? String.join(", ", ex.getSupportedMethods()) : "nenhum";
    	  ObjetoErroDto objetoErroDTO = new ObjetoErroDto();
          objetoErroDTO.setError("Método " + ex.getMethod() + " não é suportado para esta requisição. Metodo suportado: "+ supportedMethods);
          objetoErroDTO.setCode(HttpStatus.METHOD_NOT_ALLOWED.value() + " ==> " + HttpStatus.METHOD_NOT_ALLOWED.getReasonPhrase());
    	
    //	return super.handleHttpRequestMethodNotSupported(ex, headers, status, request);
          return new ResponseEntity<>(objetoErroDTO, status);
    }
}
