package com.jiran.qa.View;

import com.jiran.qa.Common.Config;
import com.jiran.qa.Common.ILogCallback;
import com.jiran.qa.Common.PostManager;
import com.jiran.qa.Common.PostVO;

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

public class MainView extends JDialog implements ILogCallback {
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

    private DefaultListModel<String> model;
    private PostManager postManager;

    private Vector<String> includeCategories;
    private Vector<String> excludeCategories;
    private static ILogCallback callBack;

    SimpleDateFormat simpleDateFormat;

    public static ILogCallback getLogger(){
        return callBack;
    }

    public MainView() {
        callBack = this;
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
                onStop();
            }
        });

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                dispose();
            }
        });

        init();

    }

    private void onStop(){
        if(Config.isDebug)  log("onStop()");

    }

    private void onOK() throws InterruptedException {
        postManager = new PostManager();
        postManager.start();

        /**
        JSONArray categories = postManager.getCategoriesArray();

        for(int i = 0; i < categories.length(); i++){
            includeCategories.add(categories.getJSONObject(i).get("name").toString());
        }
         **/

        postManager.join();

        ArrayList<PostVO> postList = postManager.getPosts();
        HashSet<String> categories = new HashSet<>();

        for(int i = 0; i<postList.size(); i++){
            String  temp = postList.get(i).getCATEGORIES_NAME();
            categories.add(temp);
        }

        includeCategories = new Vector<>();
        excludeCategories = new Vector<>();

        list1.setListData(categories.toArray());
        list2.setListData(excludeCategories);
        list1.updateUI();
    }

    private void init(){

        simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        this.pack();
        this.setVisible(true);
        this.setResizable(false);
        System.exit(0);
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
    }

    @Override
    public void log(String text) {
        logTxt.append("[" + simpleDateFormat.format(new Date()) + "] " + text + "\n");
        logTxt.setCaretPosition(logTxt.getDocument().getLength());
    }
}
