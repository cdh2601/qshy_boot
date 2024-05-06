package com.qshy.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

/**
 * @Classname Result
 * @Description TODO
 * @Date 2022/10/26 22:26
 * @Created by senorisky
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Result {
    private Map<String, Object> data;
    private Integer code;
    private String msg;

    public Map<String, Object> getData() {
        return data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
