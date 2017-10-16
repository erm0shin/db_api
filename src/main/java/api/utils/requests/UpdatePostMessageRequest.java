package api.utils.requests;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

@SuppressWarnings("unused")
public class UpdatePostMessageRequest {
    private String message;

    @JsonCreator
    public UpdatePostMessageRequest(@JsonProperty(value = "message") String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
