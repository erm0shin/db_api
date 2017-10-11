package application.utils.responses;

import application.models.Forum;
import application.models.Post;
import application.models.Thread;
import application.models.User;

@SuppressWarnings("unused")
public class SuccessPostDetailsResponse {
    private User author;
    private Forum forum;
    private Post post;
    private Thread thread;

    public SuccessPostDetailsResponse(User author, Forum forum, Post post, Thread thread) {
        this.author = author;
        this.forum = forum;
        this.post = post;
        this.thread = thread;
    }

    public SuccessPostDetailsResponse() {
        this.author = null;
        this.forum = null;
        this.post = null;
        this.thread = null;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public Forum getForum() {
        return forum;
    }

    public void setForum(Forum forum) {
        this.forum = forum;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    public Thread getThread() {
        return thread;
    }

    public void setThread(Thread thread) {
        this.thread = thread;
    }
}
