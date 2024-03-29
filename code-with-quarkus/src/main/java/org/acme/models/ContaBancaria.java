package org.acme.models;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.acme.GlobalExceptionHandler.SaldoInsuficienteException;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContaBancaria {
    private String numConta;
    private Double saldo;
    private Cliente titular;
    private static final double TAXA_DE_TRANSFERENCIA = 0.001;

    public void sacar(double valorSaque) throws SaldoInsuficienteException {
        if (valorSaque <= 0) {
            throw new IllegalArgumentException("Valor de saque inválido.");
        }
        if (saldo >= valorSaque) {
            saldo -= valorSaque;
        } else {
            throw new SaldoInsuficienteException("Saldo insuficiente para realizar o saque.");
        }
    }

    public void depositar(double valor) {
        this.saldo += valor;
        System.out.printf("Valor de %.2f depositado na conta de número %s \n", valor, this.numConta);
    }

    public void transferir(ContaBancaria origem, ContaBancaria destino, double valor) {
        try {
            if (origem.getSaldo() < valor) {
                throw new SaldoInsuficienteException("Saldo insuficiente para realizar a transferência.");
            }
            origem.setSaldo(origem.getSaldo() - valor - (valor * TAXA_DE_TRANSFERENCIA ));
            destino.setSaldo(destino.getSaldo() + valor);
        } catch (SaldoInsuficienteException e) {
            System.out.println(e.getMessage());
        }
    }
    public String toString() {
        return "Numero da Conta: " + numConta +
                "\nSaldo:" + saldo +
                "\nTitular:" + titular;
    }
}
