package com.example.demo.controllers;

import com.example.demo.models.ProdutoModel;
import com.example.demo.repositories.ProdutoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.hateoas.MediaTypes;


import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@AutoConfigureMockMvc
@SpringBootTest
class ProdutoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProdutoRepository produtoRepository;

    @BeforeEach
    public void setup() {
        produtoRepository.deleteAll();

        ProdutoModel produto1 = new ProdutoModel();
        produto1.setNome("produto1");
        produto1.setValor(BigDecimal.valueOf(25.80));
        ProdutoModel produto2 = new ProdutoModel();
        produto2.setNome("produto2");
        produto2.setValor(BigDecimal.valueOf(98.52));

        produtoRepository.saveAll(List.of(produto1, produto2));
    }

    @Test
    public void testDeRetornoDeTodosProdutos() throws Exception {
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/produtos")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].nome").value("produto1"))
                .andExpect(jsonPath("$[0].valor").value(BigDecimal.valueOf(25.80)))
                .andExpect(jsonPath("$[0].links[0].href").exists())
                .andExpect(jsonPath("$[1].nome").value("produto2"))
                .andExpect(jsonPath("$[1].valor").value(BigDecimal.valueOf(98.52)))
                .andExpect(jsonPath("$[1].links[0].href").exists())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        System.out.println("Response content: " + content);
    }

    @Test
    public void testQuandoFalhaORetornoDosProdutos() throws Exception {
        produtoRepository.deleteAll();

        mockMvc.perform(MockMvcRequestBuilders.get("/produtos")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void testeRetornoApenasUmProduto() throws Exception {
        produtoRepository.deleteAll();

        ProdutoModel produto0 = new ProdutoModel();

        produto0.setNome("produto0");
        produto0.setValor(BigDecimal.valueOf(52.63));
        ProdutoModel savedProduto = produtoRepository.save(produto0);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/produtos/" + savedProduto.getIdProduto())
                        .accept(MediaTypes.HAL_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaTypes.HAL_JSON))
                .andExpect(jsonPath("$.idProduto").value(savedProduto.getIdProduto()))
                .andExpect(jsonPath("$.nome").value("produto0"))
                .andExpect(jsonPath("$.valor").value(BigDecimal.valueOf(52.63)))
                .andExpect(jsonPath("$._links['Lista de Produtos'].href").exists())  // Verifica a existência do link HATEOAS
                .andReturn();

        String content = result.getResponse().getContentAsString();
        System.out.println("Response content: " + content);
    }


    @Test
    void testeQuandoFalhaORetornoDeUmSóProduto() throws Exception {
        produtoRepository.deleteAll();

        mockMvc.perform(MockMvcRequestBuilders.get("/produtos/999")
                .accept(MediaTypes.HAL_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void testeParaQuandoForAdicionarAlgumProdutoComOMetodoPost() throws Exception {
        ProdutoModel produtoAdicionado = new ProdutoModel();
        produtoAdicionado.setNome("Bicicleta");
        produtoAdicionado.setValor(BigDecimal.valueOf(12.90));

        String jsonProduto = "{\"nome\":\"Bicicleta\",\"valor\":12.90}";

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/produtos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonProduto))
                .andExpect(status().isCreated())
                .andReturn();

        int status = result.getResponse().getStatus();
        assertEquals(201, status);
    }

    @Test
    void testeParaQuandoForRemoverUmProdutoComOMetodoDelet() throws Exception{
        ProdutoModel produtoDeletado = new ProdutoModel();
        produtoDeletado.setNome("Onibus");
        produtoDeletado.setValor(BigDecimal.valueOf(20000.00));

        ProdutoModel produtosalvo = produtoRepository.save(produtoDeletado);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.delete("/produtos/" + produtosalvo.getIdProduto())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        int status = result.getResponse().getStatus();
        assertEquals(200, status);
    }

    @Test
    void testeParaQaundoOMetodoDeleteRetornarNotFound() throws Exception{
        produtoRepository.deleteAll();

        mockMvc.perform(MockMvcRequestBuilders.delete("/produtos/872487")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testeParaQuandoForAtualizarUmProdutoComOMetodoPut() throws Exception {

        ProdutoModel produtoAtualizado = new ProdutoModel();
        produtoAtualizado.setNome("Bicicleta");
        produtoAtualizado.setValor(BigDecimal.valueOf(299.90));

        ProdutoModel produtoSalvo = produtoRepository.save(produtoAtualizado);

        produtoSalvo.setNome("Bicicleta Novo Nome");
        produtoSalvo.setValor(BigDecimal.valueOf(350.00));

        String jsonProduto = "{\"nome\":\"" + produtoSalvo.getNome() + "\",\"valor\":" + produtoSalvo.getValor() + "}";

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.put("/produtos/" + produtoSalvo.getIdProduto())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonProduto))
                .andExpect(status().isOk())
                .andReturn();

        int status = result.getResponse().getStatus();
        assertEquals(200, status);


    }

    @Test
    void TesteParaQuandoAAtualizacaoComOmetodoPutFalhar () throws Exception {

        long idProdutoInexistente = 999L;
        String jsonProdutoInexistente = "{\"nome\":\"Produto Inexistente\",\"valor\":100.00}";

        mockMvc.perform(MockMvcRequestBuilders.put("/produtos/" + idProdutoInexistente)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonProdutoInexistente))
                .andExpect(status().isNotFound());
    }

}