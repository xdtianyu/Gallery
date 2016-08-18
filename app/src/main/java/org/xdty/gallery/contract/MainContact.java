package org.xdty.gallery.contract;

import org.xdty.gallery.model.Media;

import java.util.List;

public interface MainContact {

    interface View extends BaseView<Presenter> {
        void setTitle(String title);
        void scrollToPosition(int position);
        void replaceData(List<Media> mediaList);

        void onItemClicked(int position, Media media);
    }

    interface Presenter extends BasePresenter {

        boolean isRoot();

        void reFresh();

        void addServer(String uri, String user, String pass);

        void clickItem(int position, Media mediaFile);

        void loadChild(int position, Media media);

        boolean loadParent(int firstVisibleItemPosition);
    }
}
