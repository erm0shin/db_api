package api.models;

@SuppressWarnings("unused")
public class Vote {
    private Long user;
    private Integer thread;
    private Integer voice;

    public Vote(Long user, Integer thread, Integer voice) {
        this.user = user;
        this.thread = thread;
        this.voice = voice;
    }

    public Long getUser() {
        return user;
    }

    public void setUser(Long user) {
        this.user = user;
    }

    public Integer getThread() {
        return thread;
    }

    public void setThread(Integer thread) {
        this.thread = thread;
    }

    public Integer getVoice() {
        return voice;
    }

    public void setVoice(Integer voice) {
        this.voice = voice;
    }
}
