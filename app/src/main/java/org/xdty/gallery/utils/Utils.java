package org.xdty.gallery.utils;

import android.content.res.Resources;
import android.util.TypedValue;
import android.webkit.MimeTypeMap;

import org.xdty.gallery.model.Media;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Utils {

    public final static String TAG = "Utils";

    private static SimpleDateFormat format =
            new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.ENGLISH);

    public static boolean isImage(String name) {
        name = name.toLowerCase();
        if (name.endsWith(".png") ||
                name.endsWith(".jpg") ||
                name.endsWith(".jpeg") ||
                name.endsWith(".bmp") ||
                name.endsWith(".gif")) {
            return true;
        } else {
            return false;
        }
    }

    public static int dpToPx(Resources res, int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                res.getDisplayMetrics());
    }

    public static String md5(String s) {
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuilder hexString = new StringBuilder();
            for (byte aMessageDigest : messageDigest)
                hexString.append(Integer.toHexString(0xFF & aMessageDigest));
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String md5(Media media) {
        return md5(
                media.getPath() + media.getLastModified() + media.getContentLength());
    }

    public static String fillString(int count, char c) {
        StringBuilder sb = new StringBuilder(count);
        for (int i = 0; i < count; i++) {
            sb.append(c);
        }
        return sb.toString();
    }

    public static String getMimeType(String url) {
        String type;
        String extension = url.substring(url.lastIndexOf('.') + 1);
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        type = mime.getMimeTypeFromExtension(extension);
        return type;
    }

    // read more from here: http://stackoverflow.com/a/3758880/2600042
    public static String humanReadableByteCount(long bytes, boolean si) {
        int unit = si ? 1000 : 1024;
        if (bytes < unit) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp - 1) + (si ? "" : "i");
        return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
    }

    public static String humanReadableDate(long timestamp) {
        Date date = new Date();
        date.setTime(timestamp);
        return format.format(date);
    }

    public static String getNameFromPath(String path) {
        if (path.endsWith("/")) {
            return path.substring(path.lastIndexOf('/', path.length() - 2) + 1);
        } else {
            return path.substring(path.lastIndexOf('/') + 1);
        }
    }

}
