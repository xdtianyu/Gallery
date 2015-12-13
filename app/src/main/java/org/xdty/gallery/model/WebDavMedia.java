package org.xdty.gallery.model;

import android.support.annotation.NonNull;

import org.xdty.webdav.WebDavAuth;
import org.xdty.webdav.WebDavFile;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class WebDavMedia extends WebDavFile implements Media<WebDavMedia>, Comparable<WebDavMedia> {

    private final static String[] SCHEME = new String[]{"dav", "davs"};

    private WebDavMedia parent;
    private List<WebDavMedia> children = new ArrayList<>();
    private int position;

    public WebDavMedia() throws MalformedURLException {
        super("dav://");
    }

    public WebDavMedia(String url) throws MalformedURLException {
        super(url);
    }

    public WebDavMedia(WebDavFile media) throws MalformedURLException {
        super(media.getPath());
        setParent(media.getParent());
        setIsDirectory(media.isDirectory());
    }

    @Override
    public WebDavMedia[] listFiles() throws MalformedURLException {
        return (WebDavMedia[]) super.listFiles();
    }

    @Override
    public String[] scheme() {
        return SCHEME;
    }

    @Override
    public long length() {
        return 0;
    }

    @Override
    public WebDavMedia parent() {
        return parent;
    }

    @Override
    public void setParent(WebDavMedia parent) {
        this.parent = parent;
    }

    @Override
    public List<WebDavMedia> children() {

        if (children.size() == 0) {
            try {
                WebDavFile[] files = super.listFiles();
                for (WebDavFile file : files) {
                    WebDavMedia media = new WebDavMedia(file);
                    children.add(media);
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            Collections.sort(children);
        }
        return Collections.unmodifiableList(children);
    }

    @Override
    public String getUri() {
        return super.getPath();
    }

    @Override
    public boolean isFile() {
        return false;
    }

    @Override
    public WebDavMedia[] listMedia() {
        ArrayList<WebDavMedia> list = new ArrayList<>();
        try {
            WebDavFile[] files = super.listFiles();
            for (WebDavFile file : files) {
                list.add(new WebDavMedia(file));
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        Collections.sort(list);
        return list.toArray(new WebDavMedia[list.size()]);
    }

    @Override
    public boolean isImage() {
        String name = getName().toLowerCase();
        return name.endsWith(".png") ||
                name.endsWith(".jpg") ||
                name.endsWith(".jpeg") ||
                name.endsWith(".bmp") ||
                name.endsWith(".gif");
    }

    @Override
    public WebDavMedia fromUri(String uri) {
        try {
            return new WebDavMedia(uri);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public WebDavMedia auth(String uri, String directory, String username, String password) {
        if (WebDavAuth.getAuth(uri) == null) {
            WebDavAuth.addAuth(uri, username, password);
        }
        return this;
    }

    @Override
    public int getPosition() {
        return position;
    }

    @Override
    public void setPosition(int position) {
        this.position = position;
    }

    @Override
    public boolean equals(Object object) {
        boolean equal = false;

        if (object != null && object instanceof Media) {
            equal = this.getPath().equals(((Media) object).getPath());
        }

        return equal;
    }

    @Override
    public int compareTo(@NonNull WebDavMedia another) {
        return getName().compareTo(another.getName());
    }
}
