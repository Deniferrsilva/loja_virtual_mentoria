package jdev.mentoria.lojavirtual.security;

import java.io.IOException;
import java.security.Key;
import java.util.Date;
import java.util.Base64;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import jdev.mentoria.lojavirtual.ApplicationContextLoad;
import jdev.mentoria.lojavirtual.model.Usuario;
import jdev.mentoria.lojavirtual.repository.UsuarioRepository;

@Service
public class JWTTokenAutenticacaoService {
	private static final long EXPIRATION_TIME = 959990000;
	private static final String SECRET_KEY_STRING = "TFVM2bdMet6pqPO3zEqqgfQChy9qq68aG6tey7iUWcCKqPuU+0AoGv+aJD42fhOHClq93VoIVg/bwtM96l1SQQ==";
	private static final byte[] SECRET_KEY_BYTES = SECRET_KEY_STRING.getBytes();
	private static final String TOKEN_PREFIX = "Bearer";
	private static final String HEADER_STRING = "Authorization";
	private static final Logger logger = LoggerFactory.getLogger(JWTTokenAutenticacaoService.class);

	private Key getSigningKey() {
		return Keys.hmacShaKeyFor(SECRET_KEY_BYTES);
	}

	/* Gera o token e dá a resposta para o cliente com JWT */
	public void addAuthentication(HttpServletResponse response, String username) throws Exception {

		/* Montagem do Token */
		String JWT = Jwts.builder() /* Chama o gerador de token */
				.setSubject(username) /* Adiciona o user */
				.setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
				.signWith(getSigningKey(), SignatureAlgorithm.HS512).compact(); /* Tempo de expiração */

		/*
		 * Exemplo: Bearer
		 * *-/a*dad9s5d6as5d4s5d4s45dsd54s.sd4s4d45s45d4sd54d45s4d5s.ds5d5s5d5s65d6s6d
		 */
		 String token = TOKEN_PREFIX + " " + JWT;

	//	String token = JWT;
		/*
		 * Dá a resposta pra tela e para o cliente, outra API, navegador, aplicativo,
		 * javascript, outra chamada java
		 */
		response.addHeader(HEADER_STRING, token);

		liberacaoCors(response);

		/* Usado para ver no Postman para teste */
		response.getWriter().write("{\"Authorization\": \"" + token + "\"}");

	}

	/* Retorna o usuário validado com token ou caso não seja válido retorna null */
	public Authentication getAuthetication(HttpServletRequest request, HttpServletResponse response) throws IOException {

		String token = request.getHeader(HEADER_STRING);
		logger.info("Token recebido: {}", token);
		System.out.println("Token recebido: " + token);

		try {
		
		if (token != null) {

			String tokenLimpo = token.replace(TOKEN_PREFIX, "").trim();

			/* Faz a validação do token do usuário na requisição e obtém o USER */
			String user = Jwts.parser().setSigningKey(getSigningKey()).build().parseClaimsJws(tokenLimpo).getBody()
					.getSubject(); /* ADMIN ou Alex */
			System.out.println("estou dentro do metodo getAuthetication " + user);
			// System.out.println("estou dentro do metodo getAuthetication "+ tokenLimpo );
			if (user != null) {

				Usuario usuario = ApplicationContextLoad.getApplicationContext().getBean(UsuarioRepository.class)
						.findUserByLogin(user);

				System.out.println("Usuário encontrado: " + (usuario != null ? usuario.getLogin() : "null"));

				if (usuario != null) {
					return new UsernamePasswordAuthenticationToken(usuario.getLogin(), usuario.getSenha(),
							usuario.getAuthorities());
				}
			}
		}
		
		} catch (SignatureException e) {
			// Tratamento específico para SignatureException
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
				response.getWriter().write("Invalid token signature: " + e.getMessage());
	            response.getWriter().write("{\"error\": \"" + e.getMessage() + "\"}");
	            response.getWriter().flush();
	            response.getWriter().close();
        } catch (ExpiredJwtException e) {
        	   response.getWriter().write("Token Esta expirado: " + e.getMessage());
           
        
		}finally {
			liberacaoCors(response);
		}

		
		return null;
	}

	/* Fazendo liberação contra erro de CORS no navegador */
	private void liberacaoCors(HttpServletResponse response) {

		if (response.getHeader("Access-Control-Allow-Origin") == null) {
			response.addHeader("Access-Control-Allow-Origin", "*");
		}

		if (response.getHeader("Access-Control-Allow-Headers") == null) {
			response.addHeader("Access-Control-Allow-Headers", "*");
		}

		if (response.getHeader("Access-Control-Request-Headers") == null) {
			response.addHeader("Access-Control-Request-Headers", "*");
		}

		if (response.getHeader("Access-Control-Allow-Methods") == null) {
			response.addHeader("Access-Control-Allow-Methods", "*");
		}
	}
}
