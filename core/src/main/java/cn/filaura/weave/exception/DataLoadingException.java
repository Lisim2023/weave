package cn.filaura.weave.exception;

public class DataLoadingException extends WeaveException {

    public DataLoadingException() {
    }

    public DataLoadingException(String message) {
        super(message);
    }

    public DataLoadingException(String message, Throwable cause) {
        super(message, cause);
    }

    public DataLoadingException(Throwable cause) {
        super(cause);
    }
}
