package itseasy.mark.api.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResponseDTO<T> {
    private String error;
    private List<T> data;

    public static <T> ResponseDTO<T> put(T data, String error) {
        List<T> list = new ArrayList<>();
        list.add(data);

        return new ResponseDTO(null, list);
    }
}
