package sistema;	

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.util.Scanner;
import java.awt.event.*;

import banco.*;
import Interface.MenuFrame;

public class MenuPrincipal{
	private static MenuPrincipal menuPrincipal = null;
	private Scanner scanner;
	Connection conn;
	MenuFrame frame;

	private MenuPrincipal() {
		conn = conexaoBD.getInstance().getConnection();
		frame = MenuFrame.getInstance();
	}

	/* SINGLETON */
	public static MenuPrincipal getInstance() {
		if (menuPrincipal == null)
			menuPrincipal = new MenuPrincipal();
		return menuPrincipal;
	}
	
	public void iniciarMenuPrincipal(Usuario usuario){
		JPanel painel = new JPanel();

		/*FALTA
		 	- while????
			- versao de usuario comum
			- melhorar o layout (tamanho)
		*/
		
		System.out.println("");
		System.out.println("#### MENU PRINCIPAL ####");
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
		
		JLabel menu = new JLabel();
		menu.setText("Menu Principal: ");
		menu.setFont(new Font("Verdana",1,30));
		menu.setPreferredSize(new Dimension(850,50));
		painel.add(menu);
		
		if(usuario.grupo == 1) { //menu principal de admin
			JButton cadastro = new JButton("- Cadastrar um novo usuário");
			cadastro.setPreferredSize(new Dimension(850,60));
			cadastro.setFont(new Font("Verdana",1,20));
			painel.add(cadastro);
			
			cadastro.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					frame.remove(painel);
					frame.revalidate();
					frame.repaint();
					MenuCadastrar.getInstance().iniciarMenuCadastrar(usuario);
				}
			});
		}
			
		JButton alterarUsuario = new JButton("- Alterar senha pessoal e certificado digital do usuário");
		alterarUsuario.setPreferredSize(new Dimension(850,60));
		alterarUsuario.setFont(new Font("Verdana",1,20));
		painel.add(alterarUsuario);
		
		JButton consultarArquivos = new JButton("- Consultar pasta de arquivos secretos do usuário");
		consultarArquivos.setPreferredSize(new Dimension(850,60));
		consultarArquivos.setFont(new Font("Verdana",1,20));
		painel.add(consultarArquivos);
		
		JButton saida = new JButton("- Sair do Sistema");
		saida.setPreferredSize(new Dimension(850,60));
		saida.setFont(new Font("Verdana",1,20));
		painel.add(saida);
	
		alterarUsuario.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				frame.remove(painel);
				frame.revalidate();
				frame.repaint();
				MenuAlterarUsuario.getInstance().iniciarMenuAlterarUsuario(usuario);;
			}
		});
		
		consultarArquivos.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				frame.remove(painel);
				frame.revalidate();
				frame.repaint();
				MenuConsultarArquivos.getInstance().iniciarMenuConsultarArquivos(usuario);;
			}
		});
		
		saida.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				frame.remove(painel);
				frame.revalidate();
				frame.repaint();
				MenuSaida.getInstance().iniciarMenuSaida(usuario);
			}
		});
			
		frame.getContentPane().add(painel);
		frame.revalidate();
		frame.repaint();

	}
}