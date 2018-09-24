package com.ulan.az.usluga;

/**
 * Created by User on 31.07.2018.
 */

public interface ClientApiListener {

    void onApiResponse(String id, String json,  boolean isOk);

}
