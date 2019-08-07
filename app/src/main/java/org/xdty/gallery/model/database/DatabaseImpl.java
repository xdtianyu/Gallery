package org.xdty.gallery.model.database;

import org.xdty.gallery.application.Application;
import org.xdty.gallery.model.db.Server;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import io.requery.Persistable;
import io.requery.sql.EntityDataStore;

public class DatabaseImpl implements Database {

    @Inject
    EntityDataStore<Persistable> mDataStore;

    private CompositeDisposable mSubscriptions = new CompositeDisposable();

    public DatabaseImpl() {
        Application.getAppComponent().inject(this);
    }

    public static Database getInstance() {
        return SingletonHelper.INSTANCE;
    }

    @Override
    public Observable<List<Server>> getServers() {
        return Observable.create((ObservableOnSubscribe<List<Server>>) emitter -> {
            emitter.onNext(getServersSync());
            emitter.onComplete();
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public List<Server> getServersSync() {
        return mDataStore.select(Server.class).get().toList();
    }

    @Override
    public void addServer(Server server) {
        mSubscriptions.add(
                Observable.just(server).observeOn(Schedulers.io())
                        .subscribe(s -> mDataStore.insert(s))
        );
    }

    @Override
    public void removeServer(Server server) {
        mSubscriptions.add(
                Observable.just(server).observeOn(Schedulers.io())
                        .subscribe(s -> mDataStore.delete(s))
        );
    }

    @Override
    public void updateServer(Server server) {
        mSubscriptions.add(
                Observable.just(server).observeOn(Schedulers.io())
                        .subscribe(s -> mDataStore.update(s))
        );
    }

    private static class SingletonHelper {
        private final static DatabaseImpl INSTANCE = new DatabaseImpl();
    }
}
