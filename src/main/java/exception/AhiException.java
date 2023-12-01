package exception;


import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class AhiException extends RuntimeException{

    private int result;
    private ErrorCode errorCode;
    private String message;

    public AhiException(ErrorCode errorCode) {
        this.result = errorCode.getStatus();
        this.errorCode = errorCode;
        this.message = errorCode.getMessage();
    }
}