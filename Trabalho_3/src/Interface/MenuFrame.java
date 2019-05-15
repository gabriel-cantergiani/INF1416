package Interface;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

import sistema.*;

import java.awt.*;
import java.awt.event.*;

public class MenuFrame extends JFrame{
	private static MenuFrame xframe = null;
	
	private MenuFrame(){

		Toolkit tk = Toolkit.getDefaultToolkit();
		Dimension ss = tk.getScreenSize();

		this.setBounds(ss.width/4, (2*ss.height-ss.width)/4, ss.width/2, ss.width/2);
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