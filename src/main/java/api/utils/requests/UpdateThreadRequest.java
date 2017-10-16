package api.utils.requests;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

@SuppressWarnings("unused")
public class UpdateThreadRequest {
    private String message;
    private String title;

    @JsonCreator
    public UpdateThreadRequest(@JsonProperty(value = "message") String message,
                               @JsonProperty(value = "title") String title) {
        this.message = message;
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public String getTitle() {
        return title;
    }
}
