import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.KeyStroke;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;

public class StringGUI extends JFrame {
    private JPanel filesDisplayPanel;
    private FilePanel plainFilePanel, encodedFilePanel;

    public StringGUI(){
        super("LZW Encoder");
        setPreferredSize(new Dimension(1280, 720));

        setJMenuBar(makeMenuBar());
        makeFileDisplayPanel();
        makeMasterFrame();
        setVisible(true);
    }

    private void makeFileDisplayPanel() {
        filesDisplayPanel = new JPanel();
        plainFilePanel = new FilePanel("Plain File");
        encodedFilePanel = new FilePanel("Encoded File");
        filesDisplayPanel.setLayout(new GridLayout(1,2));
        filesDisplayPanel.add(plainFilePanel);
        filesDisplayPanel.add(encodedFilePanel);
    }

    private void makeMasterFrame(){
        JPanel masterContentPane = new JPanel();
        masterContentPane.setBorder(new EmptyBorder(10, 10, 10, 10));
        masterContentPane.setLayout(new BorderLayout(10, 10));

        masterContentPane.add(filesDisplayPanel,BorderLayout.CENTER);

        setContentPane(masterContentPane);
        pack();
        setLocationByPlatform(true);
    }

    private JMenuBar makeMenuBar() {
        final JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File(System.getProperty("user.dir")));

        final int SHORTCUT_MASK =
                Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx();

        JMenuBar menuBar = new JMenuBar();

        JMenu fileMenu= new JMenu("File");
        JMenu processMenu = new JMenu("Process");
        JMenu helpMenu = new JMenu("Help");

        JMenuItem loadPlain = new JMenuItem("Load plain document");
        loadPlain.addActionListener(e -> {
            int returnValue = fileChooser.showOpenDialog(plainFilePanel);
            if (returnValue == JFileChooser.APPROVE_OPTION)
                plainFilePanel.loadFile(fileChooser.getSelectedFile());
        });
        JMenuItem loadEncoded = new JMenuItem("Load encoded document");
        loadEncoded.addActionListener(e -> {
            int returnValue = fileChooser.showOpenDialog(encodedFilePanel);
            if (returnValue == JFileChooser.APPROVE_OPTION)
                encodedFilePanel.loadFile(fileChooser.getSelectedFile());
        });
        JMenuItem quitItem = new JMenuItem("Quit");
        quitItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, SHORTCUT_MASK));
        quitItem.addActionListener(e -> System.exit(0));

        JMenuItem stringEncodeFile = new JMenuItem("LZW-Encode file string");
        stringEncodeFile.addActionListener(e -> {
            int returnValue = fileChooser.showSaveDialog(plainFilePanel);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                try {
                    StringLZW.encode(plainFilePanel.getFile(), fileChooser.getSelectedFile());
                    encodedFilePanel.loadFile(fileChooser.getSelectedFile());
                    plainFilePanel.clearFile();
                } catch (IOException exception) {
                    JOptionPane.showMessageDialog(this, "Unable to load file. Please check the path and " +
                            "try again.");
                }
            }
        });
        JMenuItem stringDecodeFile = new JMenuItem("Decode LZW-encoded file string");
        stringDecodeFile.addActionListener(e-> {
            int returnValue = fileChooser.showSaveDialog(encodedFilePanel);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                try {
                    StringLZW.decode(encodedFilePanel.getFile(), fileChooser.getSelectedFile());
                    plainFilePanel.loadFile(fileChooser.getSelectedFile());
                    encodedFilePanel.clearFile();
                } catch (IOException exception) {
                    JOptionPane.showMessageDialog(this, "Unable to load file. Please check the path and " +
                            "try again.");
                }
            }
        });

        JMenuItem about = new JMenuItem("About");
        about.addActionListener(e -> JOptionPane.showMessageDialog(this, CommonStrings.STRING_ABOUT.getString()));
        fileMenu.add(loadPlain);
        fileMenu.add(loadEncoded);
        fileMenu.add(new JSeparator());
        fileMenu.add(quitItem);

        processMenu.add(stringEncodeFile);
        processMenu.add(stringDecodeFile);

        helpMenu.add(about);

        menuBar.add(fileMenu);
        menuBar.add(processMenu);
        menuBar.add(helpMenu);

        return menuBar;
    }
}
