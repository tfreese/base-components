#! /bin/bash
#
# Thomas Freese
#
# CN: CommonName
# OU: OrganizationalUnit
# O: Organization
# L: Locality
# S: StateOrProvinceName
# C: CountryName

# Exit, if one Command fails.
set -e

trap bashtrap SIGINT SIGTERM

bashtrap()
{
	echo "Exit"
	exit 1;
}

export PW="password"
readonly DNAME="OU=Development,O=MyCompany,L=MyCity,ST=MyState,C=DE";

rm -rf "demo";
mkdir "demo";

echo;
echo "####################################################################################################";
echo "Create AES Key";
echo "####################################################################################################";
keytool -genseckey -v \
    -storetype PKCS12 \
    -keystore demo/aes_keystore.p12 \
    -storepass "$PW" \
    -alias my_aes_key \
    -keyalg AES \
    -keysize 256;

echo;
echo "####################################################################################################";
echo "Create Certificates for server and client.";
echo "####################################################################################################";
keytool -genkey -v \
    -storetype PKCS12 \
    -keystore demo/server_keystore.p12 \
    -storepass "$PW" \
    -alias my_server \
    -dname "CN=server,$DNAME" \
    -keyalg RSA \
    -keysize 4096 \
    -validity 36500;

keytool -genkey -v \
    -storetype PKCS12 \
    -keystore demo/client_keystore.p12 \
    -storepass "$PW" \
    -alias my_client \
    -dname "CN=client,$DNAME" \
    -keyalg RSA \
    -keysize 4096 \
    -validity 36500;

echo;
echo "####################################################################################################";
echo "Export Certificates";
echo "####################################################################################################";
keytool -export -v \
    -storetype PKCS12 \
    -keystore demo/server_keystore.p12 \
    -storepass "$PW" \
    -alias my_server \
    -file demo/server.crt -rfc; # Text
    #-file demo/server.crt; # Binary

keytool -export -v \
    -storetype PKCS12 \
    -keystore demo/client_keystore.p12 \
    -storepass "$PW" \
    -alias my_client \
    -file demo/client.crt -rfc; # Text
    #-file demo/client.crt; # Binary

echo;
echo "####################################################################################################";
echo "Export private Keys: -nodes = Key in Plain Text";
echo "####################################################################################################";
openssl pkcs12 -in demo/server_keystore.p12 -nodes -nocerts -out demo/server_private_key.pem -passin pass:"$PW";
openssl pkcs12 -in demo/client_keystore.p12 -nodes -nocerts -out demo/client_private_key.pem -passin pass:"$PW";

## Export the certificate with private key as a PEM file without a password
#openssl pkcs12 -in demo/server_keystore.p12 -nodes -out demo/server_crt_with_private.crt -passin pass:"$PW"
#
## Export the private key as a PEM file
#openssl pkcs12 -in demo/server_keystore.p12 -nodes -nocerts -out demo/server_private_key.pem -passin pass:"$PW"
#
## Export the certificate with public key as a PEM file
#openssl pkcs12 -in demo/server_keystore.p12 -nokeys -out demo/server_crt_with_public.crt -passin pass:"$PW"

echo;
echo "####################################################################################################";
echo "Import Certificates into the TrustStores";
echo "####################################################################################################";
keytool -import -v \
    -storetype PKCS12 \
    -keystore demo/server_truststore.p12 \
    -storepass "$PW" \
    -alias client_public \
    -file demo/client.crt << EOF
ja
EOF

keytool -import -v \
    -storetype PKCS12 \
    -keystore demo/client_truststore.p12 \
    -storepass "$PW" \
    -noprompt \
    -alias server_public \
    -file demo/server.crt;

echo;
echo "####################################################################################################";
echo "Content of AES-KeyStore";
echo "####################################################################################################";
keytool -list -v -storetype PKCS12 -keystore demo/aes_keystore.p12 -storepass "$PW";

#echo;
#echo "####################################################################################################";
#echo "Content of Server-KeyStore";
#echo "####################################################################################################";
#keytool -list -v -storetype PKCS12 -keystore demo/server_keystore.p12 -storepass "$PW";
#
#echo;
#echo "####################################################################################################";
#echo "Content of Server-TrustStore";
#echo "####################################################################################################";
#keytool -list -v -storetype PKCS12 -keystore demo/server_truststore.p12 -storepass "$PW";
#
#echo;
#echo "####################################################################################################";
#echo "Content of Client-KeyStore";
#echo "####################################################################################################";
#keytool -list -v -storetype PKCS12 -keystore demo/client_keystore.p12 -storepass "$PW";
#
#echo;
#echo "####################################################################################################";
#echo "Content of Client-TrustStore";
#echo "####################################################################################################";
#keytool -list -v -storetype PKCS12 -keystore demo/client_truststore.p12 -storepass "$PW";
