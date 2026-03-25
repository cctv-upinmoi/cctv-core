package init.upinmcse.cctvcore.mapper;

import init.upinmcse.cctvcore.dto.request.AddCameraRequest;
import init.upinmcse.cctvcore.dto.request.UpdateCameraRequest;
import init.upinmcse.cctvcore.dto.response.CameraResponse;
import init.upinmcse.cctvcore.dto.response.LocationDetailResponse;
import init.upinmcse.cctvcore.model.CCTVCameraInfo;
import init.upinmcse.cctvcore.model.LocationDetail;
import org.mapstruct.*;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;

@Mapper(componentModel = "spring", 
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CameraMapper {

    // ===== TO ENTITY =====
    @Mapping(target = "location", source = ".", qualifiedByName = "toGeoJsonPoint")
    CCTVCameraInfo toEntity(AddCameraRequest request);

    @Mapping(target = "location", source = ".", qualifiedByName = "toGeoJsonPoint")
    void updateEntity(@MappingTarget CCTVCameraInfo entity, UpdateCameraRequest request);

    // ===== TO DTO =====
    @Mapping(target = "longitude", source = "location", qualifiedByName = "toLongitude")
    @Mapping(target = "latitude", source = "location", qualifiedByName = "toLatitude")
    @Mapping(target = "status", expression = "java(entity.getStatus() != null ? entity.getStatus().name() : null)")
    @Mapping(target = "mode", expression = "java(entity.getMode() != null ? entity.getMode().name() : null)")
    CameraResponse toResponse(CCTVCameraInfo entity);

    LocationDetailResponse toLocationResponse(LocationDetail detail);

    // ===== CUSTOM MAPPERS FOR GEOJSON =====
    @Named("toGeoJsonPoint")
    default GeoJsonPoint toGeoJsonPoint(Object request) {
        Double lon = null;
        Double lat = null;
        
        if (request instanceof AddCameraRequest req) {
            lon = req.getLongitude();
            lat = req.getLatitude();
        } else if (request instanceof UpdateCameraRequest req) {
            lon = req.getLongitude();
            lat = req.getLatitude();
        }

        if (lon != null && lat != null) {
            return new GeoJsonPoint(lon, lat);
        }
        return null;
    }

    @Named("toLongitude")
    default Double toLongitude(GeoJsonPoint point) {
        return point != null ? point.getX() : null;
    }

    @Named("toLatitude")
    default Double toLatitude(GeoJsonPoint point) {
        return point != null ? point.getY() : null;
    }
}
