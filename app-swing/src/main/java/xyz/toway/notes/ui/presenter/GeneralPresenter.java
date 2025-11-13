package xyz.toway.notes.ui.presenter;

import xyz.toway.notes.ui.view.GeneralView;

public interface GeneralPresenter<V extends GeneralView> {

    default void init() {
    }

    default void destroy() {
    }

    void setView(V view);

    V getView();
}
