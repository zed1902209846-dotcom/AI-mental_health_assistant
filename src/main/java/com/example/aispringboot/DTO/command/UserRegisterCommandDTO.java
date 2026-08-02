package com.example.aispringboot.DTO.command;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserRegisterCommandDTO {
    @NotBlank(message = "用户名不能为空")
    @Size(min = 3,max = 50,message = "用户名长度必须在 3 到 50之间")
    @Pattern(regexp ="^[a-zA-Z0-9_]+$", message = "用户名只能包含字母,数字和下划线")
    private String username;

    private String email;
    private String nickname;
    private String phone;
    private String password;
    private String confirmPassword;
    private Integer gender;
    private Integer userType;
}
