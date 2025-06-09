package tiil.edu.cuoiki.model;

public class LogMessage {
    private String message;
    private boolean isError;

    public LogMessage(String message, boolean isError) {
        this.message = message;
        this.isError = isError;
    }

    public String getMessage() {
        return message;
    }

    public boolean isError() {
        return isError;
    }
} 