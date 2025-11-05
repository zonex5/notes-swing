package xyz.toway.notes.ui.view;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.icons.FlatTabbedPaneCloseIcon;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rtextarea.Gutter;
import org.fife.ui.rtextarea.RTextScrollPane;
import xyz.toway.notes.domain.model.ContentModel;
import xyz.toway.notes.domain.model.NoteModel;
import xyz.toway.notes.domain.model.StoredSettings;
import xyz.toway.notes.ui.presenter.MainPresenter;
import xyz.toway.notes.ui.view.components.StatusBar;
import xyz.toway.notes.ui.view.dialogs.InputValueDialog;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.time.Instant;
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
        var _this = this;
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent e) {
                super.windowOpened(e);
            }

            @Override
            public void windowClosing(WindowEvent e) {
                // no tabs/database opened
                if (tabbedPane.getTabCount() == 0) {
                    presenter.destroy();
                    _this.dispose();
                    System.exit(0);
                    return;
                }
                // collect ids of opened documents
                List<String> ids = Stream.of(tabbedPane.getComponents())
                        .map(c -> (JPanel) c)
                        .filter(p -> p.getClientProperty("model") != null)
                        .map(p -> saveContent(p))
                        .filter(Objects::nonNull)
                        .map(ContentModel::getId)
                        .toList();
                presenter.saveOpenedDocs(ids);
                presenter.destroy();
                _this.dispose(); // close window
                System.exit(0);  // optional: terminate JVM
            }
        });
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

        tabbedPane.putClientProperty(FlatClientProperties.TABBED_PANE_TAB_CLOSABLE, true);
        tabbedPane.putClientProperty(FlatClientProperties.TABBED_PANE_TAB_CLOSE_TOOLTIPTEXT, "Close");
        tabbedPane.putClientProperty(FlatClientProperties.TABBED_PANE_TAB_CLOSE_CALLBACK, (BiConsumer<JTabbedPane, Integer>) (tp, index) -> closeTab(index));

        statusBar = new StatusBar();
        mainPanel.add(statusBar, BorderLayout.SOUTH);

        ///---- tmp
        JPopupMenu popup = new JPopupMenu();
        JMenuItem renameItem = new JMenuItem("Rename", icon("/icons/doc-edit.svg", 16, 16));
        JMenuItem duplicateItem = new JMenuItem("Duplicate", icon("/icons/duplicate.svg", 16, 16));
        JMenuItem closeItem = new JMenuItem("Close", icon("/icons/close.svg", 16, 16));
        popup.add(renameItem);
        popup.add(duplicateItem);
        popup.addSeparator();
        popup.add(closeItem);
        // Mouse listener for header clicks
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
        renameItem.addActionListener(ev -> {
            getCurrentContentModel().ifPresent(model -> {
                InputValueDialog.show(
                        this,
                        "Enter new title:",
                        model.getTitle(),
                        newTitle -> {
                            model.setTitle(newTitle);
                            tabbedPane.setTitleAt(tabbedPane.getSelectedIndex(), newTitle);
                            presenter.save(model);
                        }
                );
            });
        });
    }

    private void closeTab(int index) {
        //try to save content before closing
        var component = (JPanel) tabbedPane.getComponentAt(index);
        saveContent(component);

        // close tab
        tabbedPane.removeTabAt(index);

        // if no tabs left, create a new empty tab
        if (tabbedPane.getTabCount() == 0) {
            createNewTab(new NoteModel(ContentModel.DEFAULT_DOCUMENT_TITLE));
        }
    }

    private Optional<ContentModel> getCurrentContentModel() {
        var component = (JPanel) tabbedPane.getSelectedComponent();
        if (component != null) {
            var model = component.getClientProperty("model");
            if (model instanceof ContentModel contentModel) {
                return Optional.of(contentModel);
            }
        }
        return Optional.empty();
    }

    public void createNewTab(ContentModel contentModel) {
        if (contentModel instanceof NoteModel noteModel) {
            // create text area
            RSyntaxTextArea textArea = new RSyntaxTextArea();
            textArea.setCodeFoldingEnabled(false);
            textArea.setTabsEmulated(true);
            textArea.setTabSize(4);
            textArea.setAntiAliasingEnabled(true);
            textArea.setMarkOccurrences(true);
            textArea.setCloseCurlyBraces(true);
            textArea.setAnimateBracketMatching(true);
            textArea.setAutoIndentEnabled(true);
            textArea.setCurrentLineHighlightColor(new Color(255, 251, 226));

            // Wrap into scroll pane with line numbers
            RTextScrollPane sp = new RTextScrollPane(textArea);
            sp.setFoldIndicatorEnabled(true);
            Gutter gutter = sp.getGutter();
            sp.setFoldIndicatorEnabled(false);
            gutter.setBookmarkingEnabled(true);
            gutter.setBookmarkIcon(icon("/icons/bookmark.svg", 14, 14));
            gutter.setBackground(UIManager.getColor("Panel.background"));
            gutter.setBorderColor(UIManager.getColor("Separator.foreground"));
            gutter.setLineNumbersEnabled(false);

            JPanel panel = new JPanel(new BorderLayout());
            panel.add(sp, BorderLayout.CENTER);

            // store client data
            panel.putClientProperty("model", contentModel);
            panel.putClientProperty("textArea", textArea);

            // create a new tab
            tabbedPane.addTab(noteModel.getTitle(), icon("/icons/doc.svg", 16, 16), panel);
            tabbedPane.setSelectedIndex(tabbedPane.getTabCount() - 1);

            // set data
            textArea.setText(noteModel.getContent());

            // set focus
            SwingUtilities.invokeLater(textArea::requestFocusInWindow);
        }
    }

    public void createEmptyTab() {
        var note = new NoteModel();
        note.setTitle(ContentModel.DEFAULT_DOCUMENT_TITLE);
        note.setGroupId(null);
        note.setCreatedAt(Instant.now());
        createNewTab(note);
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
        toolBar.add(createButton("Save", "/icons/save.svg", () -> {
            var component = (JPanel) tabbedPane.getSelectedComponent();
            saveContent(component);
        }));
        toolBar.add(createButton("Export", "/icons/download.svg", () -> {
            System.out.println("Export action");
        }));
        toolBar.addSeparator();

        return toolBar;
    }

    private void openExistingNote() {
        NotesManagerWindow w = new NotesManagerWindow(this);
        w.showWindow();
        if (w.getResult() != null) {
            var firstPanel = (JPanel) tabbedPane.getComponentAt(0);
            var remove = tabbedPane.getTabCount() == 1 && !panelContentChanged(firstPanel) && panelIsNew(firstPanel);
            createNewTab(w.getResult());
            if (remove) {
                tabbedPane.removeTabAt(0);
            }
        }
    }

    private ContentModel saveContent(JPanel panel) {
        var model = panel.getClientProperty("model");
        if (model instanceof NoteModel noteModel) {
            var textArea = (RSyntaxTextArea) panel.getClientProperty("textArea");
            var newHash = NoteModel.calculateContentHash(textArea.getText());
            if (newHash != noteModel.getContentHash()) {
                noteModel.setContent(textArea.getText());
                ContentModel saved = presenter.save(noteModel);
                System.out.println("Saved note: " + noteModel.getTitle() + " with new hash: " + newHash);
                return saved;
            } else {
                System.out.println("No changes detected for note: " + noteModel.getTitle() + " with hash: " + noteModel.getContentHash());
                return noteModel;
            }
        }
        return null;
    }

    /**
     * Check if the content in the panel has changed compared to the model's content hash.
     *
     * @param panel the JPanel containing the note
     * @return true if the content has changed, false otherwise
     */
    private boolean panelContentChanged(JPanel panel) {
        var model = panel.getClientProperty("model");
        if (model instanceof NoteModel noteModel) {
            var textArea = (RSyntaxTextArea) panel.getClientProperty("textArea");
            var newHash = NoteModel.calculateContentHash(textArea.getText());
            return newHash != noteModel.getContentHash();
        }
        return false;
    }

    private boolean panelIsNew(JPanel panel) {
        var model = panel.getClientProperty("model");
        if (model instanceof NoteModel noteModel) {
            return noteModel.isNew();
        }
        return false;
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