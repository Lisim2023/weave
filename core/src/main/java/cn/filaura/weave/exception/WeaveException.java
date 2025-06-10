package cn.filaura.weave.exception;

public class WeaveException extends RuntimeException {

    public WeaveException() {
    }

    public WeaveException(String message) {
        super(message);
    }

    public WeaveException(String message, Throwable cause) {
        super(message, cause);
    }

    public WeaveException(Throwable cause) {
        super(cause);
    }
}
