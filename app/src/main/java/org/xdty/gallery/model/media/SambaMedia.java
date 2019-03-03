package org.xdty.gallery.model.media;

import androidx.annotation.NonNull;

import org.xdty.gallery.model.Media;

import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;

public class SambaMedia extends SmbFile implements Media<SambaMedia>, Comparable<SambaMedia> {

    private final static String[] SCHEME = new String[]{"smb"};
    private final static Map<String, NtlmPasswordAuthentication> smbAuthList = new HashMap<>();

    private SambaMedia parent;
    private List<SambaMedia> children = new ArrayList<>();
    private int position;
    private boolean hasImage = false;

    public SambaMedia() throws MalformedURLException {
        this("smb://");
    }

    public SambaMedia(String uri) throws MalformedURLException {
        super(uri, getAuth(uri));
    }

    public SambaMedia(SmbFile media) throws MalformedURLException, UnknownHostException {
        super(media.getPath(), getAuth(media.getPath()));
    }

    private static NtlmPasswordAuthentication getAuth(String uri) {
        for (String key : smbAuthList.keySet()) {
            if (uri.contains(key)) {
                return smbAuthList.get(key);
            }
        }
        return null;
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
    public SambaMedia parent() {
        return parent;
    }

    @Override
    public void setParent(SambaMedia parent) {
        this.parent = parent;
    }

    @Override
    public void clear() {
        children.clear();
        hasImage = false;
        position = 0;
    }

    @Override
    public boolean hasImage() {
        return hasImage;
    }

    @Override
    public synchronized List<SambaMedia> children() {

        if (children.size() == 0) {
            try {
                SmbFile[] files = super.listFiles();
                for (SmbFile file : files) {
                    if (!file.getName().contains(":")) {
                        SambaMedia media = new SambaMedia(file);
                        media.setParent(this);
                        children.add(media);

                        if (!hasImage) {
                            if (media.isImage()) {
                                hasImage = true;
                            }
                        }
                    }
                }
            } catch (SmbException | MalformedURLException | UnknownHostException e) {
                e.printStackTrace();
            }
            Collections.sort(children);
        }
        return Collections.unmodifiableList(children);
    }

    @Override
    public int childrenSize() {
        return children.size();
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
            return new SambaMedia(uri);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public SambaMedia auth(String uri, String directory, String username, String password) {
        smbAuthList.put(uri + "/" + directory,
                new NtlmPasswordAuthentication(uri.replace("smb://", ""), username, password));
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
        return NumericComparator.factory().compare(this, another);
    }
}
