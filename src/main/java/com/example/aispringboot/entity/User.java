package com.example.aispringboot.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

//数据库的用户实体类
@Data
@TableName("user")
public class User {
    //用户id
    @TableId(type = IdType.AUTO)
    private Long id;
}
