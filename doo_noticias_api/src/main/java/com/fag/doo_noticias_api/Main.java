package com.fag.doo_noticias_api;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import com.fag.doo_noticias_api.gui.NewsAppGUI;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            new NewsAppGUI();
        });
    }
}