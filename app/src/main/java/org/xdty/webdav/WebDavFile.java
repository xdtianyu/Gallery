package org.xdty.webdav;

import android.util.Log;

import com.squareup.okhttp.Credentials;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;
import org.xdty.webdav.model.MultiStatus;
import org.xdty.webdav.model.Prop;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class WebDavFile {

    public final static String TAG = WebDavFile.class.getSimpleName();

    private final static String DIR = "<?xml version=\"1.0\"?>\n" +
            "<a:propfind xmlns:a=\"DAV:\">\n" +
            "<a:prop><a:resourcetype/></a:prop>\n" +
            "</a:propfind>";
    protected URL url;
    OkHttpClient okHttpClient = new OkHttpClient();
    private String canon;
    private long createTime;
    private long lastModified;
    private long size;

    public WebDavFile(String url) throws MalformedURLException {
        this.url = new URL(null, url, Handler.DAV_HANDLER);
    }

    public URL getUrl() {
        try {
            return new URL(url.toString().replace("dav://", "http://").replace("davs://", "https://"));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getPath() {
        return url.toString();
    }

    public WebDavFile[] listFiles() throws MalformedURLException {

        Request.Builder request = new Request.Builder()
                .url(getUrl())
                .method("PROPFIND", RequestBody.create(MediaType.parse("text/plain"), DIR));

        WebDavAuth.Auth auth = WebDavAuth.getAuth(url.toString());
        if (auth != null) {
            request.header("Authorization", Credentials.basic(auth.getUser(), auth.getPass()));
        }

        try {
            Response response = okHttpClient.newCall(request.build()).execute();
            String s = response.body().string();
            Log.d(TAG, s);
            return parseDir(s);
        } catch (IOException | XmlPullParserException | IllegalArgumentException e) {
            e.printStackTrace();
        }

        return null;
    }

    public InputStream getInputStream() {
        Request.Builder request = new Request.Builder()
                .url(getUrl());

        WebDavAuth.Auth auth = null;
        try {
            auth = WebDavAuth.getAuth(url.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        if (auth != null) {
            request.header("Authorization", Credentials.basic(auth.getUser(), auth.getPass()));
        }

        try {
            Response response = okHttpClient.newCall(request.build()).execute();
            return response.body().byteStream();
        } catch (IOException | IllegalArgumentException e) {
            e.printStackTrace();
        }
        return null;
    }

    private WebDavFile[] parseDir(String s) throws XmlPullParserException, IOException {

        List<WebDavFile> list = new ArrayList<>();

        Serializer serializer = new Persister();
        try {
            MultiStatus multiStatus = serializer.read(MultiStatus.class, s);
            for (org.xdty.webdav.model.Response response : multiStatus.getResponse()) {
                WebDavFile webDavFile = new WebDavFile(
                        url.getProtocol() + "://" + url.getHost() + response.getHref());
                Prop prop = response.getPropstat().getProp();
                webDavFile.setCanon(prop.getDisplayname());
                webDavFile.setCreateTime(0);
                webDavFile.setLastModified(0);
                webDavFile.setSize(prop.getGetcontentlength());
                list.add(webDavFile);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list.toArray(new WebDavFile[list.size()]);
    }

    public String getCanon() {
        return canon;
    }

    public void setCanon(String canon) {
        this.canon = canon;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public long getLastModified() {
        return lastModified;
    }

    public void setLastModified(long lastModified) {
        this.lastModified = lastModified;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }
}