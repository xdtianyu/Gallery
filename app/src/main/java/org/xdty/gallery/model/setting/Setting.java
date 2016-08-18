package org.xdty.gallery.model.setting;

import java.util.Set;

public interface Setting {

    boolean isCatchCrashEnable();

    Set<String> getServers();

    void addServer(String server);
}
