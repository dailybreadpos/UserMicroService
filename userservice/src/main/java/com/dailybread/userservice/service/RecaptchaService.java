package com.dailybread.userservice.service;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

// import lombok.Value; // Removed Lombok's Value annotation
import org.springframework.beans.factory.annotation.Value;

@Service
public class RecaptchaService {

    @Value("${recaptcha.secret}")
    private String recaptchaSecret;

    private final RestTemplate restTemplate = new RestTemplate();

    public boolean isCaptchaValid(String token) {
        String url = "https://www.google.com/recaptcha/api/siteverify";

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("secret", recaptchaSecret);
        params.add("response", token);

        ResponseEntity<Map> response = restTemplate.postForEntity(url, params, Map.class);
        Map body = response.getBody();

        return body != null && Boolean.TRUE.equals(body.get("success"));
    }
}
