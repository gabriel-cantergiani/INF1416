package Interface;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import sistema.*;

public class MenuFrame extends JFrame{
	private static MenuFrame xframe = null;
	
	private MenuFrame(){
		this.setBounds(0,0,960,900);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setVisible(true);
	} 

	/* SINGLETON */
	public static MenuFrame getInstance(){
		if(xframe == null) {
			xframe = new MenuFrame();
		}
		return xframe;
	}

}	