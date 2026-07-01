package com.example.shoppingverse;

import com.example.shoppingverse.Enum.UserRole;
import com.example.shoppingverse.model.User;
import com.example.shoppingverse.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class ShoppingVerseApplication {

	public static void main(String[] args) {
		SpringApplication.run(ShoppingVerseApplication.class, args);
	}


	@Bean
	CommandLineRunner runner(
			UserRepository userRepository,
			PasswordEncoder passwordEncoder) {

		return args -> {

			if (userRepository
					.findByEmail("admin@gmail.com")
					.isEmpty()) {

				User admin_user = User.builder()
						.email("admin@gmail.com")
						.password(
								passwordEncoder.encode("admin123"))
						.role(UserRole.ADMIN)
						.build();

				userRepository.save(admin_user);
			}
		};
	}
}
