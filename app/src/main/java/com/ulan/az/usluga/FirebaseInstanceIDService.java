package com.ulan.az.usluga;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by filipp on 5/23/2016.
 */
public class FirebaseInstanceIDService extends FirebaseInstanceIdService {

    private static  final String REG_TOKEN = "REG_TOKEN";
    @Override
    public void onTokenRefresh() {

        String token = FirebaseInstanceId.getInstance().getToken();
        //Log.e(REG_TOKEN, token);
      //  registerToken(token);
    }

    public static void jiij() {
        String token = FirebaseInstanceId.getInstance().getToken();
        //Log.e(REG_TOKEN, token);
    }
}
