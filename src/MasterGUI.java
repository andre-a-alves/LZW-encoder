import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;

public class MasterGUI extends JFrame {

    public MasterGUI() {
        super("LZW Encoder/Decoder");
        makeMasterPane();
        setJMenuBar(makeMenuBar());
        setVisible(true);
    }

    private void makeMasterPane() {
        setPreferredSize(new Dimension(360,240));
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        JPanel masterContentPane = new JPanel();
        masterContentPane.setBorder(new EmptyBorder(10,10,10,10));
        masterContentPane.setLayout(new GridLayout(4,1));
        makeAndSetButtons(masterContentPane);
        setContentPane(masterContentPane);
        pack();
        setLocationByPlatform(true);
    }

    private void makeAndSetButtons(JPanel jpanel) {
        final JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
        JButton stringViewer = new JButton("LZW on Text File Strings (Visualize the Process)");
        stringViewer.addActionListener(e -> new StringGUI());

        JButton compress = new JButton("LZW Compress a File");
        compress.addActionListener(e -> {
            int openReturnValue = fileChooser.showOpenDialog(this);
            if (openReturnValue == JFileChooser.APPROVE_OPTION) {
                File openFile = fileChooser.getSelectedFile();
                int saveReturnValue = fileChooser.showSaveDialog(this);
                if (saveReturnValue == JFileChooser.APPROVE_OPTION) {
                    File saveFile = fileChooser.getSelectedFile();
                    try {
                        LZW.encode(openFile, saveFile);
                    } catch (IOException exception) {
                        JOptionPane.showMessageDialog(this, CommonStrings.NO_OPEN.getString());
                    } catch (IllegalArgumentException exception) {
                        JOptionPane.showMessageDialog(this, CommonStrings.TOO_LARGE.getString());
                    }
                }

            }
        });

        JButton decompress = new JButton("Decompress a LZW-Compressed File");
        decompress.addActionListener(e -> {
            int openReturnValue = fileChooser.showOpenDialog(this);
            if (openReturnValue == JFileChooser.APPROVE_OPTION) {
                File openFile = fileChooser.getSelectedFile();
                int saveReturnValue = fileChooser.showSaveDialog(this);
                if (saveReturnValue == JFileChooser.APPROVE_OPTION) {
                    File saveFile = fileChooser.getSelectedFile();
                    try {
                        LZW.decode(openFile, saveFile);
                    } catch (IOException exception) {
                        JOptionPane.showMessageDialog(this, CommonStrings.NO_OPEN.getString());
                    } catch (IllegalArgumentException exception) {
                        JOptionPane.showMessageDialog(this, CommonStrings.WRONG_FORMAT.getString());
                    }
                }
            }
        });

        jpanel.add(stringViewer);
        jpanel.add(new JSeparator());
        jpanel.add(compress);
        jpanel.add(decompress);
    }

    private JMenuBar makeMenuBar() {
        final int SHORTCUT_MASK =
                Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx();

        JMenuBar menuBar = new JMenuBar();

        JMenu fileMenu= new JMenu("File");
        JMenu helpMenu = new JMenu("Help");

        JMenuItem quitItem = new JMenuItem("Quit");
        quitItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, SHORTCUT_MASK));
        quitItem.addActionListener(e -> System.exit(0));

        JMenuItem about = new JMenuItem("About");
        about.addActionListener(e -> JOptionPane.showMessageDialog(this, CommonStrings.ABOUT.getString()));

        fileMenu.add(quitItem);
        helpMenu.add(about);

        menuBar.add(fileMenu);
        menuBar.add(helpMenu);

        return menuBar;
    }

    public static void main(String[] args) {
        new MasterGUI();
    }
}
