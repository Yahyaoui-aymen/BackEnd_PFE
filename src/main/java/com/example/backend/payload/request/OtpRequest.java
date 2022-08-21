package com.example.backend.payload.request;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;


import java.util.ArrayList;


@Getter
@Setter
public class OtpRequest {


    private String sender;
    @JsonProperty("APIKEY")

    private String apikey;

    @JsonProperty("Messages")
    private ArrayList<Message> messages = new ArrayList<>();

    @Getter
    @Setter
    public static class Message {
        private String number;
        private String message;

        public String getNumber() {
            return number;
        }

        public void setNumber(String number) {
            this.number = number;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}
