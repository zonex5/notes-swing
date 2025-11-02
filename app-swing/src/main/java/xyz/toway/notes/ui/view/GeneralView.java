package xyz.toway.notes.ui.view;

import xyz.toway.notes.domain.model.StoredSettings;
import xyz.toway.notes.ui.presenter.GeneralPresenter;

public interface GeneralView<P extends GeneralPresenter<?>> {

    P getPresenter();

    void applyUISettings(StoredSettings settings);
}
