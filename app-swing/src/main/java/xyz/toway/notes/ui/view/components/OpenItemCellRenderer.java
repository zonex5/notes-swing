package xyz.toway.notes.ui.view.components;

import xyz.toway.notes.domain.model.ContentModel;

import javax.swing.*;
import java.awt.*;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

import static xyz.toway.notes.ui.Main.icon;

public class OpenItemCellRenderer extends JPanel implements ListCellRenderer<ContentModel> {
    private final DefaultListCellRenderer base = new DefaultListCellRenderer();
    private final JLabel iconLabel = new JLabel();
    private final JLabel titleLabel = new JLabel();
    private final JLabel subtitleLabel = new JLabel();
    private final JLabel dateLabel = new JLabel();

    private final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("MM-dd-yyyy HH:mm").withZone(ZoneId.systemDefault());

    public OpenItemCellRenderer() {
        setLayout(new BorderLayout(5, 5));
        setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        setOpaque(true);

        // text area
        JPanel text = new JPanel();
        text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));
        text.setOpaque(false);

        // top row: title left, date right
        JPanel topRow = new JPanel(new BorderLayout());
        topRow.setOpaque(false);
        topRow.add(titleLabel, BorderLayout.WEST);
        topRow.add(dateLabel, BorderLayout.EAST);

        // Ensure left alignment inside BoxLayout
        topRow.setAlignmentX(LEFT_ALIGNMENT);
        subtitleLabel.setAlignmentX(LEFT_ALIGNMENT);

        Font baseFont = UIManager.getFont("Label.font");
        titleLabel.setFont(baseFont.deriveFont(Font.BOLD, baseFont.getSize()));
        subtitleLabel.setFont(baseFont.deriveFont(Font.PLAIN, baseFont.getSize()));
        dateLabel.setFont(baseFont.deriveFont(Font.PLAIN, baseFont.getSize() - 2f));

        add(iconLabel, BorderLayout.WEST);
        add(text, BorderLayout.CENTER);

        text.add(topRow);
        text.add(subtitleLabel);
    }

    @Override
    public Component getListCellRendererComponent(JList<? extends ContentModel> list, ContentModel value, int index, boolean isSelected, boolean cellHasFocus) {
        var title = Objects.requireNonNullElse(value.getTitle(), "<Untitled>");

        JLabel lafLabel = (JLabel) base.getListCellRendererComponent(list, title, index, isSelected, cellHasFocus);
        setBackground(lafLabel.getBackground());
        titleLabel.setForeground(lafLabel.getForeground());

        Color secondary = UIManager.getColor("Label.disabledForeground");
        subtitleLabel.setForeground(secondary);

        iconLabel.setIcon(icon("/icons/ui/note.svg", 18, 20));
        titleLabel.setText(title);
        subtitleLabel.setText(value.getContentPreview());

        var date = value.getCreatedAt() != null ? DATE_FORMAT.format(value.getCreatedAt()) : "";
        dateLabel.setText(date);

        // store model in client property
        this.putClientProperty("model", value);

        return this;
    }
}
