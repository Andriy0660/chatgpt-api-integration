package com.example.chatboot;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;


@SpringBootApplication
public class ChatbootApplication {
    private static final String API_URL = "https://api.openai.com/v1/chat/completions";
    private static final String API_KEY = "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";
    public static void main(String[] args) {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .connectTimeout(120, TimeUnit.SECONDS)
                .readTimeout(120, TimeUnit.SECONDS)
                .writeTimeout(120, TimeUnit.SECONDS)
                .build();
        MediaType mediaType = MediaType.parse("application/json");

        // Запит до API ChatGPT
        JSONObject requestBody = new JSONObject();
        requestBody.put("model", "gpt-3.5-turbo");
        requestBody.put("temperature", 0.2);
        JSONArray messagesArray = new JSONArray();

        JSONObject systemMessage = new JSONObject();
        systemMessage.put("role", "system");
        systemMessage.put("content", "You are a helpful assistant.");
        messagesArray.put(systemMessage);

        Scanner scanner = new Scanner(System.in);
        System.out.print("Введіть своє запитання до AZN GPT: ");
        String msg = scanner.nextLine();
        if(msg.matches(".*<\\d>$")){
            requestBody.put("n", Integer.parseInt(msg.substring(msg.length()-2,msg.length()-1)));
        }
        JSONObject userMessage1 = new JSONObject();
        userMessage1.put("role", "user");
        userMessage1.put("content", msg);
        messagesArray.put(userMessage1);

        requestBody.put("messages", messagesArray);

        RequestBody body = RequestBody.create(mediaType, requestBody.toString());
        Request request = new Request.Builder()
                .url(API_URL)
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + API_KEY)
                .post(body)
                .build();

        try {
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                String responseBody =response.body().string() ;

                List<String> chatGptResponse = extractResponseFromJsonArray(responseBody);

                // Відображення відповіді користувачу
                System.out.println(chatGptResponse);
            } else {
                System.out.println("Помилка: " + response.code() + " " + response.message());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private static List<String> extractResponseFromJsonArray(String jsonResponse) throws JsonProcessingException {
        JSONObject jsonObject = new JSONObject(jsonResponse);
        ObjectMapper objectMapper = new ObjectMapper();
        JSONArray choices = jsonObject.getJSONArray("choices");
        List<Choice> choicesList = objectMapper.readValue(choices.toString(), new TypeReference<List<Choice>>() {});
        return choicesList.stream().map(i->i.getMessage().getContent()).toList();
    }

}
