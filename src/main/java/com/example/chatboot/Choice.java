package com.example.chatboot;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Choice {
    @JsonProperty("index")

    private Integer index;
    @JsonProperty("message")

    private Message message;
    @JsonProperty("finish_reason")

    private String finish_reason;

    public Choice(Integer index, Message message, String finish_reason) {
        this.index = index;
        this.message = message;
        this.finish_reason = finish_reason;
    }

    public Choice() {
    }
}
