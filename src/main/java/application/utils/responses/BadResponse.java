package application.utils.responses;

@SuppressWarnings("unused")
public class BadResponse {
    private String error;

    public BadResponse(String error) {
        this.error = error;
    }

    public String getError() {
        return error;
    }
}
