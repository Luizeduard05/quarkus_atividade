package org.acme.services;


import lombok.Data;
import org.acme.GlobalExceptionHandler.ContaInvalidaException;
import org.acme.GlobalExceptionHandler.SaldoInsuficienteException;
import org.acme.models.ContaCorrente;

import java.util.List;

public interface ContaCorrenteService {
    ContaCorrente criarConta(String nome, String cpf) throws ContaInvalidaException;
    ContaCorrente getContaPorNumero(String numeroConta);
    void depositar(String numeroConta, double valor) throws ContaInvalidaException;
    void sacar(String numeroConta, double valor) throws ContaInvalidaException, SaldoInsuficienteException;
    void transferir(String contaOrigem, String contaDestino, double valor) throws ContaInvalidaException, SaldoInsuficienteException;

}
