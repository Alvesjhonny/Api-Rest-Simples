package com.example.demo.models;

import jakarta.persistence.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.RepresentationModel;

import java.io.Serializable;
import java.math.BigDecimal;


@Entity
@Table(name = "TB_PRODUTO")
public class ProdutoModel extends RepresentationModel<ProdutoModel> implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Autowired
    private long idProduto;
    @Autowired
    private String nome;
    @Autowired
    private BigDecimal valor;

    public long getIdProduto() {
        return idProduto;
    }

    public void setIdProduto(long idProduto){
        this.idProduto = idProduto;
    }

    public String getNome(){
        return nome;
    }

    public void setNome(String nome){
        this.nome = nome;
    }

    public BigDecimal getValor(){
        return valor;
    }

    public void setValor(BigDecimal valor){
        this.valor = valor;
    }

}
