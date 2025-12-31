# 🍳 Receita AI - Gerador Inteligente de Receitas

Aplicação web que utiliza **Inteligência Artificial** para gerar receitas culinárias personalizadas baseadas nos ingredientes disponíveis. O sistema permite gerenciar uma despensa virtual e receber sugestões criativas de receitas usando **Spring AI** com integração **OpenAI/Gemini**.

## 📋 Sobre o Projeto

O **Receita AI** é um sistema que combina:
- **Backend**: API REST em Spring Boot para gerenciar ingredientes e gerar receitas
- **IA Generativa**: Integração com OpenAI e/ou Google Gemini para criar receitas personalizadas
- **Banco de Dados**: Persistência de ingredientes em MySQL
- **Frontend**: Interface Angular 19 (incluída eu outro repositório)

### Casos de Uso

✅ Cadastrar ingredientes disponíveis na despensa  
✅ Listar, buscar, atualizar e remover ingredientes  
✅ Gerar receitas automaticamente com base nos ingredientes cadastrados  
✅ Personalizar receitas por tipo de cozinha e restrições alimentares  
✅ Receber instruções detalhadas de preparo em formato estruturado  

## 🏗️ Arquitetura

O projeto segue uma arquitetura em camadas organizada e escalável:

```
src/main/java/br/com/sistema/
│
├── config/                        # Configurações da aplicação
│   └── WebConfig.java             # Configuração de CORS
│
├── controller/                    # Camada de Apresentação (API REST)
│   ├── ItemController.java        # Endpoints CRUD de ingredientes
│   └── ReceitaController.java     # Endpoint de geração de receitas
│
├── dto/                           # Data Transfer Objects
│   ├── ItemRequestDTO.java        # DTO de requisição de item
│   ├── ItemResponseDTO.java       # DTO de resposta de item
│   └── ReceitaResponseDTO.java    # DTO de resposta de receita
│
├── model/                         # Camada de Domínio (Entidades)
│   └── Item.java                  # Entidade de ingrediente
│
├── repository/                    # Camada de Persistência
│   └── ItemRepository.java        # Repository JPA com queries customizadas
│
├── service/                       # Camada de Negócio
│   ├── ItemService.java           # Lógica de negócio de ingredientes
│   └── ReceitaService.java        # Serviço de geração de receitas com IA
│
└── Startup.java                   # Classe principal do Spring Boot
```

### Fluxo de Geração de Receitas

1. **Usuário cadastra ingredientes** via API REST
2. **Sistema armazena** no banco de dados MySQL
3. **Ao solicitar receita**, o sistema:
   - Busca todos os ingredientes cadastrados
   - Monta um prompt personalizado
   - Envia para a API da OpenAI/Gemini
   - Processa a resposta usando regex
   - Retorna receita estruturada em JSON

## Tecnologias Utilizadas

### Backend
- **Java 21**
- **Spring Boot 3.5.8**
- **Spring Data JPA** - Persistência de dados
- **Spring Web** - API REST
- **Spring AI 1.1.0** - Integração com modelos de IA
  - OpenAI (GPT)
  - Google Vertex AI (Gemini 2.0 Flash)

### Banco de Dados
- **MySQL 8** - Armazenamento de ingredientes

### Ferramentas
- **Maven** - Gerenciamento de dependências
- **Spring DevTools** - Hot reload durante desenvolvimento

## Pré-requisitos

- ☕ **Java 21**
- 🐬 **MySQL 8.0+**
- 🔑 **Chave de API OpenAI** (obtenha em: https://platform.openai.com/api-keys)
- 🌐 **Projeto Google Cloud** (opcional, para usar Gemini)

## ⚙️ Configuração e Instalação

### 1. Clone o Repositório

```bash
git clone https://github.com/seu-usuario/receita-ai.git
cd receita-ai
```

### 2. Configure o Banco de Dados

Crie o banco de dados MySQL:

```sql
CREATE DATABASE receitas_ai CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 3. Configure as Credenciais

Edite o arquivo `src/main/resources/application.yml`:

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/receitas_ai?useUnicode=yes&characterEncoding=UTF-8
    username: SEU_USUARIO
    password: SUA_SENHA
```

### 4. Configure a API Key da OpenAI

Defina a variável de ambiente com sua chave da OpenAI:

**Linux/Mac:**
```bash
export OPEN_AI_API_KEY="sk-sua-chave-aqui"
```

**Windows (CMD):**
```cmd
set OPEN_AI_API_KEY=sk-sua-chave-aqui
```

**Windows (PowerShell):**
```powershell
$env:OPEN_AI_API_KEY="sk-sua-chave-aqui"
```

> 💡 **Dica**: Para tornar permanente, adicione ao `.bashrc`, `.zshrc` ou variáveis de ambiente do sistema.

### 5. Compile e Execute

```bash
# Compilar o projeto
mvn clean install

# Executar a aplicação
mvn spring-boot:run
```

A aplicação estará disponível em: `http://localhost:8080`

### Exemplo com Postman/Insomnia

1. **Cadastre alguns ingredientes** usando POST em `/api/v1/receita/itens`
2. **Gere uma receita** fazendo GET em `/api/v1/receita`
3. A IA criará uma receita baseada nos ingredientes cadastrados

## 🎯 Funcionalidades Principais

### 1. Gerenciamento de Ingredientes
- CRUD completo de ingredientes
- Busca por nome (case-insensitive)
- Busca por quantidade
- Query customizada com JPQL para busca em múltiplos campos

### 2. Geração Inteligente de Receitas
- Utiliza Spring AI com OpenAI GPT ou Google Gemini
- Prompt engineering otimizado para receitas
- Parsing automático da resposta da IA
- Extração de título, ingredientes e instruções
- Suporte a múltiplos formatos de resposta

### 3. Configuração CORS
- Suporte para integração com frontend Angular
- Endpoints configurados para `localhost:4200` e `localhost:8080`
- Métodos HTTP permitidos: GET, POST, PUT, DELETE, OPTIONS

## 🔧 Personalização da IA

### Modificar o Prompt de Geração

Edite o arquivo `ReceitaService.java` para personalizar o prompt:

```java
var template = """
    Eu preciso criar uma receita usando os ingredientes a seguir: {ingredientes}
    O tipo de cozinha que eu prefiro é {tipoCozinha}
    Por favor, considere as restrições da dieta: {restricoesDieta}
    Por favor, me forneça uma receita detalhada incluindo título, lista de ingredientes e instruções de preparo
    """;
```

### Trocar o Modelo de IA

No `application.yml`, você pode alternar entre OpenAI e Gemini:

**Para usar OpenAI (GPT):**
```yaml
spring:
  ai:
    openai:
      api-key: ${OPEN_AI_API_KEY}
      chat:
        options:
          model: gpt-4-turbo-preview  # ou gpt-3.5-turbo
          temperature: 0.7
```

**Para usar Google Gemini:**
```yaml
spring:
  ai:
    vertex:
      ai:
        gemini:
          project-id: seu-projeto-google-cloud
          location: us-east4
          chat:
            options:
              model: gemini-2.0-flash
              temperature: 0.5
```

## Melhorias Futuras

- [ ] Adicionar autenticação e autorização (Spring Security + JWT)
- [ ] Adicionar suporte para imagens de receitas
- [ ] Permitir favoritar receitas
- [ ] Criar histórico de receitas geradas
- [ ] Adicionar validação de entrada com Bean Validation
- [ ] Criar sistema de avaliação de receitas

## Recursos Adicionais

- [Documentação Spring AI](https://docs.spring.io/spring-ai/reference/)
- [OpenAI API Documentation](https://platform.openai.com/docs/)
- [Google Vertex AI Documentation](https://cloud.google.com/vertex-ai/docs)
- [Spring Boot Documentation](https://docs.spring.io/spring-boot/docs/current/reference/html/)
- [JPA/Hibernate Guide](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/)

---

⭐ **Se este projeto foi útil para você, considere dar uma estrela no repositório!**
