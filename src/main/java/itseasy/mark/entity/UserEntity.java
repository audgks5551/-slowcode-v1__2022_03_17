package itseasy.mark.entity;

import itseasy.mark.oauth.entity.ProviderType;
import itseasy.mark.oauth.entity.RoleType;
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
}

