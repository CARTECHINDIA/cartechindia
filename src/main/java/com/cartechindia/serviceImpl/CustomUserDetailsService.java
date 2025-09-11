package com.cartechindia.serviceImpl;

import com.cartechindia.entity.User;
import com.cartechindia.repository.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository repo) {
        this.userRepository = repo;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User u = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));

        // Convert roles from DB into Spring Security authorities
        Collection<SimpleGrantedAuthority> authorities = u.getRole().stream()
                .map(role -> new SimpleGrantedAuthority(role.startsWith("ROLE_") ? role : "ROLE_" + role))
                .collect(Collectors.toList());

        return new CustomUserDetails(u, authorities);
    }
}









//package com.cartechindia.serviceImpl;
//
//import com.cartechindia.entity.User;
//import com.cartechindia.repository.UserRepository;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.core.userdetails.*;
//import org.springframework.stereotype.Service;
//import java.util.Collection;
//
//@Service
//public class CustomUserDetailsService implements UserDetailsService {
//
//    private final UserRepository userRepository;
//    public CustomUserDetailsService(UserRepository repo) {
//        this.userRepository = repo;
//    }
//
//    @Override
//    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
//        User u = userRepository.findByEmail(email)
//                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));
//
//        Collection<SimpleGrantedAuthority> authorities = u.getRole().stream()
//                .map(SimpleGrantedAuthority::new)
//                .toList();
//
//        return new CustomUserDetails(u, authorities); //return custom user
//    }
//
//}
