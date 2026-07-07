# Prompt para criação do design — Agenda Pro

Estou criando o **Agenda Pro**, um sistema web de agendamento e controle para
profissionais autônomos que atendem clientes por horário marcado (ex.:
barbeiros, cabeleireiros, esteticistas, personal trainers, tatuadores).

O backend (API REST em Java/Spring Boot) já está pronto. Preciso agora do
**design de interface** (telas, fluxos, componentes) para um frontend web
responsivo — uso principal em desktop/tablet (o profissional geralmente
consulta a agenda num computador ou tablet no local de trabalho), mas deve
funcionar bem em mobile também.

## Quem usa o sistema

O usuário é o profissional autônomo (dono do negócio). Ele não tem equipe
técnica, não é necessariamente familiarizado com sistemas complexos — a
interface precisa ser direta, rápida de bater o olho e entender o dia, sem
excesso de cliques para tarefas do dia a dia (agendar, ver quem vem hoje,
marcar como pago).

## Módulos e dados que a interface precisa cobrir

**Autenticação**
- Cadastro (nome, e-mail, senha) e login.

**Clientes**
- Nome, telefone.
- Ações: criar, listar, remover.

**Serviços** (o catálogo do profissional)
- Nome, preço, duração em minutos.
- Ações: criar, listar.

**Agendamentos** — o coração do sistema
- Campos: cliente, serviço, data/hora, preço (pode divergir do preço-tabela
  do serviço), observações.
- **Status do atendimento**, com transições que são sempre uma ação humana
  explícita do profissional (nunca automáticas por horário, porque atrasos
  são normais nesse tipo de negócio e o sistema não pode "adivinhar" que um
  atendimento começou/terminou):
  - `AGENDADO` → `EM ATENDIMENTO` → `FINALIZADO`
  - `AGENDADO` ou `EM ATENDIMENTO` → `CANCELADO`
- **Status de pagamento**, independente do status do atendimento:
  - `PENDENTE` → `PAGO` (o cliente pode pagar adiantado, antes mesmo do
    atendimento começar, ou só depois de finalizado)
- Visões: agenda de um dia específico, lista de agendamentos com pagamento
  pendente.

## O maior desafio de UX deste sistema

Uma tela de agendamento precisa comunicar **duas dimensões de status ao
mesmo tempo** (atendimento + pagamento) de forma que o profissional entenda
em menos de 1 segundo o que precisa fazer a seguir, e só ofereça as ações
que fazem sentido para o estado atual (ex.: não faz sentido mostrar
"Finalizar" num agendamento que ainda não começou). Pense em como comunicar
isso visualmente sem poluir a tela — badges, cores, ícones, agrupamento?

## Direção visual

Estilo **suave, minimalista, tons pasteis** — evitar visual "corporativo
frio" ou excesso de elementos. Referências de produtos do mesmo tipo de
negócio (agendamento para profissionais autônomos): Calendly, Fresha,
Trafft — mas com uma identidade mais pastel/artesanal, não tão "SaaS
genérico".

Não existe marca/logo definida ainda — liberdade criativa nessa parte,
desde que pareça um produto profissional (é peça de portfólio).

## Telas esperadas (mínimo)

1. Login / Cadastro
2. Agenda do dia (tela principal — provavelmente a home)
3. Lista/cadastro de clientes
4. Lista/cadastro de serviços
5. Criação de agendamento
6. Detalhe de um agendamento com as ações de status disponíveis conforme o
   estado atual

## Entregável esperado

[AJUSTAR conforme a ferramenta: ex. "telas em alta fidelidade no Figma",
"protótipo navegável", "componentes de design system com variantes de
estado", etc.]
