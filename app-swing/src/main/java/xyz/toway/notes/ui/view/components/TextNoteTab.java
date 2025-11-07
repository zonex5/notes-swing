package xyz.toway.notes.ui.view.components;

import lombok.NonNull;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rtextarea.Gutter;
import org.fife.ui.rtextarea.RTextScrollPane;
import xyz.toway.notes.domain.model.NoteModel;
import xyz.toway.notes.ui.EventBus;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import static xyz.toway.notes.ui.Main.icon;

public class TextNoteTab extends JPanel {

    private final RSyntaxTextArea textArea;
    private final NoteModel noteModel;

    public TextNoteTab(@NonNull NoteModel noteModel) {
        super(new BorderLayout());
        this.noteModel = noteModel;

        // create text area
        textArea = new RSyntaxTextArea();
        textArea.setCodeFoldingEnabled(false);
        textArea.setTabsEmulated(true);
        textArea.setTabSize(4);
        textArea.setAntiAliasingEnabled(true);
        textArea.setMarkOccurrences(true);
        textArea.setCloseCurlyBraces(true);
        textArea.setAnimateBracketMatching(true);
        textArea.setAutoIndentEnabled(true);
        textArea.setCurrentLineHighlightColor(new Color(255, 251, 226));

        RTextScrollPane scrollPane = new RTextScrollPane(textArea);
        scrollPane.setFoldIndicatorEnabled(true);
        Gutter gutter = scrollPane.getGutter();
        scrollPane.setFoldIndicatorEnabled(false);
        gutter.setBookmarkingEnabled(true);
        gutter.setBookmarkIcon(icon("/icons/bookmark.svg", 14, 14));
        gutter.setBackground(UIManager.getColor("Panel.background"));
        gutter.setBorderColor(UIManager.getColor("Separator.foreground"));
        gutter.setLineNumbersEnabled(false);

        add(scrollPane, BorderLayout.CENTER);

        // set initial text
        textArea.setText(noteModel.getContent());

        // register hotkeys
        KeyStroke keyStrokeS = KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_DOWN_MASK);  // Ctrl+S
        textArea.getInputMap(JComponent.WHEN_FOCUSED).put(keyStrokeS, "saveFile");
        textArea.getActionMap().put("saveFile", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Notify parent component to save the note
                EventBus.emitEvent("saveNote", noteModel.getId());
            }
        });

        KeyStroke ctrlN = KeyStroke.getKeyStroke(KeyEvent.VK_N, KeyEvent.CTRL_DOWN_MASK);
        InputMap inputMap = getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        inputMap.put(ctrlN, "newTab");
        getActionMap().put("newTab", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                EventBus.emitEvent("newTab", null);
            }
        });
    }

    public String getText() {
        return textArea.getText();
    }

    public void setText(String text) {
        textArea.setText(text);
    }

    public void setFocus() {
        SwingUtilities.invokeLater(textArea::requestFocusInWindow);
    }

    public NoteModel getModel() {
        return noteModel;
    }

    public boolean isChanged() {
        return NoteModel.calculateContentHash(getText()) != noteModel.getContentHash();
    }

    public boolean isEmpty() {
        return getText().isBlank();
    }

    public boolean isNew() {
        return noteModel.isNew();
    }

    public boolean canBeRemoved() {
        return !isChanged() && isNew();
    }
}
