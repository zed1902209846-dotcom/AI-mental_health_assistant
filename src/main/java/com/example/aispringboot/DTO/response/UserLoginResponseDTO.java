package com.example.aispringboot.DTO.response;


import lombok.Data;

@Data
public class UserLoginResponseDTO {
    private String token;
    private String roleType;
    private UserDetailResponseDTO userInfo;
//           "id": 1,
//            "username": "admin",
//            "email": "admin@example.com",
//            "nickname": "kk",
//            "avatar": "/files/bussiness/user_avatar/1757687320361.jpg",
//            "phone": "19999525252",
//            "gender": 1,
//            "genderDisplayName": "男",
//            "birthday": "2019-09-02",
//            "userType": 2,
//            "userTypeDisplayName": "管理员",
//            "status": 1,
//            "statusDisplayName": "正常",
//            "displayName": "kk",
//            "createdAt": "2025-08-30 12:00:01",
//            "updatedAt": "2026-07-10 10:47:02"
@Data
    public static class UserDetailResponseDTO {
        private long id;
        private String username;
        private String email;
        private String nickname;
        private String avatar;
        private String phone;
        private Integer gender;
        private String genderDisplayName;
        private String birthday;
        private Integer userType;
        private String userTypeDisplayName;
        private Integer status;
        private String statusDisplayName;
        private String displayName;
        private String createdAt;
        private String updatedAt;
    }
}
