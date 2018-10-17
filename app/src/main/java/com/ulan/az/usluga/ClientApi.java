package com.ulan.az.usluga;

import android.content.Context;
import android.support.annotation.NonNull;

import com.ulan.az.usluga.helpers.E;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by User on 31.07.2018.
 */

public class ClientApi {

    public static void requestPostImage(final String url, RequestBody body, final ClientApiListener clientApiListener) {

        OkHttpClient client = new OkHttpClient();


        final Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();


        //Log.e("sssss", "ssss");

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                //Log.e("Error", e.getMessage());
                sendFail(url, e.getMessage(), clientApiListener);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String json = response.body().string();
                //Log.e("RESPONSE_Finish", json);
                sendResponse(url, json, clientApiListener);

            }
        });
    }

    public static void requesPutImage(final String url, RequestBody body, final ClientApiListener clientApiListener) {

        OkHttpClient client = new OkHttpClient();


        final Request request = new Request.Builder()
                .url(url)
                .put(body)
                .build();


        //Log.e("sssss", "ssss");

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                //Log.e("Error", e.getMessage());
                sendFail(url, e.getMessage(), clientApiListener);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String json = response.body().string();
                //Log.e("RESPONSE_Finish", json);
                sendResponse(url, json, clientApiListener);

            }
        });
    }


    private static void sendResponse(String id, String json, ClientApiListener listener) {

        listener.onApiResponse(id, json, true);

    }

    private static void sendFail(String id, String json, ClientApiListener listener) {

        listener.onApiResponse(id, json, false);

    }

    public static void requestGet(final String url, final ClientApiListener listener) {
        OkHttpClient client = new OkHttpClient();

        final Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                sendFail(url, e.getMessage(), listener);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String json = response.body().string();
                //Log.e("json", json);
                sendResponse(url, json, listener);
            }
        });


    }

    public static void requestPut(String device, Context context) {

        OkHttpClient client = new OkHttpClient();

        RequestBody requestBody = new FormBody.Builder()
                .add("device_id", device)
                .build();

        final Request request = new Request.Builder()
                .url(URLS.users_put + String.valueOf(E.getAppPreferencesINT(E.APP_PREFERENCES_ID, context)) + "/")
                .put(requestBody)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
            }

            @Override
            public void onResponse(Call call, @NonNull Response response) throws IOException {

            }
        });
    }

    public static void requestPutForum(int count, int id, Context context) {

        OkHttpClient client = new OkHttpClient();

        RequestBody requestBody = new FormBody.Builder()
                .add("count", count+"")
                .build();

        final Request request = new Request.Builder()
                .url(URLS.forum_put +id + "/")
                .put(requestBody)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
            }

            @Override
            public void onResponse(Call call, @NonNull Response response) throws IOException {

            }
        });
    }

    public static void requestDelete(int id, Context context) {

        OkHttpClient client = new OkHttpClient();

        RequestBody requestBody = new FormBody.Builder()
                .build();

        final Request request = new Request.Builder()
                .url(URLS.order_delete + id + "/")
                .delete()
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
            }

            @Override
            public void onResponse(Call call, @NonNull Response response) throws IOException {

            }
        });
    }

    public static void requestDeleteService(int id, Context context) {

        OkHttpClient client = new OkHttpClient();

        RequestBody requestBody = new FormBody.Builder()
                .build();

        final Request request = new Request.Builder()
                .url(URLS.services_delete + id + "/")
                .delete()
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
            }

            @Override
            public void onResponse(Call call, @NonNull Response response) throws IOException {

            }
        });
    }

    public static void requestDelete1(String url,int id, Context context) {

        OkHttpClient client = new OkHttpClient();

        RequestBody requestBody = new FormBody.Builder()
                .build();

        final Request request = new Request.Builder()
                .url(url + id + "/")
                .delete()
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
            }

            @Override
            public void onResponse(Call call, @NonNull Response response) throws IOException {

            }
        });
    }

    public static void requestDeleteForum(int id, Context context) {

        OkHttpClient client = new OkHttpClient();

        RequestBody requestBody = new FormBody.Builder()
                .build();

        final Request request = new Request.Builder()
                .url(URLS.forum_put + id + "/")
                .delete()
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
            }

            @Override
            public void onResponse(Call call, @NonNull Response response) throws IOException {

            }
        });
    }

    public static void requestDeleteConfirmation(int id, Context context) {

        OkHttpClient client = new OkHttpClient();

        RequestBody requestBody = new FormBody.Builder()
                .build();

        final Request request = new Request.Builder()
                .url(URLS.confirmation_delete + id + "/")
                .delete()
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
            }

            @Override
            public void onResponse(Call call, @NonNull Response response) throws IOException {

            }
        });
    }

}
