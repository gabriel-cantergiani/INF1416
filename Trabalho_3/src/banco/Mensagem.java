package banco;

import java.util.HashMap;
import java.util.Map;

public class Mensagem {
	public Map<Integer,String> map = new HashMap<Integer,String>();
	
	public Map<Integer, String> criaMap() {
		map.put(1001, "Sistema iniciado.");
		map.put(1002, "Sistema encerrado.");
		map.put(2001, "Autenticação etapa 1 iniciada.");
		map.put(2002, "Autenticação etapa 1 encerrada.");
		map.put(2003, "Login name <login_name> identificado com acesso liberado.");
		map.put(2004, "Login name <login_name> identificado com acesso bloqueado.");
		map.put(2005, "Login name <login_name> não identificado.");
		map.put(3001, "Autenticação etapa 2 iniciada para <login_name>.");
		map.put(3002, "Autenticação etapa 2 encerrada para <login_name>.");
		map.put(3003, "Senha pessoal verificada positivamente para <login_name>.");
		map.put(3004, "Primeiro erro da senha pessoal contabilizado para <login_name>.");
		map.put(3005, "Segundo erro da senha pessoal contabilizado para <login_name>.");
		map.put(3006, "Terceiro erro da senha pessoal contabilizado para <login_name>.");
		map.put(3007, "Acesso do usuario <login_name> bloqueado pela autenticação etapa 2.");
		map.put(4001, "Autenticação etapa 3 iniciada para <login_name>.");
		map.put(4002, "Autenticação etapa 3 encerrada para <login_name>.");
		map.put(4003, "Chave privada verificada positivamente para <login_name>.");
		map.put(4004, "Chave privada verificada negativamente para <login_name> (caminho inválido).");
		map.put(4005, "Chave privada verificada negativamente para <login_name> (frase secreta inválida).");
		map.put(4006, "Chave privada verificada negativamente para <login_name> (assinatura digital inválida).");
		map.put(4007, "Acesso do usuario <login_name> bloqueado pela autenticação etapa 3.");
		map.put(5001, "Tela principal apresentada para <login_name>.");
		map.put(5002, "Opção 1 do menu principal selecionada por <login_name>.");
		map.put(5003, "Opção 2 do menu principal selecionada por <login_name>.");
		map.put(5004, "Opção 3 do menu principal selecionada por <login_name>.");
		map.put(5005, "Opção 4 do menu principal selecionada por <login_name>.");
		map.put(6001, "Tela de cadastro apresentada para <login_name>.");
		map.put(6002, "Botão cadastrar pressionado por <login_name>.");
		map.put(6003, "Senha pessoal inválida fornecida por <login_name>.");
		map.put(6004, "Caminho do certificado digital inválido fornecido por <login_name>.");
		map.put(6005, "Confirmação de dados aceita por <login_name>.");
		map.put(6006, "Confirmação de dados rejeitada por <login_name>.");
		map.put(6007, "Botão voltar de cadastro para o menu principal pressionado por <login_name>.");
		map.put(7001, "Tela de alteração da senha pessoal e certificado apresentada para <login_name>.");
		map.put(7002, "Senha pessoal inválida fornecida por <login_name>.");
		map.put(7003, "Caminho do certificado digital inválido fornecido por <login_name>.");
		map.put(7004, "Confirmação de dados aceita por <login_name>.");
		map.put(7005, "Confirmação de dados rejeitada por <login_name>.");
		map.put(7006, "Botão voltar de carregamento para o menu principal pressionado por <login_name>.");
		map.put(8001, "Tela de consulta de arquivos secretos apresentada para <login_name>.");
		map.put(8002, "Botão voltar de consulta para o menu principal pressionado por <login_name>.");
		map.put(8003, "Botão Listar de consulta pressionado por <login_name>.");
		map.put(8004, "Caminho de pasta inválido fornecido por <login_name>.");
		map.put(8005, "Arquivo de índice decriptado com sucesso para <login_name>.");
		map.put(8006, "Arquivo de índice verificado (integridade e autenticidade) com sucesso para <login_name>.");
		map.put(8007, "Falha na decriptação do arquivo de índice para <login_name>.");
		map.put(8008, "Falha na verificação (integridade e autenticidade) do arquivo de índice para <login_name>.");
		map.put(8009, "Lista de arquivos presentes no índice apresentada para <login_name>.");
		map.put(8010, "Arquivo <arq_name> selecionado por <login_name> para decriptação.");
		map.put(8011, "Acesso permitido ao arquivo <arq_name> para <login_name>.");
		map.put(8012, "Acesso negado ao arquivo <arq_name> para <login_name>.");
		map.put(8013, "Arquivo <arq_name> decriptado com sucesso para <login_name>.");
		map.put(8014, "Arquivo <arq_name> verificado (integridade e autenticidade) com sucesso para <login_name>.");
		map.put(8015, "Falha na decriptação do arquivo <arq_name> para <login_name>.");
		map.put(8016, "Falha na verificação (integridade e autenticidade) do arquivo <arq_name> para <login_name>.");
		map.put(9001, "Tela de saída apresentada para <login_name>.");
		map.put(9002, "Saída não liberada por falta de one-time password para <login_name>.");
		map.put(9003, "Botão sair pressionado por <login_name>.");
		map.put(9004, "Botão voltar de sair para o menu principal pressionado por <login_name>.");
		
		return map;
	}
}
