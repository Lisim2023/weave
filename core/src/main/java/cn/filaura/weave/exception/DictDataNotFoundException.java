package cn.filaura.weave.exception;

public class DictDataNotFoundException extends WeaveException {

    public DictDataNotFoundException() {
    }

    public DictDataNotFoundException(String message) {
        super(message);
    }

    public DictDataNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public DictDataNotFoundException(Throwable cause) {
        super(cause);
    }
}
