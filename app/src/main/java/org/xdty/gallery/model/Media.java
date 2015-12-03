package org.xdty.gallery.model;

import org.xdty.gallery.utils.Utils;
import org.xdty.webdav.WebDavFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.ArrayList;

import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;

public class Media {

    public static final int TYPE_IMAGE = 1;
    public static final int TYPE_VIDEO = 2;
    public static final int TYPE_FOLDER = 3;

    private SmbFile smbFile;
    private File localFile;
    private WebDavFile davFile;
    private int imageHeight;
    private int imageWidth;

    public Media(String path) throws MalformedURLException {
        if (path.startsWith(Config.DAV_PREFIX) || path.startsWith(Config.DAVS_PREFIX)) {
            davFile = new WebDavFile(path);
        } else {
            this.localFile = new File(path);
        }
    }

    public Media(String path, String host) throws MalformedURLException {

        if (path.startsWith(Config.SAMBA_PREFIX)) {
            this.smbFile = new SmbFile(path, Samba.getAuth(host));
        } else if (path.startsWith(Config.DAV_PREFIX) || path.startsWith(Config.DAVS_PREFIX)) {
            this.davFile = new WebDavFile(path);
        } else {
            this.localFile = new File(path);
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

    public InputStream getInputStream() throws IOException {
        if (smbFile != null) {
            return smbFile.getInputStream();
        } else if (localFile != null) {
            return new FileInputStream(localFile);
        } else {
            return null;
        }
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
}
