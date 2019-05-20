package sistema;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Key;
import java.security.SecureRandom;
import java.security.Signature;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;

import Interface.MenuFrame;
import banco.*;

public class MenuConsultarArquivos{
	private static MenuConsultarArquivos menuConsultarArquivos = null;
	private MouseAdapter cliqueArquivo = null;
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
		Registro registro = new Registro();
		registro.login_name = usuario.login_name;
		registro.insereRegistro(8001, "");
	

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
		numAcessos.setText("Total de consultas do usuário: "+usuario.numero_consultas);
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
				
				registro.login_name = usuario.login_name;
				registro.insereRegistro(8003, "");
								
				// DECRIPTA INDEX
				String arquivoIndex;
				try {
					arquivoIndex = new String (decriptaArquivo(caminhoPasta.getText(), "index", usuario), "UTF8" );
				}
				catch (Exception ex) {
					JOptionPane.showMessageDialog(frame, "Erro ao abrir e/ou decriptar pasta fornecida!");
					return;
				}				
					
				if (arquivoIndex == null)
					return;
				
				painel.remove(caminho);
				painel.remove(caminhoPasta);
				painel.remove(listar);
				painel.remove(voltar);

				String linha = "";
				int inicioLinha = 0;
				int fimLinha = arquivoIndex.length()-1;

				while (arquivoIndex.indexOf("\n", inicioLinha) != -1){
					fimLinha = arquivoIndex.indexOf("\n", inicioLinha);
					linha = arquivoIndex.substring(inicioLinha, fimLinha);
					inicioLinha = fimLinha + 1;

					JLabel arquivo = new JLabel(linha, SwingConstants.CENTER);
					arquivo.setFont(new Font("Verdana",1,20));
					arquivo.setPreferredSize(new Dimension(850,40));
					arquivo.addMouseListener(cliqueArquivo);
					painel.add(arquivo);
				}
				
				registro.insereRegistro(8009, "");

				painel.add(voltar);

				usuario.incrementaConsultasUsuario();
				numAcessos.setText("Total de consultas do usuário: "+usuario.numero_consultas);
				
				frame.revalidate();
				frame.repaint();
				
			}
		});
		
		voltar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				registro.login_name = usuario.login_name;
				registro.insereRegistro(8002, "");
				
				frame.remove(painel);
				frame.revalidate();
				frame.repaint();
				MenuPrincipal.getInstance().iniciarMenuPrincipal(usuario);
			}
		});
		
		cliqueArquivo = new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent event) {

				JLabel label = (JLabel) event.getSource();
				String [] linhaArquivo = label.getText().split(" "); 

				String nomeCodigo = linhaArquivo[0];
				String nomeSecreto = linhaArquivo[1];
				String donoArquivo = linhaArquivo[2];
				int grupoArquivo;

				registro.insereRegistro(8010, nomeCodigo);

				if ( linhaArquivo[3].equals("usuario") )
					grupoArquivo = 2;
				else
					grupoArquivo = 1;

				if ( !donoArquivo.equals(usuario.login_name) && (grupoArquivo != usuario.grupo)){
					JOptionPane.showMessageDialog(frame, "Você não tem permissão para acessar este arquivo!");
					
					registro.insereRegistro(8012, nomeCodigo);
					
					return;	
				}
				
				registro.login_name = usuario.login_name;
				registro.insereRegistro(8011, nomeCodigo);

				byte [] conteudoArquivo = decriptaArquivo(caminhoPasta.getText(), nomeCodigo, usuario);
				
				if(conteudoArquivo == null)	
					return;
				
				
				try (FileOutputStream stream = new FileOutputStream(caminhoPasta.getText()+"\\"+nomeSecreto)) {
				    stream.write(conteudoArquivo);
				}
				catch(Exception e){
					JOptionPane.showMessageDialog(frame, "Erro ao escrever conteudo do arquivo");					
					return;
				}
				

				JOptionPane.showMessageDialog(frame, "Arquivo decriptado com sucesso!");				
				return;

			}
		};

		frame.getContentPane().add(painel);
		frame.revalidate();
		frame.repaint();

	}



	private byte [] decriptaArquivo(String caminhoPasta, String nomeArquivo, Usuario usuario){
		
		Registro registro = new Registro();
		registro.login_name = usuario.login_name;

		byte [] envelope = null;
		byte [] arquivoCifrado = null;
		byte [] assinatura = null;
		byte[] arquivoBytes = null;

		String caminho = caminhoPasta+"\\"+nomeArquivo;

		// Abre arquivos de indice
		try{
			Path path = Paths.get(caminho+".env");
			envelope = Files.readAllBytes(path);
			path = Paths.get(caminho+".enc");
			arquivoCifrado = Files.readAllBytes(path);
			path = Paths.get(caminho+".asd");
			assinatura = Files.readAllBytes(path);
		}
		catch(IOException IOe){
			System.err.println(IOe);

			registro.insereRegistro(8004, nomeArquivo);

			JOptionPane.showMessageDialog(frame, "Erro ao abrir pasta fornecida!");
			return null;
		}
		
		// decripta envelope digital
		Cipher cipher;
		byte [] semente = null;
		try {
			cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
			cipher.init(Cipher.DECRYPT_MODE, usuario.chavePrivada);
			semente = cipher.doFinal(envelope);
		} catch (Exception e1) {
			e1.printStackTrace();


			if (nomeArquivo.equals("index"))
				registro.insereRegistro(8007, nomeArquivo);
			else
				registro.insereRegistro(8015, nomeArquivo);
			
			JOptionPane.showMessageDialog(frame, "A chave privada fornecida não é válida para decriptar este arquivo.");
			return null;
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
			
			if (nomeArquivo.equals("index"))
				registro.insereRegistro(8007, nomeArquivo);
			else
				registro.insereRegistro(8015, nomeArquivo);
			
			JOptionPane.showMessageDialog(frame, "Envelope digital inválido.");
			return null;
		}

		try {
			// Decripta arquivoIndex usando a chave simetrica gerada
			cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
			cipher.init(Cipher.DECRYPT_MODE, chaveSimetrica);
	    	arquivoBytes = cipher.doFinal(arquivoCifrado);
	    	
		} catch (Exception e1) {
			e1.printStackTrace();
			
			if (nomeArquivo.equals("index"))
				registro.insereRegistro(8007, nomeArquivo);
			else
				registro.insereRegistro(8015, nomeArquivo);
			
			JOptionPane.showMessageDialog(frame, "Envelope digital inválido. Não foi possível decriptar o arquivo.");
			return null;
		}

		if (nomeArquivo.equals("index"))
			registro.insereRegistro(8005, nomeArquivo);
		else
			registro.insereRegistro(8013, nomeArquivo);

		// decriptar assinatura com chave publica do user01 que ta no certificado e obter digest original
		
		try {
			Signature sig = Signature.getInstance("MD5withRSA");
			sig.initVerify(usuario.chavePublica);
			sig.update(arquivoBytes);
			
			if (! sig.verify(assinatura)) {
				JOptionPane.showMessageDialog(frame, "Teste de integridade e autenticidade falhou! Você não têm permissão para acessar esta pasta de arquivos!");
				
				if (nomeArquivo.equals("index"))
					registro.insereRegistro(8008, nomeArquivo);
				else
					registro.insereRegistro(8016, nomeArquivo);
				
				return null;
			}
			
			System.out.println("Assinatura verificada! Arquivo decriptado corretamente!");

		} catch (Exception e1) {
			e1.printStackTrace();
			
			if (nomeArquivo.equals("index"))
				registro.insereRegistro(8008, nomeArquivo);
			else
				registro.insereRegistro(8016, nomeArquivo);
			
			JOptionPane.showMessageDialog(frame, "Assinatura digital inválida.");
			return null;
		}

		if (nomeArquivo.equals("index")){
			registro.insereRegistro(8006, nomeArquivo);
		}
		else{
			registro.insereRegistro(8014, nomeArquivo);
		}

		return arquivoBytes;

	}

}