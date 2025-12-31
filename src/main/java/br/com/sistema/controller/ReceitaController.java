package br.com.sistema.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import br.com.sistema.dto.ReceitaResponseDTO;
import br.com.sistema.model.Item;
import br.com.sistema.service.ItemService;
import br.com.sistema.service.ReceitaService;

@RestController
@RequestMapping("/api/v1/receita")
public class ReceitaController {
	private ReceitaService receitaService;
	private ItemService itemService;

	public ReceitaController(ReceitaService receitaService, ItemService itemService) {
		this.receitaService = receitaService;
		this.itemService = itemService;
	}

	@GetMapping()
	public ResponseEntity<ReceitaResponseDTO> criarReceita() {

		String tipoCozinha = "any";
		String restricoesDieta = "none";
		List<Item> itens = itemService.listar();
		String ingredientes = itens.stream().map(Item::getNome).collect(Collectors.joining(", "));

		// Obter o texto da receita do serviço existente
		String textoReceita = receitaService.criarReceita(ingredientes, tipoCozinha, restricoesDieta);
		
		//System.out.println(textoReceita);

		// Converter o texto para o formato estruturado usando o record
		ReceitaResponseDTO receitaResponse = converterTextoParaReceita(textoReceita, itens);

		return ResponseEntity.ok(receitaResponse);
	}

	private ReceitaResponseDTO converterTextoParaReceita(String textoReceita, List<Item> itensDisponiveis) {
		// Extrair o título
		Pattern patternTitulo = Pattern.compile("(?:### (?:Receita: )?|\\*\\*(?:Título: )?)([^*#]+)(?:\\*\\*|$)", Pattern.MULTILINE);
		Matcher matcherTitulo = patternTitulo.matcher(textoReceita);
		String titulo = matcherTitulo.find() ? matcherTitulo.group(1).trim() : "Nova Receita";

		// Gerar ID único
		String id = UUID.randomUUID().toString();

		// Extrair as instruções
		List<String> instrucoes = new ArrayList<>();

		// Padrão mais flexível para capturar a seção de instruções
		Pattern patternInstrucoes = Pattern.compile("#### (?:Instruções de Preparo|Modo de Preparo|Preparo|Instruções)(?::|\\s*)(.*?)(?=###|$)", Pattern.DOTALL);
		Matcher matcherInstrucoes = patternInstrucoes.matcher(textoReceita);

		if (matcherInstrucoes.find()) {
			String textoInstrucoes = matcherInstrucoes.group(1);

			// Primeiro, tenta extrair os passos formatados com subtítulos (##### Passo X:)
			Pattern passoTituloPattern = Pattern.compile("##### (.*?)(?=##### |$)", Pattern.DOTALL);
			Matcher passoTituloMatcher = passoTituloPattern.matcher(textoInstrucoes);

			boolean encontrouPassosTitulo = false;

			while (passoTituloMatcher.find()) {
				encontrouPassosTitulo = true;
				String blocoPassoCompleto = passoTituloMatcher.group(1).trim();
				instrucoes.add(blocoPassoCompleto);
			}

			// Se não encontrou passos com subtítulos, tenta o formato numérico simples
			if (!encontrouPassosTitulo) {
				Pattern passoPattern = Pattern.compile("(\\d+)\\.(.*?)(?=\\d+\\.|$)", Pattern.DOTALL);
				Matcher passoMatcher = passoPattern.matcher(textoInstrucoes);

				while (passoMatcher.find()) {
					String passo = passoMatcher.group(2).trim();
					if (!passo.isEmpty()) {
						instrucoes.add(passo);
					}
				}

				// Se ainda não encontrou passos, tenta dividir por linhas
				if (instrucoes.isEmpty()) {
					String[] linhas = textoInstrucoes.split("\n");
					for (String linha : linhas) {
						linha = linha.trim();
						if (linha.matches("\\d+\\..*")) {
							instrucoes.add(linha);
						}
					}
				}
			}
		}

		// Se ainda não encontrou instruções, tenta um padrão mais simples
		if (instrucoes.isEmpty()) {
			Pattern simplePattern = Pattern.compile("\\d+\\.\\s*(.*?)(?=\\d+\\.|$)", Pattern.DOTALL);
			Matcher simpleMatcher = simplePattern.matcher(textoReceita);

			while (simpleMatcher.find()) {
				String instrucao = simpleMatcher.group(1).trim();
				if (!instrucao.isEmpty()) {
					instrucoes.add(instrucao);
				}
			}
		}

		// System.out.println("=========================");
		// System.out.println(id);
		// System.out.println(titulo);
		// System.out.println(itensDisponiveis);
		// System.out.println(instrucoes);
		// System.out.println("=========================");

		// Criar e retornar o DTO usando o construtor do record
		return new ReceitaResponseDTO(id, titulo, itensDisponiveis, instrucoes);
	}

}
