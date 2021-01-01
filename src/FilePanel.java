import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class FilePanel extends JPanel {
    private final JTextArea textArea;
    private final JLabel sizeLabel;
    private File file;

    public FilePanel(String panelTitle) {
        super();
        textArea = new JTextArea();
        sizeLabel = new JLabel("File Size: ");

        textArea.setLineWrap(false);
        textArea.setEditable(false);
        JScrollPane scroll = new JScrollPane (textArea);

        this.setBorder(new EmptyBorder(10, 10, 10, 10));
        this.setLayout(new BorderLayout());
        this.add(new JLabel(panelTitle), BorderLayout.NORTH);
        this.add(scroll, BorderLayout.CENTER);
        this.add(sizeLabel, BorderLayout.SOUTH);
    }

    public void loadFile(File newFile) {
        file = newFile;
        try {
            textArea.setText(Files.readString(file.toPath()));
            sizeLabel.setText(String.format("%-11s%20d%-10s%12s%s", "File Size:",
                    file.length() / 1024
                    , " kb", "File Name: ", file.getName()));
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, CommonStrings.NO_OPEN.getString());
        }
    }

    public void clearFile() {
        textArea.setText("");
        sizeLabel.setText("File Size:");
    }

    public File getFile() {
        return file;
    }
}
