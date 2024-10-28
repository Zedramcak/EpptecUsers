package cz.zedramcak.epptecusers.entity;

import lombok.Data;
import lombok.NonNull;

@Data
public class User {
    private String birthNumber;
    private String firstName;
    private String lastName;
}
