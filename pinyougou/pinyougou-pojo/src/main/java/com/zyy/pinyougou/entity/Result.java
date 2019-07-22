package com.zyy.pinyougou.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author: Zyy
 * @date: 2019-06-20 10:49
 * @description:
 * @version:
 */
public class Result implements Serializable {

    private Boolean success;
    private String message;

    //错误信息
    private List<Error> errorsList= new ArrayList<>();

    public Result(Boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public Result(Boolean success, String message, List<Error> errorsList) {
        this.success = success;
        this.message = message;
        this.errorsList = errorsList;
    }

    public Result() {
    }

    public List<Error> getErrorsList() {
        return errorsList;
    }

    public void setErrorsList(List<Error> errorsList) {
        this.errorsList = errorsList;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
