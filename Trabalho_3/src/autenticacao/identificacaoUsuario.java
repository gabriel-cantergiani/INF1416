package autenticacao;

import conexaoBD.conexaoBD;
import java.sql.*;
import java.util.Scanner;

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
				- Verificar se conta está bloqueada.
				- Criar interface para receber input do login_name.
				- Pular para a proxima etapa de autenticacao.
		*/
		
		/* Conexão com o banco de dados */
		Connection conn = conexaoBD.getInstance().getConnection();

		/* Recebe input do usuario pelo console (temporario) */
		Scanner scanner = new Scanner(System.in);
		Boolean login_valido = false;

		while (!login_valido) {
			System.out.println("Digite o login_name:");
			String login_name = scanner.nextLine();

			/* Busca login no banco */
			String query = "SELECT * FROM USUARIOS WHERE LOGIN_NAME='"+login_name+"';";

			try {
				Statement stmt = conn.createStatement();
				ResultSet result = stmt.executeQuery(query);

				if (!result.next()){
					System.out.println("Usuário não encontrado!");
					continue;
				}

				System.out.println(result.getString("LOGIN_NAME"));
				System.out.println(result.getString("NOME"));
				System.out.println(result.getInt("GRUPO"));
				login_valido = true;
				
				if (stmt != null)
	        		stmt.close();
			}
			catch (SQLException e) {
				System.err.println(e);
				System.out.println("Erro ao executar query");
			}

		}

		/* PASSAR AQUI PARA A PROXIMA ETAPA */

	}


}