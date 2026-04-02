package org.spartahub.hubservice;

import org.spartahub.common.exception.GlobalExceptionAdviceImpl;
import org.spartahub.config.security.LoginFilter;
import org.spartahub.config.security.SecurityConfigImpl;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import({SecurityConfigImpl.class, LoginFilter.class, GlobalExceptionAdviceImpl.class})
public class HubServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(HubServiceApplication.class, args);
	}

}
