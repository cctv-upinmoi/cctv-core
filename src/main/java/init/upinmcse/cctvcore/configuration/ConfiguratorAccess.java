package init.upinmcse.cctvcore.configuration;

import org.springframework.security.access.prepost.PreAuthorize;

import java.lang.annotation.*;
@Target({ElementType.TYPE, ElementType.METHOD})
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@PreAuthorize("hasAnyAuthority(T(io.upinmcse.shopnoteserver.shop.model.Roles).CONFIGURATOR.role, " +
        "T(io.upinmcse.shopnoteserver.shop.model.Roles).ADMIN.role)")
public @interface ConfiguratorAccess {}
