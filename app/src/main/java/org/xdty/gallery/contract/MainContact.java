package org.xdty.gallery.contract;

import org.xdty.gallery.model.Media;

import java.util.List;

public interface MainContact {

    interface View extends BaseView<Presenter> {
        void setTitle(String title);
        void scrollToPosition(int position);
        void replaceData(List<Media> mediaList);
        void startViewer(int position, Media media);
        void showLoading(boolean isLoading);
    }

    interface Presenter extends BasePresenter {

        boolean isRoot();

        void reFresh();

        void addServer(String uri, String user, String pass);

        void clickItem(int position, Media mediaFile, int firstPosition);

        void loadChild(int position, Media media);

        boolean loadParent(int firstVisibleItemPosition);

        void clear();
    }
}
