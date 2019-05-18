package banco;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;
import java.sql.Date;

public class Registro {
	
	/* FALTA
		- mostrar erro ao inves do programa crashar qndo dois usuarios inserirem no mesmo momento = primary key (codigo, timestamp)
	 */
	
	Connection conn;
	public String login_name;
	Date timestamp;
	static Map<Integer, String> mensagem;
		
	public Registro(){
		conn = conexaoBD.getInstance().getConnection();	
	}
	
	public void getMensagem(Map<Integer, String> m) {
		mensagem = m;
	}
	
	public void insereRegistro(int codigo, String nome_arquivo) {
		timestamp = new Date(System.currentTimeMillis());
		
		try {
			String insert = "INSERT INTO REGISTROS VALUES (?,?,?,?)";
			PreparedStatement stmt = conn.prepareStatement(insert);
			stmt.setInt(1,codigo);
			stmt.setDate(2, timestamp);
			stmt.setString(3, login_name);
			stmt.setString(4, nome_arquivo);
			
			stmt.execute();

			if (stmt != null)
        		stmt.close();
		}
		catch (SQLException e) {
			System.err.println(e);
			System.out.println("Erro ao inserir registro no banco de dados.");
			System.exit(1);
		}
		
		try {
			String insert = "INSERT INTO MENSAGENS VALUES (?,?,?)";
			PreparedStatement stmt = conn.prepareStatement(insert);
			stmt.setString(1, mensagem.get(codigo));			
			stmt.setInt(2,codigo);
			stmt.setDate(3, timestamp);
			
			stmt.execute();

			if (stmt != null)
        		stmt.close();
		}
		catch (SQLException e) {
			System.err.println(e);
			System.out.println("Erro ao inserir mensagem no banco de dados.");
			System.exit(1);
		}
		
	}
}
