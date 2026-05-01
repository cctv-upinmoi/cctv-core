package init.upinmcse.cctvcore.model.enums;

import lombok.Getter;

@Getter
public enum Roles {
    ROLE_ADMIN("ROLE_ADMIN"),
    ROLE_USER("ROLE_VIEWER"),
    ROLE_CONFIGURATOR("ROLE_CONFIGURATOR");

    private final String role;

    Roles(String role) {
        this.role = role;
    }
}
