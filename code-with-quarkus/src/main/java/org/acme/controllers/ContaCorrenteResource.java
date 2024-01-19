package org.acme.controllers;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.acme.GlobalExceptionHandler.ContaInvalidaException;
import org.acme.GlobalExceptionHandler.SaldoInsuficienteException;
import org.acme.models.Cliente;
import org.acme.models.ContaBancaria;
import org.acme.models.ContaCorrente;

import org.acme.services.ContaCorrenteService;
import org.acme.services.ContaCorrenteServiceImpl;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Path("api/v1/contacorrente")
public class ContaCorrenteResource {
    List<ContaCorrente> contasCorrentes = new ArrayList<>();
    ContaCorrenteService contaService = new ContaCorrenteServiceImpl(contasCorrentes);

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response criarConta(Cliente cliente) {
        try {
            ContaCorrente contaCorrente = contaService.criarConta(cliente.getNome(), cliente.getCpf());
            return Response.ok("Conta criada com sucesso!\n" + contaCorrente.toString()).build();
        } catch (ContaInvalidaException ex) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Conta inválida").build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Erro ao criar conta. Tente novamente.").build();
        }
    }

    @GET
    public Response listarContas() {
        return Response.ok().entity(contasCorrentes).build();
    }

    @POST
    @Path("/depositar/{numeroConta}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response depositar(@PathParam("numeroConta") String numeroConta, ContaBancaria contaBancaria) {
        try {
            contaService.depositar(numeroConta, contaBancaria.getSaldo());
            return Response.ok("Depósito realizado com sucesso para a conta " + numeroConta).build();
        } catch (ContaInvalidaException ex) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Conta inválida. Verifique o número da conta.").build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Ocorreu um erro durante o depósito. Tente novamente.").build();
        }
    }

    @POST
    @Path("/sacar")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response sacar(ContaBancaria contaBancaria) {
        try {
            if (contaBancaria == null || contaBancaria.getSaldo() == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Conta inválida ou saldo não informado corretamente.").build();
            }

            String numConta = contaBancaria.getNumConta();
            double valorSaque = contaBancaria.getSaldo();

            if (valorSaque <= 0) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("O valor do saque deve ser maior que zero.").build();
            }

            contaService.sacar(numConta, valorSaque);

            String mensagem = "Saque de R$ " + valorSaque + " realizado na conta: " + numConta;
            return Response.ok(mensagem).build();
        } catch (ContaInvalidaException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Erro ao sacar: " + e.getMessage()).build();
        } catch (SaldoInsuficienteException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Saldo insuficiente para sacar R$ " + contaBancaria.getSaldo() + " na conta: " + contaBancaria.getNumConta()).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Ocorreu um erro inesperado ao realizar o saque.").build();
        }
    }
    @PATCH
    @Path("/transferir/{numeroConta}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response transferir(@PathParam("numeroConta") String numeroConta, Map<String, Object> transferenciaParams) {
        try {
            String contaDestino = (String) transferenciaParams.get("contaDestino");
            double valorTransferencia = (double) transferenciaParams.get("valorTransferencia");

            contaService.transferir(numeroConta, contaDestino, valorTransferencia);

            String mensagem = "Transferência de R$ " + valorTransferencia + " realizada com sucesso de " +
                    "Conta de origem: " + numeroConta + " para Conta de destino: " + contaDestino;
            return Response.ok(mensagem).build();
        } catch (ContaInvalidaException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Erro ao transferir: " + e.getMessage()).build();
        } catch (SaldoInsuficienteException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Saldo insuficiente para transferir R$ " + transferenciaParams.get("valorTransferencia") + " da conta: " + numeroConta).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Ocorreu um erro inesperado na transação de transferência.").build();
        }
    }

}



