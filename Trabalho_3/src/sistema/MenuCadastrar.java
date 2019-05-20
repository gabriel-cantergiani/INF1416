package sistema;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileWriter;
import java.io.IOException;
import java.security.Principal;
import java.security.cert.X509Certificate;
import javax.swing.*;
import java.sql.*;

import banco.*;
import Interface.MenuFrame;
import autenticacao.autenticacaoChavePrivada;
import autenticacao.autenticacaoSenha;

public class MenuCadastrar{
	private static MenuCadastrar menuCadastrar = null;
	Connection conn;
	MenuFrame frame;

	private MenuCadastrar(){
		conn = conexaoBD.getInstance().getConnection();
		frame = MenuFrame.getInstance();
	}

	/* SINGLETON */
	public static MenuCadastrar getInstance() {
		if (menuCadastrar == null)
			menuCadastrar = new MenuCadastrar();
		return menuCadastrar;
	}

	protected void iniciarMenuCadastrar(Usuario usuario){		
		JPanel painel = new JPanel();
		
		Registro registro = new Registro();
		registro.login_name = usuario.login_name;
		registro.insereRegistro(6001, "");
		
		int larguraFrame = frame.getWidth();
		
		/*FALTA
			- limitar senha pra no max 9
		*/
		
		System.out.println("");
		System.out.println("#### MENU CADASTRAR ####");
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
		
		String query = "select count(*) from usuarios;";
		ResultSet result = null;
		try {
			Statement stmt = conn.createStatement();
			result = stmt.executeQuery(query);
			
			JLabel totalUsuarios = new JLabel();
			totalUsuarios.setText("Total de usuários: "+result.getInt(1));
			totalUsuarios.setFont(new Font("Verdana",1,30));
			totalUsuarios.setPreferredSize(new Dimension(850,50));
			painel.add(totalUsuarios);

			stmt.close();
			result.close();
		}
		catch (SQLException e) {
			System.err.println(e);
			System.out.println("Erro ao fazer count na tabela de usuários.");
			System.exit(1);
		}
		
		JLabel formulario = new JLabel();
		formulario.setText("Formulário de Cadastro: ");
		formulario.setFont(new Font("Verdana",1,30));
		formulario.setPreferredSize(new Dimension(850,50));
		painel.add(formulario);
		
		JLabel cert = new JLabel();
		cert.setText("- Caminho do arquivo do certificado digital: <max 255 caracteres>");
		cert.setFont(new Font("Verdana",1,20));
		cert.setPreferredSize(new Dimension(850,50));
		painel.add(cert);
		
		JTextArea certificado = new JTextArea(4,10);
		certificado.setEditable(true);
		certificado.setFont(new Font("Verdana",1,20));
		certificado.setPreferredSize(new Dimension(850,50));
		certificado.setLineWrap(true);
		painel.add(certificado);
		
		JLabel g = new JLabel();
		g.setText("– Grupo: ");
		g.setFont(new Font("Verdana",1,20));
		painel.add(g);
		
		JComboBox<Integer> combo = new JComboBox<Integer>();
		String q = "select * from grupos;";
		ResultSet r = null;
		try {
			Statement stmt = conn.createStatement();
			r = stmt.executeQuery(q);
			
			while(r.next()) {
				combo.addItem(r.getInt(2));
			}
			
			stmt.close();
			r.close();
		}
		catch (SQLException e) {
			System.err.println(e);
			System.out.println("Erro ao recuperar valor da tabela grupos.");
			System.exit(1);
		}
		painel.add(combo);
		
		JLabel password = new JLabel();
		password.setText("- Senha pessoal: <seis, sete ou oito dígitos>");
		password.setFont(new Font("Verdana",1,20));
		password.setPreferredSize(new Dimension(850,40));
		painel.add(password);
		
		JPasswordField pw = new JPasswordField(9);
		pw.setEnabled(true);
		pw.setPreferredSize(new Dimension(100,50));
		painel.add(pw);
		
		JLabel confirm = new JLabel();
		confirm.setText("- Confirmação senha pessoal: <seis, sete ou oito dígitos>");
		confirm.setFont(new Font("Verdana",1,20));
		confirm.setPreferredSize(new Dimension(850,40));
		painel.add(confirm);
		
		JPasswordField cpw = new JPasswordField(9);
		cpw.setEnabled(true);
		cpw.setPreferredSize(new Dimension(100,50));
		painel.add(cpw);
		
		JButton cadastrar = new JButton("Cadastrar");
		cadastrar.setPreferredSize(new Dimension(850,60));
		cadastrar.setFont(new Font("Verdana",1,20));
		painel.add(cadastrar);
		
		JButton voltar = new JButton("Voltar para Menu Principal");
		voltar.setPreferredSize(new Dimension(850,60));
		voltar.setFont(new Font("Verdana",1,20));
		painel.add(voltar);
		
		JButton logview = new JButton("LogView");
		logview.setFont(new Font("Verdana",1,larguraFrame/40));
		logview.setBounds((10*larguraFrame - 2*larguraFrame)/20+400, logview.getY()+1200, 2*larguraFrame/10, 70);
		painel.add(logview);
		
		logview.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event){
				
				FileWriter txt = null;
				try {
					txt = new FileWriter("log.txt", false);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				
				try {
					String join = "SELECT (SELECT datetime(r.timestamp/1000, 'unixepoch', 'localtime')), r.codigo, m.mensagem, r.login_name, r.nome_arquivo FROM REGISTROS AS R JOIN MENSAGENS AS M ON R.CODIGO = M.CODIGO AND R.TIMESTAMP = M.TIMESTAMP;";
					PreparedStatement stmt = conn.prepareStatement(join);	
					ResultSet res = stmt.executeQuery();

					while(res.next()) {
					
						txt.write(res.getObject(1)+", "+res.getInt(2)+", "+res.getString(3)+", "+res.getString(4)+", "+res.getString(5)+"\r\n");
					}
					
					txt.close();
					
					if (stmt != null)
		        		stmt.close();
				}
				catch (SQLException e) {
					System.err.println(e);
					System.out.println("Erro ao fazer join em registros e mensagens.");
					System.exit(1);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		});

		
		cadastrar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				registro.login_name = usuario.login_name;
				registro.insereRegistro(6002, certificado.getText());

				// verifica senha e confirmação
				String senha = new String(pw.getPassword());
				String confirmaSenha = new String(cpw.getPassword());
				
				if (! senha.equals(confirmaSenha) ){
					JOptionPane.showMessageDialog(frame, "As senhas devem ser iguais!");
					
					registro.login_name = usuario.login_name;
					registro.insereRegistro(6003, certificado.getText());

					return;
				}

				if (! verificaFormatoSenha(senha) )
					return;

				// decodifica e abre certificado digital
				byte [] certCodificado = Usuario.obtemCertificadoDigitalCodificado(certificado.getText());
				
				X509Certificate certificadoX509 = autenticacaoChavePrivada.getInstance().obtemCertificado(certCodificado);
				
				if(certificadoX509 == null) {
					JOptionPane.showMessageDialog(frame, "Erro ao abrir arquivo pelo caminho! Tente novamente.");
					
					registro.login_name = usuario.login_name;
					registro.insereRegistro(6004, certificado.getText());
				}
				
				int grupo = (int) combo.getSelectedItem();

				// obtem dados do certificado (parse)
				Principal principalEmissor = certificadoX509.getIssuerDN();
				Principal principalSujeito = certificadoX509.getSubjectDN();
							
				int indexCNEmissor = principalEmissor.getName().indexOf("CN=") + 3;
				int indexCNSujeito = principalSujeito.getName().indexOf("CN=") + 3;
				int indexEmailSujeito = principalSujeito.getName().indexOf("EMAILADDRESS=") + 13;
				String nomeEmissor = principalEmissor.getName().substring(indexCNEmissor, principalEmissor.getName().indexOf(", ", indexCNEmissor));
				String nomeSujeito = principalSujeito.getName().substring(indexCNSujeito, principalSujeito.getName().indexOf(", ", indexCNSujeito));
				String emailSujeito = principalSujeito.getName().substring(indexEmailSujeito, principalSujeito.getName().indexOf(", ", indexEmailSujeito));
				
				// gera string com dados
				String stringConfirmacao = "\t\tPorfavor, confirme os dados do usuário:\n\n"
						+ "Grupo: "+grupo+"\n"
						+ "Senha: "+senha+"\n"
						+ "\n"
								+ "\tCertificado Digital:\n\n"
						+ "Versão: "+certificadoX509.getVersion()+"\n"
						+ "Série: "+certificadoX509.getSerialNumber()+"\n"
						+ "Validade Not Before: "+certificadoX509.getNotBefore()+"\n"
						+ "Validade Not After: "+certificadoX509.getNotAfter()+"\n"
						+ "Tipo de assinatura: "+certificadoX509.getSigAlgName()+"\n"
						+ "Emissor: "+nomeEmissor+"\n"
						+ "Sujeito: "+nomeSujeito+"\n"
						+ "Email: "+emailSujeito+"\n\n\n";
				
				System.out.println(stringConfirmacao);

				// abre OptionPane de confirmação
				int resultadoConfirmacao = JOptionPane.showConfirmDialog (frame, stringConfirmacao, "Confirmar dados", JOptionPane.OK_CANCEL_OPTION);

				// caso positivo, insere dados no banco
				if (resultadoConfirmacao == JOptionPane.OK_OPTION) {
					
					registro.login_name = usuario.login_name;
					registro.insereRegistro(6005, certificado.getText());

					if (Usuario.verificaUsuarioExistente(emailSujeito)){
						JOptionPane.showMessageDialog(frame, "Este email de usuário já existe!");
						return;
					}

					String saltUsuario = autenticacaoSenha.geraSaltAleatorio();
					Usuario novoUsuario = new Usuario(emailSujeito, nomeSujeito, grupo, saltUsuario, autenticacaoSenha.geraHashDaSenha(senha, saltUsuario), certCodificado, 0, 0, 0);
		
					if (novoUsuario.insereUsuario()) {
						JOptionPane.showMessageDialog(frame, "Usuário inserido com sucesso!");

						certificado.setText("");
						pw.setText("");
						cpw.setText("");
						
						return;

					}
				}
				else {
					registro.login_name = usuario.login_name;
					registro.insereRegistro(6006, certificado.getText());
				}
				
			}
		});
		
		
		voltar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				registro.login_name = usuario.login_name;
				registro.insereRegistro(6007, "");
				
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


	protected boolean verificaFormatoSenha(String senha){

		if (senha.length() < 6 || senha.length() > 8){
			JOptionPane.showMessageDialog(frame, "A senha deve conter entre 6 e 8 dígitos!");
			return false;
		}
		
		for (int i=0; i<senha.length()-1; i++) {
			
			int dig = (int) senha.charAt(i);
			int proximo_dig = (int) senha.charAt(i+1);

			if( dig < 48 || dig > 57 ) {
				JOptionPane.showMessageDialog(frame, "A senha deve conter somente dígitos numéricos!");
				return false;
			}
			
			if (dig == proximo_dig){
				JOptionPane.showMessageDialog(frame, "A senha não pode conter repetições de números consecutivos!");
				return false;
			}
			
			if ((dig == proximo_dig+1) || (dig == proximo_dig-1)){
				JOptionPane.showMessageDialog(frame, "A senha não pode conter sequências crescentes ou decrescentes de números!");
				return false;
			}		
		}

		
		return true;
	}


}