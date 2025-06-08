package common;

import java.io.Serial;
import java.io.Serializable;

public class CommandResponse implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private final boolean success;
    private final String message;

    public CommandResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }
}