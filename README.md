# 🏢 Portaria Digital — Aplicativo Android (Projeto de Live)

<p align="center">
  <img src="https://img.shields.io/badge/Platform-Android-3DDC84?style=for-the-badge&logo=android&logoColor=white" alt="Android" />
  <img src="https://img.shields.io/badge/Kotlin-2.0+-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white" alt="Kotlin" />
  <img src="https://img.shields.io/badge/Jetpack%20Compose-Material%203-4285F4?style=for-the-badge&logo=jetpackcompose&logoColor=white" alt="Jetpack Compose" />
  <img src="https://img.shields.io/badge/Backend-Django%20%2B%20DRF-092E20?style=for-the-badge&logo=django&logoColor=white" alt="Django DRF" />
  <img src="https://img.shields.io/badge/Web%20UI-Bootstrap%205-7952B3?style=for-the-badge&logo=bootstrap&logoColor=white" alt="Bootstrap" />
</p>

---

## 📌 Sobre o Projeto

Este projeto está sendo desenvolvido em uma **série de transmissões ao vivo (Lives)** com o objetivo de demonstrar na prática a construção e integração de uma solução corporativa completa de **Portaria e Controle de Acesso Digital para Condomínios**.

O ecossistema é dividido em duas frentes complementares:
1. **Painel Web & API Central (Backend)**: Desenvolvido em **Django**, **Django REST Framework (DRF)** e estilizado com **Django Bootstrap**, responsável pelo gerenciamento de moradores, unidades, logs de segurança, autenticação e relatórios.
2. **Aplicativo Mobile (Este Repositório)**: Desenvolvido nativamente em **Android com Kotlin e Jetpack Compose**, atuando como o terminal operacional da portaria (smartphones ou tablets na guarita).

---

## 📱 Recursos do Aplicativo Android

- 🚪 **Controle de Acessos em Tempo Real**:
  - Listagem dos acessos recentes com status (`Autorizado`, `Pendente`, `Encerrado`).
  - Identificação por categoria: Visitante, Prestador de Serviços, Morador e Veículos.
  - Modal rápido de autorização de novos acessos e encomendas de delivery.
- 📦 **Gestão de Correspondências & Encomendas**:
  - Recepção de pacotes com registro de remetente, código de rastreio e morador destinatário.
  - Geração de código de verificação (*hash de segurança*) para baixa na entrega.
- 🚨 **Operacional & Pânico Silencioso**:
  - Acionamento discreto de alerta de pânico com registro imediato de ocorrência para a administração.
- 🧪 **Modo Sandbox / Offline Mock**:
  - `MockInterceptor` embutido para permitir o desenvolvimento, testes de UI e demonstração nas lives sem depender do servidor local estar ativo.

---

## 🛠️ Tecnologias Utilizadas

### Mobile (Android)
- **Linguagem**: [Kotlin](https://kotlinlang.org/)
- **UI Toolkit**: [Jetpack Compose](https://developer.android.com/jetpack/compose) com [Material Design 3](https://m3.material.io/)
- **Arquitetura**: MVVM (*Model-View-ViewModel*) com `StateFlow` e `Coroutines`
- **Comunicação de Rede**: [Retrofit 2/3](https://square.github.io/retrofit/) + [OkHttp 5](https://square.github.io/okhttp/) + [Gson](https://github.com/google/gson)
- **Carregamento de Imagens**: [Coil Compose](https://coil-kt.github.io/coil/compose/)
- **Navegação**: Jetpack Navigation Compose

### Backend Integrado
- **Framework Web**: [Django](https://www.djangoproject.com/)
- **API REST**: [Django REST Framework (DRF)](https://www.django-rest-framework.org/) (Token Authentication)
- **Front-end Web**: Django Templates com [Bootstrap 5](https://getbootstrap.com/)

---

## 📂 Estrutura do Código Android

```text
app/src/main/java/br/ordnax/portariadigital/
├── data/
│   ├── api/
│   │   ├── MockInterceptor.kt       # Simulação de respostas da API para testes offline
│   │   ├── PortariaApiService.kt    # Interface Retrofit com contratos dos endpoints DRF
│   │   └── RetrofitClient.kt        # Configuração do cliente HTTP, interceptors e tokens
│   └── model/
│       └── Models.kt                # DTOs para autenticação, acessos, encomendas e pânico
├── ui/
│   ├── screens/
│   │   └── AcessosScreen.kt         # Tela principal de histórico e liberação de acessos
│   ├── theme/                       # Configuração de temas, cores Material 3 e tipografia
│   └── viewmodel/
│       └── AcessoViewModel.kt       # ViewModel que gerencia estados de tela (UI State)
└── MainActivity.kt                  # Ponto de entrada da aplicação
```

---

## 🔌 Integração com a API Django DRF

O aplicativo consome os seguintes contratos da API REST do Django:

| Método | Endpoint | Descrição |
|---|---|---|
| `POST` | `/api/v1/token/` | Autenticação do operador da guarita / operador |
| `GET` | `/api/v1/acessos/recentes/` | Consulta dos últimos acessos e status |
| `POST` | `/api/v1/acessos/autorizar/` | Registro e autorização de novo acesso/visitante |
| `POST` | `/api/v1/acessos/saida/{uuid}/` | Baixa/registro de saída do visitante |
| `GET` | `/api/v1/correspondencias/pendentes/` | Encomendas aguardando retirada |
| `POST` | `/api/v1/correspondencias/` | Cadastro de nova encomenda recebida |
| `POST` | `/api/v1/correspondencias/{uuid}/entregar/` | Confirmação de retirada com hash |
| `POST` | `/api/v1/operacional/panico/` | Disparo do alarme de pânico silencioso |

---

## 🚀 Como Executar o Projeto

### Pré-requisitos
- [Android Studio Ladybug ou superior](https://developer.android.com/studio)
- JDK 11 ou superior (recomendado JDK 17+)
- Android SDK (API 34/35+)
- Git instalado

### 1. Clonar o repositório
```bash
git clone https://github.com/elivandrosantos/projetoportariadigital.git
cd projetoportariadigital
```

### 2. Configurar a Conexão (Mock vs Django Real)
No arquivo [`app/build.gradle.kts`](app/build.gradle.kts), você pode configurar se o app usará o servidor mock interno ou se conectará ao Django real:

```kotlin
defaultConfig {
    // ...
    // Emulador Android: use 10.0.2.2 para acessar o localhost da sua máquina
    // Dispositivo Físico: use o IP local da sua máquina na rede (ex: 192.168.1.50)
    buildConfigField("String", "BASE_URL", "\"http://10.0.2.2:8000/\"")

    // true = Dados simulados (não precisa de backend ligado)
    // false = Conecta diretamente à API Django DRF
    buildConfigField("Boolean", "USE_MOCK", "true")
}
```

### 3. Compilar e Rodar
- Abra o projeto no **Android Studio**.
- Aguarde a sincronização do Gradle (*Sync Project with Gradle Files*).
- Selecione um dispositivo físico ou emulador e clique em **Run ▶**.

Ou execute via linha de comando:
```bash
# Compilar o APK de Debug
./gradlew assembleDebug

# Instalar diretamente no dispositivo conectado
./gradlew installDebug
```

---

## 📺 Roteiro das Lives

- [x] **Live 01**:
  - Inicialização do projeto Android com Jetpack Compose & Material 3.
  - Definição da arquitetura MVVM e contratos de dados (`Models.kt`, `PortariaApiService.kt`).
  - Criação do `MockInterceptor` para desenvolvimento desacoplado.
  - Interface da tela de Acessos com cards informativos e modal de cadastro de visitantes.
- [ ] **Live 02**:
  - Integração real com o backend Django + Django REST Framework.
  - Sistema de login com persistência de Token.
  - Gerenciamento de Correspondências e Encomendas.
- [ ] **Live 03**:
  - Leitura de QR Code para validação de visitantes e encomendas.
  - Polimento de UI/UX, animações e tratamento de erros de rede.

---

## 👨‍💻 Autor & Transmissão

Desenvolvido por **Elivandro Santos** durante as lives de desenvolvimento de software.

- **GitHub**: [@elivandrosantos](https://github.com/elivandrosantos)
- Se você gostou do projeto, deixe uma ⭐️ no repositório!

---

## 📄 Licença

Este projeto é desenvolvido para fins educacionais e demonstrativos durante lives de programação. Consulte o repositório para detalhes sobre licença e reutilização de código.
