package init.upinmcse.cctvcore.security;

import org.springframework.security.access.prepost.PreAuthorize;

import java.lang.annotation.*;

@Target({ElementType.TYPE, ElementType.METHOD})
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@PreAuthorize("hasAnyAuthority(" +
        "T(init.upinmcse.cctvcore.model.enums.Roles).ROLE_ADMIN.getRole())")
public @interface AdminAccess {
}
