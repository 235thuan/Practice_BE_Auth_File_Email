package com.example.authenticationauthorization.configuration.security;


import com.example.authenticationauthorization.model.Role;
import com.example.authenticationauthorization.model.User;
import com.example.authenticationauthorization.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


import java.util.Collection;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserService userService;

    @Autowired
    public CustomUserDetailsService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userService.getUserByUsername(username);

        return new org.springframework.security.core.userdetails.User(
                user.getUserName(),
                user.getPassword(),
                mapRolesToAuthorities(user.getRoles()));  // Ensure this returns a UserDetails instance
    }
    // ghép role và permission vào authorities
    private Collection<GrantedAuthority> mapRolesToAuthorities(Set<Role> roles) {
        return roles.stream()
                .flatMap(role -> {
                    // Map role to authority ROLE_ADMIN
                    Stream<GrantedAuthority> roleAuthorities = Stream.of(new SimpleGrantedAuthority("ROLE_" + role.getName()));

                    // Map permissions to authorities , PERMISSION_CREATE , PERMISSION_READ,PERMISSION_UPDATE,PERMISSION_DELETE
                    Stream<GrantedAuthority> permissionAuthorities = role.getPermissions().stream()
                            .map(permission -> new SimpleGrantedAuthority("PERMISSION_" + permission.getNamePermission()));

                    // Combine role and permission authorities
                    return Stream.concat(roleAuthorities, permissionAuthorities);
                })
                .collect(Collectors.toList());
    }
}
