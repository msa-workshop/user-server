package com.example.userserver.users;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class PostService {

    @Value("${sns.post-server}")
    private String postServerUrl;
    private final RestClient restClient = RestClient.builder().build();

    public boolean deactivate(String id) {
        return Boolean.TRUE.equals(restClient.delete()
                .uri(postServerUrl + "/api/posts/deactivate/" + id)
                .retrieve()
                .onStatus(HttpStatusCode::isError, (request, response) -> {
                    throw new RuntimeException("invalid server response " + response.getStatusText());
                })
                .body(Boolean.class));
    }

    public boolean activate(String id) {
        return Boolean.TRUE.equals(restClient.put()
                .uri(postServerUrl + "/api/posts/activate/" + id)
                .retrieve()
                .onStatus(HttpStatusCode::isError, (request, response) -> {
                    throw new RuntimeException("invalid server response " + response.getStatusText());
                })
                .body(Boolean.class));
    }
}
