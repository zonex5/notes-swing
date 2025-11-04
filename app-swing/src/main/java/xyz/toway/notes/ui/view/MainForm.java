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

import javax.swing.*;
import java.awt.*;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

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
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setPreferredSize(new Dimension(1100, 700));
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
        setIconImages(List.of(icon("/icons/icon.svg", 16, 16).getImage()));

        // create a default tab
        if (tabbedPane.getTabCount() == 0) {
            requestNewTabCreation(new NoteModel("New Note"));
        }
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
        tabbedPane.putClientProperty(FlatClientProperties.TABBED_PANE_TAB_CLOSE_CALLBACK, (BiConsumer<JTabbedPane, Integer>) (tp, index) -> {
            //try to save content before closing
            var component = (JPanel) tp.getComponentAt(index);
            saveContent(component);

            // close tab
            tp.removeTabAt(index);

            // if no tabs left, create a new empty tab
            if (tp.getTabCount() == 0) {
                requestNewTabCreation(new NoteModel("New Note"));
            }
        });

        statusBar = new StatusBar();
        mainPanel.add(statusBar, BorderLayout.SOUTH);
    }

    public void requestNewTabCreation(ContentModel contentModel) {
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

    public JMenuBar createMenuBar() {
        var menuBar = new JMenuBar();

        JMenu fileMenu = new JMenu("File");
        fileMenu.add(new JMenuItem("Open"));
        fileMenu.add(new JMenuItem("Exit"));
        menuBar.add(fileMenu);

        JMenu viewMenu = new JMenu("View");
        JCheckBoxMenuItem statusBarItem = new JCheckBoxMenuItem("Show Status Bar");
        statusBarItem.addActionListener(e -> {
            presenter.menuItemStatusBar(statusBarItem.isSelected());
            showStatusBar(statusBarItem.isSelected());
        });
        viewMenu.add(statusBarItem);
        menuBar.add(viewMenu);
        menuItems.put("statusbar", statusBarItem);

        JMenu helpMenu = new JMenu("Help");
        helpMenu.add(new JMenuItem("About"));
        menuBar.add(helpMenu);

        menuBar.add(Box.createHorizontalGlue());
        menuBar.add(getToolbar());

        return menuBar;
    }

    public JToolBar getToolbar() {

        JToolBar toolBar = new JToolBar();
        toolBar.setMargin(new Insets(3, 3, 3, 3));

        toolBar.addSeparator();
        toolBar.add(createButton("New", "/icons/new.svg", this::createEmptyTab));
        toolBar.add(createButton("Open", "/icons/open.svg", () -> {
            System.out.println("Open action");
        }));
        toolBar.add(createButton("Save", "/icons/save.svg", () -> {
            var component = (JPanel) tabbedPane.getSelectedComponent();
            saveContent(component);
        }));
        toolBar.add(createButton("Export", "/icons/download.svg", () -> {
            System.out.println("Export action");

            NotesManagerWindow w = new NotesManagerWindow(this);
            w.showWindow();
            if (w.getResult() != null) {
                requestNewTabCreation(w.getResult());
            }
        }));
        toolBar.addSeparator();

        return toolBar;
    }

    private void saveContent(JPanel panel) {
        var model = panel.getClientProperty("model");
        if (model instanceof NoteModel noteModel) {
            var textArea = (RSyntaxTextArea) panel.getClientProperty("textArea");
            var newHash = NoteModel.calculateContentHash(textArea.getText());
            if (newHash != noteModel.getContentHash()) {
                noteModel.setContent(textArea.getText());
                presenter.save(noteModel);
                System.out.println("Saved note: " + noteModel.getTitle() + " with new hash: " + newHash);
            } else {
                System.out.println("No changes detected for note: " + noteModel.getTitle() + " with hash: " + noteModel.getContentHash());
            }
        }
    }

    private void createEmptyTab() {
        var note = new NoteModel();
        note.setTitle("New Note");
        note.setGroupId(null);
        note.setCreatedAt(Instant.now());
        requestNewTabCreation(note);
    }

    private void showStatusBar(boolean visible) {
        statusBar.setVisible(visible);
        mainPanel.revalidate();
        mainPanel.repaint();
    }

    // create button with action
    private JButton createButton(String tooltip, String iconPath, Runnable action) {
        JButton button = new JButton();
        button.setToolTipText(tooltip);
        button.setIcon(icon(iconPath));
        button.addActionListener(e -> action.run());
        return button;
    }

    @Override
    public MainPresenter getPresenter() {
        return presenter;
    }

    @Override
    public void applyUISettings(StoredSettings settings) {
        showStatusBar(settings.statusBarVisible());
        menuItems.get("statusbar").setSelected(settings.statusBarVisible());
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