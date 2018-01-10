package api.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@SuppressWarnings("unused")
public class Post {
    private Long id;
    private String author;
    private String created;
    private String forum;
    private Boolean isEdited;
    private String message;
    private Long parent;
    private Integer thread;

    @JsonIgnore
    private List<Long> path;

    @SuppressWarnings("unchecked")
    public <T> Post(Long id, String author, String created, String forum,
                    Boolean isEdited, String message, Long parent, Integer thread, List<T> path) {
        this.id = id;
        this.author = author;
        this.created = created;
        this.forum = forum;
        this.isEdited = isEdited;
        this.message = message;
        this.parent = parent;
        this.thread = thread;
        this.path = (List<Long>)path;
    }

    @JsonCreator
    public Post(@JsonProperty(value = "id") Long id,
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

    public void setId(Long id) {
        this.id = id;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getForum() {
        return forum;
    }

    public void setForum(String forum) {
        this.forum = forum;
    }

    public Boolean getIsEdited() {
        return isEdited;
    }

    public void setIsEdited(Boolean edited) {
        isEdited = edited;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getParent() {
        return parent;
    }

    public void setParent(Long parent) {
        this.parent = parent;
    }

    public Integer getThread() {
        return thread;
    }

    public void setThread(Integer thread) {
        this.thread = thread;
    }

    public List<Long> getPath() {
        return path;
    }

    public void setPath(List<Long> path) {
        this.path = path;
    }
}
