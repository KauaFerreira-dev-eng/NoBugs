# Cidade Ouve — Documento de modelagem (Entrega 2)

Equipe NoBugs · PIPP II · UEPB ADS 7

> Pontos marcados com **[CONFIRMAR]** foram assumidos a partir da Proposta, dos requisitos RF01–RF10 e do Registro da Iteração 1. Ajustem aos nomes do diagrama de casos de uso da sessão de 14/10 e ao código do repositório.

## 1. Dicionário de classes

### Usuario
Pessoa que acessa o sistema: morador, servidor do órgão público ou administrador.

| Atributo | Tipo | Significado |
|---|---|---|
| id | int | Identificador único |
| nome | String | Nome da pessoa |
| email | String | E-mail usado no acesso |
| senhaHash | String | Senha armazenada de forma criptografada |
| perfil | Perfil | Define o que a pessoa pode fazer |

| Método | O que faz |
|---|---|
| podeClassificarRisco() | Retorna true só se o perfil for ORGAO_PUBLICO (RN03) |
| podeAtualizarStatus() | Retorna true só se o perfil for ORGAO_PUBLICO ou ADMINISTRADOR |

### Ocorrencia
Um problema urbano relatado por um morador (buraco, lixo, iluminação etc.). É a classe central do sistema.

| Atributo | Tipo | Significado |
|---|---|---|
| id | int | Identificador único |
| protocolo | String | Código que o morador usa para acompanhar |
| descricao | String | Texto do problema; obrigatório (RN01) |
| dataRegistro | LocalDateTime | Momento em que foi registrada |
| latitude / longitude | double | Coordenadas informadas no registro |
| enderecoAproximado | String | Endereço obtido a partir das coordenadas |
| fotoUrl | String | Caminho da foto; opcional (pode ser nulo) |
| status | StatusOcorrencia | Situação atual |
| risco | NivelRisco | Prioridade de risco; nulo até o órgão público classificar |

| Método | O que faz |
|---|---|
| validarDescricao(texto) | Retorna false se a descrição for vazia (RN01) |
| registrar(...) | Cria a ocorrência com status REGISTRADA, gera o protocolo, grava o primeiro histórico e vincula a um relato semelhante, se houver (RN02, RN05) |
| buscarSimilar(categoria, lat, lng) | Procura ocorrência aberta da mesma categoria próxima ao ponto informado (RN05) |
| gerarProtocolo() | Gera o protocolo único (RN02) |
| classificarRisco(nivel, autor) | Define o risco, se o autor tiver permissão (RN03) |
| atualizarStatus(novo, autor) | Muda o status se o autor tiver permissão e a transição for válida; grava histórico e notifica o morador (RN04, RN06) |
| vincularRelato(repetida) | Associa à ocorrência principal um novo relato do mesmo problema (RN05) |

### HistoricoStatus
Registro de cada mudança de status de uma ocorrência. Existe para o morador acompanhar o andamento.

| Atributo | Tipo | Significado |
|---|---|---|
| id | int | Identificador único |
| statusAnterior | StatusOcorrencia | Status antes da mudança (nulo no primeiro registro) |
| statusNovo | StatusOcorrencia | Status depois da mudança |
| dataHora | LocalDateTime | Quando mudou |

| Método | O que faz |
|---|---|
| registrar(oc, anterior, novo, autor) | Grava uma linha de histórico |

### Notificacao
Aviso enviado ao morador quando sua ocorrência muda de status.

| Atributo | Tipo | Significado |
|---|---|---|
| id | int | Identificador único |
| mensagem | String | Texto do aviso |
| dataEnvio | LocalDateTime | Quando foi enviado |
| lida | boolean | Se o morador já viu |

| Método | O que faz |
|---|---|
| enviar(destinatario, oc, mensagem) | Cria e entrega o aviso (RN07) |
| marcarComoLida() | Marca como lida |

### Categoria
Tipo de problema (buraco na via, iluminação, saneamento...). Atende RF09 e as estatísticas do RF08.

| Atributo | Tipo | Significado |
|---|---|---|
| id | int | Identificador único |
| nome | String | Nome da categoria |
| descricao | String | Explicação do que a categoria cobre |

| Método | O que faz |
|---|---|
| contarOcorrencias() | Quantidade de ocorrências da categoria (estatística, RF08) |

### Bairro
Região usada para visualizar ocorrências e gerar estatísticas (RF07, RF08).

| Atributo | Tipo | Significado |
|---|---|---|
| id | int | Identificador único |
| nome | String | Nome do bairro |
| cidade | String | Cidade a que pertence |

| Método | O que faz |
|---|---|
| contarOcorrencias() | Quantidade de ocorrências no bairro (estatística, RF08) |

### Enums
- **Perfil**: MORADOR, ORGAO_PUBLICO, ADMINISTRADOR.
- **StatusOcorrencia**: REGISTRADA, EM_ANALISE, EM_ANDAMENTO, RESOLVIDA (da Proposta). Método `podeAvancarPara(novo)` aplica a RN04.
- **NivelRisco**: BAIXO, MEDIO, ALTO **[CONFIRMAR os níveis do RF04]**.

## 2. Regras de negócio e onde moram

O documento de requisitos não numera regras de negócio. Derivei as abaixo do RF04, da Proposta e do Registro da Iteração 1 **[CONFIRMAR com a equipe]**.

| Regra | Onde mora | Método |
|---|---|---|
| RN01 — A descrição é obrigatória | Ocorrencia | validarDescricao() |
| RN02 — Toda ocorrência nasce REGISTRADA e com protocolo único | Ocorrencia | registrar(), gerarProtocolo() |
| RN03 — Só o órgão público classifica o risco | Usuario / Ocorrencia | podeClassificarRisco(), classificarRisco() |
| RN04 — O status só avança na ordem REGISTRADA → EM_ANALISE → EM_ANDAMENTO → RESOLVIDA | StatusOcorrencia | podeAvancarPara() |
| RN05 — Relato do mesmo problema, perto do mesmo local, é vinculado à ocorrência existente | Ocorrencia | buscarSimilar(), vincularRelato() |
| RN06 — Só órgão público ou administrador altera status | Usuario | podeAtualizarStatus() |
| RN07 — Toda mudança de status notifica o morador | Notificacao | enviar() |

## 3. Descrição dos cenários

### Registrar ocorrência (sequencia-registrar-ocorrencia)
- **Caso de uso:** Registrar ocorrência. Ator: Morador.
- **Fluxo principal:** o morador informa categoria, descrição, localização e, se quiser, foto. A descrição é validada, a ocorrência é criada com status REGISTRADA, recebe protocolo e o primeiro histórico é gravado. O protocolo é exibido.
- **Alt:** se a descrição estiver vazia, o sistema recusa e pede o preenchimento (RN01).
- **Opt:** se já existir ocorrência semelhante (mesma categoria, mesmo local), o novo relato é vinculado a ela (RN05).
- **Premissas:** o endereço aproximado e o bairro são obtidos das coordenadas antes de registrar; o morador já está autenticado.

### Atualizar status da ocorrência (sequencia-atualizar-status)
- **Caso de uso:** Atualizar status da ocorrência. Ator: Órgão Público.
- **Fluxo principal:** o servidor escolhe a ocorrência e o novo status. O sistema confere a permissão e se a transição é válida, grava o histórico, notifica o morador e confirma.
- **Alt:** sem permissão ou com transição inválida (por exemplo, REGISTRADA direto para RESOLVIDA), o sistema recusa e informa o motivo (RN04, RN06).
- **Premissas:** o servidor está autenticado; a ocorrência existe.

## 4. Tabela de correspondência **[CONFIRMAR com o diagrama de 14/10]**

| Caso de uso | Classes que participam |
|---|---|
| Registrar ocorrência | Usuario, Ocorrencia, Categoria, Bairro, HistoricoStatus |
| Classificar risco da ocorrência | Usuario, Ocorrencia, NivelRisco |
| Atualizar status da ocorrência | Usuario, Ocorrencia, StatusOcorrencia, HistoricoStatus, Notificacao |
| Consultar minhas ocorrências / acompanhar status | Usuario, Ocorrencia, HistoricoStatus |
| Visualizar ocorrências por região | Ocorrencia, Bairro, Categoria |
| Gerar estatísticas por região e categoria | Ocorrencia, Bairro, Categoria |
| Manter categorias (Administrador) | Usuario, Categoria |
| Gerenciar usuários (Administrador) | Usuario, Perfil |

## 5. Decisões de modelagem

**Classes separadas:** Ocorrencia, Usuario, Categoria, Bairro, HistoricoStatus e Notificacao. Categoria e Bairro viraram classes, e não enums, porque o administrador pode precisar cadastrar novos valores e porque as estatísticas agrupam por eles. HistoricoStatus é classe porque a mudança de status guarda dados próprios (quem, quando, de qual status para qual) e é composição de Ocorrencia: some junto com ela. Notificacao existe porque a Proposta prevê avisar o morador e o aviso precisa ser guardado para ser consultado depois. **Atributos:** descrição, foto, coordenadas, endereço aproximado e protocolo ficam em Ocorrencia, pois não têm vida própria; a foto é um único campo opcional. **Enums:** Perfil, StatusOcorrencia e NivelRisco têm valores fixos e ninguém cadastra novos. Os três atores viraram um atributo `perfil` em Usuario, e não herança, porque a diferença entre eles é de permissão, não de dados. Relatos repetidos foram modelados como associação da Ocorrencia com ela mesma, e não como classe Relato, para manter o modelo enxuto; se a equipe quiser guardar dados próprios de cada relato repetido (quem relatou, quando), Relato vira classe. **Fora do modelo:** estatísticas, painel e relatórios são saídas de consultas, e não há classes de tela.

## 6. Perguntas para a equipe antes de entregar
1. Os casos de uso e atores batem com o diagrama de 14/10?
2. As entidades do código Spring Boot batem com estas classes (nomes e atributos)?
3. Categoria como classe ou enum? O Administrador cadastra categorias?
4. Relato repetido: autoassociação ou classe Relato?
5. Quais são os níveis de risco do RF04?
