<body>
  <header>
    <h1 align="center">📗 XPTO Financeiro – API REST (Java 8 + Spring Boot 2.7 + Oracle)</h1>
    <p align="center">
      <img src="https://img.shields.io/badge/spring--boot-2.7.18-6DB33F" alt="Spring Boot 2.7.18"/>
      <img src="https://img.shields.io/badge/java-1.8-orange" alt="Java 8"/>
      <img src="https://img.shields.io/badge/db-Oracle%20XE%2021c-blue" alt="Oracle XE 21c"/>
      <img src="https://img.shields.io/badge/docker-compose%203.8-2496ED" alt="Docker Compose"/>
      <img src="https://img.shields.io/badge/tests-JUnit%205%20%2B%20Mockito-blueviolet" alt="Tests"/>
      <img src="https://img.shields.io/badge/docs-Swagger%20UI-85EA2D" alt="Swagger UI"/>
    </p>
    <p align="center">
      API para controle de clientes (PF/PJ), contas bancárias e movimentações (receitas/despesas),
      incluindo cobrança por ciclos de 30 dias e relatórios. Infra com Oracle XE em Docker, documentação via Swagger
      e testes unitários com JUnit 5/Mockito.
    </p>
  </header>

  <!-- =============================== SUMÁRIO =============================== -->
  <main>
    <h2>🧭 Índice</h2>
    <ol>
      <li><a href="#visao-geral">Visão Geral</a></li>
      <li><a href="#arquitetura-e-pacotes">Arquitetura &amp; Pacotes</a></li>
      <li><a href="#tech-stack">Tech Stack &amp; Dependências</a></li>
      <li><a href="#como-rodar">Como Rodar (Docker &amp; Local)</a></li>
      <li><a href="#configuracao-oracle">Configuração Oracle &amp; PL/SQL</a></li>
      <li><a href="#regras-de-negocio">Regras de Negócio</a></li>
      <li><a href="#endpoints">Endpoints (Postman/Swagger)</a></li>
      <li><a href="#tratamento-erros">Tratamento de Erros</a></li>
      <li><a href="#testes">Testes</a></li>
      <li><a href="#boas-praticas">Boas Práticas &amp; Padrões</a></li>
      <li><a href="#licenca-autor">Licença &amp; Autor</a></li>
    </ol>
    <!-- =============================== VISÃO GERAL =============================== -->
    <section id="visao-geral">
      <h2>ℹ️ Visão Geral</h2>
      <p>
        O projeto atende ao desafio de construir uma <strong>API REST com Java 8 + Spring Boot</strong> e
        <strong>Oracle</strong> para controlar receitas e despesas de clientes (PF/PJ) da empresa fictícia
        <em>XPTO</em>. Inclui:
      </p>
      <ul>
        <li>CRUD de clientes (PF/PJ), endereços e contas bancárias;</li>
        <li>Cadastro e consulta de movimentações (RECEITA/DESPESA) por cliente, com
            <strong>método de pagamento</strong> obrigatório (enum <code>PaymentMethod</code>);</li>
        <li>Cobrança por ciclos de 30 dias a partir do <code>createdAt</code> do cliente;</li>
        <li>Relatórios de saldo por cliente/período e receita da empresa;</li>
        <li>Integração com <strong>PL/SQL</strong> (funções Oracle) via <code>CompanyRevenueService</code>;</li>
        <li>Documentação via <strong>Swagger UI</strong> e testes unitários.</li>
      </ul>
    </section>
    <!-- =============================== ARQUITETURA =============================== -->
    <section id="arquitetura-e-pacotes">
      <h2>🏗️ Arquitetura &amp; Pacotes</h2>
      <p>Arquitetura limpa em camadas, com separação de responsabilidades:</p>
      <pre><code>📦src
 ┣ 📂main
 ┃ ┣ 📂java/com/mv/financeiro_controladoria
 ┃ ┃ ┣ 📂application
 ┃ ┃ ┃ ┣ 📂dto
 ┃ ┃ ┃ ┃ ┣ 📂account (Create/Response/Update)
 ┃ ┃ ┃ ┃ ┣ 📂client  (Create/Response/Update + Corporate/Individual)
 ┃ ┃ ┃ ┃ ┣ 📂common  (AddressDTO, ErrorResponse)
 ┃ ┃ ┃ ┃ ┗ 📂movement/report
 ┃ ┃ ┃ ┣ 📂mapper (AccountMapper, ClientMapper)
 ┃ ┃ ┃ ┣ 📂service (ReportService - orquestra relatórios)
 ┃ ┃ ┃ ┗ 📂usecase (AccountService, BillingService, ClientService, MovementService)
 ┃ ┃ ┣ 📂domain
 ┃ ┃ ┃ ┣ 📂entity (+ enums MovementType, PaymentMethod, PersonType)
 ┃ ┃ ┃ ┗ 📂services (FeeCalculatorService)
 ┃ ┃ ┣ 📂infra
 ┃ ┃ ┃ ┣ 📂config (OpenApiConfig)
 ┃ ┃ ┃ ┣ 📂db (CompanyRevenueService - Oracle PL/SQL)
 ┃ ┃ ┃ ┣ 📂persistence/repository (JPA Repositories)
 ┃ ┃ ┃ ┗ 📂web/exception (GlobalExceptionHandler)
 ┃ ┃ ┣ 📂interfaces/rest (Controllers REST)
 ┃ ┃ ┗ 📜FinanceiroControladoriaApplication.java
 ┃ ┗ 📂resources/application.properties
 ┗ 📂test/java/com/mv/financeiro_controladoria
   ┣ 📂application/service (ReportServiceTest)
   ┣ 📂application/usecase (Account/Billing/Client/Movement Service Tests)
   ┗ 📂domain/services (FeeCalculatorServiceTest)
</code></pre>
      <ul>
        <li><strong>interfaces.rest</strong>: entrada HTTP (controllers);</li>
        <li><strong>application.usecase</strong>: regras de caso de uso (services de aplicação);</li>
        <li><strong>application.service</strong>: orquestrações e relatórios;</li>
        <li><strong>domain.entity/services</strong>: entidades e regras de domínio (ex.: <code>FeeCalculatorService</code>);</li>
        <li><strong>infra</strong>: config, repositórios JPA, integração Oracle PL/SQL, exceções.</li>
      </ul>
      <section id="tech-stack">
<section id="tech-stack">
  <h2 id="tech-stack-utilizada">Tech Stack Utilizada 🛠️</h2>

  <!-- Plataformas principais -->
  <table align="center" width="1000">
    <thead>
      <tr>
        <th><img src="https://skillicons.dev/icons?i=spring" width="100" height="100" alt="Spring Boot"/></th>
        <th><img src="https://skillicons.dev/icons?i=java" width="100" height="100" alt="Java"/></th>
        <th><img src="https://skillicons.dev/icons?i=docker" width="100" height="100" alt="Docker"/></th>
        <th><img src="https://skills-icons.vercel.app/api/icons?i=datagrip" width="100" height="100" alt="Oracle XE (via JDBC)"/></th>
      </tr>
    </thead>
    <tbody align="center">
      <tr>
        <td>Spring Boot</td>
        <td>Java</td>
        <td>Docker</td>
        <td>Oracle XE 21c</td>
      </tr>
      <tr>
        <td>🔖 2.7.18</td>
        <td>⚙️ 1.8</td>
        <td>🐳 gvenzl/oracle-xe</td>
        <td>🗄️ 21-slim</td>
      </tr>
    </tbody>
  </table>

  <!-- Ferramentas e bibliotecas -->
  <table align="center" width="1000" style="margin-top: 12px;">
    <thead>
      <tr>
        <th><img src="https://skillicons.dev/icons?i=maven" width="90" height="90" alt="Maven"/></th>
        <th><img src="https://skills-icons.vercel.app/api/icons?i=swagger" width="100" height="100" alt="Swagger"/></th>
        <th><img src="https://img.shields.io/badge/Lombok-enabled-CB2D3E?logo=lombok&logoColor=white" alt="Lombok"/></th>
        <th><img src="https://skills-icons.vercel.app/api/icons?i=junit5" width="100" height="100" alt="Junit5"/></th>
        <th><img src="https://img.shields.io/badge/Mockito-mocks-239120" alt="Mockito"/></th>
      </tr>
    </thead>
    <tbody align="center">
      <tr>
        <td>Maven</td>
        <td>OpenAPI / Swagger UI</td>
        <td>Lombok</td>
        <td>JUnit</td>
        <td>Mockito</td>
      </tr>
      <tr>
        <td>🧱 Build</td>
        <td>📚 Docs REST</td>
        <td>✂️ Boilerplate</td>
        <td>✅ Testes</td>
        <td>🧪 Mocks</td>
      </tr>
    </tbody>
  </table>

  <ul>
    <li>Spring Web, Spring Data JPA, Bean Validation;</li>
    <li>Oracle XE 21c (Docker <code>gvenzl/oracle-xe:21-slim</code>) + driver <code>ojdbc8</code>;</li>
    <li>Lombok, DevTools;</li>
    <li><strong>OpenAPI</strong> (<code>org.springdoc:springdoc-openapi-ui:1.7.0</code>) → Swagger UI;</li>
    <li>JUnit 5 &amp; Mockito.</li>
  </ul>

  <details>
    <summary><b>POM (principais deps)</b></summary>
    <pre><code>&lt;dependency&gt;
  &lt;groupId&gt;org.springframework.boot&lt;/groupId&gt;
  &lt;artifactId&gt;spring-boot-starter-web&lt;/artifactId&gt;
&lt;/dependency&gt;
&lt;dependency&gt;
  &lt;groupId&gt;org.springframework.boot&lt;/groupId&gt;
  &lt;artifactId&gt;spring-boot-starter-data-jpa&lt;/artifactId&gt;
&lt;/dependency&gt;
&lt;dependency&gt;
  &lt;groupId&gt;org.springframework.boot&lt;/groupId&gt;
  &lt;artifactId&gt;spring-boot-starter-validation&lt;/artifactId&gt;
&lt;/dependency&gt;
&lt;dependency&gt;
  &lt;groupId&gt;com.oracle.database.jdbc&lt;/groupId&gt;
  &lt;artifactId&gt;ojdbc8&lt;/artifactId&gt;
  &lt;scope&gt;runtime&lt;/scope&gt;
&lt;/dependency&gt;
&lt;dependency&gt;
  &lt;groupId&gt;org.springdoc&lt;/groupId&gt;
  &lt;artifactId&gt;springdoc-openapi-ui&lt;/artifactId&gt;
  &lt;version&gt;1.7.0&lt;/version&gt;
&lt;/dependency&gt;
&lt;dependency&gt;
  &lt;groupId&gt;org.springframework.boot&lt;/groupId&gt;
  &lt;artifactId&gt;spring-boot-starter-test&lt;/artifactId&gt;
  &lt;scope&gt;test&lt;/scope&gt;
&lt;/dependency&gt;</code></pre>
  </details>
</section>
    <!-- =============================== COMO RODAR =============================== -->
    <section id="como-rodar">
      <h2>🚀 Como Rodar</h2>
      <h3>Opção A) Com Docker Compose (recomendado)</h3>
      <p>Arquivos na raiz do projeto:</p>
      <details open>
        <summary><b>docker-compose.yml</b></summary>
        <pre><code>version: "3.8"
services:
  oracle-xe:
    image: gvenzl/oracle-xe:21-slim
    container_name: oracle-xe
    ports:
      - "1521:1521"
      - "5500:5500"
    environment:
      ORACLE_PASSWORD: oracle
      APP_USER: APP
      APP_USER_PASSWORD: app_pwd
      ORACLE_DATABASE: XEPDB1
      ORACLE_CHARACTERSET: AL32UTF8
    restart: unless-stopped

  api:
    build: .
    container_name: financeiro-api
    depends_on:
      - oracle-xe
    ports:
      - "8080:8080"
    environment:
      SPRING_DATASOURCE_URL: jdbc:oracle:thin:@//oracle-xe:1521/XEPDB1
      SPRING_DATASOURCE_USERNAME: app
      SPRING_DATASOURCE_PASSWORD: app_pwd
      SPRING_DATASOURCE_DRIVER_CLASS_NAME: oracle.jdbc.OracleDriver
      SPRING_JPA_HIBERNATE_DDL_AUTO: update
      SPRING_JPA_PROPERTIES_HIBERNATE_DIALECT: org.hibernate.dialect.Oracle12cDialect
      SPRING_JPA_SHOW_SQL: "true"
      SPRING_JPA_PROPERTIES_HIBERNATE_FORMAT_SQL: "true"
      SPRINGDOC_API_DOCS_PATH: /v3/api-docs
      SPRINGDOC_SWAGGER_UI_PATH: /swagger-ui.html
      SPRINGDOC_SWAGGER_UI_DISPLAY_OPERATION_ID: "true"
      SPRINGDOC_SWAGGER_UI_DEFAULT_MODELS_EXPAND_DEPTH: "-1"
      SPRINGDOC_SWAGGER_UI_DISPLAY_REQUEST_DURATION: "true"
      SPRINGDOC_SWAGGER_UI_OPERATIONS_SORTER: alpha
      SPRINGDOC_SWAGGER_UI_TAGS_SORTER: alpha
    restart: unless-stopped
</code></pre>
      </details>
      <details>
        <summary><b>Dockerfile</b></summary>
        <pre><code>FROM eclipse-temurin:8-jre
WORKDIR /app
COPY target/financeiro-controladoria-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]
</code></pre>
      </details>
      <p><b>Passos:</b></p>
      <ol>
        <li>Gerar o JAR:
          <pre><code>mvn clean package -DskipTests</code></pre>
        </li>
        <li>Subir tudo:
          <pre><code>docker compose up -d --build</code></pre>
        </li>
        <li>Acessar Swagger:
          <pre><code>http://localhost:8080/swagger-ui.html</code></pre>
        </li>
      </ol>
      <h3>Opção B) Local (sem Docker)</h3>
      <p>Execute o Oracle XE localmente e configure <code>src/main/resources/application.properties</code>:</p>
      <pre><code>spring.datasource.url=jdbc:oracle:thin:@//localhost:1521/XEPDB1
spring.datasource.username=app
spring.datasource.password=app_pwd
spring.datasource.driver-class-name=oracle.jdbc.OracleDriver

spring.jpa.hibernate.ddl-auto=update
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.Oracle12cDialect
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

springdoc.swagger-ui.path=/swagger-ui.html
springdoc.api-docs.path=/v3/api-docs
</code></pre>
      <p>Rodar a aplicação:</p>
      <pre><code>mvn spring-boot:run</code></pre>
    </section>
    <!-- =============================== ORACLE & PL/SQL =============================== -->
    <section id="configuracao-oracle">
      <h2>🧩 Configuração Oracle &amp; PL/SQL</h2>
      <p>
        O compose cria o usuário <code>APP/app_pwd</code> e banco <code>XEPDB1</code>. É <strong>obrigatória</strong> a
        criação de ao menos um objeto PL/SQL (function/procedure/trigger). Este projeto chama 2
        <strong>funções</strong> via <code>CompanyRevenueService</code>:
      </p>
      <ul>
        <li><code>XPTO_PKG.FN_COMPANY_REVENUE(p_start_date, p_end_date) return NUMBER</code></li>
        <li><code>XPTO_PKG.FN_CLIENT_NET_BALANCE(p_client_id) return NUMBER</code></li>
      </ul>
      <details open>
        <summary><b>📜 Exemplo de pacote PL/SQL (coloque em <code>db/xpto_pkg.sql</code>)</b></summary>
        <pre><code>CREATE OR REPLACE PACKAGE XPTO_PKG AS
  FUNCTION FN_COMPANY_REVENUE (P_START_DATE IN DATE, P_END_DATE IN DATE) RETURN NUMBER;
  FUNCTION FN_CLIENT_NET_BALANCE (P_CLIENT_ID IN NUMBER) RETURN NUMBER;
END XPTO_PKG;
/

CREATE OR REPLACE PACKAGE BODY XPTO_PKG AS
  FUNCTION FN_COMPANY_REVENUE (P_START_DATE IN DATE, P_END_DATE IN DATE) RETURN NUMBER IS
    V_TOTAL NUMBER := 0;
  BEGIN
    -- Exemplo: soma de taxas já calculadas e persistidas (ou calcule on-the-fly)
    SELECT NVL(SUM(FEE_AMOUNT), 0)
      INTO V_TOTAL
      FROM COMPANY_FEES
     WHERE FEE_DATE BETWEEN TRUNC(P_START_DATE) AND TRUNC(P_END_DATE);
    RETURN V_TOTAL;
  END;

  FUNCTION FN_CLIENT_NET_BALANCE (P_CLIENT_ID IN NUMBER) RETURN NUMBER IS
    V_CREDIT NUMBER := 0;
    V_DEBIT  NUMBER := 0;
  BEGIN
    SELECT NVL(SUM(AMOUNT),0) INTO V_CREDIT FROM MOVEMENTS
      WHERE CLIENT_ID = P_CLIENT_ID AND TYPE = 'RECEITA';
    SELECT NVL(SUM(AMOUNT),0) INTO V_DEBIT FROM MOVEMENTS
      WHERE CLIENT_ID = P_CLIENT_ID AND TYPE = 'DESPESA';
    RETURN V_CREDIT - V_DEBIT;
  END;
END XPTO_PKG;
/
</code></pre>
      </details>
      <p><b>Java &rarr; Oracle:</b> <code>CompanyRevenueService</code> usa <code>SimpleJdbcCall</code> com
        <code>.withCatalogName("XPTO_PKG")</code> e <code>.withFunctionName(...)</code> para invocar as funções.</p>
    </section>
    <!-- =============================== REGRAS DE NEGÓCIO =============================== -->
<section id="regras-de-negocio">
  <h2>📐 Regras de Negócio</h2>

  <ul>
    <li>
      <b>Cadastro de Cliente (PF/PJ)</b>
      <ul>
        <li><code>personType</code> é <b>obrigatório</b> e aceita apenas <code>PF</code> ou <code>PJ</code>.</li>
        <li><b>PF</b>: exige <code>CPF</code> único na base; <b>PJ</b>: exige <code>CNPJ</code> único na base.</li>
        <li><b>Imutáveis após criação</b>: <code>personType</code>, <code>CPF</code> (PF) e <code>CNPJ</code> (PJ).</li>
        <li><b>Movimentação inicial</b> obrigatória no <em>create</em> do cliente (ponto de partida do saldo).</li>
      </ul>
    </li>
    <li>
      <b>Contas Bancárias</b>
      <ul>
        <li><b>Edição bloqueada</b> se houver qualquer movimentação associada.</li>
        <li><b>Exclusão lógica</b>: marca <code>active=false</code>; dados permanecem para histórico.</li>
        <li>Busca/lista por cliente; número e banco atualizáveis apenas enquanto não houver movimentos.</li>
      </ul>
    </li>
    <li>
      <b>Movimentações</b>
      <ul>
        <li>Obrigatórios: <code>type</code> (CRÉDITO/DÉBITO via enum <code>MovementType</code>), <code>amount</code> (&gt; 0), <code>description</code>.</li>
        <li>Data: se não enviada, usa <code>LocalDate.now()</code>.</li>
        <li>Conta: se <code>accountId</code> informado, deve existir e pertencer ao cliente.</li>
        <li><b>Forma de pagamento</b> (opcional nos DTOs): enum <code>PaymentMethod</code> com valores
          <code>CREDITO, DEBITO, PIX, BOLETO, TED, DOC, TRANSFERENCIA</code>
          (case-insensitive; validação via <code>@JsonCreator</code>).
        </li>
      </ul>
    </li>
    <li>
      <b>Cobrança (Billing) por Ciclos de 30 dias</b>
      <ul>
        <li>Janela rolling ancorada em <code>client.createdAt</code>: ciclos <code>[D, D+30)</code>, repetidos até <code>end</code>.</li>
        <li>Para cada ciclo, calcula-se o preço por quantidade de movimentos daquele ciclo:
          <ul>
            <li>0 a 10 mov.: R$ <b>1,00</b> por movimento;</li>
            <li>11 a 20 mov.: R$ <b>0,75</b> por movimento;</li>
            <li>&gt; 20 mov.: R$ <b>0,50</b> por movimento.</li>
          </ul>
        </li>
        <li>Total devido no período = <b>soma</b> das tarifas de todos os ciclos inteiros e parciais dentro de <code>[start, end]</code> (<code>scale(2)</code>).</li>
      </ul>
    </li>
    <li>
      <b>Relatórios</b>
      <ul>
        <li><b>Saldo do Cliente (período)</b>:
          <ul>
            <li><code>initialBalance</code> = créditos − débitos até o <b>dia anterior</b> a <code>start</code>.</li>
            <li><code>currentBalance</code> = <code>initialBalance + créditos(start..end) − débitos(start..end)</code>.</li>
            <li>Inclui <code>totalCount</code> (nº de movimentações) e <code>feePaid</code> (tarifa por ciclos).</li>
          </ul>
        </li>
        <li><b>Saldo de todos os clientes (em data)</b>: lista <code>balance = créditos − débitos</code> até a data.</li>
        <li><b>Receita da empresa (período)</b>: soma das tarifas por cliente (mesma regra de ciclos).</li>
      </ul>
    </li>
    <li>
      <b>Regras de Consistência &amp; Erros</b>
      <ul>
        <li><b>404</b>: cliente/conta/movimentação inexistente.</li>
        <li><b>400</b>: parâmetros inválidos (datas, enum, valores &le; 0, CPF/CNPJ ausentes/formatos inválidos).</li>
        <li><b>409</b>: tentativa de alterar dados imutáveis ou conflito de versão (otimista) quando habilitado.</li>
        <li><b>422</b> (opcional): violação de regra de negócio (ex.: editar conta com movimentos).</li>
      </ul>
    </li>
    <li>
      <b>PL/SQL (Oracle)</b>
      <ul>
        <li>Exposição de funções do pacote <code>XPTO_PKG</code> (ex.: <code>FN_COMPANY_REVENUE</code>, <code>FN_CLIENT_NET_BALANCE</code>) via
          <code>SimpleJdbcCall</code>; entradas/saídas tipadas; tratamento de erros propagado para HTTP.</li>
      </ul>
    </li>
  </ul>
</section>

    <!-- =============================== ENDPOINTS =============================== -->
  <section id="endpoints">
  <h2>📦 Exemplos de Requisição e Resposta</h2>
  <p>Todos os endpoints aceitam e retornam JSON. Use <code>Content-Type: application/json</code>.</p>

  <!-- ==================== CLIENTS ==================== -->
  <h3>👤 Clients</h3>

  <h4>POST /api/clients – criar PF/PJ (com movimentação inicial)</h4>
  <pre><code class="language-bash">curl -X POST http://localhost:8080/api/clients \
  -H "Content-Type: application/json" \
  -d '{
    "personType": "PF",
    "name": "Ana Silva",
    "phone": "11-9999-0000",
    "individual": { "cpf": "12345678901" },
    "address": {
      "street": "Rua A",
      "city": "São Paulo",
      "state": "SP",
      "zipCode": "01000-000",
      "complement": "apto 12"
    },
    "initialMovement": {
      "type": "RECEITA",
      "amount": 150.00,
      "description": "Saldo inicial"
    }
  }'</code></pre>

  <p><b>201 Created</b></p>
  <pre><code class="language-json">{
  "id": 1,
  "name": "Ana Silva",
  "phone": "11-9999-0000",
  "personType": "PF"
}</code></pre>

  <p><b>400 Bad Request</b> (exemplo de validação)</p>
  <pre><code class="language-json">{
  "timestamp": "2025-08-13T10:15:00-03:00",
  "path": "/api/clients",
  "status": 400,
  "error": "Bad Request",
  "message": "Movimentação inicial é obrigatória."
}</code></pre>

  <h4>GET /api/clients – listar (paginado)</h4>
  <pre><code class="language-bash">curl "http://localhost:8080/api/clients?page=0&amp;size=10&amp;sort=name,asc"</code></pre>
  <pre><code class="language-json">{
  "content": [
    { "id": 1, "name": "Ana Silva", "phone": "11-9999-0000", "personType": "PF" },
    { "id": 2, "name": "XPTO Ltda", "phone": "11-2222-3333", "personType": "PJ" }
  ],
  "pageable": { "pageNumber": 0, "pageSize": 10, ... },
  "totalElements": 2,
  "last": true,
  "totalPages": 1,
  "size": 10,
  "number": 0,
  "sort": { "sorted": true, "unsorted": false, "empty": false },
  "first": true,
  "numberOfElements": 2,
  "empty": false
}</code></pre>

  <h4>GET /api/clients/{id} – detalhes</h4>
  <pre><code class="language-bash">curl http://localhost:8080/api/clients/1</code></pre>
  <pre><code class="language-json">{
  "id": 1,
  "name": "Ana Silva",
  "phone": "11-9999-0000",
  "personType": "PF"
}</code></pre>

  <h4>PUT /api/clients/{id} – atualizar dados editáveis</h4>
  <pre><code class="language-bash">curl -X PUT http://localhost:8080/api/clients/1 \
  -H "Content-Type: application/json" \
  -d '{ "name": "Ana S. Silva", "phone": "11-9999-1111" }'</code></pre>
  <pre><code class="language-json">{
  "id": 1,
  "name": "Ana S. Silva",
  "phone": "11-9999-1111",
  "personType": "PF"
}</code></pre>

  <h4>GET /api/clients/{id}/address – obter endereço</h4>
  <pre><code class="language-bash">curl http://localhost:8080/api/clients/1/address</code></pre>
  <pre><code class="language-json">{
  "street": "Rua A",
  "city": "São Paulo",
  "state": "SP",
  "zipCode": "01000-000",
  "complement": "apto 12"
}</code></pre>

  <h4>PUT /api/clients/{id}/address – atualizar endereço</h4>
  <pre><code class="language-bash">curl -X PUT http://localhost:8080/api/clients/1/address \
  -H "Content-Type: application/json" \
  -d '{
    "street": "Rua Nova",
    "city": "São Paulo",
    "state": "SP",
    "zipCode": "01000-000",
    "complement": "casa"
  }'</code></pre>
  <pre><code class="language-json">{
  "street": "Rua Nova",
  "city": "São Paulo",
  "state": "SP",
  "zipCode": "01000-000",
  "complement": "casa"
}</code></pre>

  <!-- ==================== ACCOUNTS ==================== -->
  <h3>🏦 Accounts</h3>

  <h4>POST /api/clients/{clientId}/accounts – criar conta</h4>
  <pre><code class="language-bash">curl -X POST http://localhost:8080/api/clients/1/accounts \
  -H "Content-Type: application/json" \
  -d '{ "bank": "Nubank", "number": "12345-6" }'</code></pre>
  <pre><code class="language-json">{
  "id": 10,
  "bank": "Nubank",
  "number": "12345-6",
  "active": true,
  "clientId": 1,
  "version": 0
}</code></pre>

  <p><b>409 Conflict</b> (número já usado pelo mesmo cliente)</p>
  <pre><code class="language-json">{
  "timestamp": "2025-08-13T10:20:00-03:00",
  "path": "/api/clients/1/accounts",
  "status": 409,
  "error": "Conflict",
  "message": "UK_ACCOUNTS_CLIENT_ACCNUMBER violada (cliente + número já cadastrado)"
}</code></pre>

  <h4>PUT /api/accounts/{id} – atualizar (bloqueia se houver movimentações ou inativa)</h4>
  <pre><code class="language-bash">curl -X PUT http://localhost:8080/api/accounts/10 \
  -H "Content-Type: application/json" \
  -d '{ "bank": "Inter", "number": "98765-0", "version": 0 }'</code></pre>
  <pre><code class="language-json">{
  "id": 10,
  "bank": "Inter",
  "number": "98765-0",
  "active": true,
  "clientId": 1,
  "version": 1
}</code></pre>

  <h4>DELETE /api/accounts/{id} – exclusão lógica (inativar)</h4>
  <pre><code class="language-bash">curl -X DELETE http://localhost:8080/api/accounts/10</code></pre>
  <p><b>204 No Content</b></p>

  <h4>GET /api/clients/{clientId}/accounts – listar contas do cliente</h4>
  <pre><code class="language-bash">curl http://localhost:8080/api/clients/1/accounts</code></pre>
  <pre><code class="language-json">[
  { "id": 10, "bank": "Nubank", "number": "12345-6", "active": true, "clientId": 1, "version": 0 },
  { "id": 11, "bank": "Inter",  "number": "22222-2", "active": true, "clientId": 1, "version": 0 }
]</code></pre>

  <!-- ==================== MOVEMENTS ==================== -->
  <h3>💸 Movements</h3>

  <h4>POST /api/movements/clients/{clientId} – criar movimentação</h4>
  <pre><code class="language-bash">curl -X POST http://localhost:8080/api/movements/clients/1 \
  -H "Content-Type: application/json" \
  -d '{
    "type": "DESPESA",
    "amount": 80.50,
    "description": "Pagamento boleto",
    "date": "2025-01-15",
    "accountId": 10,
    "paymentMethod": "PIX"
  }'</code></pre>
  <pre><code class="language-json">{
  "id": 2001,
  "type": "DESPESA",
  "amount": 80.50,
  "description": "Pagamento boleto",
  "date": "2025-01-15",
  "paymentMethod": "PIX",
  "accountId": 10,
  "clientId": 1
}</code></pre>

  <p><b>400 Bad Request</b> (ex.: conta não pertence ao cliente)</p>
  <pre><code class="language-json">{
  "timestamp": "2025-08-13T10:25:00-03:00",
  "path": "/api/movements/clients/1",
  "status": 400,
  "error": "Bad Request",
  "message": "Conta não pertence ao cliente informado."
}</code></pre>

  <h4>GET /api/movements/clients/{clientId}?start=YYYY-MM-DD&amp;end=YYYY-MM-DD&amp;type=RECEITA – listar</h4>
  <pre><code class="language-bash">curl "http://localhost:8080/api/movements/clients/1?start=2025-01-01&amp;end=2025-01-31&amp;type=RECEITA"</code></pre>
  <pre><code class="language-json">[
  { "id": 3001, "type": "RECEITA", "amount": 150.00, "description": "Saldo inicial", "date": "2025-01-10",
    "paymentMethod": "TRANSFERENCIA", "accountId": 10, "clientId": 1 },
  { "id": 3002, "type": "RECEITA", "amount": 200.00, "description": "Venda", "date": "2025-01-12",
    "paymentMethod": "PIX", "accountId": 11, "clientId": 1 }
]</code></pre>

  <h4>GET /api/movements/{id} – buscar por ID</h4>
  <pre><code class="language-bash">curl http://localhost:8080/api/movements/3001</code></pre>
  <pre><code class="language-json">{
  "id": 3001,
  "type": "RECEITA",
  "amount": 150.00,
  "description": "Saldo inicial",
  "date": "2025-01-10",
  "paymentMethod": "TRANSFERENCIA",
  "accountId": 10,
  "clientId": 1
}</code></pre>

  <!-- ==================== COMPANY (PL/SQL) ==================== -->
  <h3>🏢 Company (PL/SQL)</h3>

  <h4>GET /api/company/clients/{clientId}/net-balance – saldo líquido</h4>
  <pre><code class="language-bash">curl http://localhost:8080/api/company/clients/1/net-balance</code></pre>
  <pre><code class="language-json">1230.50</code></pre>

  <h4>GET /api/company/revenue?start=YYYY-MM-DD&amp;end=YYYY-MM-DD – receita da empresa</h4>
  <pre><code class="language-bash">curl "http://localhost:8080/api/company/revenue?start=2025-01-01&amp;end=2025-01-31"</code></pre>
  <pre><code class="language-json">1575.00</code></pre>

  <!-- ==================== REPORTS ==================== -->
  <h3>📊 Reports</h3>

  <h4>GET /api/reports/clients/{clientId}/balance?start=YYYY-MM-DD&amp;end=YYYY-MM-DD</h4>
  <pre><code class="language-bash">curl "http://localhost:8080/api/reports/clients/1/balance?start=2025-01-01&amp;end=2025-01-31"</code></pre>
  <pre><code class="language-json">{
  "clientId": 1,
  "clientName": "Ana Silva",
  "clientSince": "2024-11-10",
  "address": "Rua A, São Paulo/SP, 01000-000",
  "creditsCount": 12,
  "debitsCount": 5,
  "totalMovements": 17,
  "feePaid": 12.75,
  "initialBalance": 150.00,
  "finalBalance": 980.25,
  "period": { "start": "2025-01-01", "end": "2025-01-31" }
}</code></pre>

  <h4>GET /api/reports/company/revenue?start=YYYY-MM-DD&amp;end=YYYY-MM-DD</h4>
  <pre><code class="language-bash">curl "http://localhost:8080/api/reports/company/revenue?start=2025-01-01&amp;end=2025-01-31"</code></pre>
  <pre><code class="language-json">{
  "period": { "start": "2025-01-01", "end": "2025-01-31" },
  "clients": [
    { "clientId": 1, "clientName": "Ana Silva", "movementsCount": 80, "feeAmount": 60.00 },
    { "clientId": 2, "clientName": "XPTO Ltda", "movementsCount": 120, "feeAmount": 75.00 }
  ],
  "totalRevenue": 135.00
}</code></pre>

  <h4>GET /api/reports/clients/balances?date=YYYY-MM-DD</h4>
  <pre><code class="language-bash">curl "http://localhost:8080/api/reports/clients/balances?date=2025-01-31"</code></pre>
  <pre><code class="language-json">{
  "date": "2025-01-31",
  "items": [
    { "clientId": 1, "clientName": "Ana Silva", "clientSince": "2024-11-10", "balance": 980.25 },
    { "clientId": 2, "clientName": "XPTO Ltda", "clientSince": "2023-05-02", "balance": 12500.00 }
  ]
}</code></pre>

  <!-- ==================== ERROS PADRÃO ==================== -->
  <h3>🚧 Formato de Erros (GlobalExceptionHandler)</h3>
  <pre><code class="language-json">{
  "timestamp": "2025-08-13T10:30:00-03:00",
  "path": "/api/clients/999",
  "status": 404,
  "error": "Not Found",
  "message": "Cliente não encontrado"
}</code></pre>
</section>
    <!-- =============================== TESTES =============================== -->
<section id="testes">
  <h2>✅ Testes</h2>

  <p><b>Objetivo:</b> garantir regras de negócio (PF/PJ, imutabilidade de CPF/CNPJ e <code>personType</code>,
    bloqueio de edição de conta com movimentações, cálculo de tarifas por ciclos de 30 dias ancorados em
    <code>createdAt</code>, relatórios e integração PL/SQL) com feedback rápido e confiável.</p>

  <h3>📚 Tipos de teste</h3>
  <ul>
    <li><b>Unitários</b> (rápidos, sem I/O): focam regras puras e orquestração com <b>mocks</b> de repositórios/serviços.
      <ul>
        <li><code>FeeCalculatorServiceTest</code> — política de preço por faixa.</li>
        <li><code>BillingServiceTest</code> — ciclos de 30 dias (bordas: exatamente 30, múltiplos ciclos, mesmo dia).</li>
        <li><code>AccountServiceTest</code> — proíbe update se houver movimentos ou se inativa.</li>
        <li><code>ClientServiceTest</code> — PF/PJ, unicidade de CPF/CNPJ, imutabilidade de <code>personType</code>.</li>
        <li><code>MovementServiceTest</code> — criação com/sem conta, normalização de data, tipo.</li>
        <li><code>ReportServiceTest</code> — saldos/receitas, cálculo de <code>initialBalance</code> e período.</li>
      </ul>
    </li>
    <li><b>Integração (opcional)</b>: validar JPA/Oracle/PL-SQL de ponta a ponta (com Docker). Podem ser ativados via profile separado.</li>
  </ul>

  <h3>🧪 Padrões e boas práticas</h3>
  <ul>
    <li><b>AAA / Given-When-Then</b> + nomes descritivos (ex.: <code>update_shouldThrow_whenAccountHasMovements</code>).</li>
    <li><b>Mocks</b> apenas em fronteiras (repos, gateways PL/SQL). Regras puras sem mock.</li>
    <li><b>Datas determinísticas</b>: use <code>LocalDate.of(...)</code> nos testes (evite <code>now()</code>).</li>
    <li><b>Cobertura com propósito</b>: priorize ramos de regra, mensagens e bordas (0, 1, 10, 11, 20, 21 movimentos).</li>
    <li><b>Verificações</b>: <code>assertThrows</code> para regras de erro + <code>verify(...)</code>/<code>verifyNoMoreInteractions</code> no Mockito.</li>
  </ul>

  <h3>🧩 Exemplos de testes (resumo)</h3>
  <details>
    <summary><b>FeeCalculatorServiceTest</b> — parametrizado</summary>
<pre><code class="language-java">@ParameterizedTest
@CsvSource({
  "0,0.00", "1,1.00", "10,10.00",
  "11,8.25", "20,15.00",
  "21,10.50", "100,50.00"
})
void revenueFor_variasFaixas(long total, String esperado) {
  BigDecimal v = service.revenueFor(total);
  assertEquals(new BigDecimal(esperado), v);
}
</code></pre>
  </details>

  <details>
    <summary><b>AccountServiceTest</b> — bloqueio de atualização</summary>
<pre><code class="language-java">@Test
void update_deveFalhar_quandoHaMovimentacoes() {
  when(accountRepo.findById(1L)).thenReturn(Optional.of(ativa));
  when(movementRepo.countByAccount_Id(1L)).thenReturn(5L);
  assertThrows(IllegalStateException.class, () -&gt; svc.update(1L, dto));
  verify(accountRepo, never()).save(any());
}
</code></pre>
  </details>

  <details>
    <summary><b>BillingServiceTest</b> — ciclos de 30 dias</summary>
<pre><code class="language-java">@Test
void fee_consideraCiclosDe30Dias_ancoradosEmCreatedAt() {
  // createdAt = 2025-01-01, período 2025-01-10..2025-02-15 cobre 2 ciclos
  // mocka contagens por janela e valida soma das tarifas por ciclo
}
</code></pre>
  </details>

  <h3>🏃 Como rodar</h3>
  <ul>
    <li>Todos os testes: <pre><code>mvn -q clean test</code></pre></li>
    <li>Somente uma classe: <pre><code>mvn -Dtest=ReportServiceTest test</code></pre></li>
    <li>Somente um método: <pre><code>mvn -Dtest=ReportServiceTest#clientBalance_ok test</code></pre></li>
  </ul>

  <h3>📈 Cobertura (JaCoCo)</h3>
  <p><i>(Opcional, mas recomendado para o repositório)</i>. Adicione ao <code>pom.xml</code>:</p>
<pre><code class="language-xml">&lt;plugin&gt;
  &lt;groupId&gt;org.jacoco&lt;/groupId&gt;
  &lt;artifactId&gt;jacoco-maven-plugin&lt;/artifactId&gt;
  &lt;version&gt;0.8.11&lt;/version&gt;
  &lt;executions&gt;
    &lt;execution&gt;
      &lt;goals&gt;&lt;goal&gt;prepare-agent&lt;/goal&gt;&lt;/goals&gt;
    &lt;/execution&gt;
    &lt;execution&gt;
      &lt;id&gt;report&lt;/id&gt;
      &lt;phase&gt;test&lt;/phase&gt;
      &lt;goals&gt;&lt;goal&gt;report&lt;/goal&gt;&lt;/goals&gt;
    &lt;/execution&gt;
  &lt;/executions&gt;
&lt;/plugin&gt;
</code></pre>
  <p>Gerar relatório:</p>
  <pre><code>mvn clean test jacoco:report
# abra: target/site/jacoco/index.html</code></pre>

  <h3>🔬 Integração com Oracle (opcional)</h3>
  <p>Para validar JPA/PL-SQL de ponta a ponta, suba o <code>docker-compose</code> e crie um <b>profile</b> de integração
     (ex.: <code>-P it</code>) com Failsafe. Use scripts em <code>src/test/resources</code> para preparar o schema/dados e
     aponte <code>SPRING_DATASOURCE_URL</code> para <code>oracle-xe</code>.</p>

  <h3>🧵 Cobertura de cenários críticos (checklist)</h3>
  <ul>
    <li>CPF/CNPJ: obrigatório conforme PF/PJ + unicidade + não alteração após criação.</li>
    <li>Account: não atualizar se inativa ou com movimentações; exclusão lógica.</li>
    <li>Movement: criação com/sem conta, data padrão, validação de <code>MovementType</code>/<code>PaymentMethod</code>.</li>
    <li>Billing: janelas [D, D+30), múltiplos ciclos, bordas de período.</li>
    <li>Reports: saldos por período, saldo de todos em data, <code>initialBalance</code> até dia anterior.</li>
    <li>PL/SQL: chamadas via <code>SimpleJdbcCall</code> (mockadas em unit; reais em integração).</li>
    <li>Exceptions: mapeamento no <code>@ControllerAdvice</code> (400/404/409/500) com <code>ErrorResponse</code>.</li>
  </ul>

  <p>CI/CD pode ser adicionado para pipeline de build, testes e relatório de cobertura automático.</p>
</section>

<section id="boas-praticas">
  <h2>🏅 Boas Práticas &amp; Padrões</h2>

  <ul>
    <li>🏗️ <b>Arquitetura Limpa</b> — camadas bem definidas:
      <ul>
        <li><code>interfaces</code> (adapters REST) → controladores finos, sem regra de negócio;</li>
        <li><code>application/usecase</code> → orquestração e regras de <i>aplicação</i> (ex.: <code>ClientService</code>, <code>MovementService</code>);</li>
        <li><code>domain</code> → entidades, enums e <i>domain services</i> puros (ex.: <code>FeeCalculatorService</code>);</li>
        <li><code>infra</code> → repositórios JPA, chamadas PL/SQL, configs e exception handling.</li>
      </ul>
    </li>
    <li>📦 <b>DTO &amp; Mapper</b>
      <ul>
        <li>DTOs em <code>application/dto</code> (request/response separados);</li>
        <li>Conversão isolada em <code>application/mapper</code> (mantém controllers e use cases enxutos);</li>
        <li>Evita vazar entidades JPA para a borda HTTP.</li>
      </ul>
    </li>
    <li>🧠 <b>Use Cases</b>
      <ul>
        <li>Métodos expressivos e transacionais (ex.: <code>create</code>, <code>update</code>, <code>listByClient</code>);</li>
        <li>Regras sensíveis centralizadas (ex.: imutabilidade de CPF/CNPJ e do <code>personType</code> no <code>ClientService</code>);</li>
        <li>Separação entre comandos (mudam estado) e consultas (relatórios).</li>
      </ul>
    </li>
    <li>📐 <b>Domain Service</b> (políticas de negócio puras)
      <ul>
        <li><code>FeeCalculatorService</code> define preço por faixa e receita total;</li>
        <li>Testes unitários dedicados garantem estabilidade das regras.</li>
      </ul>
    </li>
    <li>✅ <b>Validação</b>
      <ul>
        <li>Bean Validation: <code>@Valid</code>, <code>@NotNull</code>, <code>@Positive</code>, etc., nos DTOs e parâmetros dos controllers;</li>
        <li>Regras adicionais nos use cases (ex.: obrigatoriedade de <code>initialMovement</code>, CPF/CNPJ único);</li>
        <li>Enums com <code>@JsonCreator</code>/<code>@JsonValue</code> (ex.: <code>PaymentMethod</code>) para payloads robustos.</li>
      </ul>
    </li>
    <li>🧾 <b>Tratamento de Exceções</b>
      <ul>
        <li><code>@ControllerAdvice</code> converte exceções em <code>ErrorResponse</code> consistente (status, mensagem, path, timestamp);</li>
        <li>Mapeamento claro: 400 (validação/regra), 404 (não encontrado), 409 (conflito), 500 (erro inesperado).</li>
      </ul>
    </li>
    <li>🧭 <b>API &amp; Contratos</b>
      <ul>
        <li>OpenAPI/Swagger UI documenta e permite testar endpoints (<code>/swagger-ui.html</code>);</li>
        <li>Paginação para listagens, filtros por período e tipo nas consultas de movimentações;</li>
        <li>Mensagens de erro legíveis e previsíveis para o cliente.</li>
      </ul>
    </li>
    <li>🗄️ <b>Oracle &amp; Persistência</b>
      <ul>
        <li>Chaves primárias: <code>IDENTITY</code> no Oracle 21c ou <code>SEQUENCE + TRIGGER</code> (evite misturar);</li>
        <li>Nomes de colunas explícitos (<code>@Column</code>) e cuidado com palavras reservadas; crie <code>@Index</code> para consultas frequentes;</li>
        <li>PL/SQL via <code>SimpleJdbcCall</code> para funções do pacote <code>XPTO_PKG</code> (ex.: receita por período, saldo líquido do cliente).</li>
      </ul>
    </li>
    <li>🧪 <b>Testes</b>
      <ul>
        <li><b>Unitários</b>: regras de domínio (ex.: <code>FeeCalculatorServiceTest</code>);</li>
        <li><b>Service/Use case</b> com Mockito (ex.: <code>ReportServiceTest</code>, <code>ClientServiceTest</code>);</li>
        <li><b>Integração</b> (opcional) com banco real Dockerizado para validar JPA/PLSQL.</li>
      </ul>
    </li>
    <li>🔎 <b>Observabilidade &amp; Logs</b>
      <ul>
        <li>Logs estruturados com contexto (método, cliente, período);</li>
        <li>Mensagens de negócio (“Conta inativa não pode ser alterada”) fáceis de diagnosticar.</li>
      </ul>
    </li>
    <li>🧹 <b>Estilo &amp; Organização</b>
      <ul>
        <li>Nomes descritivos, métodos curtos, classes focadas;</li>
        <li>Lombok para reduzir boilerplate (atenção a <code>equals/hashCode</code> em entidades);</li>
        <li>Regra de ouro: controllers finos, domínio/coordenadores ricos.</li>
      </ul>
    </li>
    <li>📝 <b>Commits Convencionais</b>
      <ul>
        <li><code>feat:</code> nova funcionalidade • <code>fix:</code> correção • <code>docs:</code> documentação • <code>test:</code> testes • <code>refactor:</code> refatoração;</li>
        <li>Mensagens curtas + contexto no corpo quando necessário.</li>
      </ul>
    </li>
  </ul>
</section>
    <!-- =============================== LICENÇA =============================== -->
    <section id="desenvolvedor">
  <h2 align="center">💻 Desenvolvedor</h2>
  <div align="center">
    <a href="https://github.com/edvaldovitor250" target="_blank" rel="noopener">
      <img src="https://github.com/edvaldovitor250.png" width="170" alt="Edvaldo Vitor">
      <br>
      <sub>Edvaldo Vitor</sub>
    </a>
  </div>
</section>

<section id="licenca">
  <h2 align="center">📄 Licença</h2>
  <p align="center">
    Este projeto está licenciado sob a MIT License. Veja o arquivo
    <a href="LICENSE">LICENSE</a> para mais detalhes.
  </p>
</section>

  </main>
</body>
