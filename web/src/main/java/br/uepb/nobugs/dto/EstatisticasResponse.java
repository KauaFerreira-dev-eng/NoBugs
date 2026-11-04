package br.uepb.nobugs.dto;

import java.util.Map;

/**
 * Números para os gráficos do painel. Cada mapa é "nome do valor -> quantidade" e já traz os valores
 * com zero ocorrências, para os gráficos terem sempre as mesmas barras. porDia traz os últimos 14 dias (data ISO).
 */
public record EstatisticasResponse(
        long total,
        long resolvidas,
        Map<String, Long> porStatus,
        Map<String, Long> porCategoria,
        Map<String, Long> porOrgao,
        Map<String, Long> porRisco,
        Map<String, Long> porDia) {
}