package org.acme.saas.model.data;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginData {
    private String email;
    private String password;
}
