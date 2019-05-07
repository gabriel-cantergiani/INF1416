package conexaoBD;

import java.sql.*;

/* Classe que fornece conexão com o banco de dados */

public class conexaoBD {
	
	private static conexaoBD conexao = null;
	private static Connection conn = null;

	private conexaoBD(){
	}

	/* SINGLETON */
	public static conexaoBD getInstance() {
		if(conexao == null)
			conexao = new conexaoBD();
		return conexao;
	}
	
	public Connection getConnection() {
		
		try {
			if (conn == null || conn.isClosed()){
				/* Configura o driver */
				Class.forName("org.sqlite.JDBC");
				//this.conn = DriverManager.getConnection("jdbc:sqlite:C:\\Program Files\\sqlite\\trab3.db");
				conn = DriverManager.getConnection("jdbc:sqlite:C:\\Users\\Gabriel\\Google Drive\\Faculdade\\2019.1\\Segurança da Informação\\Trabalhos\\INF1416\\Trabalho_3\\trab3.db");
				
				if (conn == null)
					System.out.println("Erro ao conectar com o banco de dados.");				
			}
		}
		catch (ClassNotFoundException e) {
			System.err.println(e);
			return null;
	    } 
		catch (SQLException e) {
			System.err.println(e);
			System.out.println("## ConexaoBD - Erro ao conectar com o banco de dados. ##");
			return null;
	    }
		
		return conn;
			
	}
	
	/* Fecha conexão */
	public void close() {
		try {
			System.out.println("## ConexaoBD - fechando conexao existente. ##");
			conn.close();
			conn = null;
		}
		catch (SQLException e) {
			System.err.println("## ConexaoBD - Erro ao fechar conexão. ## ");
		}
		
	}

}