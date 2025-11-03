package xyz.toway.notes.ui;

import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import xyz.toway.notes.domain.port.DatabaseRepository;
import xyz.toway.notes.domain.port.SettingsRepository;
import xyz.toway.notes.domain.port.factory.DatabaseRepositoryFactory;
import xyz.toway.notes.domain.port.factory.SettingsRepositoryFactory;
import xyz.toway.notes.service.DatabaseService;
import xyz.toway.notes.service.NoteService;
import xyz.toway.notes.service.SettingsService;
import xyz.toway.notes.ui.presenter.MainPresenter;
import xyz.toway.notes.ui.view.MainForm;

import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.util.*;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        FlatLaf.registerCustomDefaultsSource("themes");
        //FlatIntelliJLaf.setup();
        FlatLightLaf.setup();

        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("My Super Notes");

            var presenter = new MainPresenter(setDI());
            MainForm form = new MainForm(presenter);
            presenter.setView(form);
            presenter.init();

            frame.setPreferredSize(new Dimension(900, 600));
            frame.pack();

/*            frame.setContentPane(form.getRootComponent());
            frame.setJMenuBar(form.getMenuBar());

            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setPreferredSize(new Dimension(900, 600));
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
            frame.setIconImages(List.of(icon("/icons/icon.svg", 16, 16).getImage()));*/
        });

        //showIcons();
        //showColors();
    }

    public static FlatSVGIcon icon(String path, int w, int h) {
        return icon(path).derive(w, h);
    }

    public static FlatSVGIcon icon(String path) {
        URL url = Main.class.getResource(path);
        if (url == null) throw new IllegalStateException("Icon not found: " + path);
        return new FlatSVGIcon(url);
    }

    private static ApplicationContext setDI() {
        SettingsRepository settingsRepository = ServiceLoader.load(SettingsRepositoryFactory.class)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No provider found"))
                .create();
        DatabaseRepository databaseRepository = ServiceLoader.load(DatabaseRepositoryFactory.class)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No provider found"))
                .create(settingsRepository);
        return new ApplicationContext(
                new DatabaseService(databaseRepository),
                new NoteService(databaseRepository),
                new SettingsService(settingsRepository)
        );
    }

    private static void showIcons() {
        UIDefaults defaults = UIManager.getDefaults();

        List<String> icons = new ArrayList<>();
        for (Object key : defaults.keySet()) {
            Object val = defaults.get(key);
            if (val instanceof Icon)
                icons.add(key + " = " + val.getClass().getName());
        }

        Collections.sort(icons);
        icons.forEach(System.out::println);
    }

    private static void showColors() {
        UIDefaults defaults = UIManager.getDefaults();
        Enumeration<Object> keys = defaults.keys();

        System.out.println("=== FlatLaf Theme Colors ===");
        while (keys.hasMoreElements()) {
            Object key = keys.nextElement();
            Object value = defaults.get(key);

            // Print only color values
            if (value instanceof Color color) {
                System.out.printf("%s = #%02x%02x%02x%n",
                        key,
                        color.getRed(),
                        color.getGreen(),
                        color.getBlue());
            }
        }
    }
}
