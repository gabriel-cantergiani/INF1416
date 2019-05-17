package Interface;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

import banco.Registro;
import sistema.*;
import banco.Usuario;

import java.awt.*;
import java.awt.event.*;

public class MenuFrame extends JFrame{
	private static MenuFrame xframe = null;
	public String usuario;
	
	private MenuFrame(){

		Toolkit tk = Toolkit.getDefaultToolkit();
		Dimension ss = tk.getScreenSize();

		this.setBounds(ss.width/6, (10*ss.height - 7*ss.height)/20, 2*ss.width/3, 7*ss.height/10);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setVisible(true);
		
		this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                
                Registro registro = new Registro();
                if(usuario == null)
                	registro.login_name = "";
                else
                	registro.login_name = usuario;
        		registro.insereRegistro(1002, "");
                
            }

            @Override
            public void windowClosed(WindowEvent e) {
            }

        });
	
	} 

	/* SINGLETON */
	public static MenuFrame getInstance(){
		if(xframe == null) {
			xframe = new MenuFrame();
		}
		return xframe;
	}

}	