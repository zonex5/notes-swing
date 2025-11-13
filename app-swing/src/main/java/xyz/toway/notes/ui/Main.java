package xyz.toway.notes.ui;

import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import lombok.NonNull;
import xyz.toway.notes.domain.port.*;
import xyz.toway.notes.domain.port.factory.*;
import xyz.toway.notes.service.DatabaseService;
import xyz.toway.notes.service.NoteService;
import xyz.toway.notes.service.SettingsService;
import xyz.toway.notes.service.UtilsService;
import xyz.toway.notes.ui.presenter.MainPresenter;
import xyz.toway.notes.ui.view.MainWindow;

import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.util.*;
import java.util.List;

public class Main {
    public static ApplicationContext context;

    public static void main(String[] args) {
        FlatLaf.registerCustomDefaultsSource("themes");
        //FlatIntelliJLaf.setup();
        FlatLightLaf.setup();

        // create DI context
        context = setDI();

        // add shutdown hook to close database
        Runtime.getRuntime().addShutdownHook(new Thread(() -> context.getDatabaseService().closeDatabase(), "db-shutdown-hook"));

        SwingUtilities.invokeLater(() -> {
            var presenter = new MainPresenter();
            new MainWindow(presenter);
        });
    }

    public static FlatSVGIcon icon(@NonNull String path, int w, int h) {
        var icon = icon(path);
        if (icon == null) return null;

        return icon.derive(w, h);
    }

    public static FlatSVGIcon icon(@NonNull String path) {
        URL url = Main.class.getResource(path);
        if (url == null) return null;
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
        NoteRepository noteRepository = ServiceLoader.load(NoteRepositoryFactory.class)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No provider found"))
                .create(databaseRepository);
        LastOpenedRepository lastOpenedRepository = ServiceLoader.load(LastOpenedRepositoryFactory.class)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No provider found"))
                .create(databaseRepository);
        GroupRepository groupRepository = ServiceLoader.load(GroupRepositoryFactory.class)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No provider found"))
                .create(databaseRepository);
        return new ApplicationContext(
                new DatabaseService(databaseRepository),
                new NoteService(noteRepository, groupRepository, lastOpenedRepository),
                new SettingsService(settingsRepository),
                new UtilsService()
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
