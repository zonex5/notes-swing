package xyz.toway.notes.ui.view;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.icons.FlatTabbedPaneCloseIcon;
import lombok.NonNull;
import xyz.toway.notes.domain.model.ContentModel;
import xyz.toway.notes.domain.model.NoteModel;
import xyz.toway.notes.domain.model.StoredSettings;
import xyz.toway.notes.ui.presenter.MainPresenter;
import xyz.toway.notes.ui.view.components.StatusBar;
import xyz.toway.notes.ui.view.components.TextNoteTab;
import xyz.toway.notes.ui.view.dialogs.InputValueDialog;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.*;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.stream.Stream;

import static xyz.toway.notes.ui.Main.icon;

public class MainForm extends JFrame implements GeneralView<MainPresenter> {

    private final MainPresenter presenter;

    private JPanel mainPanel;
    private StatusBar statusBar;
    private JTabbedPane tabbedPane;

    private final Map<String, JCheckBoxMenuItem> menuItems = new HashMap<>();

    public MainForm(MainPresenter presenter) {
        this.presenter = presenter;

        createUIComponents();

        setTitle("My Super Notes");
        setContentPane(mainPanel);
        setJMenuBar(createMenuBar());
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setPreferredSize(new Dimension(1100, 700));
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
        setIconImages(List.of(icon("/icons/icon.svg", 16, 16).getImage()));

        // window listeners
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                onWindowClosing(e);
            }
        });
    }

    private void onWindowClosing(WindowEvent e) {
        // save opened documents
        if (tabbedPane.getTabCount() > 0) {
            // collect ids of opened documents
            List<String> ids = Stream.of(tabbedPane.getComponents())
                    .map(c -> c instanceof TextNoteTab tab ? tab : null)
                    .filter(Objects::nonNull)
                    .map(this::saveTextContent)
                    .map(ContentModel::getId)
                    .toList();
            presenter.saveOpenedDocs(ids);
        }

        // close window
        presenter.destroy();
        this.dispose();
        System.exit(0);
    }

    private void createUIComponents() {
        mainPanel = new JPanel(new BorderLayout(8, 0));

        tabbedPane = new JTabbedPane();
        mainPanel.add(tabbedPane, BorderLayout.CENTER);

        UIManager.put("TabbedPane.closeHoverForeground", UIManager.getColor("TabbedPane.background"));
        UIManager.put("TabbedPane.closeCrossPlainSize", 6);
        UIManager.put("TabbedPane.closeArc", 999);
        UIManager.put("TabbedPane.closeCrossFilledSize", 5.5f);
        UIManager.put("TabbedPane.closeIcon", new FlatTabbedPaneCloseIcon());
        tabbedPane.updateUI();

        addTabHeaderContextMenu();
        tabbedPane.putClientProperty(FlatClientProperties.TABBED_PANE_TAB_CLOSABLE, true);
        tabbedPane.putClientProperty(FlatClientProperties.TABBED_PANE_TAB_CLOSE_TOOLTIPTEXT, "Close");
        tabbedPane.putClientProperty(FlatClientProperties.TABBED_PANE_TAB_CLOSE_CALLBACK,
                (BiConsumer<JTabbedPane, Integer>) (tp, index) -> closeTab(index));

        statusBar = new StatusBar();
        mainPanel.add(statusBar, BorderLayout.SOUTH);
    }

    private void addTabHeaderContextMenu() {
        // create popup menu for tab headers
        JPopupMenu popup = new JPopupMenu();
        JMenuItem renameItem = new JMenuItem("Rename", icon("/icons/doc-edit.svg", 16, 16));
        JMenuItem duplicateItem = new JMenuItem("Duplicate", icon("/icons/duplicate.svg", 16, 16));
        JMenuItem closeItem = new JMenuItem("Close", icon("/icons/close.svg", 16, 16));
        popup.add(renameItem);
        popup.add(duplicateItem);
        popup.addSeparator();
        popup.add(closeItem);
        tabbedPane.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.isPopupTrigger()) showPopup(e);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger()) showPopup(e);
            }

            private void showPopup(MouseEvent e) {
                int index = tabbedPane.indexAtLocation(e.getX(), e.getY());
                if (index != -1) {
                    tabbedPane.setSelectedIndex(index);
                    popup.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        });
        closeItem.addActionListener(ev -> {
            int index = tabbedPane.getSelectedIndex();
            if (index != -1) closeTab(index);
        });
        renameItem.addActionListener(ev -> renameSelectedTabModel());
        duplicateItem.addActionListener(ev -> duplicateTab());
    }

    private void duplicateTab() {
        if (tabbedPane.getSelectedComponent() instanceof TextNoteTab tab) {
            // save current content before duplicating
            saveTextContent(tab);

            NoteModel originalModel = tab.getModel();
            NoteModel newModel = new NoteModel();
            newModel.setTitle(originalModel.getTitle() + " (Copy)");
            newModel.setContent(originalModel.getContent());
            createNewTab(newModel);

            // save the duplicated note
            saveSelectedTabContent();
        }
    }

    private void closeTab(int index) {
        //try to save content before closing
        var component = (TextNoteTab) tabbedPane.getComponentAt(index);
        saveTextContent(component);

        // close tab
        tabbedPane.removeTabAt(index);

        // if no tabs left, create a new empty tab
        if (tabbedPane.getTabCount() == 0) {
            createNewTab(new NoteModel());
        }
    }

    private void renameSelectedTabModel() {
        if (tabbedPane.getSelectedComponent() instanceof TextNoteTab tab) {
            var model = tab.getModel();
            InputValueDialog.show(
                    this,
                    "Enter new title:",
                    model.getTitle(),
                    newTitle -> {
                        model.setTitle(newTitle);
                        tabbedPane.setTitleAt(tabbedPane.getSelectedIndex(), newTitle);
                        var saved = presenter.save(model);
                        model.setId(saved.getId());
                    }
            );
        }
    }

    public void createNewTab(ContentModel contentModel) {
        if (contentModel instanceof NoteModel noteModel) {
            // create text note tab
            TextNoteTab textNoteTab = new TextNoteTab(noteModel);
            textNoteTab.addPropertyChangeListener("saveNote", evt -> saveTextContent(textNoteTab));

            // create a new tab
            tabbedPane.addTab(noteModel.getTitle(), icon("/icons/doc.svg", 16, 16), textNoteTab);
            tabbedPane.setSelectedIndex(tabbedPane.getTabCount() - 1);
        }
    }

    public void createEmptyTab() {
        createNewTab(new NoteModel());
    }

    public JMenuBar createMenuBar() {
        var menuBar = new JMenuBar();

        JMenu fileMenu = new JMenu("File");
        fileMenu.add(new JMenuItem("Open"));
        fileMenu.add(new JMenuItem("Exit"));
        menuBar.add(fileMenu);

        JMenu viewMenu = new JMenu("View");
        JCheckBoxMenuItem statusBarItem = new JCheckBoxMenuItem("Show Status Bar");
        viewMenu.add(statusBarItem);
        menuBar.add(viewMenu);
        menuItems.put("showStatusBar", statusBarItem);
        JMenu optionsMenu = new JMenu("Options");
        JCheckBoxMenuItem restoreItem = new JCheckBoxMenuItem("Restore Last Session");
        optionsMenu.add(restoreItem);
        menuBar.add(optionsMenu);
        menuItems.put("restoreLastSession", restoreItem);

        menuBar.add(Box.createHorizontalGlue());
        menuBar.add(getToolbar());

        //---- callbacks
        statusBarItem.addActionListener(e -> {
            presenter.setSettingsOption("statusBarVisible", statusBarItem.isSelected());
            showStatusBar(statusBarItem.isSelected());
        });
        restoreItem.addActionListener(e -> {
            presenter.setSettingsOption("restoreLastSession", restoreItem.isSelected());
        });

        return menuBar;
    }

    public JToolBar getToolbar() {

        JToolBar toolBar = new JToolBar();
        toolBar.setMargin(new Insets(3, 3, 3, 3));

        toolBar.addSeparator();
        toolBar.add(createButton("New", "/icons/new.svg", this::createEmptyTab));
        toolBar.add(createButton("Open", "/icons/open.svg", this::openExistingNote));
        toolBar.add(createButton("Save", "/icons/save.svg", this::saveSelectedTabContent));
        toolBar.add(createButton("Export", "/icons/download.svg", this::exportCurrentNote));
        toolBar.addSeparator();

        return toolBar;
    }

    private void exportCurrentNote() {
        var component = (TextNoteTab) tabbedPane.getSelectedComponent();
        NoteModel model = component.getModel();

        // show Save File Dialog and save content as text file
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setSelectedFile(new java.io.File(presenter.toValidFileName(model.getTitle()) + ".txt"));
        int userSelection = fileChooser.showSaveDialog(this);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            presenter.saveNoteAsTextFile(component.getText(), fileChooser.getSelectedFile());
        }
    }

    private void openExistingNote() {
        NotesManagerWindow w = new NotesManagerWindow(this);
        w.showWindow();
        if (w.getResult() != null) {
            var firstPanel = (TextNoteTab) tabbedPane.getComponentAt(0);
            var remove = tabbedPane.getTabCount() == 1 && firstPanel.canBeRemoved();
            createNewTab(w.getResult());
            if (remove) {
                tabbedPane.removeTabAt(0);
            }
        }
    }

    private ContentModel saveSelectedTabContent() {
        if (tabbedPane.getSelectedComponent() instanceof TextNoteTab tab) {
            return saveTextContent(tab);
        }
        return null;
    }

    private ContentModel saveTextContent(@NonNull TextNoteTab tab) {
        var noteModel = tab.getModel();
        var newHash = NoteModel.calculateContentHash(tab.getText());
        if (newHash != noteModel.getContentHash()) {
            noteModel.setContent(tab.getText());
            ContentModel saved = presenter.save(noteModel);
            System.out.println("Saved note: " + noteModel.getTitle() + " with new hash: " + newHash);
            noteModel.setId(saved.getId());
            return saved;
        } else {
            System.out.println("No changes detected for note: " + noteModel.getTitle() + " with hash: " + noteModel.getContentHash());
            return noteModel;
        }
    }

    private void showStatusBar(boolean visible) {
        statusBar.setVisible(visible);
        mainPanel.revalidate();
        mainPanel.repaint();
    }

    @Override
    public MainPresenter getPresenter() {
        return presenter;
    }

    @Override
    public void applyUISettings(StoredSettings settings) {
        showStatusBar(settings.statusBarVisible());
        menuItems.get("showStatusBar").setSelected(settings.statusBarVisible());
        menuItems.get("restoreLastSession").setSelected(settings.restoreLastSession());
    }

    public String requestPassword() {
        JPasswordField passwordField = new JPasswordField();
        JLabel label = new JLabel("Please enter your password:");

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        passwordField.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(label);
        panel.add(Box.createVerticalStrut(6));
        panel.add(passwordField);

        int option = JOptionPane.showConfirmDialog(SwingUtilities.windowForComponent(this.mainPanel), panel, "Password Required", JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE, UIManager.getIcon("OptionPane.informationIcon"));
        if (option == JOptionPane.OK_OPTION) {
            return new String(passwordField.getPassword());
        }
        return null;
    }

    public void showErrorMessage(String message) {
        JOptionPane.showMessageDialog(SwingUtilities.windowForComponent(this.mainPanel), message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public void showNotification(String message) {
        statusBar.setLeftText(message);
    }

    public void showNotification(String message, Color color) {
        String hex = String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
        statusBar.setLeftText("<html><span style='color: " + hex + ";'>" + message + "</span></html>");
    }
}