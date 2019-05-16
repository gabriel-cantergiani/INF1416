package autenticacao;

import java.sql.*;
import java.util.Scanner;
import java.util.regex.Matcher; 
import java.util.regex.Pattern;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import Interface.MenuFrame;
import banco.*;
import sistema.MenuPrincipal;

public class identificacaoUsuario{
	private static identificacaoUsuario identificacao_usuario = null;
	Connection conn;
	MenuFrame frame;
	
	private identificacaoUsuario() {
		conn = conexaoBD.getInstance().getConnection();
		frame = MenuFrame.getInstance();
	}

	/* SINGLETON */
	public static identificacaoUsuario getInstance() {
		if (identificacao_usuario == null)
			identificacao_usuario = new identificacaoUsuario();
		return identificacao_usuario;
	}
	
	public void iniciarIdentificacao() {

		
		/* Conexao com o banco de dados */
		System.out.println("#### IDENTIFICACAO DO USUARIO - 1a ETAPA ####");
		System.out.println("");
		JPanel painel = new JPanel();
		painel.setLayout(null);
		int larguraFrame = frame.getWidth();
		
		JLabel labelLogin = new JLabel();
		labelLogin.setText("Digite o login_name do usuário");
		labelLogin.setFont(new Font("Verdana",1,larguraFrame/40));
		labelLogin.setBounds(larguraFrame/2 - 225, 150, 450, 80);
		painel.add(labelLogin);
		
		JTextArea inputLogin = new JTextArea(4,10);
		inputLogin.setEditable(true);
		inputLogin.setFont(new Font("Verdana",1,larguraFrame/40));
		inputLogin.setBounds((10*larguraFrame - 8*larguraFrame)/20, labelLogin.getY()+100, 8*larguraFrame/10, 60);
		inputLogin.setLineWrap(true);
		painel.add(inputLogin);

		JButton buscarLogin = new JButton("Buscar");
		buscarLogin.setFont(new Font("Verdana",1,larguraFrame/40));
		buscarLogin.setBounds((10*larguraFrame - 2*larguraFrame)/20, inputLogin.getY()+100, 2*larguraFrame/10, 70);
		painel.add(buscarLogin);

		frame.getContentPane().add(painel);
		frame.revalidate();
		frame.repaint();
		
		buscarLogin.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event){

				String login_name = inputLogin.getText();

				if (!emailValido(login_name)){
					JOptionPane.showMessageDialog(frame, "O login_name deve ser em um formato de email valido!");
					return;
				}

				/* Busca login no banco */
				String query = "SELECT * FROM USUARIOS WHERE LOGIN_NAME='"+login_name+"';";
				ResultSet result = null;
				try {
					Statement stmt = conn.createStatement();
					result = stmt.executeQuery(query);

					if (!result.next())
						JOptionPane.showMessageDialog(frame, "Usuário não encontrado!");

					else if (result.getInt("BLOQUEADO") == 1)
						JOptionPane.showMessageDialog(frame, "Este usuário está temporariamente bloqueado (2 minutos) !");

					else{

						Usuario usuario = new Usuario(result.getString("LOGIN_NAME"), result.getString("NOME"), result.getInt("GRUPO"), result.getString("SALT"), result.getString("SENHA"), result.getBytes("CERTIFICADO_DIGITAL"), result.getInt("BLOQUEADO"), result.getInt("NUMERO_ACESSOS"), result.getInt("NUMERO_CONSULTAS"));
						
						/* Fecha statement para passar para 2 etapa */
						stmt.close();
						result.close();

						// Remove painel atual
						frame.remove(painel);
						frame.revalidate();
						frame.repaint();
						
						/* PASSANDO PARA PROXIMA ETAPA DE AUTENTICACAO */
						autenticacaoSenha.getInstance().iniciarAutenticacaoSenha(usuario);
					}

					if (stmt != null)
		        		stmt.close();
				}
				catch (SQLException e) {
					System.err.println(e);
					System.out.println("Erro ao buscar usuário no banco de dados.");
					System.exit(1);
				}			

			}

		});

	}


	private boolean emailValido(String email){
		String regex = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";

		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(email);

		return matcher.matches();
	}
}