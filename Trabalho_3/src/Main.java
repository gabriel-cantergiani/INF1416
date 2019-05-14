import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Time;
import java.util.Calendar;
import java.util.Date;

import autenticacao.identificacaoUsuario;
import banco.conexaoBD;

public class Main {
	
	public static void main(String args[]){
		
		/* iniciando conexao com o banco q sera usada durante todo o sistema */
		Connection conn = conexaoBD.getInstance().getConnection();
		java.time.LocalTime.now();
		
		/* Classe que implementa 1a etapa de autenticacao */
		identificacaoUsuario idUsuario = identificacaoUsuario.getInstance();
		idUsuario.iniciarIdentificacao();
		
	}

}