package sistema;

import java.sql.Connection;
import java.util.Scanner;

import banco.*;

public class MenuConsultarArquivos{
	


	private static MenuConsultarArquivos menuConsultarArquivos = null;
	private Scanner scanner;
	Connection conn;

	private MenuConsultarArquivos() {
		conn = conexaoBD.getInstance().getConnection();
	}

	/* SINGLETON */
	public static MenuConsultarArquivos getInstance() {
		if (menuConsultarArquivos == null)
			menuConsultarArquivos = new MenuConsultarArquivos();
		return menuConsultarArquivos;
	}


	protected void iniciarMenuConsultarArquivos(Usuario usuario){

		/*FALTA
				

		*/
		System.out.println("");
		System.out.println("#### MENU CONSULTAR ARQUIVOS SECRETOS ####");
		System.out.println("");
		





	}
}