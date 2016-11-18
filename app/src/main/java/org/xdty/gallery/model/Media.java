package org.xdty.gallery.model;

import java.io.IOException;
import java.io.InputStream;
import java.util.Comparator;
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

    class NumericComparator implements Comparator<Media> {

        public static NumericComparator factory() {
            return SingletonHelper.INSTANCE;
        }

        private boolean isDigit(char ch) {
            return ch >= 48 && ch <= 57;
        }

        /**
         * Length of string is passed in for improved efficiency (only need to calculate it once)
         **/
        private String getChunk(String s, int length, int marker) {
            StringBuilder chunk = new StringBuilder();
            char c = s.charAt(marker);
            chunk.append(c);
            marker++;
            if (isDigit(c)) {
                while (marker < length) {
                    c = s.charAt(marker);
                    if (!isDigit(c)) {
                        break;
                    }
                    chunk.append(c);
                    marker++;
                }
            } else {
                while (marker < length) {
                    c = s.charAt(marker);
                    if (isDigit(c)) {
                        break;
                    }
                    chunk.append(c);
                    marker++;
                }
            }
            return chunk.toString();
        }

        @Override
        public int compare(Media m1, Media m2) {
            String s1 = m1.getName();
            String s2 = m2.getName();

            return compare(s1, s2);
        }

        public int compare(String s1, String s2) {

            int thisMarker = 0;
            int thatMarker = 0;
            int s1Length = s1.length();
            int s2Length = s2.length();

            while (thisMarker < s1Length && thatMarker < s2Length) {
                String thisChunk = getChunk(s1, s1Length, thisMarker);
                thisMarker += thisChunk.length();

                String thatChunk = getChunk(s2, s2Length, thatMarker);
                thatMarker += thatChunk.length();

                // If both chunks contain numeric characters, sort them numerically
                int result = 0;
                if (isDigit(thisChunk.charAt(0)) && isDigit(thatChunk.charAt(0))) {
                    // Simple chunk comparison by length.
                    int thisChunkLength = thisChunk.length();
                    result = thisChunkLength - thatChunk.length();
                    // If equal, the first different number counts
                    if (result == 0) {
                        for (int i = 0; i < thisChunkLength; i++) {
                            result = thisChunk.charAt(i) - thatChunk.charAt(i);
                            if (result != 0) {
                                return result;
                            }
                        }
                    }
                } else {
                    result = thisChunk.compareTo(thatChunk);
                }

                if (result != 0) {
                    return result;
                }
            }

            return s1Length - s2Length;
        }

        private final static class SingletonHelper {
            private final static NumericComparator INSTANCE = new NumericComparator();
        }
    }

}
