package itseasy.mark.api.controller;

import itseasy.mark.oauth.service.RemoteProductService;
import lombok.RequiredArgsConstructor;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/")
@RequiredArgsConstructor
public class TestController {

    private final RemoteProductService remoteProductService;

    @GetMapping("/test")
    public String RemoteProductService(HttpServletRequest request) {
        KeycloakAuthenticationToken principal = (KeycloakAuthenticationToken) request.getUserPrincipal();
        String s = principal.getName();
        System.out.println("s = " + s);
        return s;
    }

    @GetMapping("/testtest")
    public String test2() {
        return "제발되자!";
    }
}
