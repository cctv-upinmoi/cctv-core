package init.upinmcse.cctvcore.service.impl;

import init.upinmcse.cctvcore.service.I18nMessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.stereotype.Service;

import java.util.Locale;
@Service
@RequiredArgsConstructor
@Slf4j
public class I18nMessageServiceImpl implements I18nMessageService {
    private final MessageSource messageSource;

    @Override
    public String resolveMessage(String key, Locale locale, Object... params) {
        Locale resolvedLocale = resolveLocale(locale);
        try {
            return messageSource.getMessage(key, params, resolvedLocale);
        } catch (NoSuchMessageException e) {
            log.warn("Missing i18n message key: '{}' for locale: {}", key, resolvedLocale);
            return key;
        }

    }

    /**
     * Resolve locale to a supported one, falling back to German (default) if unsupported.
     */
    private Locale resolveLocale(Locale locale) {
        return LanguageType.fromLocale(locale).getLocale();
    }

}

