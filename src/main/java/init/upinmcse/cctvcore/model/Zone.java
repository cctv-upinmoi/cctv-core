package init.upinmcse.cctvcore.model;

import lombok.*;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Zone {

    /**
     * Tên zone, ví dụ: "Zone A", "Khu vực cửa chính"
     */
    @Field("NAME")
    private String name;

    /**
     * Loại zone: INTRUSION, LOITERING, LINE_CROSSING...
     */
    @Field("TYPE")
    private ZoneType type;

    /**
     * Bật/tắt zone
     */
    @Field("ENABLED")
    private boolean enabled;

    /**
     * Danh sách các điểm tạo thành polygon của zone.
     * Mỗi điểm là [x, y] với giá trị normalized 0.0 - 1.0
     * tính theo tỷ lệ chiều rộng/chiều cao của frame camera.
     * Ví dụ: [[0.1, 0.1], [0.9, 0.1], [0.9, 0.9], [0.1, 0.9]]
     */
    @Field("POINTS")
    private List<double[]> points;
}
