package cn.filaura.weave.exception;

public class RefDataNotFoundException extends WeaveException {

    public RefDataNotFoundException() {
    }

    public RefDataNotFoundException(String message) {
        super(message);
    }

    public RefDataNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public RefDataNotFoundException(Throwable cause) {
        super(cause);
    }
}
