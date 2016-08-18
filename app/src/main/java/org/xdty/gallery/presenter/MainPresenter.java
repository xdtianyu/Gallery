package org.xdty.gallery.presenter;

import android.os.Environment;
import android.util.Log;

import com.google.gson.Gson;

import org.xdty.gallery.application.Application;
import org.xdty.gallery.contract.MainContact;
import org.xdty.gallery.model.LocalMedia;
import org.xdty.gallery.model.Media;
import org.xdty.gallery.model.SambaMedia;
import org.xdty.gallery.model.ServerInfo;
import org.xdty.gallery.model.WebDavMedia;
import org.xdty.gallery.model.setting.Setting;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class MainPresenter implements MainContact.Presenter {

    @Inject
    Setting mSetting;

    @Inject
    Gson mGson;

    MainContact.View mView;

    boolean isRoot = false;

    private List<Media> mMediaFileList = new ArrayList<>();

    public MainPresenter(MainContact.View view) {
        Application.getAppComponent().inject(this);
        mView = view;
    }

    @Override
    public void start() {
        try {
            Media.Builder.register(new LocalMedia());
            Media.Builder.register(new SambaMedia());
            Media.Builder.register(new WebDavMedia());

            Media.Builder.addRoot(Environment.getExternalStorageDirectory().getAbsolutePath(), null,
                    null);

            Set<String> servers = mSetting.getServers();
            for (String server : servers) {
                ServerInfo info = mGson.fromJson(server, ServerInfo.class);
                Media.Builder.addRoot(info.getUri(), info.getUser(), info.getPass());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        loadRootDir();
    }

    @Override
    public boolean isRoot() {
        return isRoot;
    }

    @Override
    public void reFresh() {
        if (isRoot) {
            loadRootDir();
        } else {
            loadDir(Media.Builder.getCurrent(), true);
        }
    }

    @Override
    public void addServer(String uri, String user, String pass) {
        String server = mGson.toJson(new ServerInfo(uri, user, pass));

        mSetting.addServer(server);

        Media.Builder.addRoot(uri, user, pass);
    }

    @Override
    public void clickItem(int position, Media media) {
        mView.onItemClicked(position, media);
    }

    @Override
    public void loadChild(int position, Media media) {
        Media current = media.parent();
        if (current != null) {
            current.setPosition(position);
        }

        loadDir(media);
    }

    @Override
    public boolean loadParent(int position) {
        if (isRoot()) {
            return false;
        }

        Media media = Media.Builder.getCurrent();
        if (media == null || media.parent() == null) {
            loadRootDir();
        } else {

            media.setPosition(position);
            loadDir(media.parent());
        }

        return true;
    }

    private void loadRootDir() {

        mMediaFileList.clear();
        mMediaFileList.addAll(Media.Builder.roots());

        Media.Builder.setCurrent(null);

        mView.replaceData(mMediaFileList);

        mView.setTitle(null);
        isRoot = true;
    }

    private void loadDir(final Media media) {
        loadDir(media, false).subscribe(new Action1<List<Media>>() {
            @Override
            public void call(List<Media> medias) {
                mView.replaceData(medias);

                isRoot = false;
                Media.Builder.setCurrent(media);

                mView.setTitle(media.getName());
                mView.scrollToPosition(media.getPosition());
            }
        });
    }

    private Observable<List<Media>> loadDir(final Media media, final boolean isRefresh) {
        return Observable.create(new Observable.OnSubscribe<List<Media>>() {

            @Override
            public void call(Subscriber<? super List<Media>> subscriber) {
                long start = System.currentTimeMillis();

                if (isRefresh) {
                    media.clear();
                }

                List<Media> medias = media.children();
                List<Media> list = new ArrayList<>();

                for (Media m : medias) {
                    if (m.isImage() || m.isDirectory()) {
                        list.add(m);
                    }
                }
                Log.e("aaa", "" + (System.currentTimeMillis() - start));

                subscriber.onNext(list);
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }
}
