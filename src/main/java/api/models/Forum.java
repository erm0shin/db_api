package api.models;

import com.fasterxml.jackson.annotation.JsonIgnore;

@SuppressWarnings("unused")
public class Forum {
    @JsonIgnore
    private Long id;

    private Long posts;
    private String slug;
    private Integer threads;
    private String title;
    private String user;

    public Forum(Long id, Long posts, String slug,
                 Integer threads, String title, String user) {
        this.id = id;
        this.posts = posts;
        this.slug = slug;
        this.threads = threads;
        this.title = title;
        this.user = user;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPosts() {
        return posts;
    }

    public void setPosts(Long posts) {
        this.posts = posts;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public Integer getThreads() {
        return threads;
    }

    public void setThreads(Integer threads) {
        this.threads = threads;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }
}
