package com.example.demo.auth.security;


import com.example.demo.auth.security.jwt.JwtAuthenticationEntryPoint;
import com.example.demo.auth.security.jwt.JwtTokenFilter;
import com.example.demo.auth.security.services.OAuth2UserService;
import com.example.demo.auth.security.services.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.session.SessionManagementFilter;


@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

  @Configuration
  @Order(2)
  public static class JwtWebSecurityConfig {
    @Autowired
    UserDetailsServiceImpl userDetailsService;

    @Autowired
    private OAuth2UserService oAuth2UserService;


    @Autowired
    private JwtAuthenticationEntryPoint unauthorizedHandler;

    @Bean
    public JwtTokenFilter authenticationJwtTokenFilter() {
      return new JwtTokenFilter();
    }

    // This authentication provider authenticates User with the help of UserDetailsService
    // Based on username/password validation
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
      DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
      authProvider.setUserDetailsService(userDetailsService);
      authProvider.setPasswordEncoder(passwordEncoder());

      return authProvider;
    }

    // Checks with the configured authentication providers on which should authenticate the user
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
      return authConfig.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
      return new BCryptPasswordEncoder();
    }

    @Bean
    CorsFilter corsFilter() {
      CorsFilter filter = new CorsFilter();
      return filter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
      http.csrf().disable();
      http.authorizeHttpRequests().requestMatchers("/oauth2/**","/login/oauth2/**", "/auth/login", "/auth/register").permitAll()
              .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll() // need this or all preflight requests will fail
              .anyRequest().authenticated().and()
              .addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class)
              .authenticationProvider(authenticationProvider())
              .exceptionHandling().authenticationEntryPoint(unauthorizedHandler).and()
              .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

      http.oauth2Login().userInfoEndpoint().userService(oAuth2UserService);
      return http.build();
    }
  }
//
//  @Configuration
//  @Order(1)
//  public static class Oauth2SecurityConfig {
//    @Bean
//
//    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//      http.csrf().disable();
//      http.securityMatcher("/google/**", "/oauth2/**", "/login/oauth2/**").authorizeHttpRequests()
//                      .requestMatchers("/").permitAll()
//                      .anyRequest().authenticated().and().oauth2Login();
//      return http.build();
//    }
//  }

}
