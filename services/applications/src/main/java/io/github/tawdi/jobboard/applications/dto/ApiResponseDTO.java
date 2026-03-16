package io.github.tawdi.jobboard.applications.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponseDTO<T> {
    private String status;
    private String message;
    private T data;
    private Long timestamp;
    private String path;
    private Object errors;

    public static <T> ApiResponseDTO<T> success(String message, T data) {
        ApiResponseDTO<T> response = new ApiResponseDTO<>();
        response.setStatus("SUCCESS");
        response.setMessage(message);
        response.setData(data);
        response.setTimestamp(System.currentTimeMillis());
        return response;
    }

    public static <T> ApiResponseDTO<T> error(String message) {
        ApiResponseDTO<T> response = new ApiResponseDTO<>();
        response.setStatus("ERROR");
        response.setMessage(message);
        response.setTimestamp(System.currentTimeMillis());
        return response;
    }

    public static <T> ApiResponseDTO<T> error(String message, Object errors) {
        ApiResponseDTO<T> response = new ApiResponseDTO<>();
        response.setStatus("ERROR");
        response.setMessage(message);
        response.setErrors(errors);
        response.setTimestamp(System.currentTimeMillis());
        return response;
    }
}
