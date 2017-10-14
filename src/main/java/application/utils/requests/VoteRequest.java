package application.utils.requests;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

@SuppressWarnings("unused")
public class VoteRequest {
    private String nickname;
    private Integer voice;

    @JsonCreator
    public VoteRequest(@JsonProperty(value = "nickname") String nickname,
                       @JsonProperty(value = "voice") Integer voice) {
        this.nickname = nickname;
        this.voice = voice;
    }

    public String getNickname() {
        return nickname;
    }

    public Integer getVoice() {
        return voice;
    }
}
