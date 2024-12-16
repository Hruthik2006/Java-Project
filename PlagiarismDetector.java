// Using Cosine similarity algo



import javax.swing.*;
import javax.swing.border.BevelBorder;
//import javax.swing.text.JTextComponent;
import javax.swing.border.LineBorder;

import java.util.HashMap;
import java.util.Map;

import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import javax.imageio.ImageIO;

public class PlagiarismDetector {
    private File file1, file2;
    private JTextField resultField;
    //JTextComponent fileNameLabel;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(PlagiarismDetector::new);
    }

    public PlagiarismChecker() {
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
        JPanel folderPanel = new JPanel(new FlowLayout());
        folderPanel.setLayout(new GridLayout(1, 2, 20, 20));
        folderPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel file1Panel = createFilePanel("Choose file 1", true, "");
        JPanel file2Panel = createFilePanel("Choose file 2", false, "");

        folderPanel.add(file1Panel);
        folderPanel.add(file2Panel);

        frame.add(folderPanel, BorderLayout.CENTER);

        // Compare button at the bottom
        JPanel bottomPanel = new JPanel(new FlowLayout());
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(0,0,10,0));
        JButton compareButton = new JButton("Compare");
        compareButton.setPreferredSize(new Dimension(100, 23));
        //compareButton.setBorder(new BevelBorder(BevelBorder.RAISED));
        compareButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        compareButton.setBorder(new LineBorder(Color.decode("#8EC5FF"), 2));
        compareButton.setBackground(Color.decode("#DAEDFF"));
        compareButton.addActionListener(e -> compareFiles());

        //compareButton.setForeground(Color.decode("#DAEDFF"));

        resultField = new JTextField("Result: ");
        resultField.setEditable(false);

        bottomPanel.add(compareButton, BorderLayout.NORTH);
        //bottomPanel.add(resultField, BorderLayout.SOUTH);

        frame.add(bottomPanel, BorderLayout.SOUTH);

        frame.setVisible(true);
    }

    private JPanel createFilePanel(String buttonText, boolean isFile1, String fileNameDisplay) {
        JPanel panel = new JPanel(new BorderLayout());
        //JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
    
        // Create a label to represent the folder icon
        JLabel folderIcon = new JLabel();
        folderIcon.setHorizontalAlignment(SwingConstants.CENTER);
        //folderIcon.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        //folderIcon.setBorder(new BevelBorder(BevelBorder.RAISED));
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

                            try {
                                BufferedImage originalImage = ImageIO.read(new File("nonEmptyFolder.png"));
                                Image resizedImage = originalImage.getScaledInstance(150, 150, Image.SCALE_SMOOTH);
                                folderIcon.setIcon(new ImageIcon(resizedImage));
                            } catch (IOException e) {
                                folderIcon.setText("📁"); // Fallback to Unicode folder icon
                            }
                            // Create a label to display the selected file name
                            JLabel fileNameLabel = new JLabel();
                            fileNameLabel.setHorizontalAlignment(SwingConstants.SOUTH);
                            fileNameLabel.setText(droppedFile.getName());
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
        button.setBorder(new BevelBorder(BevelBorder.RAISED));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
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
                JLabel fileNameLabel = new JLabel();
                //fileNameLabel.setHorizontalAlignment(SwingConstants.CENTER);
                fileNameLabel.setText(selectedFile.getName());
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
            if (similarity > 15) {
                //resultField.setText("Result: Plagiarism Detected with " + similarity + "% similarity");
                //resultField.setBackground(Color.decode("#D30000"));
                JOptionPane.showMessageDialog(null, "Result: Plagiarism Detected with " + similarity + "% similarity\"");
            }
            else {
                //resultField.setText("Result: " + similarity + "% similarity");
                //resultField.setBackground(Color.decode("#29AB87"));
                JOptionPane.showMessageDialog(null, "Result: " + similarity + "% similarity");
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error reading files: " + e.getMessage());
        }
    }

    

    private int calculateSimilarity(String text1, String text2) {
        // Preprocess texts: remove punctuation, convert to lowercase, and split into words
        String[] words1 = text1.replaceAll("[^a-zA-Z0-9\\s]", "").toLowerCase().split("\\s+");
        String[] words2 = text2.replaceAll("[^a-zA-Z0-9\\s]", "").toLowerCase().split("\\s+");

        // Create frequency maps for both texts
        Map<String, Integer> freqMap1 = createFrequencyMap(words1);
        Map<String, Integer> freqMap2 = createFrequencyMap(words2);

        // Compute cosine similarity
        double cosineSimilarity = computeCosineSimilarity(freqMap1, freqMap2);

        // Convert to percentage similarity
        return (int) (cosineSimilarity * 100);
    }

    private Map<String, Integer> createFrequencyMap(String[] words) {
        Map<String, Integer> freqMap = new HashMap<>();
        for (String word : words) {
            freqMap.put(word, freqMap.getOrDefault(word, 0) + 1);
        }
        return freqMap;
    }

    private double computeCosineSimilarity(Map<String, Integer> freqMap1, Map<String, Integer> freqMap2) {
        // Compute dot product and magnitudes
        double dotProduct = 0.0;
        double magnitude1 = 0.0;
        double magnitude2 = 0.0;

        for (String key : freqMap1.keySet()) {
            int freq1 = freqMap1.getOrDefault(key, 0);
            int freq2 = freqMap2.getOrDefault(key, 0);
            dotProduct += freq1 * freq2;
            magnitude1 += Math.pow(freq1, 2);
        }

        for (int freq : freqMap2.values()) {
            magnitude2 += Math.pow(freq, 2);
        }

        // Avoid division by zero
        if (magnitude1 == 0 || magnitude2 == 0) {
            return 0.0;
        }

        return dotProduct / (Math.sqrt(magnitude1) * Math.sqrt(magnitude2));
    }

}
