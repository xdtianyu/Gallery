package org.xdty.gallery.model.database;

import org.xdty.gallery.application.Application;
import org.xdty.gallery.model.db.Server;

import java.util.List;

import javax.inject.Inject;

import io.requery.Persistable;
import io.requery.sql.EntityDataStore;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class DatabaseImpl implements Database {

    @Inject
    EntityDataStore<Persistable> mDataStore;

    public DatabaseImpl() {
        Application.getAppComponent().inject(this);
    }

    public static Database getInstance() {
        return SingletonHelper.INSTANCE;
    }

    @Override
    public Observable<List<Server>> getServers() {
        return Observable.create(new Observable.OnSubscribe<List<Server>>() {
            @Override
            public void call(Subscriber<? super List<Server>> subscriber) {
                subscriber.onNext(getServersSync());
                subscriber.onCompleted();
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public List<Server> getServersSync() {
        return mDataStore.select(Server.class).get().toList();
    }

    @Override
    public void addServer(Server server) {
        Observable.just(server).observeOn(Schedulers.io()).subscribe(new Action1<Server>() {
            @Override
            public void call(Server server) {
                mDataStore.insert(server);
            }
        });
    }

    @Override
    public void removeServer(Server server) {
        Observable.just(server).observeOn(Schedulers.io()).subscribe(new Action1<Server>() {
            @Override
            public void call(Server server) {
                mDataStore.delete(server);
            }
        });
    }

    @Override
    public void updateServer(Server server) {
        Observable.just(server).observeOn(Schedulers.io()).subscribe(new Action1<Server>() {
            @Override
            public void call(Server server) {
                mDataStore.update(server);
            }
        });
    }

    private static class SingletonHelper {
        private final static DatabaseImpl INSTANCE = new DatabaseImpl();
    }
}
