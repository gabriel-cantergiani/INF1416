package sistema;

import java.sql.Connection;
import java.util.Scanner;

import banco.*;

public class MenuAlterarUsuario{
	


	private static MenuAlterarUsuario menuAlterarUsuario = null;
	private Scanner scanner;
	Connection conn;

	private MenuAlterarUsuario() {
		conn = conexaoBD.getInstance().getConnection();
	}

	/* SINGLETON */
	public static MenuAlterarUsuario getInstance() {
		if (menuAlterarUsuario == null)
			menuAlterarUsuario = new MenuAlterarUsuario();
		return menuAlterarUsuario;
	}


	protected void iniciarMenuAlterarUsuario(Usuario usuario){

		/*FALTA
				

		*/
		System.out.println("");
		System.out.println("#### MENU ALTERAR USUARIO ####");
		System.out.println("");
		





	}
}