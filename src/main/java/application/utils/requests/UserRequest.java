package application.utils.requests;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

@SuppressWarnings("unused")
public class UserRequest {
    private String about;
    private String email;
    private String fullname;

    @JsonCreator
    public UserRequest(@JsonProperty(value = "about") String about,
                       @JsonProperty(value = "email") String email,
                       @JsonProperty(value = "fullname") String fullname) {
        this.about = about;
        this.email = email;
        this.fullname = fullname;
    }

    public String getAbout() {
        return about;
    }

    public String getEmail() {
        return email;
    }

    public String getFullname() {
        return fullname;
    }
}
