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

    public String generateToken(User user) {
        return Jwts.builder()
                .setSubject(user.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + validityInMilliseconds))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    public String getUsernameFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }
    public boolean validateToken(String token) {
        try {
            // Valida a assinatura do token e extrai as claims
            Claims claims = Jwts.parser()
                    .setSigningKey("secreta-chave-de-exemplo") // Substitua pela sua chave secreta
                    .parseClaimsJws(token)
                    .getBody();

            // Verifica se o token expirou
            Date expiration = claims.getExpiration();
            if (expiration != null && expiration.before(new Date())) {
                return false;
            }

            return true; // Token é válido
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
