package application.models;

@SuppressWarnings("unused")
public class User {
    private Long id;
    private String email;
    private String fullname;
    private String nickname;
    private String about;

    public User(Long id, String email, String fullname,
                String nickname, String about) {
        this.id = id;
        this.email = email;
        this.fullname = fullname;
        this.nickname = nickname;
        this.about = about;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }
}
