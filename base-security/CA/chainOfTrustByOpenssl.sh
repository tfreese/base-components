#! /bin/bash
#
# Thomas Freese
# Erzeugt ein self-signed RootCA und signiert damit weitere Zertifikate.
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

readonly PW="password"
readonly DNAME="OU=Development,O=MyCompany,L=MyCity,ST=MyState,C=DE";

rm -rf "openssl";
mkdir "openssl";

# https://wiki.form-solutions.de/wiki/admindoku/view/Main/01_Systemadministration/05_Anleitungen/01_Keystore/

echo;
echo "####################################################################################################";
echo "Create a self signed CA Certificate.";
echo "####################################################################################################";
# Create only the PrivateKey.
#openssl genrsa -aes256 -passout pass:"$PW" -out openssl/ca.key 4096;

# Create Certificate from PrivateKey.
#openssl req -x509 -new -key openssl/ca.key -out openssl/ca.crt -days 36500 -passin pass:"$PW" -sha512 -subj "/CN=ca,$DNAME";

openssl req -x509 -newkey rsa:4096 -keyout openssl/ca.key -out openssl/ca.crt -days 36500 -passout pass:"$PW" -sha512 -subj "/CN=ca,$DNAME";
openssl rsa -in openssl/ca.key -check -noout -passin pass:"$PW";

echo;
echo "####################################################################################################";
echo "Create PrivateKeys for server and client.";
echo "####################################################################################################";
openssl genrsa -aes256 -passout pass:"$PW" -out openssl/server.key 4096;
openssl rsa -in openssl/server.key -check -noout -passin pass:"$PW";

openssl genrsa -aes256 -passout pass:"$PW" -out openssl/client.key 4096;
openssl rsa -in openssl/client.key -check -noout -passin pass:"$PW";

echo;
echo "####################################################################################################";
echo "Create Certificate Signing Requests (CSR) for server and client.";
echo "####################################################################################################";
openssl req -new -key openssl/server.key -sha512 -out openssl/server.csr -subj "/CN=myServer,$DNAME" -passin pass:"$PW";
openssl req -verify -noout -in openssl/server.csr;

openssl req -new -key openssl/client.key -sha512 -out openssl/client.csr -subj "/CN=myClient,$DNAME" -passin pass:"$PW";
openssl req -verify -noout -in openssl/client.csr;

echo;
echo "####################################################################################################";
echo "Signing the CSRs with the CA Certificate and create server/client certificates.";
echo "####################################################################################################";
#-CAcreateserial
#-set_serial 100
#-set_serial 101
openssl x509 -req -CA openssl/ca.crt -CAkey openssl/ca.key -in openssl/server.csr -out openssl/server.crt -days 36500 -CAcreateserial -sha512 -passin pass:"$PW";
openssl verify -CAfile openssl/ca.crt openssl/server.crt;

openssl x509 -req -CA openssl/ca.crt -CAkey openssl/ca.key -in openssl/client.csr -out openssl/client.crt -days 36500 -CAcreateserial -sha512 -passin pass:"$PW";
openssl verify -CAfile openssl/ca.crt openssl/client.crt;

echo;
echo "####################################################################################################";
echo "Create the KeyStores.";
echo "####################################################################################################";
# Combine Certificates for Chain.
cat openssl/ca.crt openssl/server.crt > openssl/server-all.crts;
#openssl pkcs12 -export -in openssl/server-all.crts -inkey openssl/server.key -name my_server -passin pass:"$PW" -passout pass:"$PW" -out openssl/server_keystore.p12;
openssl pkcs12 -export -chain -CAfile openssl/server-all.crts -in openssl/server.crt -inkey openssl/server.key -name myServer -passin pass:"$PW" -passout pass:"$PW" -out openssl/server_keystore.p12;

cat openssl/ca.crt openssl/client.crt > openssl/client-all.crts;
#openssl pkcs12 -export -in openssl/client-all.crts -inkey openssl/client.key -name my_client -passin pass:"$PW" -passout pass:"$PW" -out openssl/client_keystore.p12;
openssl pkcs12 -export -chain -CAfile openssl/client-all.crts -in openssl/client.crt -inkey openssl/client.key -name myClient -passin pass:"$PW" -passout pass:"$PW" -out openssl/client_keystore.p12;

# Import CA Certificate
keytool -importcert -keystore openssl/server_keystore.p12 -alias ca -file openssl/ca.crt -storepass "$PW" << EOF
ja
EOF

keytool -importcert -keystore openssl/client_keystore.p12 -alias ca -file openssl/ca.crt -storepass "$PW" << EOF
ja
EOF

#keytool -importkeystore -srckeystore openssl/server_keystore.p12 -srcstoretype PKCS12 -srcstorepass "$PW" \
#    -destkeystore openssl/server_keystore.jks -deststoretype JKS -deststorepass "$PW";
#
#keytool -importkeystore -srckeystore openssl/client_keystore.p12 -srcstoretype PKCS12 -srcstorepass "$PW" \
#    -destkeystore openssl/client_keystore.jks -deststoretype JKS -deststorepass "$PW";


echo;
echo "####################################################################################################";
echo "Create the TrustStores.";
echo "####################################################################################################";
keytool -importcert -keystore openssl/server_truststore.p12 -alias ca -file openssl/ca.crt -storepass "$PW" << EOF
ja
EOF

keytool -importcert -keystore openssl/server_truststore.p12 -alias myClient -file openssl/client.crt -storepass "$PW" << EOF
ja
EOF

keytool -importcert -keystore openssl/client_truststore.p12 -alias ca -file openssl/ca.crt -storepass "$PW" << EOF
ja
EOF

keytool -importcert -keystore openssl/client_truststore.p12 -alias myServer -file openssl/server.crt -storepass "$PW" << EOF
ja
EOF

#echo;
#echo "####################################################################################################";
#echo "Content of Server-Keystore";
#echo "####################################################################################################";
#openssl pkcs12 -nokeys -info \
#    -in openssl/server_keystore.p12 \
#    -passin pass:"$PW";
#
#echo;
#echo "####################################################################################################";
#echo "Content of Client-Keystore";
#echo "####################################################################################################";
#openssl pkcs12 -nokeys -info \
#    -in openssl/client_keystore.p12 \
#    -passin pass:"$PW";
#
#echo;
#echo "####################################################################################################";
#echo "Content of Server TrustStore";
#echo "####################################################################################################";
#openssl pkcs12 -nokeys -info \
#    -in openssl/server_truststore.p12 \
#    -passin pass:"$PW";
#
#echo;
#echo "####################################################################################################";
#echo "Content of Client TrustStore";
#echo "####################################################################################################";
#openssl pkcs12 -nokeys -info \
#    -in openssl/client_truststore.p12 \
#    -passin pass:"$PW";
