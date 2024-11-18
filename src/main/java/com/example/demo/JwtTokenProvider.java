package com.example.demo;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtTokenProvider {

    private String secretKey = "secret"; // Use um segredo forte para produção
    private long validityInMilliseconds = 3600000; // 1 hora

    // Gera o token JWT
    public String generateToken(User user) {
        return Jwts.builder()
                .setSubject(user.getUsername())  // Define o nome do usuário como sujeito
                .setIssuedAt(new Date())         // Define a data de emissão
                .setExpiration(new Date(System.currentTimeMillis() + validityInMilliseconds)) // Define a data de expiração
                .signWith(SignatureAlgorithm.HS256, secretKey) // Assina o token com a chave secreta
                .compact();
    }

    // Obtém o nome de usuário a partir do token
    public String getUsernameFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    // Valida o token JWT
    public boolean validateToken(String token) {
        try {
            // Analisa o token e verifica sua assinatura
            Claims claims = Jwts.parser()
                    .setSigningKey(secretKey)
                    .parseClaimsJws(token)
                    .getBody();

            // Verifica se o token expirou
            Date expiration = claims.getExpiration();
            if (expiration != null && expiration.before(new Date())) {
                return false;  // Se o token expirou, ele é inválido
            }

            return true;  // O token é válido
        } catch (SignatureException e) {
            // Assinatura inválida
            System.out.println("Token com assinatura inválida: " + e.getMessage());
            return false;
        } catch (Exception e) {
            // Outros erros (token inválido ou malformado)
            System.out.println("Erro ao validar token: " + e.getMessage());
            return false;
        }
    }
}
