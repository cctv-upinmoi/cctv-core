package init.upinmcse.cctvcore.config;

import java.util.Arrays;
import java.util.Locale;

/**
 * Supported languages for i18n.
 * Default language is Vietnamese (VN).
 */
public enum LanguageType {

    EN(Locale.ENGLISH),
    VN(Locale.of("vi"));

    public static final LanguageType DEFAULT = VN;

    private final Locale locale;

    LanguageType(Locale locale) {
        this.locale = locale;
    }

    public Locale getLocale() {
        return locale;
    }

    /**
     * Resolve a LanguageType from a locale, falling back to DEFAULT if not supported.
     */
    public static LanguageType fromLocale(Locale locale) {
        if (locale == null) {
            return DEFAULT;
        }
        return Arrays.stream(values())
                .filter(lt -> lt.locale.getLanguage().equalsIgnoreCase(locale.getLanguage()))
                .findFirst()
                .orElse(DEFAULT);
    }
}