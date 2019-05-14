package sistema;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextArea;

import Interface.MenuFrame;
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
				//mesmo que cadastrar em menucadastrar (checar)
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