package sistema;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.security.Principal;
import java.security.cert.X509Certificate;
import java.sql.Connection;
import java.util.Scanner;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextArea;

import Interface.MenuFrame;
import autenticacao.*;
import banco.*;

public class MenuAlterarUsuario{
	private static MenuAlterarUsuario menuAlterarUsuario = null;
	private Scanner scanner;
	Connection conn;
	MenuFrame frame;

	private MenuAlterarUsuario() {
		conn = conexaoBD.getInstance().getConnection();
		frame = MenuFrame.getInstance();
	}

	/* SINGLETON */
	public static MenuAlterarUsuario getInstance() {
		if (menuAlterarUsuario == null)
			menuAlterarUsuario = new MenuAlterarUsuario();
		return menuAlterarUsuario;
	}


	protected void iniciarMenuAlterarUsuario(Usuario usuario){
		JPanel painel = new JPanel();

		/*FALTA
		 	- botao alterar
		*/

		Registro registro = new Registro();
		registro.login_name = usuario.login_name;
		registro.insereRegistro(7001, "");
		
		System.out.println("");
		System.out.println("#### MENU ALTERAR USUARIO ####");
		System.out.println("");
		
		JLabel login = new JLabel();
		login.setText("Login: "+usuario.login_name);
		login.setFont(new Font("Verdana",1,30));
		login.setPreferredSize(new Dimension(850,50));
		painel.add(login);
		
		JLabel grupo = new JLabel();
		if(usuario.grupo == 1)
			grupo.setText("Grupo: "+usuario.grupo);
		else
			grupo.setText("Grupo: Usuário");
		grupo.setFont(new Font("Verdana",1,30));
		grupo.setPreferredSize(new Dimension(850,50));
		painel.add(grupo);
		
		JLabel nome = new JLabel();
		nome.setText("Nome: "+usuario.nome);
		nome.setFont(new Font("Verdana",1,30));
		nome.setPreferredSize(new Dimension(850,50));
		painel.add(nome);
		
		JLabel numAcessos = new JLabel();
		numAcessos.setText("Total de acessos do usuário: "+usuario.numero_acessos);
		numAcessos.setFont(new Font("Verdana",1,30));
		numAcessos.setPreferredSize(new Dimension(850,50));
		painel.add(numAcessos);
		
		JLabel cert = new JLabel();
		cert.setText("– Caminho do arquivo do certificado digital: <max 255 caracteres>");
		cert.setFont(new Font("Verdana",1,20));
		cert.setPreferredSize(new Dimension(850,50));
		painel.add(cert);
		
		JTextArea certificado = new JTextArea(4,10);
		certificado.setEditable(true);
		certificado.setFont(new Font("Verdana",1,20));
		certificado.setPreferredSize(new Dimension(850,50));
		certificado.setLineWrap(true);
		painel.add(certificado);
		
		JLabel password = new JLabel();
		password.setText("– Senha pessoal: <seis, sete ou oito dígitos>");
		password.setFont(new Font("Verdana",1,20));
		password.setPreferredSize(new Dimension(850,40));
		painel.add(password);
		
		JPasswordField pw = new JPasswordField(9);
		pw.setEnabled(true);
		pw.setPreferredSize(new Dimension(100,50));
		painel.add(pw);
		
		JLabel confirm = new JLabel();
		confirm.setText("– Confirmação senha pessoal: <seis, sete ou oito dígitos>");
		confirm.setFont(new Font("Verdana",1,20));
		confirm.setPreferredSize(new Dimension(850,40));
		painel.add(confirm);
		
		JPasswordField cpw = new JPasswordField(9);
		cpw.setEnabled(true);
		cpw.setPreferredSize(new Dimension(100,50));
		painel.add(cpw);
		
		JButton alterar = new JButton("Alterar");
		alterar.setPreferredSize(new Dimension(850,60));
		alterar.setFont(new Font("Verdana",1,20));
		painel.add(alterar);
		
		JButton voltar = new JButton("Voltar para Menu Principal");
		voltar.setPreferredSize(new Dimension(850,60));
		voltar.setFont(new Font("Verdana",1,20));
		painel.add(voltar);
		
		alterar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {


				// verifica senha e confirmação
				String senha = new String(pw.getPassword());
				String confirmaSenha = new String(cpw.getPassword());
				
				if (! senha.equals(confirmaSenha) ){
					JOptionPane.showMessageDialog(frame, "As senhas devem ser iguais!");
					
					registro.login_name = usuario.login_name;
					registro.insereRegistro(7002, "");

					return;
				}

				if ( senha.length() > 0 && ! MenuCadastrar.getInstance().verificaFormatoSenha(senha) )
					return;

				String stringConfirmacao = "\t\tPorfavor, confirme os novos dados do usuário:\n\n"
							+ "Senha: "+senha+"\n"
							+ "\n";

				byte [] certCodificado = null;
				String emailSujeito = null;
				String nomeSujeito = null;

				// decodifica e abre certificado digital
				if (! certificado.getText().equals("")) {
					certCodificado = Usuario.obtemCertificadoDigitalCodificado(certificado.getText());
					
					if(certCodificado == null) {
						JOptionPane.showMessageDialog(frame, "Erro ao abrir arquivo pelo caminho! Tente novamente.");
						
						registro.login_name = usuario.login_name;
						registro.insereRegistro(7003, "");
						return;
					}

					
					X509Certificate certificadoX509 = autenticacaoChavePrivada.getInstance().obtemCertificado(certCodificado);
					
					if(certificadoX509 == null)
						JOptionPane.showMessageDialog(frame, "Erro ao ler certificado! Tente novamente.");
				

					// obtem dados do certificado (parse)
					Principal principalEmissor = certificadoX509.getIssuerDN();
					Principal principalSujeito = certificadoX509.getSubjectDN();
								
					int indexCNEmissor = principalEmissor.getName().indexOf("CN=") + 3;
					int indexCNSujeito = principalSujeito.getName().indexOf("CN=") + 3;
					int indexEmailSujeito = principalSujeito.getName().indexOf("EMAILADDRESS=") + 13;
					String nomeEmissor = principalEmissor.getName().substring(indexCNEmissor, principalEmissor.getName().indexOf(", ", indexCNEmissor));
					nomeSujeito = principalSujeito.getName().substring(indexCNSujeito, principalSujeito.getName().indexOf(", ", indexCNSujeito));
					emailSujeito = principalSujeito.getName().substring(indexEmailSujeito, principalSujeito.getName().indexOf(", ", indexEmailSujeito));
					
					// gera string com dados
					
					stringConfirmacao += "\tCertificado Digital:\n\n"
							+ "Versão: "+certificadoX509.getVersion()+"\n"
							+ "Série: "+certificadoX509.getSerialNumber()+"\n"
							+ "Validade Not Before: "+certificadoX509.getNotBefore()+"\n"
							+ "Validade Not After: "+certificadoX509.getNotAfter()+"\n"
							+ "Tipo de assinatura: "+certificadoX509.getSigAlgName()+"\n"
							+ "Emissor: "+nomeEmissor+"\n"
							+ "Sujeito: "+nomeSujeito+"\n"
							+ "Email: "+emailSujeito+"\n\n\n";
					
					System.out.println(stringConfirmacao);

				}

				// verifica se todos os campos ficaram em branco
				if ( senha.length() < 1 && certCodificado == null )
					return;

				// abre OptionPane de confirmação
				int resultadoConfirmacao = JOptionPane.showConfirmDialog (frame, stringConfirmacao, "Confirmar dados", JOptionPane.OK_CANCEL_OPTION);

				// caso positivo, insere dados no banco
				if (resultadoConfirmacao == JOptionPane.OK_OPTION) {

					if ( !usuario.login_name.equals(emailSujeito) && Usuario.verificaUsuarioExistente(emailSujeito)){
						JOptionPane.showMessageDialog(frame, "Este email de usuário já existe!");
						return;
					}

					String salt = null;
					String senhaHash = null;

					if (senha.length() > 1){
						salt = autenticacaoSenha.geraSaltAleatorio();
						usuario.salt = salt;
						senhaHash = autenticacaoSenha.geraHashDaSenha(senha, salt);
						usuario.senhaHash = senhaHash;
					}

					if ( usuario.alterarUsuario(emailSujeito, nomeSujeito, certCodificado, salt, senhaHash , usuario.login_name) ) {
						JOptionPane.showMessageDialog(frame, "Usuário alterado com sucesso!");

						if (certCodificado != null){
							usuario.login_name = emailSujeito;
							usuario.nome = nomeSujeito;
							usuario.certificado = certCodificado;
						}
						
						certificado.setText("");
						pw.setText("");
						cpw.setText("");
						
						return;

					}
				}

			}
		});
		
		voltar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				frame.remove(painel);
				frame.revalidate();
				frame.repaint();
				MenuPrincipal.getInstance().iniciarMenuPrincipal(usuario);
			}
		});
		
		frame.getContentPane().add(painel);
		frame.revalidate();
		frame.repaint();
		
	}
}