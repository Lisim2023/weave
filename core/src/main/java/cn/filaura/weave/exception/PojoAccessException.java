package cn.filaura.weave.exception;

public class PojoAccessException extends WeaveException {

    public PojoAccessException() {
    }

    public PojoAccessException(String message) {
        super(message);
    }

    public PojoAccessException(String message, Throwable cause) {
        super(message, cause);
    }

    public PojoAccessException(Throwable cause) {
        super(cause);
    }
}
