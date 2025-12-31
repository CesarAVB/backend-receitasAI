package br.com.sistema.controller;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import br.com.sistema.dto.ItemRequestDTO;
import br.com.sistema.dto.ItemResponseDTO;
import br.com.sistema.model.Item;
import br.com.sistema.service.ItemService;

@RestController
@RequestMapping("/api/v1/receita/itens")
public class ItemController {

    private final ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @GetMapping
    public ResponseEntity<List<ItemResponseDTO>> listarTodos() {
        List<ItemResponseDTO> dtos = itemService.listar().stream()
            .map(item -> new ItemResponseDTO(item.getId(), item.getNome(), item.getQuantidade()))
            .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ItemResponseDTO> buscarPorId(@PathVariable Long id) {
        return itemService.buscarPorId(id)
            .map(item -> new ItemResponseDTO(item.getId(), item.getNome(), item.getQuantidade()))
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<ItemResponseDTO> salvar(@RequestBody ItemRequestDTO dto) {
        Item item = new Item();
        item.setNome(dto.nome());
        item.setQuantidade(dto.quantidade());

        Item itemSalvo = itemService.salvar(item);
        ItemResponseDTO responseDTO = new ItemResponseDTO(
            itemSalvo.getId(), 
            itemSalvo.getNome(), 
            itemSalvo.getQuantidade()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ItemResponseDTO> atualizar(
            @PathVariable Long id, 
            @RequestBody ItemRequestDTO dto) {
        try {
            Item item = new Item();
            item.setNome(dto.nome());
            item.setQuantidade(dto.quantidade());

            Item itemAtualizado = itemService.atualizar(id, item);
            ItemResponseDTO responseDTO = new ItemResponseDTO(
                itemAtualizado.getId(), 
                itemAtualizado.getNome(), 
                itemAtualizado.getQuantidade()
            );

            return ResponseEntity.ok(responseDTO);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        try {
            itemService.deletar(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
