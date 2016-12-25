package org.xdty.gallery.utils;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;

public class OkHttp {
    private OkHttpClient.Builder mOkHttpBuilder;

    private OkHttp() {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.NONE);

        Interceptor interceptor = new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request();
                HttpUrl url = request.url()
                        .newBuilder()
                        //.addQueryParameter("timestamp",
                        //        Long.toString(System.currentTimeMillis() / 1000 / 60))
                        .build();
                request = request.newBuilder().url(url).build();
                return chain.proceed(request);
            }
        };

        mOkHttpBuilder = new OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .addInterceptor(interceptor);
    }

    public static OkHttp getInstance() {
        return SingletonHelper.INSTANCE;
    }

    public void addNetworkInterceptor(Interceptor interceptor) {
        mOkHttpBuilder.addNetworkInterceptor(interceptor);
    }

    public OkHttpClient client() {
        OkHttpClient client = mOkHttpBuilder.build();
        org.xdty.http.OkHttp.getInstance().setClient(client);
        return client;
    }

    private static class SingletonHelper {
        private final static OkHttp INSTANCE = new OkHttp();
    }
}
