"""Test of ASN.1 processing of PKCS#7 messages"""
import glob
import asn1tools

print("Processing ASN specification")
asn_files = glob.glob("*.asn")
cms = asn1tools.compile_files(asn_files, codec="der")

id_signedData = "1.2.840.113549.1.7.2"


def parse_signature_as_sigData(fileName):
    with open(fileName, "rb") as sigFile:
        sigBytes = sigFile.read()
    sigMsg = cms.decode("ContentInfo", sigBytes)
    if sigMsg["contentType"] != id_signedData:
        raise ValueError(f"Unsupported content type: {sigMsg['contentType']}")
    # sigMsg["content"] просто бинарная строка, парсер оставляет тип ANY как есть.
    sigData = cms.decode("SignedData", sigMsg["content"])

    return sigData


def sigData_to_CryptoMessage_DER(sigData):
    # Сначала SignedData кодируется в бинарную строку (тип ANY)
    sigDataBytes = cms.encode("SignedData", sigData)
    # Полное сообщение состоит из идентификатора контента и контента в виде двоичной строки
    msg = {"contentType": id_signedData, "content": sigDataBytes}
    return cms.encode("ContentInfo", msg)


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

print("Done")