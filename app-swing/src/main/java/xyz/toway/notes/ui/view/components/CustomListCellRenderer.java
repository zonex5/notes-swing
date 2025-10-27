package xyz.toway.notes.ui.view.components;

import javax.swing.*;
import java.awt.*;

public class CustomListCellRenderer extends JPanel implements ListCellRenderer<ListItem> {
    private final DefaultListCellRenderer base = new DefaultListCellRenderer(); // delegates LAF colors
    private final JLabel iconLabel = new JLabel();
    private final JLabel titleLabel = new JLabel();
    private final JLabel subtitleLabel = new JLabel();

    public CustomListCellRenderer() {
        setLayout(new BorderLayout(10, 0));
        setBorder(BorderFactory.createEmptyBorder(6, 8, 6, 8));
        setOpaque(true); // paint full-cell background

        JPanel text = new JPanel();
        text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));
        text.setOpaque(false);

        // derive smaller font for subtitle; do NOT set fixed colors
        Font baseFont = UIManager.getFont("Label.font");
        subtitleLabel.setFont(baseFont.deriveFont(Font.PLAIN, baseFont.getSize() - 2));

        add(iconLabel, BorderLayout.WEST);
        add(text, BorderLayout.CENTER);
        text.add(titleLabel);
        text.add(subtitleLabel);
    }

    @Override
    public Component getListCellRendererComponent(JList<? extends ListItem> list, ListItem value, int index, boolean isSelected, boolean cellHasFocus) {

        // Let DefaultListCellRenderer decide correct colors for current LAF and state
        JLabel lafLabel = (JLabel) base.getListCellRendererComponent(list, value.getTitle(), index, isSelected, cellHasFocus);

        // Apply LAF-driven background/foreground to the whole cell
        setBackground(lafLabel.getBackground());
        titleLabel.setForeground(lafLabel.getForeground());

        // Secondary text uses LAF resource, not a hard color
        Color secondary = UIManager.getColor("Label.disabledForeground");
        subtitleLabel.setForeground(secondary);

        // Content
        iconLabel.setIcon(value.getIcon());
        titleLabel.setText(value.getTitle());
        subtitleLabel.setText(value.getSubtitle());

        return this;
    }
}
