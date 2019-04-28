package conexaoBD;

import java.sql.*;

/* Classe que fornece conexão com o banco de dados */

public class conexaoBD {
	
	private static conexaoBD conexao = null;
	private Connection conn;

	private conexaoBD(){
		conn = null;
	}

	/* SINGLETON */
	public static conexaoBD getInstance() {
		if(conexao == null)
			conexao = new conexaoBD();
		return conexao;
	}
	
	public Connection getConnection() {
		
		/* Caso a conexão já exista, retorna direto */
		if (this.conn != null)
			return this.conn;
		
		try {
			/* Configura o driver */
			Class.forName("org.sqlite.JDBC");
			this.conn = DriverManager.getConnection("jdbc:sqlite:C:\\Program Files\\sqlite\\trab3.db");
			
			if (this.conn == null)
				System.out.println("Erro ao conectar com o banco de dados.");
			
			return this.conn;
		}
		catch (ClassNotFoundException e) {
			System.err.println(e);
			System.out.println("Driver não encontrado.");
			return null;
        } 
		catch (SQLException e) {
			System.err.println(e);
			System.out.println("Erro ao conectar com o banco de dados.");
			return null;
        }
			
	}
	
	/* Fecha conexão */
	public void close() {
		try {
			this.conn.close();
		}
		catch (SQLException e) {
			System.err.println("Erro ao fechar conexão.");
		}
		
	}


}