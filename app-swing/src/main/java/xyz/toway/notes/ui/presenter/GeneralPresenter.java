package xyz.toway.notes.ui.presenter;

import xyz.toway.notes.ui.view.GeneralView;

public interface GeneralPresenter<V extends GeneralView<?>> {

    void setView(V view);

    V getView();
}
