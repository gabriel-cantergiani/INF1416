package sistema;

import java.sql.Connection;
import java.util.Scanner;

import banco.*;

public class MenuSaida{
	


	private static MenuSaida menuSaida = null;
	private Scanner scanner;
	Connection conn;

	private MenuSaida() {
		conn = conexaoBD.getInstance().getConnection();
	}

	/* SINGLETON */
	public static MenuSaida getInstance() {
		if (menuSaida == null)
			menuSaida = new MenuSaida();
		return menuSaida;
	}


	protected void iniciarMenuSaida(Usuario usuario){

		/*FALTA
				

		*/
		System.out.println("");
		System.out.println("#### MENU SAIDA ####");
		System.out.println("");
		





	}
}