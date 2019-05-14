package sistema;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.Connection;
import java.util.Scanner;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import Interface.MenuFrame;
import banco.*;

public class MenuConsultarArquivos{
	private static MenuConsultarArquivos menuConsultarArquivos = null;
	private Scanner scanner;
	Connection conn;
	MenuFrame frame;
	
	private MenuConsultarArquivos() {
		conn = conexaoBD.getInstance().getConnection();
		frame = MenuFrame.getInstance();
	}

	/* SINGLETON */
	public static MenuConsultarArquivos getInstance() {
		if (menuConsultarArquivos == null)
			menuConsultarArquivos = new MenuConsultarArquivos();
		return menuConsultarArquivos;
	}


	protected void iniciarMenuConsultarArquivos(Usuario usuario){
		JPanel painel = new JPanel();

		/*FALTA
			- decriptar o env com a chave privada etc
		*/
		
		System.out.println("");
		System.out.println("#### MENU CONSULTAR ARQUIVOS SECRETOS ####");
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
		
		JLabel caminho = new JLabel();
		caminho.setText("– Caminho da pasta: <max 255 caracteres>");
		caminho.setFont(new Font("Verdana",1,20));
		caminho.setPreferredSize(new Dimension(850,50));
		painel.add(caminho);
		
		JTextArea caminhoPasta = new JTextArea(4,10);
		caminhoPasta.setEditable(true);
		caminhoPasta.setFont(new Font("Verdana",1,20));
		caminhoPasta.setPreferredSize(new Dimension(850,50));
		caminhoPasta.setLineWrap(true);
		painel.add(caminhoPasta);
		
		JButton listar = new JButton("Listar");
		listar.setPreferredSize(new Dimension(850,60));
		listar.setFont(new Font("Verdana",1,20));
		painel.add(listar);
		
		JButton voltar = new JButton("Voltar para Menu Principal");
		voltar.setPreferredSize(new Dimension(850,60));
		voltar.setFont(new Font("Verdana",1,20));
		painel.add(voltar);
		
		listar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				byte [] envelopeIndex = null;
				byte [] cifraIndex = null;
				byte [] assinaturaIndex = null;
				
				// Abre arquivos de indice
				try{
					Path path = Paths.get(caminhoPasta.getText()+"\\index.env");
					envelopeIndex = Files.readAllBytes(path);
					path = Paths.get(caminhoPasta.getText()+"\\index.enc");
					cifraIndex = Files.readAllBytes(path);
					path = Paths.get(caminhoPasta.getText()+"\\index.asd");
					assinaturaIndex = Files.readAllBytes(path);
				}
				catch(IOException IOe){
					System.err.println(IOe);
					System.out.println("Erro ao abrir a pasta de indices");
					System.exit(1);
				}
				
				
				// decripta envelope digital
				Cipher cipher;
				byte [] semente = null;
				try {
					cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
					cipher.init(Cipher.DECRYPT_MODE, usuario.chavePrivada);
					semente = cipher.doFinal(envelopeIndex);
				} catch (Exception e1) {
					e1.printStackTrace();
				}
				
				
				// Inicializa o gerador PRNG com a frase secreta (semente)
				Key chaveSimetrica = null;
				
				try {
					SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
					secureRandom.setSeed(semente);
					// Gera a chave simetrica apartir do gerador inicializado anteriormente
					KeyGenerator keyGen = KeyGenerator.getInstance("DES");
					keyGen.init(56, secureRandom);
					chaveSimetrica = keyGen.generateKey();
					
				} catch (Exception e1) {
					e1.printStackTrace();
				}
				
				
				
				try {
					// Decripta chave privada (codificada em B64) usando a chave simetrica gerada
					cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
					cipher.init(Cipher.DECRYPT_MODE, chaveSimetrica);
			    	byte[] arquivoIndexBytes = cipher.doFinal(cifraIndex);
			    	String arquivoIndex;
					arquivoIndex = new String(arquivoIndexBytes, "UTF8");
					System.out.println( arquivoIndex );
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
		    	
				// decriptar assinatura com chave publica do user01 que ta no certificado
				
				// gerar digest do arquivoIndex
				
				// comparar digest do arquivo Index com digest obtido pela assinatura
				
				// se estiver okk, listar linhas do arquivoIndex
				
				// quando clicar em uma linha, decriptar envelope, arquivo e assinatura, verificar integridade e controle de acesso
				
				// caso positivo, guardar arquivo decriptado em novo arquivo
				
				
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