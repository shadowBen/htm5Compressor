package com.wyj.compressor;

import javax.swing.*;

public class main {

	public static void main(String[] args) {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                try {
					createAndShowGUI();
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				} catch (InstantiationException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (UnsupportedLookAndFeelException e) {
					e.printStackTrace();
				}
            }

			private void createAndShowGUI() throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException {
				UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");  
				// 确保一个漂亮的外观风格
		        JFrame.setDefaultLookAndFeelDecorated(true);
		        JFrame frame = new FileChooser();
		        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		        // 显示窗口
		        frame.pack();
		        frame.setVisible(true);
			}
        });
	}

}
