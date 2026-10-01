# Shafi 🌸

<div align="center">

<img width="789" height="546" alt="Interface Shafi" src="https://github.com/user-attachments/assets/206f62c9-9189-49ab-8ef8-ae8c37ac72f6" />

Um Sistema de Compartilhamento de Arquivos (SiCA) Cliente-Servidor TCP moderno e profissional desenvolvido em Java e Java Swing, contendo uma interface rosa elegante, verificação de status de conexão em tempo real, seleção de arquivos via gerenciador do sistema e mapeamento de caminhos personalizados para download.

[English](README.md) | [Português](#-português)

</div>

---

## 📋 Sobre o Projeto

O **Shafi** é uma aplicação cliente-servidor desenvolvida para redes de computadores utilizando Sockets TCP. A ferramenta permite que o usuário:
* Conecte-se a um servidor remoto informando o endereço IP.
* Envie arquivos locais utilizando o gerenciador de arquivos nativo (`Choose and Send File`).
* Visualize os arquivos enviados no painel esquerdo e os arquivos disponíveis no servidor no painel direito.
* Baixe arquivos individualmente ou todos de uma vez, escolhendo exatamente a pasta de destino no computador.

---

## 🚀 Como Executar e Configurar

### 1. Requisitos
* Java Development Kit (JDK) 17 ou superior instalado (recomendado: JDK 23).
* Uma IDE compatível, como o IntelliJ IDEA.

### 2. Executando o Servidor (`SicaServer.java`)
1. Insira o arquivo `SicaServer.java` dentro da pasta `src` do seu projeto.
2. Execute o método `main` do servidor. Ele escutará na porta `5000` e criará automaticamente o diretório `server_files/`.

### 3. Executando a Interface Cliente (`ShafiClientGUI.java`)
1. Insira o arquivo `ShafiClientGUI.java` dentro da pasta `src` do seu projeto.
2. Execute o método `main` para abrir a interface gráfica rosa.

---

## 🕹️ Guia de Uso e Capturas de Tela

### Interface Principal e Status de Conexão
Ao abrir o cliente, digite o IP do servidor na caixa de texto superior e clique em **Connect / Refresh**. O rótulo de status abaixo atualizará dinamicamente indicando se a conexão foi estabelecida com sucesso ou se está desconectado.

> <img width="789" height="546" alt="Interface Principal" src="https://github.com/user-attachments/assets/b6b2b710-701a-48cc-abf5-4eec5a0860c5" />

### Enviando Arquivos
Clicando em **Choose and Send File**, o gerenciador de arquivos nativo do seu sistema operacional se abrirá para que você selecione qualquer arquivo para envio. Após enviado, ele aparecerá na lista à esquerda (`My Sent Files`).

> <img width="793" height="549" alt="Gerenciador de Upload" src="https://github.com/user-attachments/assets/3c10572e-3ab8-42f8-83e0-e2fb49c1f663" />

### Escolhendo o Destino do Download
Ao acionar o download de um arquivo selecionado ou de todos os arquivos, uma janela de seleção de pastas se abrirá, permitindo escolher o diretório exato onde os arquivos baixados serão salvos.

> <img width="784" height="545" alt="Selecionar Pasta de Destino" src="https://github.com/user-attachments/assets/02684115-8576-42ed-be90-f6c323c4cff4" />

---

## 📂 Estrutura do Projeto
* **`SicaServer.java`**: Gerencia conexões simultâneas de múltiplos clientes utilizando multithreading e threads isoladas para os comandos (`UPLOAD`, `LIST` e `DOWNLOAD`).
* **`ShafiClientGUI.java`**: Fornece a interface gráfica baseada em Swing com processamento assíncrono em segundo plano (`Threads`) para garantir que a tela nunca trave durante as transferências de rede.
