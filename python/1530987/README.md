К ответу на вопрос [Совместить подписи в одно криптосообщение](https://ru.stackoverflow.com/questions/1530987/)

Пример объединения двух подписей формата PKCS#7 в одно криптографическое сообщение.

# Генерация подписей

Есть два подписанта: `Signer1` и `Signer2`. Их сертификаты и приватные ключи RSA находятся в папках `signer1` и `signer2` соответственно.

Подписанты подписали файл `lorem.md`:

```bash
openssl smime -sign -binary -in lorem.md -signer signer1/signer.crt -inkey signer1/signer.key -outform DER -out sig1.der
openssl smime -sign -binary -in lorem.md -signer signer2/signer.crt -inkey signer2/signer.key -outform DER -out sig2.der
```

Подписи находятся в файлах `sig1.der` и `sig2.der`.

*Проверка*

```bash
openssl smime -verify -inform DER -in sig1.der -content lorem.md -noverify >/dev/null
```
```text
Verification successful
```

Команда `smime -verify` дополнена ключом `-noverify` для того, чтобы `openssl` не ругалось на самоподписной сертификат. Вывод `>/dev/null` скрывает содержимое файла `lorem.md`, которое в противном случае печатает команда `-verify`

# Создание файла с двумя подписями

Скрипт `main.py` парсит файлы подписей и генерирует файл `both.der`.

Скрипт использует пакет `asn1tools` и ASN.1 спецификацию `CryptographicMessageSyntax2004` из [RFC 5652](https://datatracker.ietf.org/doc/html/rfc5652#section-12.1), немного модифицированную для `asn1tools`. Спецификация дополнена модулями, от которого зависит `CryptographicMessageSyntax2004`.

```bash
pip3 install asn1tools
```
Запуск скрипта:
```bash
python3 main.py
```
```
Processing ASN specification
Loading sig1.der
Loading sig2.der
Combining the signatures
Writing the combined signature to both.der
Done
```

Скрипт генерирует файл `both.der`. Как убедиться, что в нём есть подписи от обоих подписантов:
```bash
openssl asn1parse -in both.der -inform der | grep Signer1
openssl asn1parse -in both.der -inform der | grep Signer2
```

Проверка подписи:
```bash
openssl smime -verify -inform DER -in both.der -content lorem.md -noverify >/dev/null
```

# Как это работает

В скрипте функция `parse_signature_as_sigData` парсит DER представление. Поддерживается только один вид контента - тип `SignedData` (OID `1.2.840.113549.1.7.2`).
```python
def parse_signature_as_sigData(fileName):
    with open(fileName, "rb") as sigFile:
        sigBytes = sigFile.read()
    sigMsg = cms.decode("ContentInfo", sigBytes)
    if sigMsg["contentType"] != id_signedData:
        raise ValueError(f"Unsupported content type: {sigMsg['contentType']}")
    # sigMsg["content"] просто бинарная строка, парсер оставляет тип ANY как есть.
    sigData = cms.decode("SignedData", sigMsg["content"])

    return sigData
```

Функция `sigData_to_CryptoMessage_DER` строит DER представление криптосообщения с содержимым `SignedData`:
```python
def sigData_to_CryptoMessage_DER(sigData):
    # Сначала SignedData кодируется в бинарную строку (тип ANY)
    sigDataBytes = cms.encode("SignedData", sigData)
    # Полное сообщение состоит из идентификатора контента и контента в виде двоичной строки
    msg = {"contentType": id_signedData, "content": sigDataBytes}
    return cms.encode("ContentInfo", msg)
```

Объединение подписей заключается в объединении списков сертификатов, отозванных сертификатов и собственно подписей.
```python
print("Loading sig1.der")
sig1 = parse_signature_as_sigData("sig1.der")
print("Loading sig2.der")
sig2 = parse_signature_as_sigData("sig2.der")

# поле crls необязательное в SignedData
if not "crls" in sig1:
    sig1["crls"] = []
if not "crls" in sig2:
    sig2["crls"] = []

print("Combining the signatures")
sig1["certificates"] += sig2["certificates"]
sig1["crls"] += sig2["crls"]
sig1["signerInfos"] += sig2["signerInfos"]

print("Writing the combined signature to both.der")
der = sigData_to_CryptoMessage_DER(sig1)
with open("both.der", "wb") as file:
    file.write(der)
```