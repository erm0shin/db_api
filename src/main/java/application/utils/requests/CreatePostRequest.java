package application.utils.requests;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

@SuppressWarnings("unused")
public class CreatePostRequest {
    private Long id;
    private String author;
    private String created;
    private String forum;
    private Boolean isEdited;
    private String message;
    private Long parent;
    private Integer thread;

    @JsonCreator
    public CreatePostRequest(@JsonProperty(value = "id") Long id,
                             @JsonProperty(value = "author") String author,
                             @JsonProperty(value = "created") String created,
                             @JsonProperty(value = "forum") String forum,
                             @JsonProperty(value = "isEdited") Boolean isEdited,
                             @JsonProperty(value = "message") String message,
                             @JsonProperty(value = "parent") Long parent,
                             @JsonProperty(value = "thread") Integer thread) {
        this.id = id;
        this.author = author;
        this.created = created;
        this.forum = forum;
        this.isEdited = isEdited;
        this.message = message;
        this.parent = parent;
        this.thread = thread;
    }

    public Long getId() {
        return id;
    }

    public String getAuthor() {
        return author;
    }

    public String getCreated() {
        return created;
    }

    public String getForum() {
        return forum;
    }

    public Boolean getEdited() {
        return isEdited;
    }

    public String getMessage() {
        return message;
    }

    public Long getParent() {
        return parent;
    }

    public Integer getThread() {
        return thread;
    }

    public void setParent(Long parent) {
        this.parent = parent;
    }
}
