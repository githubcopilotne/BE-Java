package com.shopquanao.bejava.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

// Filter chạy trước mỗi request — đọc và verify JWT token từ C# BE
@Component
public class JwtFilter extends OncePerRequestFilter {

    // Claim types của .NET (URL dài)
    private static final String CLAIM_ROLE = "http://schemas.microsoft.com/ws/2008/06/identity/claims/role";
    // private static final String CLAIM_EMAIL =
    // "http://schemas.xmlsoap.org/ws/2005/05/identity/claims/emailaddress";
    private static final String CLAIM_USER_ID = "http://schemas.xmlsoap.org/ws/2005/05/identity/claims/nameidentifier";

    // Đọc config từ application.properties
    @Value("${jwt.secret-key}")
    private String secretKey;

    @Value("${jwt.issuer}")
    private String issuer;

    @Value("${jwt.audience}")
    private String audience;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        // Lấy header Authorization
        String authHeader = request.getHeader("Authorization");

        // Nếu không có token hoặc không bắt đầu bằng "Bearer " → bỏ qua, để
        // SecurityConfig xử lý
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Cắt bỏ "Bearer " để lấy token
        String token = authHeader.substring(7);

        try {
            // Tạo secret key từ chuỗi (giống key bên C#)
            SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));

            // Parse và verify token
            Claims claims = Jwts.parser()
                    .verifyWith(key)
                    .requireIssuer(issuer)
                    .requireAudience(audience)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            // Lấy role từ token (dùng claim type URL dài của .NET)
            String role = claims.get(CLAIM_ROLE, String.class);

            // Set authentication vào Spring Security context
            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                    claims.get(CLAIM_USER_ID, String.class), // principal = userId
                    null, // credentials
                    List.of(new SimpleGrantedAuthority("ROLE_" + role)) // authorities
            );

            SecurityContextHolder.getContext().setAuthentication(auth);

        } catch (Exception e) {
            // Token không hợp lệ → không set authentication → SecurityConfig sẽ trả 401
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }
}
