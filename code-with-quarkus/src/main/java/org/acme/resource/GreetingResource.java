package org.acme.resource;

import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.acme.GlobalExceptionHandler.ContaInvalidaException;
import org.acme.GlobalExceptionHandler.SaldoInsuficienteException;
import org.acme.model.ContaCorrente;
import org.acme.service.ContaCorrenteService;
import org.acme.service.ContaCorrenteServiceImpl;

import java.util.ArrayList;
import java.util.List;

@Path("/contacorrente")
public class GreetingResource {

    List<ContaCorrente> contaCorrentes = new ArrayList<>();
    ContaCorrenteService contaService = new ContaCorrenteServiceImpl(contaCorrentes);

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response criarConta(@FormParam("nome") String nome, @FormParam("cpf") String cpf) {
        try {
            ContaCorrente contaCorrente = contaService.criarConta(nome, cpf);

            String mensagem = String.format(
                    "\nConta criada:" +
                            "\nConta Corrente: %s" +
                            "\nSaldo: %s" +
                            "\nTitular:" +
                            "\n  Nome: %s" +
                            "\n  CPF: %s",
                    contaCorrente.getNumConta(),
                    contaCorrente.getSaldo(),
                    nome,
                    cpf);

            return Response.status(Response.Status.CREATED).entity(mensagem).build();
        } catch (ContaInvalidaException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Erro ao criar conta: " + e.getMessage()).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Ocorreu um erro ao criar a conta.").build();
        }
    }

    @GET
    @Path("/contas")
    @Produces(MediaType.APPLICATION_JSON)
    public List<String> listarContas() {
        List<ContaCorrente> contas = contaService.listarContas();
        List<String> informacoesContas = new ArrayList<>();

        for (ContaCorrente conta : contas) {
            informacoesContas.add(String.format("Conta: %s, Saldo: R$%s", conta.getNumConta(), conta.getSaldo().floatValue()));
        }
        return informacoesContas;
    }

    @GET
    @Path("/saldo/{numConta}")
    @Produces(MediaType.TEXT_PLAIN)
    public Response verSaldo(@PathParam("numConta") String numConta) {
        ContaCorrente conta = contaService.getContaPorNumero(numConta);
        if (conta != null) {
            String mensagem = "O saldo  da conta " + numConta + " é: R$" + conta.getSaldo().floatValue();
            return Response.ok(mensagem).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).entity("Conta não inexistente").build();
        }
    }

    @POST
    @Path("/depositar")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_PLAIN)
    public Response depositar(@FormParam("numConta") String numConta, @FormParam("valor") double valorDeposito) {
        try {
            contaService.depositar(numConta, valorDeposito);
            String mensagem = "Depósito de R$ " + valorDeposito + " realizado na conta de número: " + numConta;
            return Response.ok(mensagem).build();
        } catch (ContaInvalidaException e) {
            return Response
                    .status(Response.Status.BAD_REQUEST)
                    .entity("Erro ao depositar: " + e.getMessage())
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response
                    .status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Ocorreu um erro ao realizar o depósito.")
                    .build();
        }
    }

    @POST
    @Path("/sacar")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_PLAIN)
    public Response sacar(@FormParam("numConta") String numConta, @FormParam("valor") double valorSaque) {
        try {
            contaService.sacar(numConta, valorSaque);
            String mensagem = "Saque de R$ " + valorSaque + " realizado na conta: " + numConta;
            return Response.ok(mensagem).build();
        } catch (ContaInvalidaException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Erro ao sacar: " + e.getMessage()).build();
        } catch (SaldoInsuficienteException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Saldo insuficiente na conta: " + numConta).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Ocorreu um erro  ao realizar o saque.").build();
        }
    }

    @PATCH
    @Path("/transferir")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_PLAIN)
    public Response transferir(@FormParam("contaOrigem") String contaOrigem,
                               @FormParam("contaDestino") String contaDestino,
                               @FormParam("valor") double valorTransferencia) {
        try {
            contaService.transferir(contaOrigem, contaDestino, valorTransferencia);
            String mensagem = "Transferência de R$ " + valorTransferencia + " realizada com sucesso de " +
                    "Conta de origem: " + contaOrigem + " para Conta de destino: " + contaDestino;
            return Response.ok(mensagem).build();
        } catch (ContaInvalidaException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Erro ao transferir: " + e.getMessage()).build();
        } catch (SaldoInsuficienteException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Saldo insuficiente para transferir R$ " + valorTransferencia + " da conta: " + contaOrigem).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Ocorreu um erro inesperado na transação de transferência.").build();
        }
    }

    @DELETE
    @Path("/excluir/{numConta}")
    @Transactional
    public Response excluirConta(@PathParam("numConta") String numConta) {
        try {
            boolean contaExcluida = contaService.excluirConta(numConta);

            if (contaExcluida) {
                return Response.ok("Conta excluída com sucesso.").build();
            } else {
                return Response.status(Response.Status.NOT_FOUND).entity("Conta não encontrada para o número: " + numConta).build();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Erro ao excluir a conta.").build();
        }
    }



}

