package com.example.demo.models;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class ProdutoModelTest {

    @Test
    void testeAtributosDaClasseProdutoModel() {
        ProdutoModel produto = new ProdutoModel();
        produto.setIdProduto(155465465415L);
        produto.setNome("Macarrao");
        produto.setValor(BigDecimal.valueOf(12.80));

        assertEquals(155465465415L, produto.getIdProduto());
        assertEquals("Macarrao", produto.getNome());
        assertEquals(BigDecimal.valueOf(12.80), produto.getValor());

    }


}