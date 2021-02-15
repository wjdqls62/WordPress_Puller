package com.jiran.qa.View;

import com.jiran.qa.Common.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;

public class MainView extends JDialog implements ILogCallback, IPostManagerCallback, IDownloadManager {
    private JPanel contentPane;
    private JButton btnStart;
    private JProgressBar progressBar1;
    private JButton btnSelector;
    private JButton btnStop;
    private JTextArea logTxt;
    private JTextField savePath;
    private JPanel JList_Panel;
    private JScrollPane include_list;
    private JScrollPane exclude_list;
    private JList list1;
    private JList list2;
    private JCheckBox chkDebug;
    private JButton btnClear;
    private String path;
    private JFileChooser selector;
    private ArrayList<PostVO> postList;
    private PostManager postManager;
    private boolean isReady;
    private boolean isRunning;

    private HashSet<String> includeCategories;
    private HashSet<String> excludeCategories;
    private static ILogCallback logCallback;
    private static IPostManagerCallback postManagerCallback;
    private static IDownloadManager downloadManagerCallback;

    SimpleDateFormat simpleDateFormat;

    public static IDownloadManager getDownloadManagerCallback(){
        return downloadManagerCallback;
    }

    public static ILogCallback getLogger(){
        return logCallback;
    }
    public static IPostManagerCallback getPostManagerCallback(){
        return postManagerCallback;
    }

    public MainView() {
        init();
    }

    // Stop 버튼 동작
    private void onNegativeButton(){
        if(isReady){
            isReady = false;
            btnStart.setText("Search");
            postList.clear();
            list1.setListData(new String[0]);
            list2.setListData(new String[0]);
        }
    }

    /**
     * isReady == false의 경우 Post 를 긁어온다 (Search 버튼일 경우)
     * isReady == true의 경우 메모리에 올라가있는 Post배열을 다운로드 한다.
     * @throws InterruptedException
     */
    private void onOK() throws InterruptedException {
        if(!isReady){
            postManager = new PostManager();
            postManager.start();
        }else{
            DownloadManager downloadManager = new DownloadManager(postList, path, includeCategories);
            progressBar1.setMaximum(postList.size());
            progressBar1.setMinimum(0);
            downloadManager.start();
        }

    }


    private void init(){
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(btnStart);

        logCallback = this;
        postManagerCallback = this;
        downloadManagerCallback = this;

        /**
         * Search, Pull 버튼
         */
        btnStart.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    onOK();
                } catch (InterruptedException interruptedException) {
                    interruptedException.printStackTrace();
                }
            }
        });

        /**
         * Stop버튼 정의 구문
         */
        // Negative 버튼 초기화
        btnStop.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onNegativeButton();
            }
        });

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                dispose();
            }
        });

        /**
         * 경로선택기 정의구문
         */
        path = System.getProperty("user.dir");
        savePath.setText(path);
        btnSelector.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(selector == null){
                    selector = new JFileChooser();
                }

                selector.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                selector.showOpenDialog(null);

                if(selector.getSelectedFile() != null){
                    savePath.setText(selector.getSelectedFile().toString());
                    if(Config.isDebug){
                        log("Selector Path : " + selector.getSelectedFile());
                    }
                }
            }
        });

        /**
         * isDebug 체크박스 정의구문
         */
        chkDebug.setSelected(Config.isDebug);
        chkDebug.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(chkDebug.isSelected()){
                    Config.isDebug = true;
                }else{
                    Config.isDebug = false;
                }
                log("isDebug : " + Config.isDebug);
            }
        });

        /**
         * Clear버튼 정의구문
         */
        btnClear.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                logTxt.setText(null);
            }
        });

        /**
         * JList 의 Item을 2번이상 연속클릭시 include <-> exclude 로 Item을 이동시키는 리스너
         */
        list1.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if(e.getClickCount() >= 2){
                    int index = list1.locationToIndex(e.getPoint());
                    excludeCategories.add(list1.getModel().getElementAt(index).toString());
                    includeCategories.remove(list1.getModel().getElementAt(index));
                    list1.setListData(includeCategories.toArray());
                    list2.setListData(excludeCategories.toArray());
                }
            }
        });
        list2.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if(e.getClickCount() >= 2){
                    int index = list2.locationToIndex(e.getPoint());
                    includeCategories.add(list2.getModel().getElementAt(index).toString());
                    excludeCategories.remove(list2.getModel().getElementAt(index));
                    list1.setListData(includeCategories.toArray());
                    list2.setListData(excludeCategories.toArray());
                }
            }
        });

        simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
    }

    @Override
    public void log(String text) {
        logTxt.append("[" + simpleDateFormat.format(new Date()) + "] " + text + "\n");
        logTxt.setCaretPosition(logTxt.getDocument().getLength());
    }

    @Override
    public void startParse() {
        isRunning = true;
        btnStop.setEnabled(false);
        btnStart.setEnabled(false);
    }

    @Override
    public void finishParse() {
        isRunning = false;
        postList = postManager.getPosts();

        /**
         *  최초 Categories 수신시 중복값 제거를 위해 HashSet 처리 후 toArray() 처리
         */
        if(postManager.getPosts() != null){
            includeCategories = new HashSet<>();
            excludeCategories = new HashSet<>();

            for(int i = 0; i<postList.size(); i++){
                String  temp = postList.get(i).getCATEGORIES_NAME();
                includeCategories.add(temp);
            }

            list1.setListData(includeCategories.toArray());
            //list2.setListData(excludeCategories);
            list1.updateUI();
            isReady = true;
        }else{
            log("Empty to PostManager.");
            isReady = false;
            btnStart.setEnabled(true);
        }
        if(isReady) btnStart.setText("Pull");
        btnStart.setEnabled(true);
        btnStop.setEnabled(true);
    }

    public static void main(String args[]){
        MainView view = new MainView();
        view.setPreferredSize(new Dimension(Config.WINDOW_WIDTH, Config.WINDOW_HEIGHT));
        view.setResizable(false);
        view.setTitle(Config.WINDOW_TITLE);

        view.pack();
        view.setVisible(true);


        System.exit(0);
    }

    @Override
    public void startDownload(String fileName, long fileSize) {
        // Byte
        if(fileSize <= 1024){
            log("Download of [" + fileName + "] started. / " + fileSize + "B");
        }
        // KByte
        else if(fileSize > 1024 && fileSize <= 1048576){
            log("Download of [" + fileName + "] started. / " + fileSize / 1024 + "KB");
        }
        // Mbyte
        else if(fileSize > 1048576){
            log("Download of [" + fileName + "] started. / " + fileSize / 1024 / 1024 + "MB");
        }
    }

    @Override
    public void finishDownload(String fileName) {
        log("Download of " + fileName + " completed.");
        updateProgressBar(progressBar1.getValue()+1);
    }

    @Override
    public void resetProgressBar() {
        progressBar1.setValue(0);
    }

    @Override
    public void updateProgressBar(int percent) {
        progressBar1.setValue(percent);
    }

    @Override
    public void setButton(boolean status) {
        btnStart.setEnabled(status);
        btnStop.setEnabled(status);
    }
}