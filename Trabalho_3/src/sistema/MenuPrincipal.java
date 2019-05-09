package sistema;

import java.sql.Connection;
import java.util.Scanner;

import banco.*;


public class MenuPrincipal {
	
	private static MenuPrincipal menuPrincipal = null;
	private Scanner scanner;
	Connection conn;

	private MenuPrincipal() {
		conn = conexaoBD.getInstance().getConnection();
	}

	/* SINGLETON */
	public static MenuPrincipal getInstance() {
		if (menuPrincipal == null)
			menuPrincipal = new MenuPrincipal();
		return menuPrincipal;
	}


	public void iniciarMenuPrincipal(Usuario usuario){

		/*FALTA
				

		*/
		System.out.println("");
		System.out.println("#### MENU PRINCIPAL ####");
		System.out.println("");
		





	}


}