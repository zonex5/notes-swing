package xyz.toway.notes.ui;

import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import xyz.toway.notes.ui.presenter.MainPresenter;
import xyz.toway.notes.ui.view.MainForm;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        FlatLaf.registerCustomDefaultsSource("themes");
        //FlatIntelliJLaf.setup();
        FlatLightLaf.setup();

        AppComponent component = DaggerAppComponent.create();

        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("FlatLaf + IntelliJ .form");
            MainForm form = component.buildMainWindow();
            //MainPresenter presenter = injector.getInstance(MainPresenter.class);
            //form.setPresenter(presenter);
            //presenter.setView(form);

            frame.setContentPane(form.$$$getRootComponent$$$());
            frame.setJMenuBar(form.getMenuBar());

            frame.add(form.getToolbar(), BorderLayout.NORTH);

            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setPreferredSize(new Dimension(800, 500));
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
            frame.setIconImages(List.of(
                    new FlatSVGIcon("icons/icon.svg", 16, 16).getImage()
            ));
        });
    }
}
