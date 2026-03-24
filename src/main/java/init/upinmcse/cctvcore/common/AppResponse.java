package init.upinmcse.cctvcore.common;

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
public class AppResponse<T> {
    @Builder.Default
    private int code = 1000;

    private String message;
    private T data;

    public static <T> AppResponse<T> success(String message){
        return AppResponse.<T>builder()
                .code(1000)
                .message(message)
                .data(null)
                .build();
    }

    public static <T> AppResponse<T> success(T data){
        return AppResponse.<T>builder()
                .code(1000)
                .data(data)
                .build();
    }

    public static <T> AppResponse<T> success(String msg, T data){
        return AppResponse.<T>builder()
                .code(1000)
                .message(msg)
                .data(data)
                .build();
    }

    public static <T> AppResponse<T> error(ErrorCode errorCode, String message){
        return AppResponse.<T>builder()
                .code(errorCode.getCode())
                .message(message)
                .data(null)
                .build();
    }
}
