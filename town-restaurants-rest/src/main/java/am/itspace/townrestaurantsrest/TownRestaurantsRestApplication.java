package am.itspace.townrestaurantsrest;

import am.itspace.townrestaurantscommon.entity.Role;
import am.itspace.townrestaurantscommon.entity.User;
import am.itspace.townrestaurantscommon.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

@Async
@SpringBootApplication
@RequiredArgsConstructor
@EntityScan(basePackages = {"am.itspace.townrestaurantscommon.*"})
@EnableJpaRepositories(basePackages = {"am.itspace.townrestaurantscommon.*"})
@ComponentScan(basePackages = {"am.itspace.townrestaurantsrest.*", "am.itspace.townrestaurantscommon.*"})
public class TownRestaurantsRestApplication implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public static void main(String[] args) {
        SpringApplication.run(TownRestaurantsRestApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        Optional<User> byEmail = userRepository.findByEmail("admin@mail.com");
        if (byEmail.isEmpty()) {
            userRepository.save(User.builder()
                    .firstName("admin")
                    .lastName("admin")
                    .email("admin@mail.com")
                    .password(passwordEncoder.encode("admin"))
                    .role(Role.MANAGER)
                    .enabled(true)
                    .build());
        }
    }
}
