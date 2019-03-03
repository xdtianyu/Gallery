package org.xdty.gallery.model.media;

import androidx.annotation.NonNull;

import org.xdty.autoindex.AutoIndexFile;
import org.xdty.gallery.model.Media;
import org.xdty.http.HttpAuth;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AutoIndexMedia extends AutoIndexFile
        implements Media<AutoIndexMedia>, Comparable<AutoIndexMedia> {

    private final static String[] SCHEME = new String[] { "http", "https" };

    private AutoIndexMedia parent;
    private List<AutoIndexMedia> children = new ArrayList<>();
    private int position;
    private boolean hasImage = false;

    public AutoIndexMedia() throws MalformedURLException {
        super();
    }

    public AutoIndexMedia(String url) throws MalformedURLException {
        super(url);
    }

    public AutoIndexMedia(AutoIndexFile media) throws MalformedURLException {
        super(media.getPath());
        setParent(media.getParent());
        setIsDirectory(media.isDirectory());
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
    public AutoIndexMedia parent() {
        return parent;
    }

    @Override
    public void setParent(AutoIndexMedia parent) {
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
    public synchronized List<AutoIndexMedia> children() {

        if (children.size() == 0) {
            try {
                List<AutoIndexFile> files = super.listFiles();
                if (files != null) {
                    for (AutoIndexFile file : files) {
                        AutoIndexMedia media = new AutoIndexMedia(file);
                        media.setParent(this);
                        children.add(media);

                        if (!hasImage) {
                            if (media.isImage()) {
                                hasImage = true;
                            }
                        }
                    }
                }
            } catch (MalformedURLException e) {
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
    public String getUri() {
        return super.getPath();
    }

    @Override
    public boolean isFile() {
        return false;
    }

    @Override
    public AutoIndexMedia[] listMedia() {
        ArrayList<AutoIndexMedia> list = new ArrayList<>();
        try {
            List<AutoIndexFile> files = super.listFiles();
            for (AutoIndexFile file : files) {
                list.add(new AutoIndexMedia(file));
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        Collections.sort(list);
        return list.toArray(new AutoIndexMedia[list.size()]);
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
    public AutoIndexMedia fromUri(String uri) {
        try {
            return new AutoIndexMedia(uri);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public AutoIndexMedia auth(String uri, String directory, String username, String password) {
        if (HttpAuth.getAuth(uri) == null) {
            HttpAuth.addAuth(uri, username, password);
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
    public int compareTo(@NonNull AutoIndexMedia another) {
        return NumericComparator.factory().compare(this, another);
    }
}
