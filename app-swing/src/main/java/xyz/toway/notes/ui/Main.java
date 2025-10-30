package xyz.toway.notes.ui;

import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import xyz.toway.notes.ui.presenter.MainPresenter;
import xyz.toway.notes.ui.view.MainForm;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        FlatLaf.registerCustomDefaultsSource("themes");
        //FlatIntelliJLaf.setup();
        FlatLightLaf.setup();

        //showIcons();

        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("My Super Notes");
            MainForm form = new MainForm(
                    new MainPresenter()
            );
            //MainPresenter presenter = injector.getInstance(MainPresenter.class);
            //form.setPresenter(presenter);
            //presenter.setView(form);

            frame.setContentPane(form.getRootComponent());
            frame.setJMenuBar(form.getMenuBar());

            //frame.add(form.getToolbar(), BorderLayout.NORTH);

            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setPreferredSize(new Dimension(900, 600));
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
            frame.setIconImages(List.of(
                    new FlatSVGIcon("icons/icon.svg", 16, 16).getImage()
            ));
        });
    }

    private static void showIcons() {
        UIDefaults defaults = UIManager.getDefaults();

        // Перебираем и печатаем только те, где значение — Icon
        List<String> icons = new ArrayList<>();
        for (Object key : defaults.keySet()) {
            Object val = defaults.get(key);
            if (val instanceof Icon)
                icons.add(key + " = " + val.getClass().getName());
        }

        Collections.sort(icons);
        icons.forEach(System.out::println);
    }
}
