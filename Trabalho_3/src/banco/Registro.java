package banco;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Date;

public class Registro {
	Connection conn;
	public String login_name;
	
	public Registro(){
		conn = conexaoBD.getInstance().getConnection();
	}
	
	public void insereRegistro(int codigo) {		
		try {
			String insert = "INSERT INTO REGISTROS VALUES (?,?,?,?)";
			PreparedStatement stmt = conn.prepareStatement(insert);
			stmt.setInt(1,codigo);
			stmt.setDate(2, new Date(System.currentTimeMillis()));
			stmt.setString(3, login_name);
			stmt.setString(4,"");
			
			stmt.execute();

			if (stmt != null)
        		stmt.close();
		}
		catch (SQLException e) {
			System.err.println(e);
			System.out.println("Erro ao inserir registro no banco de dados.");
			System.exit(1);
		}
	}
}
