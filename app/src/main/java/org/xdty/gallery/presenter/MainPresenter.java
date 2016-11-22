package org.xdty.gallery.presenter;

import com.google.gson.Gson;

import org.xdty.gallery.application.Application;
import org.xdty.gallery.contract.MainContact;
import org.xdty.gallery.data.MediaDataSource;
import org.xdty.gallery.model.Media;
import org.xdty.gallery.model.database.Database;
import org.xdty.gallery.model.db.Server;
import org.xdty.gallery.model.media.AutoIndexMedia;
import org.xdty.gallery.model.media.LocalMedia;
import org.xdty.gallery.model.media.SambaMedia;
import org.xdty.gallery.model.media.WebDavMedia;
import org.xdty.gallery.setting.Setting;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import rx.functions.Action1;

public class MainPresenter implements MainContact.Presenter {

    @Inject
    Setting mSetting;

    @Inject
    Gson mGson;

    @Inject
    MediaDataSource mMediaDataSource;

    @Inject
    Database mDatabase;

    boolean isRoot = false;
    private MainContact.View mView;
    private List<Media> mMediaFileList = new ArrayList<>();

    private boolean isLoading = false;

    public MainPresenter(MainContact.View view) {
        Application.getAppComponent().inject(this);
        mView = view;
    }

    @Override
    public void start() {
        try {
            mMediaDataSource.register(new LocalMedia());
            mMediaDataSource.register(new SambaMedia());
            mMediaDataSource.register(new WebDavMedia());
            mMediaDataSource.register(new AutoIndexMedia());

            mMediaDataSource.addRoot(mSetting.getLocalPath(), null, null);

            mDatabase.getServers().subscribe(new Action1<List<Server>>() {
                @Override
                public void call(List<Server> servers) {
                    for (Server server : servers) {
                        mMediaDataSource.addRoot(server.getUri(), server.getUsername(),
                                server.getPassword());
                    }
                    loadRootDir();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
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
            loadDir(mMediaDataSource.getCurrent(), true);
        }
    }

    @Override
    public void addServer(String uri, String user, String pass) {
        Server server = new Server();
        server.setUri(uri);
        server.setUsername(user);
        server.setPassword(pass);
        mDatabase.addServer(server);
        mMediaDataSource.addRoot(uri, user, pass);
    }

    @Override
    public void clickItem(int position, Media media, int firstPosition) {
        if (isLoading) {
            return;
        }

        if (media.isImage()) {
            mMediaDataSource.setFilePosition(position);
            mView.startViewer(position, media);
        } else {
            loadChild(firstPosition, media);
        }
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

        Media media = mMediaDataSource.getCurrent();
        if (media == null || media.parent() == null) {
            loadRootDir();
        } else {

            media.setPosition(position);
            loadDir(media.parent());
        }

        return true;
    }

    @Override
    public void clear() {
        mMediaDataSource.clearCache();
        mMediaFileList.clear();
    }

    @Override
    public int getPosition() {
        return mMediaDataSource.getFilePosition();
    }

    private void loadRootDir() {

        mMediaFileList.clear();
        mMediaFileList.addAll(mMediaDataSource.roots());

        mMediaDataSource.setCurrent(null);

        mView.replaceData(mMediaFileList);

        mView.setTitle(null);
        isRoot = true;
    }

    private void loadDir(Media media) {
        mView.setTitle(media.getName());
        mView.showLoading(true);
        isLoading = true;
        loadDir(media, false);
    }

    private void loadDir(final Media media, final boolean isRefresh) {
        mMediaDataSource.loadDir(media, isRefresh).subscribe(new Action1<List<Media>>() {
            @Override
            public void call(List<Media> medias) {
                mView.replaceData(medias);

                isRoot = false;
                mMediaDataSource.setCurrent(media);

                mView.setTitle(media.getName());
                mView.scrollToPosition(media.getPosition());
                mView.showLoading(false);
                isLoading = false;
            }
        });
    }
}
