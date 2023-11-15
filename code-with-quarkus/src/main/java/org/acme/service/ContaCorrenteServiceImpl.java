package org.acme.service;

import org.acme.GlobalExceptionHandler.ContaInvalidaException;
import org.acme.GlobalExceptionHandler.SaldoInsuficienteException;
import org.acme.model.Cliente;
import org.acme.model.ContaBancaria;
import org.acme.model.ContaCorrente;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class ContaCorrenteServiceImpl implements ContaCorrenteService {

    private final List<ContaCorrente> contasCorrentes;
    private static final AtomicInteger contadorContas = new AtomicInteger(1);

    public ContaCorrenteServiceImpl(List<ContaCorrente> contasCorrentes) {
        this.contasCorrentes = contasCorrentes;
    }

    @Override
    public ContaCorrente criarConta(String nome, String cpf) throws ContaInvalidaException {
        if (cpfExistente(cpf)) {
            throw new ContaInvalidaException("CPF já está em uso. Por favor, escolha outro CPF.");
        }
        if (nome == null || nome.isEmpty() || cpf == null || cpf.isEmpty()) {
            throw new ContaInvalidaException("Nome e CPF são obrigatórios");
        }
        String numConta = gerarNumContaUnico();
        Cliente cliente = new Cliente(nome, cpf);

        ContaCorrente contaNova = new ContaCorrente(numConta, 0.0, cliente);
        contasCorrentes.add(contaNova);

        return contaNova;
    }

    @Override
    public boolean cpfExistente(String cpf) {
        return contasCorrentes.stream().anyMatch(conta -> conta.getTitular().getCpf().equals(cpf));
    }

    private String gerarNumContaUnico() {
        return String.format("%05d", contadorContas.getAndIncrement());
    }

    @Override
    public List<ContaCorrente> listarContas() {
        return contasCorrentes;
    }

    @Override
    public ContaCorrente getContaPorNumero(String numConta) {
        return contasCorrentes.stream()
                .filter(conta -> conta.getNumConta().equals(numConta))
                .findFirst()
                .orElse(null);
    }

    @Override
    public void depositar(String numConta, double valor) throws ContaInvalidaException {
        ContaCorrente conta = getContaPorNumero(numConta);
        if (conta == null) {
            throw new ContaInvalidaException("Conta inválida. Verifique o número da conta.");
        }
        conta.depositar(valor);
    }

    @Override
    public void sacar(String numConta, double valor) throws ContaInvalidaException, SaldoInsuficienteException {
        ContaCorrente conta = getContaPorNumero(numConta);
        if (conta == null) {
            throw new ContaInvalidaException("Conta inválida. Verifique o número da conta.");
        }
        if (conta.getSaldo() < valor) {
            throw new SaldoInsuficienteException("Saldo insuficiente para realizar o saque.");
        }
        conta.sacar(valor);
    }

    @Override
    public void transferir(String contaOrigem, String contaDestino, double valor)
            throws ContaInvalidaException, SaldoInsuficienteException {
        ContaBancaria origem = getContaPorNumero(contaOrigem);
        ContaBancaria destino = getContaPorNumero(contaDestino);

        if (origem == null || destino == null) {
            throw new ContaInvalidaException("Conta de origem ou destino inválida. Verifique os números das contas.");
        }
        if (origem.getSaldo() < valor) {
            throw new SaldoInsuficienteException("Saldo insuficiente para realizar a transferência.");
        }
        origem.transferir(origem, destino, valor);
    }

    @Override
    public boolean excluirConta(String numConta) {
        ContaCorrente conta = getContaPorNumero(numConta);

        if (conta != null) {
            return contasCorrentes.remove(conta);
        } else {
            return false;
        }
    }
}