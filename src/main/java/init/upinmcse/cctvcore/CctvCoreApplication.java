package init.upinmcse.cctvcore;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class CctvCoreApplication {

    public static void main(String[] args) {
        SpringApplication.run(CctvCoreApplication.class, args);
    }

}
