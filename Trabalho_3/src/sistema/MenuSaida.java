package sistema;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import Interface.MenuFrame;
import banco.*;

public class MenuSaida{
	private static MenuSaida menuSaida = null;
	Connection conn;
	MenuFrame frame;

	private MenuSaida() {
		conn = conexaoBD.getInstance().getConnection();
		frame = MenuFrame.getInstance();
	}

	/* SINGLETON */
	public static MenuSaida getInstance() {
		if (menuSaida == null)
			menuSaida = new MenuSaida();
		return menuSaida;
	}


	protected void iniciarMenuSaida(Usuario usuario){
		JPanel painel = new JPanel();
		
		Registro registro = new Registro();
		registro.login_name = usuario.login_name;
		registro.insereRegistro(9001, "");
		
		/*FALTA
		*/
		
		System.out.println("");
		System.out.println("#### MENU SAIDA ####");
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
		
		JLabel saida = new JLabel();
		saida.setText("Saída do sistema: ");
		saida.setFont(new Font("Verdana",1,30));
		saida.setPreferredSize(new Dimension(850,50));
		painel.add(saida);

		JLabel mensagem = new JLabel();
		mensagem.setText("Pressione o botão Sair para confirmar.");
		mensagem.setFont(new Font("Verdana",1,30));
		mensagem.setPreferredSize(new Dimension(850,50));
		painel.add(mensagem);
		
		JButton sair = new JButton("Sair");
		sair.setPreferredSize(new Dimension(850,60));
		sair.setFont(new Font("Verdana",1,20));
		painel.add(sair);
		
		JButton voltar = new JButton("Voltar para Menu Principal");
		voltar.setPreferredSize(new Dimension(850,60));
		voltar.setFont(new Font("Verdana",1,20));
		painel.add(voltar);
		
		sair.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				registro.login_name = usuario.login_name;
				registro.insereRegistro(9003, "");
				
				registro.login_name = usuario.login_name;
        		registro.insereRegistro(1002, "");
				
				frame.dispose();
			}
		});
		
		voltar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				registro.login_name = usuario.login_name;
				registro.insereRegistro(9004, "");
				
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