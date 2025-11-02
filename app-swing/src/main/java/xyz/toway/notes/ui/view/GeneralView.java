package xyz.toway.notes.ui.view;

import xyz.toway.notes.ui.presenter.GeneralPresenter;

import java.util.Map;

public interface GeneralView<P extends GeneralPresenter<?>> {

    P getPresenter();

    void applyUISettings(Map<String, Object> settings);
}
