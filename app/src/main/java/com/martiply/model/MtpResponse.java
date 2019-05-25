package com.martiply.model;

import com.martiply.model.interfaces.IMtpResponse;

import java.util.List;

public class MtpResponse<T> implements IMtpResponse {

    final boolean success;
    final String error;
    final List<T> data;

    public MtpResponse(boolean success, String error, List<T> data) {
        this.success = success;
        this.error = error;
        this.data = data;
    }

    @Override
    public boolean isSuccess() {
        return success;
    }

    public String getError() {
        return error;
    }

    public List<T> getData() {
        return data;
    }
}
