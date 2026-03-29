package org.spartahub.userservice;

import lombok.extern.slf4j.Slf4j;
import org.spartahub.common.exception.GlobalExceptionAdviceImpl;
import org.spartahub.config.security.LoginFilter;
import org.spartahub.config.security.SecurityConfigImpl;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@Slf4j
@SpringBootApplication
@Import({SecurityConfigImpl.class, LoginFilter.class, GlobalExceptionAdviceImpl.class})
public class UserServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(UserServiceApplication.class, args);
	}

}
