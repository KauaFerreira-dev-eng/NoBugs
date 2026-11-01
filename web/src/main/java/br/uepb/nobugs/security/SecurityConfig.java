package br.uepb.nobugs.security;

import java.nio.charset.StandardCharsets;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Decisão registrada: SESSÃO no servidor (cookie), e não token JWT.
 * Motivo: a tela é servida pela própria aplicação Spring Boot (aplicação única, mesmo servidor).
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /** Hash BCrypt: a senha nunca é guardada nem comparada em texto puro. */
    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration configuracao) throws Exception {
        return configuracao.getAuthenticationManager();
    }

    @Bean
    SecurityFilterChain filtros(HttpSecurity http) throws Exception {
        http
            // CSRF desligado por simplicidade: o cookie de sessão usa SameSite=Strict (ver application.properties).
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                // públicas: a tela, o login e o cadastro
                .requestMatchers("/", "/index.html", "/saude", "/auth/login", "/auth/cadastro").permitAll()
                // autorização por papel: a lista completa é do órgão e do administrador
                .requestMatchers(HttpMethod.GET, "/ocorrencias").hasAnyRole("ORGAO", "ADMIN")
                // todo o resto exige estar logado
                .anyRequest().authenticated())
            .exceptionHandling(erros -> erros
                // sem login -> 401 e com login mas sem permissão -> 403, os dois no mesmo formato de erro
                .authenticationEntryPoint((request, response, ex) ->
                        escrever(response, 401, "NAO_AUTENTICADO", "Faça login para continuar."))
                .accessDeniedHandler((request, response, ex) ->
                        escrever(response, 403, "SEM_PERMISSAO", "Você não tem permissão para esta ação.")));
        return http.build();
    }

    private void escrever(jakarta.servlet.http.HttpServletResponse response, int status, String codigo, String mensagem)
            throws java.io.IOException {
        response.setStatus(status);
        response.setContentType("application/json;charset=UTF-8");
        response.getOutputStream().write(
                ("{\"codigo\":\"" + codigo + "\",\"mensagem\":\"" + mensagem + "\",\"campos\":{}}")
                        .getBytes(StandardCharsets.UTF_8));
    }
}
