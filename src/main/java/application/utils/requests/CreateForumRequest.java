package application.utils.requests;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

@SuppressWarnings("unused")
public class CreateForumRequest {
    private String slug;
    private String title;
    private String user;

    @JsonCreator
    public CreateForumRequest(@JsonProperty(value = "slug") String slug,
                              @JsonProperty(value = "title") String title,
                              @JsonProperty(value = "user") String user) {
        this.slug = slug;
        this.title = title;
        this.user = user;
    }

    public String getSlug() {
        return slug;
    }

    public String getTitle() {
        return title;
    }

    public String getUser() {
        return user;
    }
}
