package br.com.sistema.service;

import java.util.Map;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.stereotype.Service;

@Service
public class ReceitaService {

	private final ChatModel chatModel;

	public ReceitaService(ChatModel chatModel) {
		this.chatModel = chatModel;
	}
	
	public String criarReceita(String ingredientes, String tipoCozinha, String restricoesDieta) {
		
		var template = """
				Eu preciso criar uma receita usando os ingredientes a seguir: {ingredientes}
				O tipo de cozinha que eu prefiro é {tipoCozinha}
				Por favor, considere as restrições da dieta: {restricoesDieta}
				Por favor, me forneça uma receita detalhada incluindo título, lista de ingredientes e intruções de preparo
				""";
		
		PromptTemplate promptTemplate = new PromptTemplate(template);
		Map<String, Object> params = Map.of(
				"ingredientes", ingredientes,
				"tipoCozinha", tipoCozinha,
				"restricoesDieta", restricoesDieta );
		
		Prompt prompt = promptTemplate.create(params);
		return chatModel.call(prompt).getResult().getOutput().getText();
	}
	
}
