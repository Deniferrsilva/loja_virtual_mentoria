package jdev.mentoria.lojavirtual.security;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import jdev.mentoria.lojavirtual.service.ImplementacaoUserDetailsService;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class WebConfigSecurity {

    @Autowired
    private ImplementacaoUserDetailsService implementacaoUserDetailsService;
    
    @Autowired
    private  JWTTokenAutenticacaoService jwtTokenAutenticacaoService;
    

    @Bean
    public UserDetailsService userDetailsService(ImplementacaoUserDetailsService implementacaoUserDetailsService) {
        return implementacaoUserDetailsService;
    }
   
    /*Irá consultar o user no banco com Spring Security*/
    @Autowired
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(implementacaoUserDetailsService).passwordEncoder(new BCryptPasswordEncoder());
		
	}

    @Bean
    @Lazy
     SecurityFilterChain securityFilterChain(HttpSecurity http, AuthenticationManager authenticationManager) throws Exception {
    	JWTLoginFilter jwtLoginFilter = new JWTLoginFilter("/login", authenticationManager,jwtTokenAutenticacaoService);

        http.csrf(csrf -> csrf.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                .disable())
		        .authorizeHttpRequests(authorize -> authorize
		        	    .requestMatchers("/").permitAll()
		        	    .requestMatchers("/index").permitAll()
		        	    .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
		        	    .requestMatchers(HttpMethod.POST, "/loja_virtual_mentoria/salvarAcesso").authenticated()
		        	    .anyRequest().authenticated()
		        	
		                )
                .logout(logout -> logout
                                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                                .logoutSuccessUrl("/index")
                )
                .addFilterBefore(jwtLoginFilter, UsernamePasswordAuthenticationFilter.class)  
 //               .addFilterBefore(new JwtApiAutenticacaoFilter(), UsernamePasswordAuthenticationFilter.class);
                .addFilterBefore(new JwtApiAutenticacaoFilter(authenticationManager(null), jwtTokenAutenticacaoService), UsernamePasswordAuthenticationFilter.class);


        System.out.println("passei por aqui securityFilterChain  ");
        return http.build();
    }
    
  
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return web -> {
            // Configurações de URLs a serem ignoradas para autenticação
            // web.ignoring().antMatchers(HttpMethod.GET, "/salvarAcesso", "/deleteAcesso")
            // .antMatchers(HttpMethod.POST, "/salvarAcesso", "/deleteAcesso");
        };
    }


}
