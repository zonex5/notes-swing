package xyz.toway.notes.ui.presenter;

import xyz.toway.notes.ui.view.MainForm;

public class MainPresenter implements GeneralPresenter<MainForm> {

    private MainForm view;

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
