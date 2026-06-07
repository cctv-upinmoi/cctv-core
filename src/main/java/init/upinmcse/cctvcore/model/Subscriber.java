package init.upinmcse.cctvcore.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "subscribers")
public class Subscriber extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "user_id", unique = true)
    private String userId;

    @Column(name = "email")
    private String email;

    @Column(name = "telegram_chat_id")
    private String telegramChatId;

    @Builder.Default
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "channels", columnDefinition = "TEXT")
    private List<String> channels = new ArrayList<>();

    @Builder.Default
    @Column(name = "enabled")
    private boolean enabled = true;

    @Builder.Default
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "subscriber_jobs",
            joinColumns = @JoinColumn(name = "subscriber_id"),
            inverseJoinColumns = @JoinColumn(name = "job_id")
    )
    private List<Job> jobs = new ArrayList<>();
}
