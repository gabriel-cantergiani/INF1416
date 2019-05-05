package autenticacao;

import conexaoBD.conexaoBD;
import java.sql.*;
import java.util.Scanner;
import java.util.regex.Matcher; 
import java.util.regex.Pattern;

public class identificacaoUsuario{
	
	private static identificacaoUsuario identificacao_usuario = null;

	private identificacaoUsuario() {

	}

	/* SINGLETON */
	public static identificacaoUsuario getInstance() {
		if (identificacao_usuario == null)
			identificacao_usuario = new identificacaoUsuario();
		return identificacao_usuario;
	}
	
	public void iniciarIdentificacao() {

		/* FALTA:
				- Criar interface para receber input do login_name.
		*/
		
		/* Conex√£o com o banco de dados */
		Connection conn = conexaoBD.getInstance().getConnection();
		
		/* Recebe input do usuario pelo console (temporario) */
		Scanner scanner = new Scanner(System.in);
		//Boolean login_valido = false;
		String login_name = null;

		while (true) {
			System.out.print("Digite o login_name:");
			login_name = scanner.nextLine();

			if (!emailValido(login_name)){
				System.out.println("O login_name deve ser em um formato de email valido!");
				continue;
			}

			/* Busca login no banco */
			String query = "SELECT * FROM USUARIOS WHERE LOGIN_NAME='"+login_name+"';";
			ResultSet result = null;

			try {
				Statement stmt = conn.createStatement();
				result = stmt.executeQuery(query);

				if (!result.next())
					System.out.println("Usu·rio n„o encontrado!");

				else if (result.getInt("BLOQUEADO") == 1)
					System.out.println("Este usu·rio est· temporariamente bloqueado!");

				else{
					System.out.println("Usu·rio encontrado!");
					/* Fecha statement para passar para 2 etapa */
					stmt.close();
					result.close();

					/* PASSANDO PARA PROXIMA ETAPA DE AUTENTICACAO */
					autenticacaoSenha.getInstance().iniciarAutenticacaoSenha(login_name, result);
				}

				if (stmt != null)
	        		stmt.close();
			}
			catch (SQLException e) {
				System.err.println(e);
				System.out.println("Erro ao buscar usu·rio no banco de dados.");
				System.exit(1);
			}			
			
		}//fim while

	}


	private boolean emailValido(String email){
		String regex = "^[(a-zA-Z-0-9-\\_\\+\\.)]+@[(a-z-A-z)]+\\.[(a-zA-z)]{2,3}$";

		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(email);

		return matcher.matches();
	}
}