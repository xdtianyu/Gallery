package org.xdty.gallery.model;

import android.support.annotation.NonNull;

import org.xdty.gallery.utils.Utils;
import org.xdty.webdav.WebDavFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collections;

import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;

public class Media implements Comparable<Media> {

    public final static String TAG = Media.class.getSimpleName();

    public final static String SCHEME_FILE = "file";
    public final static String SCHEME_SAMBA = "smb";
    public final static String SCHEME_DAV = "dav";
    public final static String SCHEME_DAVS = "davs";
    public final static String SCHEME_CACHE = "media-cache";

    public static final int TYPE_IMAGE = 1;
    public static final int TYPE_VIDEO = 2;
    public static final int TYPE_FOLDER = 3;
    private static String cachePath;
    private SmbFile smbFile;
    private File localFile;
    private WebDavFile davFile;
    private File cacheFile;
    private int imageHeight;
    private int imageWidth;

    public Media(String path) throws MalformedURLException {
        this(path, getSmbHost(path));
    }

    public static Media fromUri(String uri) {
        try {
            return new Media(uri);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public Media(String path, String host) throws MalformedURLException {
        if (cachePath == null) {
            throw new RuntimeException("Cache directory is not been set");
        }

        boolean isCache = false;

        if (path.startsWith(SCHEME_CACHE)) {
            path = path.substring(path.indexOf('@') + 1, path.length());
            host = getSmbHost(path);
            isCache = true;
        }

        if (path.startsWith(SCHEME_FILE)) {
            path = path.replace("file://", "");
        }

        if (path.startsWith(SCHEME_SAMBA)) {
            this.smbFile = new SmbFile(path, Samba.getAuth(host));
        } else if (path.startsWith(SCHEME_DAV) || path.startsWith(SCHEME_DAVS)) {
            this.davFile = new WebDavFile(path);
        } else {
            this.localFile = new File(path);
        }

        if (isCache) {
            this.cacheFile = new File(getCachePath());
        }
    }

    public Media(SmbFile smbFile) {
        this.smbFile = smbFile;
    }

    public Media(WebDavFile davFile) {
        this.davFile = davFile;
    }

    public Media(File localFile) {
        this.localFile = localFile;
    }

    public static void setCacheDir(String path) {
        cachePath = path;
    }

    private static String getSmbHost(String path) {
        if (path.startsWith("smb://")) {
            path = path.replace("smb://", "");
            return path.substring(0, path.indexOf("/"));
        } else {
            return null;
        }
    }

    public int getImageHeight() {
        return imageHeight;
    }

    public void setImageHeight(int imageHeight) {
        this.imageHeight = imageHeight;
    }

    public int type() {
        return TYPE_IMAGE;
    }

    public int getImageWidth() {
        return imageWidth;
    }

    public void setImageWidth(int imageWidth) {
        this.imageWidth = imageWidth;
    }

    public boolean isDirectory() {
        boolean result = false;
        try {
            result = smbFile != null && smbFile.isDirectory() ||
                    localFile != null && localFile.isDirectory() ||
                    davFile != null && davFile.isDirectory();
        } catch (SmbException e) {
            e.printStackTrace();
        }
        return result;
    }

    public boolean isHiding() {
        boolean result = false;
        if (smbFile != null) {
            result = smbFile.getName().toLowerCase().startsWith(".");
        } else if (localFile != null) {
            result = localFile.getName().toLowerCase().startsWith(".");
        } else if (davFile != null) {
            result = davFile.getName().toLowerCase().startsWith(".");
        }
        return result;
    }

    public String getName() {
        if (smbFile != null) {
            return smbFile.getName();
        } else if (localFile != null) {
            return localFile.getName();
        } else if (davFile != null) {
            return davFile.getName();
        } else {
            return null;
        }
    }

    public String getHost() {
        if (smbFile != null) {
            return smbFile.getServer();
        } else if (davFile != null) {
            return davFile.getHost();
        } else {
            return "";
        }
    }

    public boolean canRead() throws SmbException {
        if (smbFile != null) {
            return smbFile.canRead();
        } else if (davFile != null) {
            return davFile.canRead();
        } else {
            return localFile != null && localFile.canRead();
        }
    }

    public boolean canWrite() throws SmbException {
        if (smbFile != null) {
            return smbFile.canWrite();
        } else if (davFile != null) {
            return davFile.canWrite();
        } else {
            return localFile != null && localFile.canWrite();
        }
    }

    public boolean exists() throws SmbException {
        if (smbFile != null) {
            return smbFile.exists();
        } else if (davFile != null) {
            return davFile.exists();
        } else {
            return localFile != null && localFile.exists();
        }
    }

    public long getLastModified() {
        if (smbFile != null) {
            return smbFile.getLastModified();
        } else if (localFile != null) {
            return localFile.lastModified();
        } else if (davFile != null) {
            return davFile.getLastModified();
        } else {
            return -1;
        }
    }

    public long getContentLength() {
        if (smbFile != null) {
            return smbFile.getContentLength();
        } else if (localFile != null) {
            return localFile.length();
        } else {
            return -1;
        }
    }

    public String getPath() {
        if (smbFile != null) {
            return smbFile.getPath();
        } else if (localFile != null) {
            return localFile.getPath();
        } else if (davFile != null) {
            return davFile.getPath();
        } else {
            return null;
        }
    }

    public String getParent() {
        String parent;

        if (smbFile != null) {
            parent = smbFile.getParent();
        } else if (davFile != null) {
            return davFile.getParent();
        } else {
            parent = "file://" + localFile.getParent();
        }
        return parent;
    }

    public String getUri() {
        if (smbFile != null) {
            return smbFile.getPath();
        } else if (localFile != null) {
            return "file://" + localFile.getPath();
        } else if (davFile != null) {
            return davFile.getPath();
        } else {
            return null;
        }
    }

    public String getCacheUri() {
        return SCHEME_CACHE + "://" + getLastModified() + "@" + getUri();
    }

    public InputStream getInputStream() throws IOException {
        if (isCache()) {
            return new FileInputStream(cacheFile);
        } else if (smbFile != null) {
            return smbFile.getInputStream();
        } else if (localFile != null) {
            return new FileInputStream(localFile);
        } else if (davFile != null) {
            return davFile.getInputStream();
        } else {
            return null;
        }
    }

    public String getCachePath() {
        return cachePath + "/" + Utils.md5(getUri());
    }

    public String getCacheFileUri() {
        return "file://" + cachePath + "/" + Utils.md5(getUri());
    }

    public boolean isCache() {
        return cacheFile != null && cacheFile.exists();
    }

    public boolean hasCache() {
        File f = new File(getCachePath());
        return f.exists();
    }

    public boolean isGif() {
        boolean result = false;
        if (smbFile != null) {
            result = smbFile.getName().toLowerCase().endsWith(".gif");
        } else if (localFile != null) {
            result = localFile.getName().toLowerCase().endsWith(".gif");
        }
        return result;
    }

    public boolean isFile() throws SmbException {
        boolean result = false;
        if (smbFile != null) {
            result = smbFile.isFile();
        } else if (localFile != null) {
            result = localFile.isFile();
        }
        return result;
    }

    public Media[] listMedia() throws SmbException {
        ArrayList<Media> list = new ArrayList<>();
        if (smbFile != null) {
            SmbFile[] files = smbFile.listFiles();
            for (SmbFile file : files) {
                if (!file.getName().contains(":")) {
                    list.add(new Media(file));
                }
            }
        } else if (localFile != null) {
            File[] files = localFile.listFiles();
            for (File file : files) {
                list.add(new Media(file));
            }
        } else if (davFile != null) {
            try {
                WebDavFile[] files = davFile.listFiles();
                for (WebDavFile file : files) {
                    list.add(new Media(file));
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
        Collections.sort(list);
        return list.toArray(new Media[list.size()]);
    }

    public boolean hasImage() throws SmbException {
        return hasImage(2);
    }

    private boolean hasImage(int step) throws SmbException {
        boolean result = false;
        if (isDirectory() && step > 0) {
            Media[] files = listMedia();
            if (files.length > 0) {
                // TODO: optimize algorithms
                for (Media f : files) {
                    if (f.isImage() || f.isDirectory() && f.hasImage(step - 1)) {
                        result = true;
                        break;
                    }
                }
            }
        }

        if (step == 0) {
            result = true;
        }

        return result;
    }

    public boolean isImage() {
        String name = getName().toLowerCase();
        return name.endsWith(".png") ||
                name.endsWith(".jpg") ||
                name.endsWith(".jpeg") ||
                name.endsWith(".bmp") ||
                name.endsWith(".gif");
    }

    public boolean isVideo() {
        String name = getName().toLowerCase();
        return name.endsWith(".mp4") ||
                name.endsWith(".avi") ||
                name.endsWith(".wmv") ||
                name.endsWith(".mkv") ||
                name.endsWith(".3gp");
    }

    public boolean isSamba() {
        return smbFile != null;
    }

    public String getMimeType() {
        if (smbFile != null) {
            return Utils.getMimeType(smbFile.getPath());
        } else {
            return Utils.getMimeType(localFile.getPath());
        }
    }

    public String getFormattedSize() {
        if (smbFile != null) {
            return Utils.humanReadableByteCount(smbFile.getContentLength(), true);
        } else {
            return Utils.humanReadableByteCount(localFile.length(), true);
        }
    }

    public String getFormattedDate() {
        if (smbFile != null) {
            return Utils.humanReadableDate(smbFile.getLastModified());
        } else {
            return Utils.humanReadableDate(localFile.lastModified());
        }
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
    public int compareTo(@NonNull Media another) {
        return getName().compareTo(another.getName());
    }
}
