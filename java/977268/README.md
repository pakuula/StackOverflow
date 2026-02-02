К вопросу [Как получить реальный IP-Адрес компьютера в локальной сети (Java)](https://ru.stackoverflow.com/questions/977268).

В этом репозитории содержится код на Java для получения локальных IPv4 адресов компьютера, используемых для маршрутов по умолчанию, на платформах Windows и Linux.

## Файлы

- `PrintLocalAddresses.java` - Главный класс для вывода локальных IPv4 адресов на консоль.
- `WindowsAddresses.java` - Код для получения локальных IPv4 адресов на Windows.
- `LinuxAddresses.java` - Код для получения локальных IPv4 адресов на Linux.
- `RouteInfo.java` - Класс для представления информации о маршрутах.
- `README.md` - Этот файл с описанием.

## Использование

Скомпилируйте и запустите `PrintLocalAddresses.java`. Программа определит операционную систему и вызовет соответствующий код для получения локальных IPv4 адресов.

```bash
javac PrintLocalAddresses.java && java PrintLocalAddresses
```
