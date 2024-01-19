package org.acme.models;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Cliente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nome;
    private String cpf;
    public Cliente(String nome, String cpf) {
        this.nome = nome;
        this.cpf = cpf;
    }
    @Override
    public String toString() {
        return "\n  Nome: " + nome + "\n  CPF: " + cpf;
    }
}