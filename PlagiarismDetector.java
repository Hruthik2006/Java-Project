import javax.swing.*;
//import javax.swing.text.JTextComponent;
import javax.swing.border.LineBorder;

import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import javax.imageio.ImageIO;

public class PlagiarismCheckerUI {
    private File file1, file2;
    private JTextField resultField;
    //JTextComponent fileNameLabel;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(PlagiarismCheckerUI::new);
    }

    public PlagiarismCheckerUI() {
        // Create main frame
        JFrame frame = new JFrame("Plagiarism Checker");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 400);
        frame.getContentPane().setBackground(Color.lightGray);
        frame.setLayout(new BorderLayout());

        // Instructions at the top
        JLabel instructions = new JLabel("Drag & drop files into the folder or choose manually", SwingConstants.CENTER);
        frame.add(instructions, BorderLayout.NORTH);

        // Panel for file selection (drag-and-drop or manual)
        JPanel folderPanel = new JPanel();
        folderPanel.setLayout(new GridLayout(1, 2, 20, 20));
        folderPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel file1Panel = createFilePanel("Choose file 1", true);
        JPanel file2Panel = createFilePanel("Choose file 2", false);

        folderPanel.add(file1Panel);
        folderPanel.add(file2Panel);

        frame.add(folderPanel, BorderLayout.CENTER);

        // Compare button at the bottom
        JPanel bottomPanel = new JPanel(new BorderLayout());
        JButton compareButton = new JButton("Compare");
        //compareButton.setPreferredSize(new Dimension(50, 30));
        compareButton.setBorder(new LineBorder(Color.decode("#8EC5FF"), 2));
        compareButton.setBackground(Color.decode("#DAEDFF"));
        compareButton.addActionListener(e -> compareFiles());

        //compareButton.setForeground(Color.decode("#DAEDFF"));

        resultField = new JTextField("Result: ");
        resultField.setEditable(false);

        bottomPanel.add(compareButton, BorderLayout.NORTH);
        bottomPanel.add(resultField, BorderLayout.SOUTH);

        frame.add(bottomPanel, BorderLayout.SOUTH);

        frame.setVisible(true);
    }

    private JPanel createFilePanel(String buttonText, boolean isFile1) {
        JPanel panel = new JPanel(new BorderLayout());
        //JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
    
        // Create a label to represent the folder icon
        JLabel folderIcon = new JLabel();
        folderIcon.setHorizontalAlignment(SwingConstants.CENTER);
        //folderIcon.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        folderIcon.setPreferredSize(new Dimension(150, 150));
    
        // Set the folder icon image
        try {
            BufferedImage originalImage = ImageIO.read(new File("emptyfolder.png"));
            Image resizedImage = originalImage.getScaledInstance(150, 150, Image.SCALE_SMOOTH);
            folderIcon.setIcon(new ImageIcon(resizedImage));
        } catch (IOException e) {
            folderIcon.setText("📁"); // Fallback to Unicode folder icon
        }
    
        // Add drag-and-drop functionality
        new DropTarget(folderIcon, new DropTargetAdapter() {
            @Override
            public void drop(DropTargetDropEvent dtde) {
                try {
                    dtde.acceptDrop(DnDConstants.ACTION_COPY);
                    List<File> droppedFiles = (List<File>) dtde.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
                    if (!droppedFiles.isEmpty()) {
                        File droppedFile = droppedFiles.get(0);
                        if (droppedFile.getName().toLowerCase().endsWith(".txt")) {
                            if (isFile1) {
                                file1 = droppedFile;
                            } else {
                                file2 = droppedFile;
                            }
                            folderIcon.setText(droppedFile.getName());
                            //fileNameLabel.setText(droppedFile.getName());

                            try {
                                BufferedImage originalImage = ImageIO.read(new File("nonEmptyFolder.png"));
                                Image resizedImage = originalImage.getScaledInstance(150, 150, Image.SCALE_SMOOTH);
                                folderIcon.setIcon(new ImageIcon(resizedImage));
                            } catch (IOException e) {
                                folderIcon.setText("📁"); // Fallback to Unicode folder icon
                            }
                            // Create a label to display the selected file name
                            JLabel fileNameLabel = new JLabel("No file selected");
                            fileNameLabel.setHorizontalAlignment(SwingConstants.CENTER);
                        } else {
                            JOptionPane.showMessageDialog(null, "Only .txt files are allowed!");
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
    
        // Create a button for manual file selection
        JButton button = new JButton(buttonText);
        //button.setPreferredSize(new Dimension(50, 30));
        button.setBackground(Color.decode("#DAEDFF"));
        button.setBorder(new LineBorder(Color.decode("#8EC5FF"), 2));
        button.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
    
            // Add a file filter for .txt files
            fileChooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
                @Override
                public boolean accept(File file) {
                    return file.isDirectory() || file.getName().toLowerCase().endsWith(".txt");
                }
    
                @Override
                public String getDescription() {
                    return "Text Files (*.txt)";
                }
            });
    
            int option = fileChooser.showOpenDialog(null);
            if (option == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                if (isFile1) {
                    file1 = selectedFile;
                } else {
                    file2 = selectedFile;
                }
                folderIcon.setText(selectedFile.getName());
                //fileNameLabel.setText(selectedFile.getName());

                try {
                    BufferedImage originalImage = ImageIO.read(new File("nonEmptyFolder.png"));
                    Image resizedImage = originalImage.getScaledInstance(150, 150, Image.SCALE_SMOOTH);
                    folderIcon.setIcon(new ImageIcon(resizedImage));
                } catch (IOException ex) {
                    folderIcon.setText("📁"); // Fallback to Unicode folder icon
                }
                // Create a label to display the selected file name
                JLabel fileNameLabel = new JLabel("No file selected");
                fileNameLabel.setHorizontalAlignment(SwingConstants.CENTER);
            }
        });
    
        panel.add(folderIcon, BorderLayout.CENTER);
        panel.add(button, BorderLayout.SOUTH);
    
        return panel;
    }


    private void compareFiles() {
        if (file1 == null || file2 == null) {
            JOptionPane.showMessageDialog(null, "Please select both files before comparing!");
            return;
        }

        try {
            String content1 = new String(Files.readAllBytes(file1.toPath()));
            String content2 = new String(Files.readAllBytes(file2.toPath()));

            int similarity = calculateSimilarity(content1, content2);
            if (similarity > 35) {
                resultField.setText("Result: Plagiarism Detected with " + similarity + "% similarity");
                resultField.setBackground(Color.decode("#D30000"));
            }
            else {
                resultField.setText("Result: " + similarity + "% similarity");
                resultField.setBackground(Color.decode("#29AB87"));
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error reading files: " + e.getMessage());
        }
    }

    private int calculateSimilarity(String text1, String text2) {
        text1 = text1.replaceAll("\\s+", "").toLowerCase();
        text2 = text2.replaceAll("\\s+", "").toLowerCase();

        int maxLength = Math.max(text1.length(), text2.length());
        int sameCount = 0;

        for (int i = 0; i < Math.min(text1.length(), text2.length()); i++) {
            if (text1.charAt(i) == text2.charAt(i)) {
                sameCount++;
            }
        }

        return (int) ((double) sameCount / maxLength * 100);
    }
}
