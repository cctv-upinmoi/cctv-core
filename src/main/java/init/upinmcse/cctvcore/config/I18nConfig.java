package init.upinmcse.cctvcore.config;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

import java.util.List;

@Configuration
public class I18nConfig {

    @Bean(name = "messageSource")
    public MessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource =
                new ReloadableResourceBundleMessageSource();
        messageSource.setBasenames("classpath:i18n/message");
        messageSource.setDefaultEncoding("UTF-8");
        messageSource.setDefaultLocale(LanguageType.VN.getLocale());
        return messageSource;
    }

    @Bean
    public LocaleResolver localeResolver() {
        AcceptHeaderLocaleResolver resolver = new AcceptHeaderLocaleResolver();
        resolver.setDefaultLocale(LanguageType.VN.getLocale());
        resolver.setSupportedLocales(List.of(
                LanguageType.VN.getLocale(),
                LanguageType.EN.getLocale()
        ));
        return resolver;
    }
}
