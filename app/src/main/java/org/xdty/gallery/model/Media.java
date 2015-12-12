package org.xdty.gallery.model;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

public interface Media<T extends Media> {

    String[] scheme();

    String getName();

    String getHost();

    long getLastModified();

    long length();

    String getPath();

    String getParent();

    String getUri();

    InputStream getInputStream() throws IOException;

    boolean isFile();

    T[] listMedia();

    boolean isImage();

    boolean isDirectory();

    T fromUri(String uri);

    T auth(String domain, String directory, String username, String password);

    class Builder {

        private static ArrayList<Media> roots = new ArrayList<>();
        private static HashMap<String, Media> medias = new HashMap<>();

        public static void register(Media media) {
            for (String scheme : media.scheme()) {
                if (!medias.containsKey(scheme)) {
                    medias.put(scheme, media);
                }
            }
        }

        public static Media uri(String uri) {
            if (uri.contains("://")) {
                String scheme = uri.substring(0, uri.indexOf("://"));
                if (medias.containsKey(scheme)) {
                    return medias.get(scheme).fromUri(uri);
                }
            }
            throw new MediaException("Unknown scheme: " + uri);
        }

        public static Media[] roots() {
            return roots.toArray(new Media[roots.size()]);
        }

        public static void addRoot(String uri, String username, String password) {

            if (uri.startsWith("/")) {
                uri = "file:/" + uri;
            }

            if (uri.contains("://")) {

                if (!uri.endsWith("/")) {
                    uri = uri + "/";
                }

                for (Media media : roots) {
                    if (uri.equals(media.getUri()) || uri.equals(media.getUri() + "/")) {
                        return;
                    }
                }

                String[] parts = uri.split("://");
                String[] parts2 = parts[1].split("/", 2);
                String directory;
                if (parts2.length == 2) {
                    directory = parts2[1];
                } else {
                    directory = "/";
                }

                if (medias.containsKey(parts[0])) {
                    medias.get(parts[0]).auth(parts[0] + "://" + parts2[0], directory, username,
                            password);
                    roots.add(uri(uri));
                    return;
                }
            }
            throw new MediaException("Unknown scheme: " + uri);
        }
    }

    class MediaException extends RuntimeException {

        public MediaException(String detailMessage) {
            super(detailMessage);
        }
    }

}
