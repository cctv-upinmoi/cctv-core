package init.upinmcse.cctvcore.model;


import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collation = "cctv-user-info")
public class CCTVUserInfo extends BaseEntity {
    @MongoId
    String profileId;
    String userId;
    String email;
}
