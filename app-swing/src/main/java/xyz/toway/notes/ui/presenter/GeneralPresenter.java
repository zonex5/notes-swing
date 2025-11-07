package xyz.toway.notes.ui.presenter;

import xyz.toway.notes.ui.view.GeneralView;

public interface GeneralPresenter {

    default void init() {
    }

    default void destroy() {
    }

    void setView(GeneralView view);

    GeneralView getView();
}
