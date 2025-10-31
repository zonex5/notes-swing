package xyz.toway.notes.ui.presenter;

import xyz.toway.notes.ui.ApplicationContext;
import xyz.toway.notes.ui.view.MainForm;


public class MainPresenter implements GeneralPresenter<MainForm> {

    private ApplicationContext context;
    private MainForm view;

    public MainPresenter(ApplicationContext applicationContext) {
        this.context = applicationContext;

        //---- tmp
        //context.getSettingsService().getSettings().setDatabaseFilePath("D:\\database.db");

        var path = context.getSettingsService().getSettings().getDatabaseFilePath();

        context.getDatabaseService().initDatabase(path.get(), "tolik", "ionel");
        System.out.println("Database initialized at: " + path.get());
        context.getDatabaseService().test();
        System.out.println("Database test completed.");

    }

    @Override
    public void setView(MainForm view) {
        this.view = view;
    }

    @Override
    public MainForm getView() {
        return view;
    }

    public void clickedButton() {
        // show a message dialog
        javax.swing.JOptionPane.showMessageDialog(null, "Button clicked!");
    }
}
