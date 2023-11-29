package ru.sobse.cloud_storage.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;
import java.util.Collection;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(exclude = "id")
@Getter
@Setter
@Entity
@Table(name = "users")
public class UserDAO implements Serializable, UserDetails {
    private String login;
    private String password;
    private String token;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return AuthorityUtils.createAuthorityList();
    }

    @Override
    public String getUsername() {
        return login;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
