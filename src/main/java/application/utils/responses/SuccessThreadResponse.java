package application.utils.responses;

import application.models.Thread;

@SuppressWarnings("unused")
public class SuccessThreadResponse {
    private String author;
    private String created;
    private String forum;
    private Integer id;
    private String message;
    private String slug;
    private String title;
    private Integer votes;

    public SuccessThreadResponse(Thread thread) {
        this.author = thread.getAuthor();
        this.created = thread.getCreated();
        this.forum = thread.getForum();
        this.id = thread.getId();
        this.message = thread.getMessage();
        this.slug = thread.getSlug();
        this.title = thread.getTitle();
        this.votes = thread.getVotes();
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

    public Integer getId() {
        return id;
    }

    public String getMessage() {
        return message;
    }

    public String getSlug() {
        return slug;
    }

    public String getTitle() {
        return title;
    }

    public Integer getVotes() {
        return votes;
    }
}
