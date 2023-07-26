package tests;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Authenticaton {
    private String username;
    private String password;
}
