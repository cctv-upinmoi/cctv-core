package init.upinmcse.cctvcore.mapper;

import init.upinmcse.cctvcore.dto.request.AddCCTVReq;
import init.upinmcse.cctvcore.dto.request.UpdateCCTVReq;
import init.upinmcse.cctvcore.dto.response.CCTVRes;
import init.upinmcse.cctvcore.dto.response.LocationDetailRes;
import init.upinmcse.cctvcore.model.CCTVCameraInfo;
import init.upinmcse.cctvcore.model.LocationDetail;
import org.mapstruct.*;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CCTVInfoMapper {

    @Mapping(target = "status", expression = "java(init.upinmcse.cctvcore.model.enums.CCTVStatus.OK)")
    CCTVCameraInfo toEntity(AddCCTVReq request);

    // zones must be updated via updateCCTVZone(), not here — Zone needs camera reference set manually
    @Mapping(target = "zones", ignore = true)
    void updateEntity(@MappingTarget CCTVCameraInfo entity, UpdateCCTVReq request);

    @Mapping(target = "status", expression = "java(entity.getStatus() != null ? entity.getStatus().name() : null)")
    @Mapping(target = "mode", expression = "java(entity.getMode() != null ? entity.getMode().name() : null)")
    CCTVRes toResponse(CCTVCameraInfo entity);

    LocationDetailRes toLocationResponse(LocationDetail detail);
}
