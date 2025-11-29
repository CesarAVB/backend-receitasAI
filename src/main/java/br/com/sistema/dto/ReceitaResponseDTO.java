package br.com.sistema.dto;

import java.util.List;
import br.com.sistema.model.Item;

public record ReceitaResponseDTO(
    String id,
    String title,
    List<Item> ingredients,
    List<String> instructions
) {}
