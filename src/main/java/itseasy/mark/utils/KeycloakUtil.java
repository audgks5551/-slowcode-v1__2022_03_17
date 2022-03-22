package itseasy.mark.utils;

import lombok.extern.slf4j.Slf4j;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.authorization.client.AuthzClient;
import org.keycloak.authorization.client.Configuration;
import org.keycloak.representations.AccessTokenResponse;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Service;

import javax.ws.rs.core.Response;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class KeycloakUtil {
    private String authServerUrl = "http://localhost:8888/auth/";
    private String refreshTokenUrl = "http://localhost:8888/auth/realms/realmtest/protocol/openid-connect/token";
    private String realm = "Realmtest";
    private String clientId = "itseasy";
    private String role = "ROLE_USER";
    private String clientSecret = "7piKTXFvrmAbZTroaEAG0wMIw53uh9B2";

    public void registerUser(String username, String password) {
        Keycloak keycloak = KeycloakBuilder.builder()
                .serverUrl(authServerUrl)
                .realm("master")
                .clientId("admin-cli")
                .grantType(OAuth2Constants.PASSWORD)
                .username("admin").password("admin")
                .resteasyClient(new ResteasyClientBuilder().connectionPoolSize(10).build()).build();

        keycloak.tokenManager().getAccessToken();

        UserRepresentation user = new UserRepresentation();
        user.setEnabled(true);
        user.setUsername(username);

        RealmResource realmResource = keycloak.realm(realm);
        UsersResource usersResource = realmResource.users();
        Response response = usersResource.create(user);

        if (response.getStatus() == 201) {
            String userId = CreatedResponseUtil.getCreatedId(response);

            log.info("생성된 유저 ID = {}", userId);

            CredentialRepresentation passwordCred = new CredentialRepresentation();
            passwordCred.setTemporary(false);
            passwordCred.setType(CredentialRepresentation.PASSWORD);
            passwordCred.setValue(password);

            UserResource userResource = usersResource.get(userId);

            userResource.resetPassword(passwordCred);

            RoleRepresentation realmRoleUser = realmResource.roles().get(role).toRepresentation();

            userResource.roles().realmLevel().add(Arrays.asList(realmRoleUser));
        }
    }

    public AccessTokenResponse getToken (String username, String password) {
        Map<String, Object> clientCredentials = new HashMap<>();
        clientCredentials.put("secret", clientSecret);
        clientCredentials.put("grant_type", "password");

        Configuration configuration =
                new Configuration(authServerUrl, realm, clientId, clientCredentials, null);

        AuthzClient authzClient = AuthzClient.create(configuration);

        AccessTokenResponse response =
                authzClient.obtainAccessToken(username, password);

        return response;
    }
}
