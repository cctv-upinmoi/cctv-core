package init.upinmcse.cctvcore.service;

import java.util.Locale;

/**
 * Service interface for resolving internationalized messages. Default locale is Vietnamese (vi)
 * when no locale is provided or the locale is unsupported.
 */
public interface I18nMessageService {

    /**
     * Resolve a message by key and locale with optional parameters. Falls back to Vietnamese
     * (default) if the locale is null or unsupported.
     *
     * @param key    message key (e.g. "error.unauthenticated")
     * @param locale requested locale (may be null)
     * @param params optional parameters for message placeholders {0}, {1}, ...
     * @return resolved message string, or the key itself if not found
     */
    String resolveMessage(String key, Locale locale, Object... params);
}

