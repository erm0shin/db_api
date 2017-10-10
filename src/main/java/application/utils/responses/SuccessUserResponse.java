package application.utils.responses;

import application.models.User;

@SuppressWarnings("unused")
public class SuccessUserResponse {
    private String about;
    private String email;
    private String fullname;
    private String nickname;

    public SuccessUserResponse(User user) {
        this.about = user.getAbout();
        this.email = user.getEmail();
        this.fullname = user.getFullname();
        this.nickname = user.getNickname();
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

    public String getNickname() {
        return nickname;
    }
}
