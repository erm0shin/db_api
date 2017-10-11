package application.utils.responses;

import application.models.Forum;

@SuppressWarnings("unused")
public class ForumResponse {
    private Long posts;
    private String slug;
    private Integer threads;
    private String title;
    private String user;

    public ForumResponse(Forum forum) {
        this.posts = forum.getPosts();
        this.slug = forum.getSlug();
        this.threads = forum.getThreads();
        this.title = forum.getTitle();
        this.user = forum.getUser();
    }

    public Long getPosts() {
        return posts;
    }

    public String getSlug() {
        return slug;
    }

    public Integer getThreads() {
        return threads;
    }

    public String getTitle() {
        return title;
    }

    public String getUser() {
        return user;
    }
}
