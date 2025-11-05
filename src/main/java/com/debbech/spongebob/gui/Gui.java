package com.debbech.spongebob.gui;

import com.debbech.spongebob.core.Core;
import com.debbech.spongebob.input.UserInput;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class Gui {

    public UserInput userInput;
    private JLabel status;
    private JTextField inputmp3;
    private JTextField inputimg;
    private int msgCount = 1;
    private JButton button;

    public void initGui(){
        SwingUtilities.invokeLater(() -> new Gui().createAndShowGUI());
    }

    public void updateStatus(String text, StatusType st, boolean restarted){
        if(restarted) msgCount = 1;

        Color ct = null;
        switch (st){
            case ERR : ct = Color.RED; break;
            case NORM : ct = Color.BLACK; break;
            case SUCC : ct = Color.GREEN; break;
            case WARN: ct = Color.YELLOW; break;
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
        button = new JButton("Generate mp4");
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
        button.setEnabled(false);
        userInput = new UserInput(inputmp3.getText().trim(), inputimg.getText().trim());
        String[] pars = {userInput.getImage(), userInput.getMp3Path()};
        new Thread(() -> {
            Core.run(pars, this);
            button.setEnabled(true);
        }).start();
    }
}
