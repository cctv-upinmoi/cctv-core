package init.upinmcse.cctvcore.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;

@Getter
@Setter
public abstract class BaseEntity {

    @CreatedDate
    @Field("created_at")
    private Date createdAt;

    @LastModifiedDate
    @Field("updated_at")
    private Date updatedAt;
}