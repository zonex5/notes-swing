package xyz.toway.notes.ui;

import dagger.Module;
import dagger.Provides;
import dagger.Component;
import jakarta.inject.Singleton;
import xyz.toway.notes.ui.presenter.MainPresenter;
import xyz.toway.notes.ui.view.MainForm;

@Module
public class DIConfiguration {

    @Provides
    @Singleton
    public MainPresenter provideMainPresenter() {
        return new MainPresenter();
    }
}

@Singleton
@Component(modules = DIConfiguration.class)
interface AppComponent {
    MainForm buildMainWindow();
}
