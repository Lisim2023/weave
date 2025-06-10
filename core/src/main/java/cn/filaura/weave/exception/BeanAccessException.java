package cn.filaura.weave.exception;

public class BeanAccessException extends WeaveException {

    public BeanAccessException() {
    }

    public BeanAccessException(String message) {
        super(message);
    }

    public BeanAccessException(String message, Throwable cause) {
        super(message, cause);
    }

    public BeanAccessException(Throwable cause) {
        super(cause);
    }
}
