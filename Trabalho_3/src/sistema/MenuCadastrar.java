package sistema;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Scanner;
import javax.swing.*;
import java.sql.*;

import banco.*;
import Interface.MenuFrame;

public class MenuCadastrar{
	private static MenuCadastrar menuCadastrar = null;
	private Scanner scanner;
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
		
		/*FALTA
			- limitar senha pra no max 9
			- botao cadastrar funcionando
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
		
		/*
		cadastrar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
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
				
			}
		});
		*/
		
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