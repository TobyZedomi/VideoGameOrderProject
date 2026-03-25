package model;

import lombok.*;

@Getter
@Setter
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@AllArgsConstructor
public class User {

    private String username;

    private String password;

}
