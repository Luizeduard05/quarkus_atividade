package org.acme.GlobalExceptionHandler;

public class SaldoInsuficienteException extends Exception{
    public SaldoInsuficienteException(String mensagem) {
        super(mensagem);
    }
}
