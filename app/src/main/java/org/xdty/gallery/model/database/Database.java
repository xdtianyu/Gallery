package org.xdty.gallery.model.database;

import org.xdty.gallery.model.db.Server;

import java.util.List;

import io.reactivex.Observable;

public interface Database {

    Observable<List<Server>> getServers();

    List<Server> getServersSync();

    void addServer(Server server);

    void removeServer(Server server);

    void updateServer(Server server);

}
