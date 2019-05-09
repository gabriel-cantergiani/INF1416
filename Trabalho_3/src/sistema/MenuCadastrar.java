package sistema;

import java.sql.Connection;
import java.util.Scanner;

import banco.*;

public class MenuCadastrar{
	


	private static MenuCadastrar menuCadastrar = null;
	private Scanner scanner;
	Connection conn;

	private MenuCadastrar() {
		conn = conexaoBD.getInstance().getConnection();
	}

	/* SINGLETON */
	public static MenuCadastrar getInstance() {
		if (menuCadastrar == null)
			menuCadastrar = new MenuCadastrar();
		return menuCadastrar;
	}


	protected void iniciarMenuCadastrar(Usuario usuario){

		/*FALTA
				

		*/
		System.out.println("");
		System.out.println("#### MENU CADASTRAR ####");
		System.out.println("");
		





	}
}