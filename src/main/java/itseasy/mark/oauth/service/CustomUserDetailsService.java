package itseasy.mark.oauth.service;

import itseasy.mark.entity.UserEntity;
import itseasy.mark.oauth.entity.UserPrincipal;
import itseasy.mark.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity user = userRepository.findByUsername(username);
        if (user == null) {
            log.info("CustomUserDetailsService.loadUserByUsername = 유저를 찾을 수 없습니다");
            throw new UsernameNotFoundException("유저를 찾을 수 없습니다");
        }

        return UserPrincipal.create(user);
    }
}
