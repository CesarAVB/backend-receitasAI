package br.com.sistema.service;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import br.com.sistema.model.Item;
import br.com.sistema.repository.ItemRepository;

@Service
public class ItemService {
	
    private final ItemRepository itemRepository;

    public ItemService(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    @Transactional
    public Item salvar(Item item) {
        return itemRepository.save(item);
    }

    public List<Item> listar() {
        return itemRepository.findAll();
    }

    public Optional<Item> buscarPorId(Long id) {
        return itemRepository.findById(id);
    }

    @Transactional
    public Item atualizar(Long id, Item item) {
        if (itemRepository.existsById(id)) {
            item.setId(id);
            return itemRepository.save(item);
        }
        throw new RuntimeException("Item não encontrado com ID: " + id);
    }

    @Transactional
    public void deletar(Long id) {
        if (itemRepository.existsById(id)) {
            itemRepository.deleteById(id);
        } else {
            throw new RuntimeException("Item não encontrado com ID: " + id);
        }
    }

    public List<Item> buscarPorNome(String nome) {
        return itemRepository.findByNomeContainingIgnoreCase(nome);
    }
}
