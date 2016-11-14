package org.xdty.gallery.model;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public interface Media<T extends Media> {

    String[] scheme();

    String getName();

    String getHost();

    long getLastModified();

    long length();

    String getPath();

    String getParent();

    void setParent(T parent);

    T parent();

    void clear();

    boolean hasImage();

    List<T> children();

    int childrenSize();

    String getUri();

    InputStream getInputStream() throws IOException;

    boolean isFile();

    T[] listMedia();

    boolean isImage();

    boolean isDirectory();

    T fromUri(String uri);

    T auth(String domain, String directory, String username, String password);

    int getPosition();

    void setPosition(int position);
    
    class MediaException extends RuntimeException {

        public MediaException(String detailMessage) {
            super(detailMessage);
        }
    }

}
