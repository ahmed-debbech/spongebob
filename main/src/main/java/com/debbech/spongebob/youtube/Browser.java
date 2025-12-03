package com.debbech.spongebob.youtube;

import java.awt.*;
import java.net.URI;

public class Browser {
    public void launchBrowser(String url) throws Exception{
        try {
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                URI uri = new URI(url);
                Desktop.getDesktop().browse(uri);
                System.out.println("Opened " + url + " in the default browser.");
            } else {
                System.out.println("Desktop browsing is not supported on this system. trying different method...");
                boolean isWindows = System.getProperty("os.name").toLowerCase().contains("win");
                if(isWindows){
                    execute("start \"\" \""+url+"\"");
                }else{
                    execute("google-chrome \""+url+"\"");
                }
            }
        } catch (Exception e) {
            System.err.println("failed to load default broswer because : " + e.getMessage());
            throw new Exception("could not load browser because: " + e.getMessage());
        }
    }

    private static void execute(String command) throws Exception {
        try {
            boolean isWin = System.getProperty("os.name").toLowerCase().contains("win");

            ProcessBuilder pb = (!isWin)? new ProcessBuilder("bash", "-c", command) :
                    new ProcessBuilder("cmd.exe", "/c", command);
            Process process = pb.start();
        } catch (Exception e) {
            throw new Exception("Error executing command: " + e.getMessage());
        }
    }
}
