# Master Transportes Mobile Driver

Aplicativo Android nativo para motoristas da Master Transportes, desenvolvido com **Kotlin**, **Jetpack Compose**, **Material 3**, **Hilt**, **MVVM** e **Clean Architecture**.

## Funcionalidades

- Autenticação via JWT (Access Token + Refresh Token)
- Gerenciamento de sessão com DataStore Preferences
- Navegação com Bottom Navigation
- Perfil do motorista com carregamento e tratamento de erros
- Suporte a tema claro, escuro e Dynamic Colors (Android 12+)
- Tratamento centralizado de erros (`ApiResult` e `AppError`)

## Tech Stack

| Categoria | Tecnologia |
|-----------|------------|
| Linguagem | Kotlin 2.2.10 |
| UI | Jetpack Compose + Material 3 |
| Arquitetura | MVVM + Clean Architecture |
| Injeção de Dependência | Dagger Hilt 2.60.1 |
| Navegação | Navigation Compose 2.9.8 |
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
│   ├── network/
│   ├── repository/
│   ├── result/
│   └── session/
├── di/
├── feature/
│   ├── activity/
│   ├── auth/
│   ├── home/
│   ├── main/
│   └── profile/
├── navigation/
├── ui/theme/
├── MainActivity.kt
└── MasterApplication.kt
```

---

# Como executar

1. Clone o repositório.
2. Abra o projeto no Android Studio.
3. Sincronize o Gradle.
4. Execute em um emulador ou dispositivo físico.

---

# Configuração da API

Configure a URL base utilizando `buildConfigField`:

```kotlin
defaultConfig {
    buildConfigField(
        "String",
        "BASE_URL",
        "\"https://api.exemplo.com/\""
    )
}
```

Para ambientes diferentes:

```kotlin
buildTypes {
    debug {
        buildConfigField("String", "BASE_URL", "\"https://dev.api.com/\"")
    }

    release {
        buildConfigField("String", "BASE_URL", "\"https://api.com/\"")
    }
}
```

---

# Endpoints

| Método | Endpoint | Descrição |
|---------|----------|-----------|
| POST | `access/login` | Autenticação do motorista |
| GET | `access/me` | Dados do motorista autenticado |

---

# Princípios adotados

- MVVM
- Clean Architecture
- Repository Pattern
- Dependency Injection
- Unidirectional Data Flow (UDF)
- Single Source of Truth