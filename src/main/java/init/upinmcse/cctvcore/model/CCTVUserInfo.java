package init.upinmcse.cctvcore.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.data.domain.Persistable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table
public class CCTVUserInfo extends BaseEntity implements Persistable<String> {
    @Id
    String userId;

    @Transient
    @Builder.Default
    private boolean isNew = true;

    @Override
    public @Nullable String getId() {
        return userId;
    }

    @Override
    public boolean isNew() {
        return isNew;
    }

    @PostLoad
    @PostPersist
    void markNotNew() { this.isNew = false; }
}
