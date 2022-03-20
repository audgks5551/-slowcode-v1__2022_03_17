package itseasy.mark.entity;

import itseasy.mark.oauth.entity.ProviderType;
import itseasy.mark.oauth.entity.RoleType;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import static javax.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@Table(name = "users")
@NoArgsConstructor(access = PROTECTED)
public class UserEntity extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "user_id", nullable = false)
    private Long id;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String encryptedPwd;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ProviderType providerType;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private RoleType roleType;

    @Column(unique = true)
    private String email;

    @Column(length = 1)
    private String emailVerifiedYn;

    private String profileImageUrl;

    public void setUsername(String username) {
        this.username = username;
    }

    public void setEncryptedPwd(String encryptedPwd) {
        this.encryptedPwd = encryptedPwd;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setProviderType(ProviderType providerType) {
        this.providerType = providerType;
    }

    public void setRoleType(RoleType roleType) {
        this.roleType = roleType;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setEmailVerifiedYn(String emailVerifiedYn) {
        this.emailVerifiedYn = emailVerifiedYn;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    @Builder
    public UserEntity(String username, String name, ProviderType providerType, RoleType roleType, String email, String emailVerifiedYn, String profileImageUrl, String encryptedPwd) {
        this.username = username;
        this.name = name;
        this.encryptedPwd = encryptedPwd;
        this.providerType = providerType;
        this.roleType = roleType;
        this.email = email;
        this.emailVerifiedYn = emailVerifiedYn;
        this.profileImageUrl = profileImageUrl;
    }
}

