package banco;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.PrivateKey;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.sql.*;
import java.util.Base64;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JOptionPane;

import autenticacao.autenticacaoSenha;
import Interface.MenuFrame;

public class Usuario {
	
	public String login_name;
	public String nome;
	public int grupo;
	public String senhaHash;
	public String salt;
	public byte [] certificado;
	public int bloqueado;
	public int numero_acessos;
	public int numero_consultas;
	public PrivateKey chavePrivada;
	Connection conn;
	

	public Usuario(String loginU, String nomeU, int grupoU, String saltU, String senhaU, byte [] certU, int bloqU, int numAcessosU, int numConsU){

		this.login_name = loginU;
		this.nome = nomeU;
		this.grupo = grupoU;
		this.senhaHash = senhaU;
		this.salt = saltU;
		this.certificado = certU; 
		this.bloqueado = bloqU;
		this.numero_acessos = numAcessosU;
		this.numero_consultas = numConsU;

		conn = conexaoBD.getInstance().getConnection();
	}


	public void bloqueiaUsuario(){

		System.out.println("Numero de tentativas excedido. Voce esta bloqueado por 2 minutos.");
		
		String query = "UPDATE USUARIOS SET BLOQUEADO=1 WHERE LOGIN_NAME='"+this.login_name+"';";

		try {
			Statement stmt = conn.createStatement();
			int res = stmt.executeUpdate(query);

			if (res == 0)
				System.out.println("Update naoo retornou resultados...");

			if (stmt != null)
        		stmt.close();
        	System.out.println("Usuario bloqueado!");
		}
		catch (SQLException e) {
			System.err.println(e);
			System.out.println("Erro ao bloquear usuario no banco de dados.");
			System.exit(1);
		}
		
		Timer timer = new Timer();		
		
		TimerTask block = new TimerTask() {

			@Override
			public void run() {
				System.out.println("Voce ja pode tentar novamente!");
		        timer.cancel(); //Terminate the timer thread
		        		        
		        String query = "UPDATE USUARIOS SET BLOQUEADO=0 WHERE LOGIN_NAME='"+login_name+"';";
				try {
					Connection conn = conexaoBD.getInstance().getConnection();
					Statement stmt = conn.createStatement();
					stmt.executeUpdate(query);

					if (stmt != null)
		        		stmt.close();
				}
				catch (SQLException e) {
					System.err.println(e);
					System.out.println("Erro ao desbloquear usuario no banco de dados.");
					System.exit(1);
				}

				JOptionPane.showMessageDialog(MenuFrame.getInstance(), "O Usu·rio "+login_name+" foi desbloqueado e j· pode acessar novamente!");
			
			}
		};
		
		timer.schedule(block, 120000);
	}

	public void insereUsuario(){

		try {
			String insert = "INSERT INTO USUARIOS VALUES (?,?,?,?,?,?,?,?,?)";
			PreparedStatement stmt = conn.prepareStatement(insert);
			stmt.setString(1,this.login_name);
			stmt.setString(2,this.nome);
			stmt.setInt(3,this.grupo);
			stmt.setString(4,this.salt);
			stmt.setString(5,this.senhaHash);
			stmt.setBytes(6,this.certificado);
			stmt.setInt(7,this.bloqueado);
			stmt.setInt(8,this.numero_acessos);
			stmt.setInt(9,this.numero_consultas);
			int res = stmt.executeUpdate();

			if (stmt != null)
        		stmt.close();
		}
		catch (SQLException e) {
			System.err.println(e);
			System.out.println("Erro ao atualizar usuario no banco de dados.");
			System.exit(1);
		}

		System.out.println("Usuario inserido com sucesso!");

	}


	//################################################### FUNCAO DE TESTE
	private static byte [] obtemCertificadoDigitalCodificado(){

		Connection conn = conexaoBD.getInstance().getConnection();
		
		// Obtem caminho para arquivo da chave privada
		Scanner scanner = new Scanner(System.in);
		System.out.print("Digite o caminho para o arquivo contendo o certificado digital: ");
		String caminhoChave = scanner.nextLine();
		byte [] certificadoDigitalPEM = null;

		// Abre arquivo com chave privada
		try{
			Path path = Paths.get(caminhoChave);
			certificadoDigitalPEM = Files.readAllBytes(path);
		}
		catch(IOException e){
			System.err.println(e);
			System.out.println("Erro ao abrir arquivo da chave privada");
			System.exit(1);
		}

		return certificadoDigitalPEM;
	}

	//################################################## FUNCAO DE TESTE
	public static void insereCertificadoNoBanco(String login_name){

		Connection conn = conexaoBD.getInstance().getConnection();
		
		byte [] certificadoPEM = obtemCertificadoDigitalCodificado();
		System.out.println(certificadoPEM);

		try {
			String update = "UPDATE USUARIOS SET CERTIFICADO_DIGITAL=? WHERE LOGIN_NAME=?;";
			PreparedStatement stmt = conn.prepareStatement(update);
			stmt.setBytes(1,certificadoPEM);
			stmt.setString(2,login_name);
			int res = stmt.executeUpdate();

			System.out.println("Resultado: "+Integer.toString(res));

			stmt.close();
		}
		catch (SQLException e) {
			System.err.println(e);
			System.out.println("Erro ao atualizar certificado no banco de dados.");
			System.exit(1);
		}

		System.out.println("Certificado inserido no banco");
	}

	//##################################################### FUNCAO DE TESTE
	public void verificaCertificado(){

		Connection conn = conexaoBD.getInstance().getConnection();
		
		byte [] certificadoPEM = obtemCertificadoDigitalCodificado();

		try {
			String s1 = new String(certificadoPEM, "UTF8");
			int index = s1.indexOf("-----BEGIN CERTIFICATE-----\n");
			String certificadoPEM_B64String = s1.substring(index);
			certificadoPEM_B64String = certificadoPEM_B64String.replace("-----BEGIN CERTIFICATE-----\n", "");
			certificadoPEM_B64String = certificadoPEM_B64String.replace("-----END CERTIFICATE-----", "");
			System.out.println(certificadoPEM_B64String);

			byte[] certificadoBytes = Base64.getMimeDecoder().decode(certificadoPEM_B64String);
	  		CertificateFactory cf = CertificateFactory.getInstance("x509");
	  		X509Certificate certificado = (X509Certificate) cf.generateCertificate(new ByteArrayInputStream(certificadoBytes));
	  		System.out.println("################################################################");
	  		System.out.println(certificado.getSigAlgName());
		}
		catch(Exception e) {
			System.err.println(e);
			System.exit(1);
		}
		
	}


	public void incrementaAcessosUsuario(){

		System.out.println("Incrementando numero de acessos do usuario: "+this.login_name);

		String update = "UPDATE USUARIOS SET NUMERO_ACESSOS=(NUMERO_ACESSOS + 1) WHERE LOGIN_NAME='"+this.login_name+"';";
		int res = 0;
		try {
			Statement stmt = conn.createStatement();
			res = stmt.executeUpdate(update);

			System.out.println("Numero de linhas afetadas: "+res);

			stmt.close();
		}
		catch (SQLException e) {
			System.err.println(e);
			System.out.println("Erro ao incrementar numero de acessos do usu√°rio no banco de dados.");
			System.exit(1);
		}			
		this.numero_acessos += 1;

	}

	public void incrementaConsultasUsuario(){

		System.out.println("Incrementando numero de consultas do usuario: "+this.login_name);

		String update = "UPDATE USUARIOS SET NUMERO_CONSULTAS=(NUMERO_CONSULTAS + 1) WHERE LOGIN_NAME='"+this.login_name+"';";
		int res = 0;
		try {
			Statement stmt = conn.createStatement();
			res = stmt.executeUpdate(update);

			System.out.println("Numero de linhas afetadas: "+res);

			stmt.close();
		}
		catch (SQLException e) {
			System.err.println(e);
			System.out.println("Erro ao incrementar numero de consultas do usu√°rio no banco de dados.");
			System.exit(1);
		}

		this.numero_consultas += 1;		

	}
	
	public static void updateSenhaUsuario(String login_name){
		
		Connection conn = conexaoBD.getInstance().getConnection();
		String salt = autenticacaoSenha.geraSaltAleatorio();
		String senhaHash = autenticacaoSenha.geraHashDaSenha("012345", salt);

		String update = "UPDATE USUARIOS SET salt='"+salt+"',senha='"+senhaHash+"' WHERE LOGIN_NAME='"+login_name+"';";
		int res = 0;
		try {
			Statement stmt = conn.createStatement();
			res = stmt.executeUpdate(update);

			System.out.println("Numero de linhas afetadas: "+res);

			stmt.close();
		}
		catch (SQLException e) {
			System.err.println(e);
			System.out.println("Erro ao incrementar numero de consultas do usu√°rio no banco de dados.");
			System.exit(1);
		}	

	}

}