package init.upinmcse.cctvcore.mapper;

import init.upinmcse.cctvcore.dto.request.AddCCTVReq;
import init.upinmcse.cctvcore.dto.request.LocationDetailReq;
import init.upinmcse.cctvcore.dto.response.ImportCCTVResult;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CSVMapper {
    public AddCCTVReq mapToReq(String[] cols, int row, List<ImportCCTVResult.RowError> errors) {
        if (cols.length < 14) {
            errors.add(ImportCCTVResult.RowError.builder()
                    .row(row)
                    .field("*")
                    .message("Insufficient columns, expected 14 got " + cols.length)
                    .build());
            return null;
        }

        try {
            LocationDetailReq locationDetail = LocationDetailReq.builder()
                    .address(cols[9].trim())
                    .ward(cols[10].trim())
                    .district(cols[11].trim())
                    .province(cols[12].trim())
                    .description(cols[13].trim())
                    .build();

            return AddCCTVReq.builder()
                    .name(cols[0].trim())
                    .ip(cols[1].trim())
                    .port(cols[2].isBlank() ? null : Integer.parseInt(cols[2].trim()))
                    .username(cols[3].trim())
                    .pwd(cols[4].trim())
                    .mode(cols[5].trim())
                    .rtspStreamUrl(cols[6].trim())
                    .longitude(cols[7].isBlank() ? null : Double.parseDouble(cols[7].trim()))
                    .latitude(cols[8].isBlank() ? null : Double.parseDouble(cols[8].trim()))
                    .locationDetail(locationDetail)
                    .build();

        } catch (NumberFormatException e) {
            errors.add(ImportCCTVResult.RowError.builder()
                    .row(row)
                    .field("port/longitude/latitude")
                    .message("Invalid number format: " + e.getMessage())
                    .build());
            return null;
        }
    }
}
