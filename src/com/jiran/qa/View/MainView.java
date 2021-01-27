package com.jiran.qa.View;

import com.jiran.qa.Common.*;
import com.sun.tools.javac.Main;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Vector;

public class MainView extends JDialog implements ILogCallback, IPostManagerCallback {
    private JPanel contentPane;
    private JButton btnStart;
    private JProgressBar progressBar1;
    private JTextField textField2;
    private JButton btnSelector;
    private JButton btnStop;
    private JTextArea logTxt;
    private JPanel JList_Panel;
    private JScrollPane include_list;
    private JScrollPane exclude_list;
    private JList list1;
    private JList list2;

    private ArrayList<PostVO> postList;
    private PostManager postManager;
    private boolean isReady;
    private boolean isRunning;

    private Vector<String> includeCategories;
    private Vector<String> excludeCategories;
    private static ILogCallback logCallback;
    private static IPostManagerCallback postManagerCallback;

    SimpleDateFormat simpleDateFormat;

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
        if(Config.isDebug)  log("onNegativeButton");
        if(isReady){
            isReady = false;
            btnStart.setText("Search");
            postList.clear();
            list1.setListData(new String[0]);
            list2.setListData(new String[0]);
        }
    }

    // Search, Pull 버튼 동작
    private void onOK() throws InterruptedException {
        postManager = new PostManager();
        postManager.start();
    }

    private void init(){
        logCallback = this;
        postManagerCallback = this;
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(btnStart);

        btnStart.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    onOK();
                } catch (InterruptedException interruptedException) {
                    interruptedException.printStackTrace();
                }
            }
        });

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
        btnSelector.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

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
        btnStart.setEnabled(false);
    }

    @Override
    public void finishParse() {
        log("finishParse()");
        postList = postManager.getPosts();
        HashSet<String> categories;

        if(postManager.getPosts() != null){
            categories = new HashSet<>();

            for(int i = 0; i<postList.size(); i++){
                String  temp = postList.get(i).getCATEGORIES_NAME();
                categories.add(temp);
            }

            includeCategories = new Vector<>();
            excludeCategories = new Vector<>();

            list1.setListData(categories.toArray());
            list2.setListData(excludeCategories);
            list1.updateUI();
            isReady = true;
        }else{
            log("Empty to PostManager.");
            isReady = false;
            btnStart.setEnabled(true);
        }
        if(isReady) btnStart.setText("Pull");
        btnStart.setEnabled(true);
    }

    public static void main(String args[]){
        MainView view = new MainView();
        view.pack();
        view.setVisible(true);
        view.setSize(Config.WINDOW_WIDTH, Config.WINDOW_HEIGHT);
        view.setResizable(false);
        view.setTitle("TEST");
        System.exit(0);
    }
}