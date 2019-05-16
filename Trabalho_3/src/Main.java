import java.sql.Connection;
import java.util.Date;

import autenticacao.identificacaoUsuario;
import banco.conexaoBD;
import banco.Registro;

public class Main {
	
	public static void main(String args[]){
		
		/* iniciando conexao com o banco q sera usada durante todo o sistema */
		Connection conn = conexaoBD.getInstance().getConnection();
		
		Registro registro = new Registro();
		registro.login_name = "";
		registro.insereRegistro(1001);
		
		/* Classe que implementa 1a etapa de autenticacao */
		identificacaoUsuario idUsuario = identificacaoUsuario.getInstance();
		idUsuario.iniciarIdentificacao();
		
	}

}