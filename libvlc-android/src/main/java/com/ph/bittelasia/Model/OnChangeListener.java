package com.ph.bittelasia.Model;

public interface OnChangeListener {

    void onBufferChanged(float buffer);

    void onChanging();

    void onLoadComplete();

    void onError(String msg);

    void onEnd();

}