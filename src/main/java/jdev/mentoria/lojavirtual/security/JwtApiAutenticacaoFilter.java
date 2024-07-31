package jdev.mentoria.lojavirtual.security;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import ch.qos.logback.core.subst.Token;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/*Filtro onde todas as requisicoes serão capturadas para autenticar*/
public class JwtApiAutenticacaoFilter extends GenericFilterBean {

	@Autowired
	private final JWTTokenAutenticacaoService jwtTokenAutenticacaoService;

	public JwtApiAutenticacaoFilter(AuthenticationManager authenticationManager,
			JWTTokenAutenticacaoService jwtTokenAutenticacaoService) {
		super();
		this.jwtTokenAutenticacaoService = jwtTokenAutenticacaoService;
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		/* Estabele a autenticao do user */
		System.out.println("Passei por aqui " + request.toString());
		Authentication authentication = new JWTTokenAutenticacaoService().getAuthetication((HttpServletRequest) request,
				(HttpServletResponse) response);

		/* Coloca o processo de autenticacao para o spring secutiry */
		SecurityContextHolder.getContext().setAuthentication(authentication);

		chain.doFilter(request, response);

	}

}