import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class PaintGui extends JFrame {
    private JPanel canvasPanel;
    private Canvas canvas;

    public PaintGui() {
        super("GoPaint");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setPreferredSize(new Dimension(1500, 1000));
        pack();
        setLocationRelativeTo(null);

        canvas = new Canvas(1500, 950);
        canvasPanel = new JPanel(null); // Change layout manager to null
        addGuiComponents();
        setLayout(new BorderLayout());
        add(canvasPanel, BorderLayout.CENTER);

        // Set focusable for the canvas to enable keyboard events
        canvas.setFocusable(true);
    }

    private void addGuiComponents() {
        // Canvas
        canvasPanel.add(canvas);
        canvas.setBounds(0, 50, 1500, 950);

        // Color Chooser
        JButton chooseColorButton = new JButton("Color Palette");
        chooseColorButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Color c = JColorChooser.showDialog(null, "Select a color", Color.BLACK);
                chooseColorButton.setBackground(c);
                canvas.setColor(c);
            }
        });
        canvasPanel.add(chooseColorButton);
        chooseColorButton.setBounds(350, 10, 150, 30);

        // Reset Button
        JButton resetButton = new JButton("Reset");
        resetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                canvas.resetCanvas();
            }
        });
        canvasPanel.add(resetButton);
        resetButton.setBounds(555, 10, 150, 30);

        // Save Button
        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveDrawingAsPNG();
            }
        });
        canvasPanel.add(saveButton);
        saveButton.setBounds(755, 10, 150, 30);

        // Undo Button
        JButton undoButton = new JButton("Undo");
        undoButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                canvas.undo();
            }
        });
        canvasPanel.add(undoButton);
        undoButton.setBounds(955, 10, 150, 30);

        // Assign a shortcut (Ctrl + Z) for the Undo button
        undoButton.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
                KeyStroke.getKeyStroke(KeyEvent.VK_Z, KeyEvent.CTRL_DOWN_MASK),
                "undoAction"
        );

        undoButton.getActionMap().put("undoAction", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                canvas.undo();
            }
        });
    }

    private void saveDrawingAsPNG() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Specify a file to save");
        int userSelection = fileChooser.showSaveDialog(this);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            try {
                Image drawing = createImage();
                BufferedImage bufferedImage = toBufferedImage(drawing);
                ImageIO.write(bufferedImage, "png", fileToSave);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    private Image createImage() {
        int width = canvas.getWidth();
        int height = canvas.getHeight();
        Image image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics g = image.getGraphics();
        canvas.paint(g);
        g.dispose();
        return image;
    }

    private BufferedImage toBufferedImage(Image img) {
        if (img instanceof BufferedImage) {
            return (BufferedImage) img;
        }

        BufferedImage bufferedImage = new BufferedImage(img.getWidth(null), img.getHeight(null),
                BufferedImage.TYPE_INT_ARGB);
        Graphics g = bufferedImage.createGraphics();
        g.drawImage(img, 0, 0, null);
        g.dispose();

        return bufferedImage;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new PaintGui().setVisible(true);
            }
        });
    }
}
