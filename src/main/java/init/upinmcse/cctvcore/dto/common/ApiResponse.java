package init.upinmcse.cctvcore.dto.common;

import com.fasterxml.jackson.annotation.JsonInclude;

import init.upinmcse.cctvcore.exception.ErrorCode;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
    @Builder.Default
    private int code = 1000;

    private String message;
    private T data;

    public static <T> ApiResponse<T> success(String message){
        return ApiResponse.<T>builder()
                .code(1000)
                .message(message)
                .data(null)
                .build();
    }

    public static <T> ApiResponse<T> success(T data){
        return ApiResponse.<T>builder()
                .code(1000)
                .message("Default message")
                .data(data)
                .build();
    }

    public static <T> ApiResponse<T> success(String msg, T data){
        return ApiResponse.<T>builder()
                .code(1000)
                .message(msg)
                .data(data)
                .build();
    }

    public static <T> ApiResponse<T> error(ErrorCode errorCode){
        return ApiResponse.<T>builder()
                .code(errorCode.getCode())
                .message(errorCode.getMessage())
                .data(null)
                .build();
    }
}
