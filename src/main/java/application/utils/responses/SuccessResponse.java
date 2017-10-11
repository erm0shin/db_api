package application.utils.responses;

@SuppressWarnings("unused")
public class SuccessResponse {
    private String success;

    public SuccessResponse(String success) {
        this.success = success;
    }

    public String getSuccess() {
        return success;
    }
}
