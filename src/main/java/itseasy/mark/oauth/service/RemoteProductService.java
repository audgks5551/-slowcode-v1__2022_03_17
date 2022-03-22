package itseasy.mark.oauth.service;

import lombok.RequiredArgsConstructor;
import org.keycloak.adapters.springsecurity.client.KeycloakRestTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RemoteProductService {

    private final KeycloakRestTemplate template;

    private String endpoint;

    public List<String> getProducts() {
        ResponseEntity<String[]> response = template.getForEntity(endpoint, String[].class);
        return Arrays.asList(response.getBody());
    }
}
