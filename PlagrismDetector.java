import javax.swing.*;
import java.awt.*;
import java.awt.dnd.*;
import java.awt.datatransfer.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.List;

public class PlagiarismDetector {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(PlagiarismDetector::createAndShowGUI);
    }

    private static void createAndShowGUI() {
        JFrame frame = new JFrame("Plagiarism Detector");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 500);

        // Layout Setup
        JPanel mainPanel = new JPanel(new BorderLayout());

        // Text Areas for File Content
        JTextArea fileOneContent = createTextArea("Drag and drop the first file here...");
        JTextArea fileTwoContent = createTextArea("Drag and drop the second file here...");
        JScrollPane scrollPane1 = new JScrollPane(fileOneContent);
        JScrollPane scrollPane2 = new JScrollPane(fileTwoContent);

        // Result Area
        JTextArea resultArea = new JTextArea("Result will appear here...");
        resultArea.setEditable(false);
        resultArea.setLineWrap(true);
        JScrollPane resultScrollPane = new JScrollPane(resultArea);

        // Split Pane for File Content Areas
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scrollPane1, scrollPane2);
        splitPane.setDividerLocation(350);

        // Plagiarism Check Button
        JButton checkButton = new JButton("Check for Plagiarism");

        // Add Components to Main Panel
        mainPanel.add(splitPane, BorderLayout.CENTER);
        mainPanel.add(checkButton, BorderLayout.NORTH);
        mainPanel.add(resultScrollPane, BorderLayout.SOUTH);

        // Drag-and-Drop Support
        addDragAndDropSupport(fileOneContent);
        addDragAndDropSupport(fileTwoContent);

        // Button Action Listener
        checkButton.addActionListener(e -> {
            String content1 = fileOneContent.getText().trim();
            String content2 = fileTwoContent.getText().trim();

            if (content1.isEmpty() || content2.isEmpty()) {
                resultArea.setText("Please provide content for both files!");
            } else {
                int similarity = calculateSimilarity(content1, content2);
                resultArea.setText("Plagiarism Score: " + similarity + "%");
            }
        });

        frame.add(mainPanel);
        frame.setVisible(true);
    }

    private static JTextArea createTextArea(String placeholderText) {
        JTextArea textArea = new JTextArea(placeholderText);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setFont(new Font("SansSerif", Font.PLAIN, 14));
        textArea.setMargin(new Insets(10, 10, 10, 10));
        return textArea;
    }

    private static void addDragAndDropSupport(JTextArea textArea) {
        new DropTarget(textArea, new DropTargetListener() {
            @Override
            public void dragEnter(DropTargetDragEvent dtde) {}

            @Override
            public void dragOver(DropTargetDragEvent dtde) {}

            @Override
            public void dropActionChanged(DropTargetDragEvent dtde) {}

            @Override
            public void dragExit(DropTargetEvent dte) {}

            @Override
            public void drop(DropTargetDropEvent dtde) {
                try {
                    dtde.acceptDrop(DnDConstants.ACTION_COPY);
                    List<File> droppedFiles = (List<File>) dtde.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);

                    StringBuilder content = new StringBuilder();
                    for (File file : droppedFiles) {
                        content.append(new String(Files.readAllBytes(file.toPath()))).append("\n");
                    }
                    textArea.setText(content.toString().trim());
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(textArea, "Failed to read file: " + ex.getMessage());
                }
            }
        });
    }

    private static int calculateSimilarity(String text1, String text2) {
        String[] words1 = text1.split("\\s+");
        String[] words2 = text2.split("\\s+");
        Set<String> uniqueWords = new HashSet<>(Arrays.asList(words1));
        uniqueWords.retainAll(Arrays.asList(words2));

        int commonWordCount = uniqueWords.size();
        int totalWordCount = Math.max(words1.length, words2.length);

        return (int) (((double) commonWordCount / totalWordCount) * 100);
    }
}
