package init.upinmcse.cctvcore.model;

import lombok.*;
import org.springframework.data.mongodb.core.mapping.Field;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LocationDetail {

    @Field("ADDRESS")
    private String address;

    @Field("WARD")
    private String ward;

    @Field("DISTRICT")
    private String district;

    @Field("PROVINCE")
    private String province;

    @Field("DESCRIPTION")
    private String description;
}
