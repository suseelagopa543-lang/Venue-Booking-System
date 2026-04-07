package com.spring.Security;

import com.spring.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean("passwordEncoder")
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    private UserDetailsService userDetailsService;

    private JwtFilter jwtFilter;

    @Autowired
    public SecurityConfig(UserDetailsService userDetailsService, JwtFilter jwtFilter) {
        this.userDetailsService = userDetailsService;
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
         http.csrf(customizer -> customizer.disable());
         http.authorizeHttpRequests(auth -> auth.requestMatchers("/auth/register","/auth/login").permitAll()
                  //Admin has authority on anything
             .requestMatchers("/admin/**").hasAuthority("ROLE_ADMIN") // Admin can access user management endpoints

                 // User methods - only accessible by users
                 .requestMatchers("/user/**").hasAuthority("ROLE_USER") // Users can access their own bookings and methods

                 // Vendors can access their own venues and methods
                 .requestMatchers("/vendor/**").hasAuthority("ROLE_VENDOR") // Vendors can access their own venues and methods


                 // Venues - Users can GET (read) venues, Vendors can manage (GET, POST, PUT, DELETE)
                 .requestMatchers(HttpMethod.GET, "/venues/**").hasAnyRole("USER", "VENDOR", "ADMIN")
                 .requestMatchers(HttpMethod.POST, "/venues/**").hasAnyRole("VENDOR", "ADMIN")
                 .requestMatchers(HttpMethod.PUT, "/venues/**").hasAnyRole("VENDOR", "ADMIN")
                 .requestMatchers(HttpMethod.DELETE, "/venues/**").hasAnyRole("VENDOR", "ADMIN")
                 .anyRequest().authenticated());
         http.httpBasic(Customizer.withDefaults());
         http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                 .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
         return http.build();

    }

    @Bean
    public AuthenticationProvider authProvider(){
        DaoAuthenticationProvider provider=new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

}
