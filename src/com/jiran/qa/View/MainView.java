package com.jiran.qa.View;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class MainView extends JDialog {
    private JPanel contentPane;
    private JButton btnStart;
    private JProgressBar progressBar1;
    private JTextField textField2;
    private JButton btnSelector;
    private JButton btnStop;
    private JTextArea textArea1;
    private JPanel JList_Panel;
    private JScrollPane include_list;
    private JScrollPane exclude_list;
    private JList list1;
    private JList list2;

    private DefaultListModel<String> model;

    public MainView() {
        String[] arr = {"EML", "Active contents Doc", "3"};
        String[] arr2 = {"Virus", "Malware", "6"};
        list1.setListData(arr);
        list2.setListData(arr2);


        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(btnStart);










        btnStart.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
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
        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
         **/
    }

    private void onOK() {
        // add your code here
    }

    public static void main(String[] args) {
        MainView dialog = new MainView();
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
    }
}
