package org.sam.powerj.datawindow.programme;

import java.awt.EventQueue;

/**
 * demo
 *
 */
public class Programme 
{
    public static void main( String[] args )
    {
    	EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					JFrmDemo frame = new JFrmDemo();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
    }
}
