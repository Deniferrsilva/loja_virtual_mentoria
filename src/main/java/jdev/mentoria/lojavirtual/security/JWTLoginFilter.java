package jdev.mentoria.lojavirtual.security;

import java.io.IOException;
import java.nio.file.AccessDeniedException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jdev.mentoria.lojavirtual.model.Usuario;

public class JWTLoginFilter extends UsernamePasswordAuthenticationFilter {

	@Autowired
	private final JWTTokenAutenticacaoService jwtTokenAutenticacaoService;

	public JWTLoginFilter(String url, AuthenticationManager authenticationManager,
			JWTTokenAutenticacaoService jwtTokenAutenticacaoService) {

		setFilterProcessesUrl(url);
		/* Obriga a autenticat a url */
		// super(new AntPathRequestMatcher(url));

		System.out.println("passei por aqui JWTLoginFilter " + url);

		/* Gerenciador de autenticao */
		setAuthenticationManager(authenticationManager);

		this.jwtTokenAutenticacaoService = jwtTokenAutenticacaoService;

		// setAuthenticationManager(authenticationManager);
		// setFilterProcessesUrl(url);

		System.out.println("passei por aqui JWTLoginFilter " + url);
	}

	/* Retorna o usuário ao processr a autenticacao */
	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
			throws AuthenticationException {
		/* Obter o usuário */
		Usuario user = null;
		try {
			user = new ObjectMapper().readValue(request.getInputStream(), Usuario.class);
			System.out.println("estou dentro do metodo getInputStream " + request.getInputStream());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println("estou dentro do metodo attemptAuthentication " + user.getLogin() + " - " + user.getSenha());

		/* Retorna o user com login e senha */
		return getAuthenticationManager()
				.authenticate(new UsernamePasswordAuthenticationToken(user.getLogin(), user.getSenha()));
	}

	@Override
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
			Authentication authResult) throws IOException, ServletException {

		try {

			jwtTokenAutenticacaoService.addAuthentication(response, authResult.getName());
			System.out.println("estou dentro do metodo successfulAuthentication " + authResult.getName());

			// jwtTokenAutenticacaoService.getAuthetication(request,response);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                              AuthenticationException failed) throws IOException, ServletException {
        // Verificar o tipo de exceção para determinar o código de status apropriado
        if (failed instanceof BadCredentialsException) {
            // Falha de autorização (403 Forbidden)
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().write("Usuario e senha nao encontrado: " + failed.getMessage());
        } else {
            // Falha de autenticação (401 Unauthorized)
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Authentication failed: " + failed.getMessage());
        }

        // Logar a falha de autenticação/autorização
        System.out.println("Unsuccessful authentication/authorization attempt: " + failed.getMessage());

        // Também é possível adicionar cabeçalhos ou realizar outras ações, conforme necessário
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        // Chamar o método da superclasse para finalizar o processamento
    //   super.unsuccessfulAuthentication(request, response, failed);
    }

}
