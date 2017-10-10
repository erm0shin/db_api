package application.models;

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

    public Post(Long id, String author, String created, String forum,
                Boolean isEdited, String message, Long parent, Integer thread) {
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

    public Boolean getEdited() {
        return isEdited;
    }

    public void setEdited(Boolean edited) {
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
}
