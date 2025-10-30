package xyz.toway.notes.ui.view;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.formdev.flatlaf.icons.FlatSearchWithHistoryIcon;
import com.formdev.flatlaf.icons.FlatTabbedPaneCloseIcon;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rtextarea.*;
import xyz.toway.notes.ui.presenter.MainPresenter;
import xyz.toway.notes.ui.view.components.ListItem;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;
import java.util.function.BiConsumer;

public class MainForm implements GeneralView<MainPresenter> {

    private final MainPresenter presenter;

    private JPanel mainPanel;
    private JPanel leftPanel;

    public MainForm(MainPresenter presenter) {
        this.presenter = presenter;
        presenter.setView(this);
        createUIComponents();
    }

    public JComponent getRootComponent() {
        return mainPanel;
    }

    private void createUIComponents() {
        mainPanel = new JPanel(new BorderLayout(8, 8));
        //leftPanel = new GroupsMenuPanel();
        //leftPanel.setPreferredSize(new Dimension(200, 0));
        //mainPanel.add(leftPanel, BorderLayout.WEST);

        // Left sidebar
        JPanel sidebar = new JPanel(new BorderLayout());
        sidebar.setBackground(Color.PINK);
        sidebar.setPreferredSize(new Dimension(200, 0));
        sidebar.setMinimumSize(new Dimension(200, 0));

        //----------
        /*List<ListItem> items = List.of(
                new ListItem("Document", "Last modified today", UIManager.getIcon("FileView.fileIcon")),
                new ListItem("Picture", "JPEG image, 1.2MB", UIManager.getIcon("FileView.directoryIcon")),
                new ListItem("Music", "MP3, 3:45 min", UIManager.getIcon("FileView.computerIcon"))
        );

        JList<ListItem> list = new JList<>(items.toArray(new ListItem[0]));
        list.setCellRenderer(new CustomListCellRenderer());
        list.setFixedCellHeight(44); // More space for two lines*/

        sidebar.add(new JScrollPane(getSidebarMenuList()));
        //----------

        // Main content
        JPanel content = new JPanel(new BorderLayout());
        //content.add(new JLabel("Main content area", SwingConstants.CENTER), BorderLayout.CENTER);
        //content.setMinimumSize(new Dimension(400, 0));
        content.add(getContentPane(), BorderLayout.CENTER);

        // Split pane
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, sidebar, content);
        split.setDividerLocation(200);           // initial
        split.setResizeWeight(0);                // give extra space to right side when resizing
        split.setContinuousLayout(true);         // live resize

        //UIManager.put("TabbedPane.closeForeground", UIManager.getColor("TabbedPane.background"));

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Super document", new FlatSVGIcon("icons/doc.svg", 16, 16), getContentPane());
        tabbedPane.addTab("My document", new FlatSVGIcon("icons/doc.svg", 16, 16), getContentPane());
        tabbedPane.addTab("huieta.txt", new FlatSVGIcon("icons/doc.svg", 16, 16), getContentPane());
        mainPanel.add(tabbedPane, BorderLayout.CENTER);

        UIManager.put("TabbedPane.closeHoverForeground", UIManager.getColor("TabbedPane.background"));
        UIManager.put("TabbedPane.closeCrossPlainSize", 6);
        UIManager.put("TabbedPane.closeArc", 999);
        UIManager.put("TabbedPane.closeCrossFilledSize", 5.5f);
        UIManager.put("TabbedPane.closeIcon", new FlatTabbedPaneCloseIcon());
        tabbedPane.updateUI();

        tabbedPane.putClientProperty(FlatClientProperties.TABBED_PANE_TAB_CLOSABLE, true);
        tabbedPane.putClientProperty(FlatClientProperties.TABBED_PANE_TAB_CLOSE_TOOLTIPTEXT, "Close");
        //tabbedPane.putClientProperty( FlatClientProperties.TABBED_PANE_HIDE_TAB_AREA_WITH_ONE_TAB, true);


        tabbedPane.putClientProperty(
                FlatClientProperties.TABBED_PANE_TAB_CLOSE_CALLBACK,
                (BiConsumer<JTabbedPane, Integer>) (tp, index) -> {
                    tp.removeTabAt(index);
                    System.out.println("Closed tab at index: " + index);
                }
        );

    }

    private static void installSearchShortcuts(RSyntaxTextArea ta) {

        // Ctrl+F
        ta.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_F, KeyEvent.CTRL_DOWN_MASK), "findPrompt");
        ta.getActionMap().put("findPrompt", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String q = JOptionPane.showInputDialog(ta, "Find:", "Find", JOptionPane.PLAIN_MESSAGE);
                if (q == null || q.isEmpty()) return;

                SearchContext ctx = new SearchContext();
                ctx.setSearchFor(q);
                ctx.setMatchCase(false);
                ctx.setWholeWord(false);
                ctx.setRegularExpression(false);
                ctx.setSearchForward(true);

                SearchResult r = SearchEngine.find(ta, ctx);
                if (!r.wasFound()) UIManager.getLookAndFeel().provideErrorFeedback(ta);
            }
        });

        // F3
        ta.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_F3, 0), "findNext");
        ta.getActionMap().put("findNext", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SearchContext ctx = new SearchContext();
                String sel = ta.getSelectedText();
                if (sel != null && !sel.isEmpty()) ctx.setSearchFor(sel);
                ctx.setSearchForward(true);

                SearchResult r = SearchEngine.find(ta, ctx);
                if (!r.wasFound()) UIManager.getLookAndFeel().provideErrorFeedback(ta);
            }
        });

        // Shift+F3
        ta.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_F3, KeyEvent.SHIFT_DOWN_MASK), "findPrev");
        ta.getActionMap().put("findPrev", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SearchContext ctx = new SearchContext();
                String sel = ta.getSelectedText();
                if (sel != null && !sel.isEmpty()) ctx.setSearchFor(sel);
                ctx.setSearchForward(false);

                SearchResult r = SearchEngine.find(ta, ctx);
                if (!r.wasFound()) UIManager.getLookAndFeel().provideErrorFeedback(ta);
            }
        });

        // Ctrl+H
        ta.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_H, KeyEvent.CTRL_DOWN_MASK), "replacePrompt");
        ta.getActionMap().put("replacePrompt", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JPanel panel = new JPanel(new java.awt.GridLayout(0, 2, 6, 6));
                JTextField find = new JTextField();
                JTextField repl = new JTextField();
                panel.add(new JLabel("Find:"));
                panel.add(find);
                panel.add(new JLabel("Replace with:"));
                panel.add(repl);
                int ok = JOptionPane.showConfirmDialog(ta, panel, "Replace", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
                if (ok != JOptionPane.OK_OPTION) return;

                SearchContext ctx = new SearchContext();
                ctx.setSearchFor(find.getText());
                ctx.setReplaceWith(repl.getText());
                ctx.setSearchForward(true);

                SearchResult r = SearchEngine.replace(ta, ctx);
                if (!r.wasFound()) UIManager.getLookAndFeel().provideErrorFeedback(ta);
            }
        });
    }

    public Component getContentPane() {
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

        // Choose a language syntax
        textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_NONE);

        textArea.setText("""
                // Sample Java code
                public class HelloWorld {
                    public static void main(String[] args) {
                        System.out.println("Hello, World!");
                    }
                }
                """.stripIndent());

        // Wrap into scroll pane with line numbers
        RTextScrollPane sp = new RTextScrollPane(textArea);
        sp.setFoldIndicatorEnabled(true);

        Gutter gutter = sp.getGutter();
        sp.setFoldIndicatorEnabled(false);
        gutter.setBookmarkingEnabled(true);
        gutter.setBookmarkIcon(new FlatSVGIcon("icons/bookmark.svg", 14, 14));

        gutter.setBackground(UIManager.getColor("Panel.background"));
        gutter.setBorderColor(UIManager.getColor("Separator.foreground"));

        gutter.setLineNumbersEnabled(false);

        try {
            gutter.toggleBookmark(3);
            gutter.addIconRowListener(new IconRowListener() {
                @Override
                public void bookmarkAdded(IconRowEvent e) {
                    System.out.println(e);
                }

                @Override
                public void bookmarkRemoved(IconRowEvent e) {
                    System.out.println(e);
                }
            });
        } catch (Exception ex) {
        }

        installSearchShortcuts(textArea);

        return sp;
    }

    public JMenuBar getMenuBar() {
        var menuBar = new JMenuBar();

        JMenu fileMenu = new JMenu("File");
        fileMenu.add(new JMenuItem("Open"));
        fileMenu.add(new JMenuItem("Exit"));
        menuBar.add(fileMenu);

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
        toolBar.add(createButton("New", "icons/new.svg", () -> {
            System.out.println("New action");
        }));
        toolBar.add(createButton("Open", "icons/open.svg", () -> {
            System.out.println("Open action");
        }));
        toolBar.add(createButton("Save", "icons/save.svg", () -> {
            System.out.println("Save action");
        }));
        toolBar.add(createButton("Export", "icons/download.svg", () -> {
            System.out.println("Export action");
        }));
        toolBar.addSeparator();
        //toolBar.add(createButton("Copy", "icons/copy.svg", () -> {
        //    System.out.println("Copy action");
        //}));
        //toolBar.add(createButton("Paste", "icons/paste.svg", () -> {
        //    System.out.println("Paste action");
        //}));

        return toolBar;
    }

    // create button with action
    private JButton createButton(String tooltip, String iconPath, Runnable action) {
        JButton button = new JButton();
        button.setToolTipText(tooltip);
        button.setIcon(new FlatSVGIcon(iconPath));
        button.addActionListener(e -> action.run());
        return button;
    }

    private JTextField getSearchField() {
        JButton searchHistoryButton = new JButton(new FlatSearchWithHistoryIcon(true));
        searchHistoryButton.setToolTipText("Search History");
        searchHistoryButton.addActionListener(e -> {
            JPopupMenu popupMenu = new JPopupMenu();
            popupMenu.add("(empty)");
            popupMenu.show(searchHistoryButton, 0, searchHistoryButton.getHeight());
        });

        JTextField searchField = new JTextField();
        searchField.putClientProperty(FlatClientProperties.TEXT_FIELD_SHOW_CLEAR_BUTTON, true);
        searchField.putClientProperty(FlatClientProperties.TEXT_FIELD_LEADING_COMPONENT, searchHistoryButton);

        Dimension pref = searchField.getPreferredSize();
        pref.width = 250;
        searchField.setPreferredSize(pref);
        searchField.setMaximumSize(pref);

        return searchField;
    }

    private JList<ListItem> getSidebarMenuList() {
        java.util.List<ListItem> items = List.of(
                new ListItem("Favorites", UIManager.getIcon("FileView.fileIcon")),
                new ListItem("Recycle Bin", UIManager.getIcon("FileView.directoryIcon")),
                new ListItem("Music", UIManager.getIcon("FileView.computerIcon"))
        );

        JList<ListItem> list = new JList<>(items.toArray(new ListItem[0]));
        list.setFixedCellHeight(28);
        list.setCellRenderer(new MainForm.IconTextCellRenderer());
        return list;
    }

    @Override
    public MainPresenter getPresenter() {
        return presenter;
    }

    static class IconTextCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(
                JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {

            JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

            if (value instanceof ListItem item) {
                label.setText(item.getTitle());
                label.setIcon(item.getIcon());
            }
            return label;
        }
    }
}
