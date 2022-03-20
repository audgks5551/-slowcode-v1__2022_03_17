package itseasy.mark.token;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRefreshTokenRepository extends JpaRepository<UserRefreshToken, Long> {
    UserRefreshToken findByUsername(String username);
    UserRefreshToken findByUsernameAndRefreshToken(String username, String refreshToken);
}
