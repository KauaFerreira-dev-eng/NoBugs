-- =====================================================================
-- NoBugs / Cidade Ouve — Entrega 3: 02_carga.sql
-- Dados FICTÍCIOS para teste. Rodar depois do 01_schema.sql, em banco vazio.
--
-- Os ids não são informados: o banco os gera na ordem dos INSERTs
-- (1, 2, 3...). Assim a sequência do banco fica correta e a aplicação
-- Spring Boot não colide com ids já usados quando inserir novos registros.
-- Os comentários "-- id n" mostram qual id cada linha recebe.
-- =====================================================================

-- ---------------------------------------------------------------------
-- categoria (5)  — a 5 fica sem nenhuma ocorrência (cardinalidade 0..*)
-- ---------------------------------------------------------------------
INSERT INTO categoria (nome, descricao) VALUES ('Buraco na via',            'Buracos, afundamentos e danos no pavimento');            -- id 1
INSERT INTO categoria (nome, descricao) VALUES ('Iluminação pública',       'Poste apagado, lâmpada queimada ou fiação exposta');     -- id 2
INSERT INTO categoria (nome, descricao) VALUES ('Lixo acumulado',           'Lixo e entulho fora do horário da coleta');              -- id 3
INSERT INTO categoria (nome, descricao) VALUES ('Saneamento e esgoto',      'Vazamento, esgoto a céu aberto e bueiro entupido');      -- id 4
INSERT INTO categoria (nome, descricao) VALUES ('Sinalização de trânsito',  'Placa ou semáforo danificado');                          -- id 5

-- ---------------------------------------------------------------------
-- bairro (6)  — o 6 (Malvinas) fica sem ocorrência
-- ---------------------------------------------------------------------
INSERT INTO bairro (nome, cidade) VALUES ('Centro',    'Campina Grande');  -- id 1
INSERT INTO bairro (nome, cidade) VALUES ('Catolé',    'Campina Grande');  -- id 2
INSERT INTO bairro (nome, cidade) VALUES ('Bodocongó', 'Campina Grande');  -- id 3
INSERT INTO bairro (nome, cidade) VALUES ('Liberdade', 'Campina Grande');  -- id 4
INSERT INTO bairro (nome, cidade) VALUES ('Mirante',   'Campina Grande');  -- id 5
INSERT INTO bairro (nome, cidade) VALUES ('Malvinas',  'Campina Grande');  -- id 6

-- ---------------------------------------------------------------------
-- usuario (8) — o hash abaixo é fictício: estes usuários não fazem login
-- ---------------------------------------------------------------------
INSERT INTO usuario (nome, email, senha_hash, perfil) VALUES ('Administrador do Sistema',   'admin@cidadeouve.example',    '$2a$10$FICTICIOxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx', 'ADMINISTRADOR'); -- id 1
INSERT INTO usuario (nome, email, senha_hash, perfil) VALUES ('Marcos Vinícius Pereira',    'obras@cidadeouve.example',    '$2a$10$FICTICIOxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx', 'ORGAO_PUBLICO');  -- id 2
INSERT INTO usuario (nome, email, senha_hash, perfil) VALUES ('Luciana Barros Nogueira',    'iluminacao@cidadeouve.example','$2a$10$FICTICIOxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx', 'ORGAO_PUBLICO');  -- id 3
INSERT INTO usuario (nome, email, senha_hash, perfil) VALUES ('Maria das Dores Lima',       'maria.lima@example.com',      '$2a$10$FICTICIOxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx', 'MORADOR');        -- id 4
INSERT INTO usuario (nome, email, senha_hash, perfil) VALUES ('Pedro Henrique Alves',       'pedro.alves@example.com',     '$2a$10$FICTICIOxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx', 'MORADOR');        -- id 5
INSERT INTO usuario (nome, email, senha_hash, perfil) VALUES ('Ana Beatriz Santos',         'ana.santos@example.com',      '$2a$10$FICTICIOxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx', 'MORADOR');        -- id 6
INSERT INTO usuario (nome, email, senha_hash, perfil) VALUES ('Francisco Eudes Costa',      'francisco.costa@example.com', '$2a$10$FICTICIOxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx', 'MORADOR');        -- id 7
INSERT INTO usuario (nome, email, senha_hash, perfil) VALUES ('Tereza Cristina Ramos',      'tereza.ramos@example.com',    '$2a$10$FICTICIOxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx', 'MORADOR');        -- id 8 (sem ocorrências)

-- ---------------------------------------------------------------------
-- ocorrencia (8)
-- colunas: protocolo, descricao, data_registro, latitude, longitude,
--          endereco_aproximado, status, risco,
--          categoria_id, bairro_id, usuario_id, ocorrencia_principal_id
-- ---------------------------------------------------------------------

-- id 1: caso feliz completo, já RESOLVIDA
INSERT INTO ocorrencia (protocolo, descricao, data_registro, latitude, longitude, endereco_aproximado, status, risco, categoria_id, bairro_id, usuario_id, ocorrencia_principal_id)
VALUES ('NB-2026-000001', 'Buraco profundo no meio da rua, em frente ao número 420. Já causou queda de moto.',
        TIMESTAMP '2026-10-05 08:12:00', -7.229000, -35.881000, 'Rua Maciel Pinheiro, Centro, Campina Grande - PB',
        'RESOLVIDA', 'ALTO', 1, 1, 4, NULL);

-- id 2
INSERT INTO ocorrencia (protocolo, descricao, data_registro, latitude, longitude, endereco_aproximado, status, risco, categoria_id, bairro_id, usuario_id, ocorrencia_principal_id)
VALUES ('NB-2026-000002', 'Poste apagado há duas semanas, a rua fica totalmente escura à noite.',
        TIMESTAMP '2026-10-12 19:40:00', -7.241500, -35.887200, 'Rua Severino Cruz, Catolé, Campina Grande - PB',
        'EM_ANDAMENTO', 'MEDIO', 2, 2, 5, NULL);

-- id 3
INSERT INTO ocorrencia (protocolo, descricao, data_registro, latitude, longitude, endereco_aproximado, status, risco, categoria_id, bairro_id, usuario_id, ocorrencia_principal_id)
VALUES ('NB-2026-000003', 'Entulho e sacos de lixo acumulados na calçada, perto da escola.',
        TIMESTAMP '2026-10-20 07:55:00', -7.214800, -35.909900, 'Rua Aprígio Veloso, Bodocongó, Campina Grande - PB',
        'EM_ANALISE', 'BAIXO', 3, 3, 6, NULL);

-- id 4
INSERT INTO ocorrencia (protocolo, descricao, data_registro, latitude, longitude, endereco_aproximado, status, risco, categoria_id, bairro_id, usuario_id, ocorrencia_principal_id)
VALUES ('NB-2026-000004', 'Esgoto a céu aberto escorrendo pela rua, com mau cheiro forte.',
        TIMESTAMP '2026-11-03 18:20:00', -7.236100, -35.874300, 'Rua Manoel Pereira de Araújo, Liberdade, Campina Grande - PB',
        'REGISTRADA', 'ALTO', 4, 4, 7, NULL);

-- id 5: ocorrência principal de um grupo de relatos repetidos
INSERT INTO ocorrencia (protocolo, descricao, data_registro, latitude, longitude, endereco_aproximado, status, risco, categoria_id, bairro_id, usuario_id, ocorrencia_principal_id)
VALUES ('NB-2026-000005', 'Buraco grande na avenida, os carros desviam para a outra faixa.',
        TIMESTAMP '2026-11-08 09:05:00', -7.217100, -35.882500, 'Avenida Floriano Peixoto, Centro, Campina Grande - PB',
        'EM_ANALISE', 'ALTO', 1, 1, 4, NULL);

-- id 6: EXCEÇÃO 1 — relato repetido do mesmo buraco, vinculado à ocorrência 5 (RN04)
INSERT INTO ocorrencia (protocolo, descricao, data_registro, latitude, longitude, endereco_aproximado, status, risco, categoria_id, bairro_id, usuario_id, ocorrencia_principal_id)
VALUES ('NB-2026-000006', 'Buraco na Floriano Peixoto, perto do banco. Muito perigoso para ciclistas.',
        TIMESTAMP '2026-11-10 12:15:00', -7.217120, -35.882520, 'Avenida Floriano Peixoto, Centro, Campina Grande - PB',
        'EM_ANALISE', 'ALTO', 1, 1, 5, 5);

-- id 7: EXCEÇÃO 2 — a geocodificação falhou, então não há endereço aproximado
INSERT INTO ocorrencia (protocolo, descricao, data_registro, latitude, longitude, endereco_aproximado, status, risco, categoria_id, bairro_id, usuario_id, ocorrencia_principal_id)
VALUES ('NB-2026-000007', 'Lâmpada do poste da esquina queimada, a travessia fica no escuro.',
        TIMESTAMP '2026-11-20 21:00:00', -7.220300, -35.896800, NULL,
        'REGISTRADA', 'MEDIO', 2, 5, 6, NULL);

-- id 8: segunda ocorrência já RESOLVIDA
INSERT INTO ocorrencia (protocolo, descricao, data_registro, latitude, longitude, endereco_aproximado, status, risco, categoria_id, bairro_id, usuario_id, ocorrencia_principal_id)
VALUES ('NB-2026-000008', 'Lixo espalhado na calçada da praça, a coleta não passou esta semana.',
        TIMESTAMP '2026-10-28 10:30:00', -7.231200, -35.879600, 'Praça da Bandeira, Centro, Campina Grande - PB',
        'RESOLVIDA', 'BAIXO', 3, 1, 7, NULL);

-- ---------------------------------------------------------------------
-- historico_status
-- colunas: ocorrencia_id, status_anterior, status_novo, data_hora, autor_id
-- Toda ocorrência começa com um registro de status_anterior NULL (1..*).
-- ---------------------------------------------------------------------

-- ocorrência 1 (morador 4; órgão de obras 2)
INSERT INTO historico_status (ocorrencia_id, status_anterior, status_novo, data_hora, autor_id) VALUES (1, NULL,           'REGISTRADA',   TIMESTAMP '2026-10-05 08:12:00', 4);
INSERT INTO historico_status (ocorrencia_id, status_anterior, status_novo, data_hora, autor_id) VALUES (1, 'REGISTRADA',   'EM_ANALISE',   TIMESTAMP '2026-10-06 09:30:00', 2);
INSERT INTO historico_status (ocorrencia_id, status_anterior, status_novo, data_hora, autor_id) VALUES (1, 'EM_ANALISE',   'EM_ANDAMENTO', TIMESTAMP '2026-10-09 14:00:00', 2);
INSERT INTO historico_status (ocorrencia_id, status_anterior, status_novo, data_hora, autor_id) VALUES (1, 'EM_ANDAMENTO', 'RESOLVIDA',    TIMESTAMP '2026-10-15 16:45:00', 2);

-- ocorrência 2 (morador 5; órgão de iluminação 3)
INSERT INTO historico_status (ocorrencia_id, status_anterior, status_novo, data_hora, autor_id) VALUES (2, NULL,           'REGISTRADA',   TIMESTAMP '2026-10-12 19:40:00', 5);
INSERT INTO historico_status (ocorrencia_id, status_anterior, status_novo, data_hora, autor_id) VALUES (2, 'REGISTRADA',   'EM_ANALISE',   TIMESTAMP '2026-10-13 10:05:00', 3);
INSERT INTO historico_status (ocorrencia_id, status_anterior, status_novo, data_hora, autor_id) VALUES (2, 'EM_ANALISE',   'EM_ANDAMENTO', TIMESTAMP '2026-10-21 11:20:00', 3);

-- ocorrência 3 (morador 6)
INSERT INTO historico_status (ocorrencia_id, status_anterior, status_novo, data_hora, autor_id) VALUES (3, NULL,           'REGISTRADA',   TIMESTAMP '2026-10-20 07:55:00', 6);
INSERT INTO historico_status (ocorrencia_id, status_anterior, status_novo, data_hora, autor_id) VALUES (3, 'REGISTRADA',   'EM_ANALISE',   TIMESTAMP '2026-10-21 08:30:00', 2);

-- ocorrência 4 (morador 7): só o registro inicial
INSERT INTO historico_status (ocorrencia_id, status_anterior, status_novo, data_hora, autor_id) VALUES (4, NULL,           'REGISTRADA',   TIMESTAMP '2026-11-03 18:20:00', 7);

-- ocorrência 5 (morador 4)
INSERT INTO historico_status (ocorrencia_id, status_anterior, status_novo, data_hora, autor_id) VALUES (5, NULL,           'REGISTRADA',   TIMESTAMP '2026-11-08 09:05:00', 4);
INSERT INTO historico_status (ocorrencia_id, status_anterior, status_novo, data_hora, autor_id) VALUES (5, 'REGISTRADA',   'EM_ANALISE',   TIMESTAMP '2026-11-09 08:40:00', 2);

-- ocorrência 6 (morador 5)
INSERT INTO historico_status (ocorrencia_id, status_anterior, status_novo, data_hora, autor_id) VALUES (6, NULL,           'REGISTRADA',   TIMESTAMP '2026-11-10 12:15:00', 5);
INSERT INTO historico_status (ocorrencia_id, status_anterior, status_novo, data_hora, autor_id) VALUES (6, 'REGISTRADA',   'EM_ANALISE',   TIMESTAMP '2026-11-11 08:10:00', 2);

-- ocorrência 7 (morador 6): só o registro inicial
INSERT INTO historico_status (ocorrencia_id, status_anterior, status_novo, data_hora, autor_id) VALUES (7, NULL,           'REGISTRADA',   TIMESTAMP '2026-11-20 21:00:00', 6);

-- ocorrência 8 (morador 7)
INSERT INTO historico_status (ocorrencia_id, status_anterior, status_novo, data_hora, autor_id) VALUES (8, NULL,           'REGISTRADA',   TIMESTAMP '2026-10-28 10:30:00', 7);
INSERT INTO historico_status (ocorrencia_id, status_anterior, status_novo, data_hora, autor_id) VALUES (8, 'REGISTRADA',   'EM_ANALISE',   TIMESTAMP '2026-10-29 09:00:00', 2);
INSERT INTO historico_status (ocorrencia_id, status_anterior, status_novo, data_hora, autor_id) VALUES (8, 'EM_ANALISE',   'EM_ANDAMENTO', TIMESTAMP '2026-10-30 13:30:00', 2);
INSERT INTO historico_status (ocorrencia_id, status_anterior, status_novo, data_hora, autor_id) VALUES (8, 'EM_ANDAMENTO', 'RESOLVIDA',    TIMESTAMP '2026-11-04 15:10:00', 2);

-- ---------------------------------------------------------------------
-- notificacao
-- colunas: ocorrencia_id, destinatario_id, mensagem, data_envio, lida
-- O destinatário é sempre o morador que registrou a ocorrência.
-- Há lidas e não lidas; as ocorrências 4 e 7 ainda não geraram aviso.
-- ---------------------------------------------------------------------
INSERT INTO notificacao (ocorrencia_id, destinatario_id, mensagem, data_envio, lida) VALUES (1, 4, 'Sua ocorrência NB-2026-000001 está em análise.',   TIMESTAMP '2026-10-06 09:31:00', TRUE);
INSERT INTO notificacao (ocorrencia_id, destinatario_id, mensagem, data_envio, lida) VALUES (1, 4, 'Sua ocorrência NB-2026-000001 está em andamento.', TIMESTAMP '2026-10-09 14:01:00', TRUE);
INSERT INTO notificacao (ocorrencia_id, destinatario_id, mensagem, data_envio, lida) VALUES (1, 4, 'Sua ocorrência NB-2026-000001 foi resolvida.',      TIMESTAMP '2026-10-15 16:46:00', TRUE);
INSERT INTO notificacao (ocorrencia_id, destinatario_id, mensagem, data_envio, lida) VALUES (2, 5, 'Sua ocorrência NB-2026-000002 está em análise.',   TIMESTAMP '2026-10-13 10:06:00', TRUE);
INSERT INTO notificacao (ocorrencia_id, destinatario_id, mensagem, data_envio, lida) VALUES (2, 5, 'Sua ocorrência NB-2026-000002 está em andamento.', TIMESTAMP '2026-10-21 11:21:00', FALSE);
INSERT INTO notificacao (ocorrencia_id, destinatario_id, mensagem, data_envio, lida) VALUES (3, 6, 'Sua ocorrência NB-2026-000003 está em análise.',   TIMESTAMP '2026-10-21 08:31:00', FALSE);
INSERT INTO notificacao (ocorrencia_id, destinatario_id, mensagem, data_envio, lida) VALUES (5, 4, 'Sua ocorrência NB-2026-000005 está em análise.',   TIMESTAMP '2026-11-09 08:41:00', FALSE);
INSERT INTO notificacao (ocorrencia_id, destinatario_id, mensagem, data_envio, lida) VALUES (6, 5, 'Sua ocorrência NB-2026-000006 está em análise.',   TIMESTAMP '2026-11-11 08:11:00', FALSE);
INSERT INTO notificacao (ocorrencia_id, destinatario_id, mensagem, data_envio, lida) VALUES (8, 7, 'Sua ocorrência NB-2026-000008 foi resolvida.',      TIMESTAMP '2026-11-04 15:11:00', FALSE);
