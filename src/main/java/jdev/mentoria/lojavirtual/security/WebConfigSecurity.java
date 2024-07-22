package jdev.mentoria.lojavirtual.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import jdev.mentoria.lojavirtual.service.ImplementacaoUserDetailsService;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class WebConfigSecurity {

    private final ImplementacaoUserDetailsService implementacaoUserDetailsService;

    public WebConfigSecurity(ImplementacaoUserDetailsService implementacaoUserDetailsService) {
        this.implementacaoUserDetailsService = implementacaoUserDetailsService;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authorizeRequests -> authorizeRequests
                .requestMatchers(HttpMethod.GET, "/salvarAcesso", "/deleteAcesso").permitAll() // URLs que não precisam de autenticação
                .requestMatchers(HttpMethod.POST, "/salvarAcesso", "/deleteAcesso").permitAll() // URLs que não precisam de autenticação
                .anyRequest().authenticated() // Qualquer outra requisição precisa de autenticação
            )
            .formLogin(formLogin -> formLogin
                .loginPage("/login").permitAll() // Página de login personalizada
            )
            .logout(logout -> logout
                .permitAll() // Permitir logout
            );

        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return implementacaoUserDetailsService;
    }
    
    
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(implementacaoUserDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }
}
