package com.example.demo;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class DemoApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        RestTemplate restTemplate = new RestTemplate();

        // 1. Prepare request body (your details)
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("name", "T. Shanmukha Naga Sai");  // ✅ your name
        requestBody.put("regNo", "22BCE7010");             // ✅ your reg no
        requestBody.put("email", "siva.shambunagar@gmail.com"); // ✅ your email

        // 2. Prepare headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // 3. Wrap body + headers
        HttpEntity<Map<String, String>> entity = new HttpEntity<>(requestBody, headers);

        // 4. Send first POST request → to generate webhook & token
        String url = "https://bfhldevapigw.healthrx.co.in/hiring/generateWebhook/JAVA";
        ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            System.out.println("Webhook Response: " + response.getBody());

            // Extract webhook + accessToken
            String webhookUrl = (String) response.getBody().get("webhook");
            String accessToken = (String) response.getBody().get("accessToken");

            // 5. ✅ Write your SQL query here (replace with actual solution!)
            String finalQuery = "SELECT * FROM employees;";

            // 6. Prepare headers with token
            HttpHeaders headers2 = new HttpHeaders();
            headers2.setContentType(MediaType.APPLICATION_JSON);
            headers2.set("Authorization", "Bearer " + accessToken);// JWT token

            // 7. Wrap query + headers
            Map<String, String> sqlBody = new HashMap<>();
            sqlBody.put("finalQuery", finalQuery);

            HttpEntity<Map<String, String>> entity2 = new HttpEntity<>(sqlBody, headers2);

            // 8. Send SQL query to webhook
            ResponseEntity<String> result = restTemplate.postForEntity(webhookUrl, entity2, String.class);

            System.out.println("Submission Result: " + result.getBody());
        }
    }
}
