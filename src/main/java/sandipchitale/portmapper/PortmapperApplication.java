package sandipchitale.portmapper;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.ObjectPostProcessor;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.PortMapperImpl;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Map;

import static org.springframework.security.config.Customizer.withDefaults;

@SpringBootApplication
public class PortmapperApplication {

	public static void main(String[] args) {
		SpringApplication.run(PortmapperApplication.class, args);
	}

	@Controller
	public static class IndexController {

		@GetMapping("/")
		public String index() {
			return "index.html";
		}
	}

	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
		return httpSecurity
				.objectPostProcessor(new PortMapperObjectPostProcessor())
				.authorizeHttpRequests(authorizeRequests -> {
						authorizeRequests.anyRequest().fullyAuthenticated();
					}
				)
				.formLogin(withDefaults())
				.build();
	}

	private static class PortMapperObjectPostProcessor implements ObjectPostProcessor<Object> {
		@Override
		public <O> O postProcess(O object) {
			if (object instanceof LoginUrlAuthenticationEntryPoint loginUrlAuthenticationEntryPoint) {
				PortMapperImpl portMapper = new PortMapperImpl();
				portMapper.setPortMappings(Map.of("80", "80", "8080", "8080"));
				loginUrlAuthenticationEntryPoint.setPortMapper(portMapper);
//				loginUrlAuthenticationEntryPoint.setForceHttps(true);
			}
			return object;
		}
	}
}
