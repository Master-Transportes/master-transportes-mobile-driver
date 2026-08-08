# Master Transportes Mobile Driver

Aplicativo Android nativo para motoristas da Master Transportes, desenvolvido com **Kotlin**, **Jetpack Compose**, **Material 3**, **Hilt**, **MVVM** e **Clean Architecture**.

## Funcionalidades

- Autenticação via JWT (Access Token + Refresh Token)
- Gerenciamento de sessão com DataStore Preferences
- Home com Google Maps e localização em tempo real
- Permissões de localização e GPS com banners de orientação
- Botão nativo de localização do Google Maps
- Status online/offline do motorista (INICIAR / FINALIZAR) com bottom sheet expansível
- Carteira do motorista com saldo em tempo real
- Tema claro e escuro com cores de marca (Dynamic Colors desabilitado)
- Tratamento centralizado de erros (`ApiResult` e `AppError`)

## Tech Stack

| Categoria | Tecnologia |
|-----------|------------|
| Linguagem | Kotlin 2.2.10 |
| UI | Jetpack Compose + Material 3 |
| Arquitetura | MVVM + Clean Architecture |
| Injeção de Dependência | Dagger Hilt 2.60.1 |
| Navegação | Navigation Compose 2.9.8 |
| Google Maps | Maps Compose 6.12.2 + Play Services Location 21.3.0 |
| Networking | Retrofit 3 + OkHttp 5 |
| Persistência | DataStore Preferences |
| Concorrência | Kotlin Coroutines + Flow |
| Build | Android Gradle Plugin 9.1.1 |

---

# Arquitetura

O projeto segue **MVVM + Clean Architecture**, organizando cada funcionalidade de forma independente.

```
Presentation
      │
      ▼
ViewModel
      │
      ▼
Domain
      │
      ▼
Repository
      │
      ▼
Remote API
```

Cada feature é dividida em três camadas:

- **presentation** — telas, estados e ViewModels
- **domain** — modelos de negócio e contratos
- **data** — API, DTOs, mappers e implementação dos repositórios

Exemplo:

```
feature/auth/
├── data/
│   ├── api/
│   ├── dto/
│   ├── mapper/
│   └── repository/
├── domain/
│   └── repository/
└── presentation/login/
    ├── LoginScreen.kt
    ├── LoginContent.kt
    ├── LoginViewModel.kt
    └── LoginUiState.kt
```

---

# Estrutura do Projeto

```
app/src/main/java/com/master/transportes/driver/
├── core/
│   ├── error/
│   ├── location/
│   ├── network/
│   ├── permission/
│   ├── repository/
│   ├── result/
│   └── session/
├── di/
│   ├── auth/
│   ├── driver/
│   ├── location/
│   ├── network/
│   └── SessionModule.kt
├── feature/
│   ├── auth/
│   ├── driver/
│   └── home/
├── navigation/
├── ui/theme/
├── MainActivity.kt
└── MasterApplication.kt
```

---

# Como executar

1. Clone o repositório.
2. Crie o arquivo `local.properties` na raiz com as chaves necessárias (ver [Configuração](#configuração-da-api)).
3. Abra o projeto no Android Studio.
4. Sincronize o Gradle.
5. Execute em um emulador ou dispositivo físico.

---

# Configuração da API

O projeto lê as chaves de configuração do arquivo `local.properties` (na raiz do projeto):

```properties
MAPS_API_KEY=<sua chave do Google Maps>
BASE_URL=<url base da API>
```

- `MAPS_API_KEY` é injetada no manifesto do Google Maps.
- `BASE_URL` é injetada via `buildConfigField`.

O build falha se alguma das duas estiver ausente.

---

# Princípios adotados

- MVVM
- Clean Architecture
- Repository Pattern
- Dependency Injection
- Unidirectional Data Flow (UDF)
- Single Source of Truth