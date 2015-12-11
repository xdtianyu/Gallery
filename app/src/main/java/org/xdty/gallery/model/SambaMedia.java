package org.xdty.gallery.model;

import android.net.Uri;
import android.support.annotation.NonNull;

import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;

import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;

public class SambaMedia extends SmbFile implements Media<SambaMedia>, Comparable<SambaMedia> {

    private final static String[] SCHEME = new String[]{"smb"};

    public SambaMedia() throws MalformedURLException {
        this("smb://", "");
    }

    public SambaMedia(String uri, String host) throws MalformedURLException {
        super(uri, Samba.getAuth(host));
    }

    public SambaMedia(SmbFile media) throws MalformedURLException, UnknownHostException {
        super(media.getPath(), Samba.getAuth(media.getServer()));
    }

    @Override
    public boolean isFile() {
        try {
            return super.isFile();
        } catch (SmbException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public SambaMedia[] listMedia() {
        ArrayList<SambaMedia> list = new ArrayList<>();
        try {
            SmbFile[] files = super.listFiles();
            for (SmbFile file : files) {
                if (!file.getName().contains(":")) {
                    list.add(new SambaMedia(file));
                }
            }
        } catch (SmbException | MalformedURLException | UnknownHostException e) {
            e.printStackTrace();
        }
        Collections.sort(list);
        return list.toArray(new SambaMedia[list.size()]);
    }

    public long length() {
        return super.getContentLength();
    }

    @Override
    public String[] scheme() {
        return SCHEME;
    }

    @Override
    public String getHost() {
        return super.getServer();
    }

    @Override
    public String getUri() {
        return super.getPath();
    }

    @Override
    public boolean isDirectory() {
        try {
            return super.isDirectory();
        } catch (SmbException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public SambaMedia fromUri(String uri) {
        try {
            return new SambaMedia(uri, Uri.parse(uri).getHost());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
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
    public boolean equals(Object object) {
        boolean equal = false;

        if (object != null && object instanceof Media) {
            equal = this.getPath().equals(((Media) object).getPath());
        }

        return equal;
    }

    @Override
    public int compareTo(@NonNull SambaMedia another) {
        return getName().compareTo(another.getName());
    }
}
