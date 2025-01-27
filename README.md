# CHAT_app

## Консольное чат-приложение для работы в одной сети WI-FI

### Для запуска необходимо скачать JDK и выполнить следующие команды:

#### Для сервера:

- `cd src`
- `javac server/ChatServer.java server/BroadcastServer.java`
- `java server/ChatServer.java`

#### Для клиента:

- `cd src`
- `javac client/ChatClient.java server/BroadcastClient.java`
- `java client/ChatClient.java`