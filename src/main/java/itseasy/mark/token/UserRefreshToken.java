package itseasy.mark.token;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class UserRefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long refreshTokenSeq;

    @Column(unique = true, nullable = false)
    private String username;

    private String refreshToken;

    public UserRefreshToken(String userId, String refreshToken) {
        this.username = userId;
        this.refreshToken = refreshToken;
    }
}
