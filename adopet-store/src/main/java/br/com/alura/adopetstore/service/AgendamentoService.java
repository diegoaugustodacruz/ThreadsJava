package br.com.alura.adopetstore.service;

import br.com.alura.adopetstore.email.EmailRelatorioGerado;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Service
public class AgendamentoService {

    @Autowired
    private RelatorioService relatorioService;

    @Autowired
    private EmailRelatorioGerado enviador;

    @Scheduled(cron = "0 0 2 * * *") //0 s, 0 min, 2h da manha
    public void envioDeEmailsAgendado(){
        var estoqueZerado = relatorioService.infoEstoque();

        var faturamentoObtido = relatorioService.faturamentoObtido();

        //espera todas as threads serem finalizadas
        CompletableFuture.allOf(estoqueZerado, faturamentoObtido).join();

        try {
            enviador.enviar(estoqueZerado.get(), faturamentoObtido.get());
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }

        System.out.println(Thread.currentThread().getName());
    }
}
