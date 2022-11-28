package com.sogong.tejava.validate.config.error;

import lombok.Getter;
import lombok.Setter;

import java.text.SimpleDateFormat;
import java.util.Calendar;

@Getter
@Setter
public class ErrorResponse {
    private String timeStamp;
    private String status;
    private String error;
    private String message;

    public ErrorResponse(String status, String error, String message) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy년 MM월 dd일 HH시 mm분 ss초");

        this.timeStamp = formatter.format(Calendar.getInstance().getTime());
        this.status = status;
        this.error = error;
        this.message = message;
    }
}