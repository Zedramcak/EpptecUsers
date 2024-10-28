package cz.zedramcak.epptecusers.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserDTO {
    private Integer id;
    private String birthNumber;
    private String firstName;
    private String lastName;
    private Integer age;
}
