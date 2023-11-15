package org.acme.service;

import org.acme.GlobalExceptionHandler.ContaInvalidaException;
import org.acme.GlobalExceptionHandler.SaldoInsuficienteException;
import org.acme.model.ContaCorrente;

import java.util.List;

public interface ContaCorrenteService {

    ContaCorrente getContaPorNumero(String numConta);

    boolean cpfExistente(String cpf);

    boolean excluirConta(String numConta);

    List<ContaCorrente> listarContas();

    ContaCorrente criarConta(String nome, String cpf) throws ContaInvalidaException;

    void depositar(String numConta, double valor) throws ContaInvalidaException;

    void sacar(String numConta, double valor) throws ContaInvalidaException, SaldoInsuficienteException;

    void transferir(String contaOrigem, String contaDestino, double valor)
            throws ContaInvalidaException, SaldoInsuficienteException;
}

