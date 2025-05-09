package com.matchango.scoutingservice.application;

import com.matchango.scoutingservice.infrastructure.web.dto.LoginRequest;
import com.matchango.scoutingservice.infrastructure.web.dto.LoginResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService {

    @Value("${keycloak.auth-server-url}")
    private String keycloakBaseUrl;

    @Value("${keycloak.realm}")
    private String realm;

    @Value("${keycloak.resource}")
    private String clientId;

    @Value("${keycloak.credentials.secret:}")
    private String clientSecret;

    private final RestTemplate restTemplate = new RestTemplate();

    public LoginResponse loginUser(LoginRequest request) {
        String tokenUrl = keycloakBaseUrl + "/realms/" + realm + "/protocol/openid-connect/token";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("grant_type", "password");
        form.add("client_id", clientId);
        if (clientSecret != null && !clientSecret.isBlank()) {
            form.add("client_secret", clientSecret);
        }
        form.add("username", request.getUsername());
        form.add("password", request.getPassword());

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(form, headers);
        ResponseEntity<Map> response = restTemplate.exchange(tokenUrl, HttpMethod.POST, entity, Map.class);

        Map body = response.getBody();
        return new LoginResponse(
                (String) body.get("access_token"),
                (String) body.get("refresh_token"),
                (String) body.get("token_type"),
                ((Number) body.get("expires_in")).longValue()
        );
    }

    public String getKeycloakRegistrationUrl() {
        return keycloakBaseUrl + "/realms/" + realm + "/protocol/openid-connect/registrations" +
                "?client_id=" + clientId +
                "&response_type=code" +
                "&scope=openid" +
                "&redirect_uri=http://localhost:8081/swagger.html";
    }
}