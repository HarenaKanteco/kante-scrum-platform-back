package com.scrumplateform.kante.http.response;

import lombok.Data;

@Data
public class Response{
    String message;
    Object data;

    public void success(Object data,String message){
        this.setData(data);
        this.setMessage(message);
    }

    public void error(Object data,String message){
        this.setData(data);
        this.setMessage(message);
    }
}
