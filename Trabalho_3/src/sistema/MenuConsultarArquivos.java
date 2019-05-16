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
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.Signature;
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
			- numero de linhas dos arquivos
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
				
				String arquivoIndex = "";
				byte[] arquivoIndexBytes = null;
				try {
					// Decripta arquivoIndex usando a chave simetrica gerada
					cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
					cipher.init(Cipher.DECRYPT_MODE, chaveSimetrica);
			    	arquivoIndexBytes = cipher.doFinal(cifraIndex);
					arquivoIndex = new String(arquivoIndexBytes, "UTF8");
					System.out.println( arquivoIndex );
				} catch (Exception e1) {
					e1.printStackTrace();
				}
		    	
				// decriptar assinatura com chave publica do user01 que ta no certificado e obter digest original
				String digestAssinatura = null;
				try {
					cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
					cipher.init(Cipher.DECRYPT_MODE, usuario.chavePublica);
					byte [] digestAssinaturaBytes = cipher.doFinal(assinaturaIndex);

					StringBuilder sb = new StringBuilder();
					for(byte b:digestAssinaturaBytes){
		        		sb.append(String.format("%02X", b));	
		        	}
		        	digestAssinatura = sb.toString();

				} catch (Exception e1) {
					e1.printStackTrace();
				}
				
				// gerar novo digest do arquivoIndex
				String digestArquivo = null;
				try {
					MessageDigest md = MessageDigest.getInstance("MD5");
					md.update(arquivoIndexBytes);
					byte [] digestArquivoBytes = md.digest();

					StringBuilder sb = new StringBuilder();
					for(byte b:digestArquivoBytes){
		        		sb.append(String.format("%02X", b));	
		        	}
		        	digestArquivo = sb.toString();

				} catch (Exception e1) {
					e1.printStackTrace();
				}
				
				// comparar digest do arquivo Index com digest obtido pela assinatura
				System.out.println(digestAssinatura);
				System.out.println(digestArquivo);
				System.out.println(digestArquivo.equals(digestAssinatura));
				
				// se estiver okk, listar linhas do arquivoIndex
				if (! digestArquivo.equals(digestAssinatura) ){
					JOptionPane.showMessageDialog(frame, "Teste de integridade e autenticidade falhou! Você não têm permissão para acessar esta pasta de arquivos!");
					return;
				}

				String linha1 = arquivoIndex.substring(0, arquivoIndex.indexOf("\n"));
				String linha2 = arquivoIndex.substring(arquivoIndex.indexOf("\n"));
				
				painel.remove(caminho);
				painel.remove(caminhoPasta);
				painel.remove(listar);
				painel.remove(voltar);
				
				JLabel arqlinha1 = new JLabel();
				arqlinha1.setText(linha1);
				arqlinha1.setFont(new Font("Verdana",1,20));
				arqlinha1.setPreferredSize(new Dimension(850,40));
				painel.add(arqlinha1);
				
				JLabel arqlinha2 = new JLabel();
				arqlinha2.setText(linha2);
				arqlinha2.setFont(new Font("Verdana",1,20));
				arqlinha2.setPreferredSize(new Dimension(850,40));
				painel.add(arqlinha2);
				
				painel.add(voltar);
				
				frame.revalidate();
				frame.repaint();
				
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