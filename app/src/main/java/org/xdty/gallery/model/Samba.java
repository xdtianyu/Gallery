package org.xdty.gallery.model;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbFile;

public class Samba {

    static List<SmbInfo> smbList = new ArrayList<>();

    public static NtlmPasswordAuthentication getAuth(String domain) {

        NtlmPasswordAuthentication smbAuth = null;
        for (SmbInfo smb : smbList) {
            if (smb.smbAuth.getDomain().equals(domain)) {
                smbAuth = smb.smbAuth;
            }
        }
        return smbAuth;
    }

    public static String getDirectory(String domain) {
        String directory = null;
        for (SmbInfo smb : smbList) {
            if (smb.smbAuth.getDomain().equals(domain)) {
                directory = smb.directory;
            }
        }
        return directory;
    }

    public static void add(String domain, String directory, String user, String password) {
        if (getAuth(domain) == null) {
            SmbInfo smbInfo = new SmbInfo(domain, directory, user, password);
            smbList.add(smbInfo);
        }
    }

//    public void add(JSONObject jsonObject) throws JSONException {
//        address = jsonObject.getString("address");
//        username = jsonObject.getString("username");
//        password = jsonObject.getString("password");
//        directory = jsonObject.getString("directory");
//        id = jsonObject.getInt("id");
//    }

    public static String build(String address, String directory) {
        String s = "";
        if (address != null && !address.isEmpty() && directory != null && !directory.isEmpty()) {
            s = "smb://" + address + "/" + directory + "/";
        }

        return s;
    }

    public static SmbFile root(String domain) {
        SmbFile smbFile = null;
        try {
            smbFile = new SmbFile(build(domain, getDirectory(domain)), getAuth(domain));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return smbFile;
    }

    static class SmbInfo {
        NtlmPasswordAuthentication smbAuth;
        String directory;

        SmbInfo(String domain, String directory, String user, String password) {
            smbAuth = new NtlmPasswordAuthentication(domain, user, password);
            this.directory = directory;
        }
    }

}
