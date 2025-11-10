# 🚀 Seu Negócio API

Marketplace de pequenos empreendedores. Plataforma de e-commerce e serviços, construída com Spring Boot.

### 🎯 Objetivo do Projeto

Criar um ecossistema digital onde pequenos comerciantes e prestadores de serviços locais possam listar seus produtos e serviços de forma acessível.

---

## 🛠️ Stack Tecnológica (Backend)

* **Linguagem:** Java 21
* **Framework:** Spring Boot 3+
* **Persistência:** Spring Data JPA
* **Banco de Dados:** PostgreSQL
* **Segurança:** Spring Security (Autenticação JWT)
* **Imagens:** MinIO

---

## ⚙️ Configuração do Ambiente

### Pré-requisitos

* JDK 21 ou superior
* PostgreSQL (versão 12+)
* MinIO Server (para armazenamento de fotos).

### Variáveis de Ambiente

O projeto utiliza o arquivo `application-dev.yaml` e requer as seguintes variáveis de ambiente definidas na sua máquina (via PowerShell ou Run Configuration da IDE):

| Variável | Exemplo de Valor | Descrição |
| :--- | :--- | :--- |
| `DATABASE_HOST` | `localhost` | Host do servidor PostgreSQL |
| `DATABASE_NAME` | `teste_db` | Nome do banco de dados |
| `DATABASE_USERNAME` | `postgres` | Usuário do DB |
| `DATABASE_PASSWORD` | `999999` | Senha do DB |
| `API_SECRET_KEY` | `sua_chave_secreta_super_forte_e_longa` | Chave secreta para assinatura JWT |
| `MINIO_...` | *(A ser adicionado)* | Credenciais do MinIO (URL, Access Key, Secret Key) |

### Como Rodar

1.  Clone o repositório.
2.  Crie o banco de dados `seunegocio_db` no PostgreSQL.
3.  Defina as variáveis de ambiente acima.
4.  Execute a aplicação

A API estará disponível em `http://localhost:8080`.