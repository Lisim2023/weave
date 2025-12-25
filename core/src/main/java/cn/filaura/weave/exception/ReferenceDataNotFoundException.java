package cn.filaura.weave.exception;

public class ReferenceDataNotFoundException extends WeaveException {

    public ReferenceDataNotFoundException() {
    }

    public ReferenceDataNotFoundException(String message) {
        super(message);
    }

    public ReferenceDataNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public ReferenceDataNotFoundException(Throwable cause) {
        super(cause);
    }
}
