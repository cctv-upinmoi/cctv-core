package init.upinmcse.cctvcore.model.converter;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.ArrayList;
import java.util.List;

@Converter
public class PointsJsonConverter implements AttributeConverter<List<double[]>, String> {

    private static final ObjectMapper mapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(List<double[]> points) {
        if (points == null) return "[]";
        try {
            return mapper.writeValueAsString(points);
        } catch (Exception e) {
            return "[]";
        }
    }

    @Override
    public List<double[]> convertToEntityAttribute(String json) {
        if (json == null || json.isBlank()) return new ArrayList<>();
        try {
            return mapper.readValue(json, new TypeReference<>() {});
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }
}
