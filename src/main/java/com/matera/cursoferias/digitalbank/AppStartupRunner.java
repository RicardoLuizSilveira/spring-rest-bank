package com.matera.cursoferias.digitalbank;

import java.math.BigDecimal;
import java.util.Arrays;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import com.matera.cursoferias.digitalbank.dto.request.ClienteRequestDTO;
import com.matera.cursoferias.digitalbank.dto.request.LancamentoRequestDTO;
import com.matera.cursoferias.digitalbank.dto.request.TransferenciaRequestDTO;
import com.matera.cursoferias.digitalbank.dto.response.ComprovanteResponseDTO;
import com.matera.cursoferias.digitalbank.dto.response.ContaResponseDTO;
import com.matera.cursoferias.digitalbank.enumerator.TipoLancamento;
import com.matera.cursoferias.digitalbank.service.ClienteService;
import com.matera.cursoferias.digitalbank.service.ContaService;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class AppStartupRunner implements ApplicationRunner {

    private final ClienteService clienteService;
    private final ContaService contaService;
    private final Environment environment;

    public AppStartupRunner(ClienteService clienteService, ContaService contaService, Environment environment) {
        this.clienteService = clienteService;
        this.contaService = contaService;
        this.environment = environment;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        if (!Arrays.asList(environment.getActiveProfiles()).contains("test")) {
            log.info("Iniciando AppStartupRunner");

            ContaResponseDTO cliente1 = clienteService.cadastra(ClienteRequestDTO.builder().bairro("Bairro 1")
                                                                                           .cep("87087087")
                                                                                           .cidade("Maringá")
                                                                                           .complemento("Casa 1")
                                                                                           .cpf("72979929921")
                                                                                           .estado("PR")
                                                                                           .logradouro("Rua 1")
                                                                                           .nome("Cliente 1")
                                                                                           .numero(100)
                                                                                           .rendaMensal(BigDecimal.valueOf(5000))
                                                                                           .telefone(44999001234L)
                                                                                           .build());

            ContaResponseDTO cliente2 = clienteService.cadastra(ClienteRequestDTO.builder().bairro("Bairro 2")
                                                                                           .cep("87087088")
                                                                                           .cidade("Maringá")
                                                                                           .complemento("Casa 2")
                                                                                           .cpf("50667427945")
                                                                                           .estado("PR")
                                                                                           .logradouro("Rua 2")
                                                                                           .nome("Cliente 2")
                                                                                           .numero(200)
                                                                                           .rendaMensal(BigDecimal.valueOf(6000))
                                                                                           .telefone(44999001235L)
                                                                                           .build());

            contaService.efetuaLancamento(cliente1.getIdConta(),
                                          LancamentoRequestDTO.builder()
                                                              .descricao("Depósito Caixa Eletrônico")
                                                              .valor(BigDecimal.valueOf(1000))
                                                              .build(),
                                          TipoLancamento.DEPOSITO);

            contaService.efetuaLancamento(cliente1.getIdConta(),
                                          LancamentoRequestDTO.builder()
                                                              .descricao("Saque Caixa Eletrônico")
                                                              .valor(BigDecimal.valueOf(100))
                                                              .build(),
                                          TipoLancamento.SAQUE);

            ComprovanteResponseDTO lancamento3 = contaService.efetuaLancamento(cliente1.getIdConta(),
                                                                               LancamentoRequestDTO.builder()
                                                                                                   .descricao("Pagamento de Boleto")
                                                                                                   .valor(BigDecimal.valueOf(50))
                                                                                                   .build(),
                                                                               TipoLancamento.PAGAMENTO);

            contaService.efetuaTransferencia(cliente1.getIdConta(),
                                             TransferenciaRequestDTO.builder()
                                                                    .descricao("Churrasco")
                                                                    .numeroAgencia(cliente2.getNumeroAgencia())
                                                                    .numeroConta(cliente2.getNumeroConta())
                                                                    .valor(BigDecimal.valueOf(30)).build());

            contaService.estornaLancamento(cliente1.getIdConta(), lancamento3.getIdLancamento());

            log.info("Finalizando AppStartupRunner");
        } else {
            log.info("AppStartupRunner não será executado para testes automáticos");
        }
    }

}
