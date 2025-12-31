package br.com.sistema.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import br.com.sistema.model.Item;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

    // Método para buscar itens pelo nome (contendo o texto e ignorando case)
    List<Item> findByNomeContainingIgnoreCase(String nome);

    // Método para buscar itens por quantidade exata
    List<Item> findByQuantidade(String quantidade);

    // Método para buscar itens por nome exato
    Item findByNomeEquals(String nome);

    // Método para verificar se existe um item com determinado nome
    boolean existsByNome(String nome);

    // Método para contar quantos itens têm uma determinada quantidade
    long countByQuantidade(String quantidade);

    // Método personalizado com JPQL para buscar itens com nome ou quantidade contendo o texto
    @org.springframework.data.jpa.repository.Query("SELECT i FROM Item i WHERE LOWER(i.nome) LIKE LOWER(CONCAT('%', :termo, '%')) OR LOWER(i.quantidade) LIKE LOWER(CONCAT('%', :termo, '%'))")
    List<Item> buscarPorTermoEmNomeOuQuantidade(String termo);
}
