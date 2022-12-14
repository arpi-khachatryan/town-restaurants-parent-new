package am.itspace.townrestaurantsweb.serviceWeb.impl;

import am.itspace.townrestaurantscommon.entity.User;
import am.itspace.townrestaurantscommon.entity.VerificationToken;
import am.itspace.townrestaurantscommon.repository.VerificationTokenRepository;
import am.itspace.townrestaurantsweb.serviceWeb.VerificationTokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class VerificationTokenServiceImpl implements VerificationTokenService {

    private final VerificationTokenRepository tokenRepository;

    @Override
    public VerificationToken createToken(User user) {
        log.info("Token successfully created");
        return tokenRepository.save(VerificationToken.builder()
                .plainToken(UUID.randomUUID().toString())
                .expiresAt(LocalDateTime.now().plus(12, ChronoUnit.HOURS))
                .user(user)
                .build());
    }

    @Override
    public VerificationToken findByPlainToken(String plainToken) {
        Optional<VerificationToken> tokenOptional = tokenRepository.findByPlainToken(plainToken);
        if(tokenOptional.isEmpty()){
            log.info("Token not found");
            throw new IllegalStateException("Token doesn't exist");
        }
        VerificationToken token = tokenOptional.get();
        if (token.getExpiresAt().isBefore(LocalDateTime.now())){
            delete(token);
            log.info("Token not found");
            throw new IllegalStateException("Token doesn't exist");
        }
        log.info("Token successfully found");
        return token;
    }

    @Override
    public void delete(VerificationToken token) {
        tokenRepository.delete(token);
        log.info("The token has been successfully deleted");
    }
}
