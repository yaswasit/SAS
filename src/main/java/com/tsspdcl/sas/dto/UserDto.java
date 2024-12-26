package com.tsspdcl.sas.dto;

import javax.validation.constraints.NotEmpty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDto
{
    private Long id;
    @NotEmpty
    private String firstName;
    @NotEmpty
    private String lastName;
    @NotEmpty(message = "Username should not be empty")
    private String username;
    private String sasoffaddr;
    private String sasdesg;
    private String sasseccd;
    private String sasusertype;
    @NotEmpty(message = "Password should not be empty")
    private String password;
}
