package init.upinmcse.cctvcore.dto.response;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class ImportCCTVResult {
    private int totalRows;
    private int successCount;
    private int failCount;
    private List<CCTVRes> imported;
    private List<RowError> errors;

    @Data
    @Builder
    public static class RowError {
        private int row;
        private String field;
        private String message;
    }
}
