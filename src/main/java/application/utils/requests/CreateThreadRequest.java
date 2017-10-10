package application.utils.requests;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

@SuppressWarnings("unused")
public class CreateThreadRequest {
    private String author;
    private String created;
    private String message;
    private String title;
    private String slug;        //??????????????????????????????????

    @JsonCreator
    public CreateThreadRequest(@JsonProperty(value = "author") String author,
                               @JsonProperty(value = "created") String created,
                               @JsonProperty(value = "message") String message,
                               @JsonProperty(value = "title") String title,
                               @JsonProperty(value = "slug") String slug) {
        this.author = author;
        this.created = created;
        this.message = message;
        this.title = title;
        this.slug = slug;
    }

    public String getAuthor() {
        return author;
    }

    public String getCreated() {
        return created;
    }

    public String getMessage() {
        return message;
    }

    public String getTitle() {
        return title;
    }

    public String getSlug() {
        return slug;
    }
}
