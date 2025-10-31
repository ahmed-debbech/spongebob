package com.debbech.spongebob.gui;

import com.debbech.spongebob.core.DiskManager;
import com.debbech.spongebob.core.FFMPEGManager;
import com.debbech.spongebob.input.InputSanitizer;
import com.debbech.spongebob.input.UserInput;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.nio.file.Path;

public class Gui {

    public UserInput userInput;
    private JLabel status;
    private JTextField inputmp3;
    private JTextField inputimg;
    private int msgCount = 1;
    private Boolean start = false;

    public void initGui(Boolean startFlow){
        SwingUtilities.invokeLater(() -> new Gui().createAndShowGUI());
        start = startFlow;
    }

    public void updateStatus(String text, StatusType st, boolean restarted){
        if(restarted) msgCount = 1;

        Color ct = null;
        switch (st){
            case ERR : ct = Color.RED;
            case NORM : ct = Color.BLACK;
            case SUCC : ct = Color.GREEN;
            case WARN: ct = Color.YELLOW;
        }
        status.setForeground(ct);
        status.setText(msgCount + " -> " + text);
        msgCount++;
    }

    public void createAndShowGUI(){
        JFrame frame = new JFrame("Simple GUI");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 500);

        JLabel img = new JLabel("Set Image");
        inputimg = new JTextField(15);
        JLabel mp3s = new JLabel("Set Mp3s Path");
        inputmp3 = new JTextField(15);
        JButton button = new JButton("Generate mp4");
        status = new JLabel("--", SwingConstants.CENTER);

        button.addActionListener((e) -> generateMp4(e));

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(img);
        panel.add(inputimg);
        panel.add(mp3s);
        panel.add(inputmp3);
        panel.add(button);
        panel.add(status);

        frame.add(panel, BorderLayout.CENTER);
        frame.setVisible(true);
    }

    private void generateMp4(ActionEvent e) {
        start = true;
        userInput = new UserInput(inputmp3.getText(), inputimg.getText());

    }
}
