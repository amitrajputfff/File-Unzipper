import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

class HuffmanNode {
    int data;
    char c;
    HuffmanNode left, right;

    public HuffmanNode(char c, int data) {
        this.c = c;
        this.data = data;
        left = right = null;
    }

    public HuffmanNode(char c, int data, HuffmanNode left, HuffmanNode right) {
        this.c = c;
        this.data = data;
        this.left = left;
        this.right = right;
    }
}

class MyComparator implements Comparator<HuffmanNode> {
    public int compare(HuffmanNode x, HuffmanNode y) {
        return x.data - y.data;
    }
}

public class FileUnzipperGUI {
    private JFrame frame;
    private JButton unzipButton;
    private JFileChooser fileChooser;
    private File selectedFile;

    public FileUnzipperGUI() {
        frame = new JFrame("File Unzipper");
        frame.setLayout(new FlowLayout());

        unzipButton = new JButton("Unzip File");
        unzipButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                selectFile();
                unzipFile();
            }
        });
        frame.add(unzipButton);

        frame.setSize(300, 100);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    private void selectFile() {
        fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(frame);
        if (result == JFileChooser.APPROVE_OPTION) {
            selectedFile = fileChooser.getSelectedFile();
        }
    }

    private void unzipFile() {
        if (selectedFile == null) {
            JOptionPane.showMessageDialog(frame, "No file selected!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(selectedFile))) {
            StringBuilder encodedText = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                encodedText.append(line);
            }
            String decodedText = decode(encodedText.toString());
            saveDecodedText(decodedText);
            JOptionPane.showMessageDialog(frame, "File unzipped successfully!");
        } catch (IOException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Error unzipping file: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String decode(String encodedText) {
        Map<Character, Integer> freq = new HashMap<>();
        for (char c : encodedText.toCharArray()) {
            freq.put(c, freq.getOrDefault(c, 0) + 1);
        }

        PriorityQueue<HuffmanNode> pq = new PriorityQueue<>(freq.size(), new MyComparator());
        for (Map.Entry<Character, Integer> entry : freq.entrySet()) {
            pq.add(new HuffmanNode(entry.getKey(), entry.getValue()));
        }

        while (pq.size() != 1) {
            HuffmanNode left = pq.poll();
            HuffmanNode right = pq.poll();
            int sum = left.data + right.data;
            pq.add(new HuffmanNode('\0', sum, left, right));
        }

        HuffmanNode root = pq.peek();
        StringBuilder decodedText = new StringBuilder();
        HuffmanNode current = root;
        for (char c : encodedText.toCharArray()) {
            if (c == '0') {
                current = current.left;
            } else {
                current = current.right;
            }
            if (current.left == null && current.right == null) {
                decodedText.append(current.c);
                current = root;
            }
        }
        return decodedText.toString();
    }

    private void saveDecodedText(String decodedText) {
        JFileChooser saveFileChooser = new JFileChooser();
        int result = saveFileChooser.showSaveDialog(frame);
        if (result == JFileChooser.APPROVE_OPTION) {
            File outputFile = saveFileChooser.getSelectedFile();
            try (PrintWriter writer = new PrintWriter(outputFile)) {
                writer.write(decodedText);
            } catch (IOException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(frame, "Error saving decoded text: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new FileUnzipperGUI();
            }
        });
    }
}
