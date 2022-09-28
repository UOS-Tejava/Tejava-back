package com.sogong.tejava.error;

import lombok.Getter;
import lombok.Setter;

import java.text.SimpleDateFormat;
import java.util.Calendar;

@Getter
@Setter
public class ErrorResponse {
    private String timeStamp;
    private String code;
    private String message;

    public ErrorResponse(String code, String message) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy년 MM월 dd일 HH시 mm분 ss초");

        this.code = code;
        this.message = message;
        this.timeStamp = formatter.format(Calendar.getInstance().getTime());
    }
}
