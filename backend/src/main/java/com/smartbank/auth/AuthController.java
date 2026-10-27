package com.smartbank.auth;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Locale;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    public record Credentials(@NotBlank @Email String email, @NotBlank String password) {
    }

    public record Registration(@NotBlank @Email String email, @NotBlank @Size(min = 12, max = 72) String password) {
    }

    public record TokenResponse(String accessToken, String tokenType, long expiresIn) {
    }

    private final UserRepository users;
    private final PasswordEncoder encoder;
    private final JwtEncoder jwt;

    public AuthController(UserRepository users, PasswordEncoder encoder, JwtEncoder jwt) {
        this.users = users;
        this.encoder = encoder;
        this.jwt = jwt;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    @Transactional
    public TokenResponse register(@Valid @RequestBody Registration request) {
        String email = request.email().trim().toLowerCase(Locale.ROOT);
        if (users.existsByEmail(email))
            throw new DuplicateEmailException();
        AppUser user = users.saveAndFlush(new AppUser(email, encoder.encode(request.password())));
        return token(user);
    }

    @PostMapping("/login")
    public TokenResponse login(@Valid @RequestBody Credentials request) {
        String email = request.email().trim().toLowerCase(Locale.ROOT);
        AppUser user = users.findByEmail(email).orElseThrow(() -> new BadCredentialsException("Invalid credentials"));
        if (!encoder.matches(request.password(), user.getPasswordHash()))
            throw new BadCredentialsException("Invalid credentials");
        return token(user);
    }

    private TokenResponse token(AppUser user) {

        Instant now = Instant.now();

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("smartbank-local")
                .issuedAt(now)
                .expiresAt(now.plus(30, ChronoUnit.MINUTES))
                .subject(user.getId().toString())
                .claim("role", user.getRole())
                .build();

        // Explicitly specify HS256 for JWT signing
        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256)
                .build();

        String accessToken = jwt.encode(
                JwtEncoderParameters.from(header, claims)).getTokenValue();

        return new TokenResponse(accessToken, "Bearer", 1800);
    }
}
