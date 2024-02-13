package final_assignment;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.*;
import java.nio.file.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class Question6 extends JFrame {
    private JTextField urlField = new JTextField("https://www.pawlovetreats.com/cdn/shop/articles/pembroke-welsh-corgi-puppy_1000x.jpg");
    private JButton addButton = new JButton("Add Download");
    private DefaultListModel<DownloadInfo> listModel = new DefaultListModel<>();
    private JList<DownloadInfo> downloadList = new JList<>(listModel);
    private ExecutorService downloadExecutor = Executors.newFixedThreadPool(10); // 10 concurrent downloads

    public Question6() {
        super("Asynchronous Image Downloader");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 900);
        layoutComponents();
        setVisible(true);
        setLocationRelativeTo(null); // Center the JFrame on the screen
    }

    private void layoutComponents() {
        // Set dark theme
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Set background color to black and font to Roboto 14 for all components
        Font roboto14 = new Font("Roboto", Font.PLAIN, 20);
        Color whiteColor = Color.WHITE;
        Color blackColor = Color.BLACK;

        getContentPane().setBackground(blackColor);
        urlField.setFont(roboto14);
        urlField.setForeground(blackColor);
        addButton.setFont(roboto14);
        addButton.setBackground(whiteColor);
        addButton.setForeground(blackColor);

        JLabel titleLabel = new JLabel("Asynchronous Image Downloader - 220401");
        titleLabel.setFont(new Font("Roboto", Font.BOLD, 24));
        titleLabel.setForeground(whiteColor);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel addPanel = new JPanel();
        addPanel.setBackground(blackColor);
        addPanel.setLayout(new BorderLayout());
        addPanel.add(urlField, BorderLayout.CENTER);
        addPanel.add(addButton, BorderLayout.EAST);

        JScrollPane scrollPane = new JScrollPane(downloadList);
        downloadList.setCellRenderer(new DownloadListRenderer());

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(blackColor);

        JButton pauseResumeButton = new JButton("Pause/Resume");
        pauseResumeButton.setFont(roboto14);
        pauseResumeButton.setBackground(whiteColor);
        pauseResumeButton.setForeground(blackColor);

        JButton cancelButton = new JButton("Cancel");
        cancelButton.setFont(roboto14);
        cancelButton.setBackground(whiteColor);
        cancelButton.setForeground(blackColor);

        JButton showDownloadsButton = new JButton("Show Downloads");
        showDownloadsButton.setFont(roboto14);
        showDownloadsButton.setBackground(whiteColor);
        showDownloadsButton.setForeground(blackColor);

        buttonPanel.add(pauseResumeButton);
        buttonPanel.add(cancelButton);
        buttonPanel.add(showDownloadsButton);

        addButton.addActionListener(e -> addDownload(urlField.getText().trim()));

        pauseResumeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DownloadInfo selectedInfo = downloadList.getSelectedValue();
                if (selectedInfo != null) {
                    selectedInfo.togglePauseResume();
                }
            }
        });

        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DownloadInfo selectedInfo = downloadList.getSelectedValue();
                if (selectedInfo != null) {
                    selectedInfo.cancel();
                    listModel.removeElement(selectedInfo);
                }
            }
        });

        showDownloadsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    Desktop.getDesktop().open(new File("downloads"));
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });

        setLayout(new BorderLayout());


        add(addPanel, BorderLayout.SOUTH);
        add(titleLabel, BorderLayout.EAST);
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.NORTH);

    }

    private void addDownload(String url) {
        try {
            new URL(url);
            DownloadInfo info = new DownloadInfo(url);
            listModel.addElement(info);
            DownloadTask task = new DownloadTask(info, () -> SwingUtilities.invokeLater(this::repaint));
            info.setFuture(downloadExecutor.submit(task));
        } catch (MalformedURLException ex) {
            JOptionPane.showMessageDialog(this, "Invalid URL: " + url, "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Question6::new);
    }
}

class DownloadInfo {
    private final String url;
    private volatile String status = "Waiting..."; // Corrected the typo here
    private volatile long totalBytes = 0L;
    private volatile long downloadedBytes = 0L;
    private Future<?> future;
    private final AtomicBoolean paused = new AtomicBoolean(false);

    public DownloadInfo(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public synchronized boolean isPaused() {
        return paused.get();
    }

    public synchronized void togglePauseResume() {
        paused.set(!paused.get());
        notifyAll();
    }

    public String getStatus() {
        return status;
    }

    public synchronized void setStatus(String status) {
        this.status = status;
    }

    public void setFuture(Future<?> future) {
        this.future = future;
    }

    public void cancel() {
        if (future != null)
            future.cancel(true);
    }

    public synchronized void setTotalBytes(long totalBytes) {
        this.totalBytes = totalBytes;
    }

    public synchronized void addDownloadedBytes(long bytes) {
        this.downloadedBytes += bytes;
    }

    public long getDownloadedBytes() {
        return downloadedBytes;
    }

    public long getTotalBytes() {
        return totalBytes;
    }
}

class DownloadTask implements Callable<Void> {
    private final DownloadInfo info;
    private final Runnable updateUI;

    public DownloadTask(DownloadInfo info, Runnable updateUI) {
        this.info = info;
        this.updateUI = updateUI;
    }

    @Override
    public Void call() throws Exception {
        info.setStatus("Downloading");
        @SuppressWarnings("deprecation")
        URL url = new URL(info.getUrl());
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        long fileSize = connection.getContentLengthLong();
        info.setTotalBytes(fileSize);

        try (InputStream in = new BufferedInputStream(connection.getInputStream())) {
            Path targetPath = Paths.get("downloads", new File(url.getPath()).getName());
            Files.createDirectories(targetPath.getParent());
            try (OutputStream out = new BufferedOutputStream(Files.newOutputStream(targetPath))) {
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = in.read(buffer)) != -1) {
                    synchronized (info) {
                        while (info.isPaused())
                            info.wait();
                    }
                    out.write(buffer, 0, bytesRead);
                    info.addDownloadedBytes(bytesRead);
                    updateUI.run();
                    Thread.sleep(200);
                }
                info.setStatus("Completed");
            }
        } catch (IOException | InterruptedException e) {
            info.setStatus("Error: " + e.getMessage());
            if (e instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
        } finally {
            updateUI.run();
        }
        return null;
    }
}

class DownloadListRenderer extends JPanel implements ListCellRenderer<DownloadInfo> {
    @Override
    public Component getListCellRendererComponent(JList<? extends DownloadInfo> list, DownloadInfo value, int index,
                                                  boolean isSelected, boolean cellHasFocus) {
        this.removeAll();
        setLayout(new BorderLayout());
        JLabel urlLabel = new JLabel(value.getUrl());
        JProgressBar progressBar = new JProgressBar(0, 100);
        if (value.getTotalBytes() > 0) {
            int progress = (int) ((value.getDownloadedBytes() * 100) / value.getTotalBytes());
            progressBar.setValue(progress);
        }
        JLabel statusLabel = new JLabel(value.getStatus());
        add(urlLabel, BorderLayout.NORTH);
        add(progressBar, BorderLayout.CENTER);
        add(statusLabel, BorderLayout.SOUTH);

        if (isSelected) {
            setBackground(list.getSelectionBackground());
            setForeground(list.getSelectionForeground());
        } else {
            setBackground(list.getBackground());
            setForeground(list.getForeground());
        }
        return this;
    }
}
