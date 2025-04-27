package com.shopme.admin.user;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordEncoderTest {

	
	@Test
	public void testEncodePassword() {
		BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		String rawPassword = "tho0411@";
		
		String encodedPassword = passwordEncoder.encode(rawPassword);
		System.out.println(encodedPassword);
		boolean match = passwordEncoder.matches(rawPassword, encodedPassword);
		assertThat(match).isTrue();
		
	}
}
