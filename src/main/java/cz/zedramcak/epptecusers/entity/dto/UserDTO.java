package cz.zedramcak.epptecusers.entity.dto;

import lombok.Data;

@Data
public class UserDTO {
    private String birthNumber;
    private String firstName;
    private String lastName;
    private Integer id;
    private Integer age;
}