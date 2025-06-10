package cn.filaura.weave.cache;

import cn.filaura.weave.exception.WeaveException;



public class SerializationException extends WeaveException {

    public SerializationException() {
    }

    public SerializationException(String message) {
        super(message);
    }

    public SerializationException(String message, Throwable cause) {
        super(message, cause);
    }

    public SerializationException(Throwable cause) {
        super(cause);
    }
}
