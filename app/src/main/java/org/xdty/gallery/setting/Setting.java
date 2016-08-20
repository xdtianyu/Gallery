package org.xdty.gallery.setting;

import java.util.Set;

public interface Setting {

    boolean isCatchCrashEnable();

    Set<String> getServers();

    void addServer(String server);

    String getLocalPath();

}
