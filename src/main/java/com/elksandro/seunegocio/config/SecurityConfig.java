package com.elksandro.seunegocio.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.elksandro.seunegocio.security.SecurityFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final SecurityFilter securityFilter;

    public SecurityConfig(SecurityFilter securityFilter) {
        this.securityFilter = securityFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> {
                })
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(request -> {
                    // ROTAS PÚBLICAS
                    request.requestMatchers("/", "/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html", "/error").permitAll();
                    request.requestMatchers(HttpMethod.POST, "/v1/user/register").permitAll();
                    request.requestMatchers(HttpMethod.POST, "/v1/user/login").permitAll();
                    request.requestMatchers(HttpMethod.GET, "/v1/businesses/**").permitAll();
                    request.requestMatchers(HttpMethod.GET, "/v1/items/**").permitAll();
                    request.requestMatchers(HttpMethod.GET, "/v1/reviews/item/**").permitAll(); 

                    // ROTAS AUTENTICADAS GERAIS (CLIENTES E VENDEDORES)
                    request.requestMatchers(HttpMethod.POST, "/v1/user/favorite/**").authenticated();
                    request.requestMatchers(HttpMethod.GET, "/v1/user/favorite").authenticated();
                    request.requestMatchers(HttpMethod.GET, "/v1/user/me").authenticated();
                    request.requestMatchers(HttpMethod.PATCH, "/v1/user/**").authenticated();
                    request.requestMatchers(HttpMethod.DELETE, "/v1/user/**").authenticated();
                    
                    request.requestMatchers("/v1/cart/**").authenticated();
                    request.requestMatchers("/v1/orders/**").authenticated();
                    
                    // Rotas para Criar/Deletar comentários
                    request.requestMatchers("/v1/reviews/**").authenticated();

                    // ROTAS RESTRITAS APENAS PARA VENDEDORES
                    request.requestMatchers(HttpMethod.POST, "/v1/businesses").authenticated();
                    request.requestMatchers(HttpMethod.PATCH, "/v1/businesses/**").hasAuthority("ROLE_SELLER");
                    request.requestMatchers(HttpMethod.DELETE, "/v1/businesses/**").hasAuthority("ROLE_SELLER");
                    request.requestMatchers(HttpMethod.POST, "/v1/items").hasAuthority("ROLE_SELLER");
                    request.requestMatchers(HttpMethod.PATCH, "/v1/items/**").hasAuthority("ROLE_SELLER");
                    request.requestMatchers(HttpMethod.DELETE, "/v1/items/**").hasAuthority("ROLE_SELLER");
                    request.requestMatchers(HttpMethod.GET, "/v1/orders/seller").hasAuthority("ROLE_SELLER");

                    // ROTA EXCLUSIVA DO ADMINISTRADOR
                    request.requestMatchers("/v1/admin/**").hasAuthority("ROLE_ADMIN");

                    request.anyRequest().authenticated();
                })
                .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration auth) throws Exception {
        return auth.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}