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
  -alias server \
  -dname "CN=server,$DNAME" \
  -keyalg RSA \
  -keysize 4096 \
  -validity 36500;

keytool -genkey -v \
  -storetype PKCS12 \
  -keystore demo/client_keystore.p12 \
  -storepass "$PW" \
  -alias client \
  -dname "CN=client,$DNAME" \
  -keyalg RSA \
  -keysize 4096 \
  -validity 36500;



echo;
echo "####################################################################################################";
echo "Export public Keys";
echo "####################################################################################################";
keytool -export -v \
  -keystore demo/server_keystore.p12 \
  -storepass "$PW" \
  -alias server \
  -file demo/server_public.crt \
  -rfc;

keytool -export -v \
  -keystore demo/client_keystore.p12 \
  -storepass "$PW" \
  -alias client \
  -file demo/client_public.crt \
  -rfc;



echo;
echo "####################################################################################################";
echo "Import public Keys";
echo "####################################################################################################";
keytool -import -v \
  -keystore demo/server_keystore.p12 \
  -storepass "$PW" \
  -alias client_public \
  -file demo/client_public.crt << EOF
ja
EOF

keytool -import -v \
  -keystore demo/client_keystore.p12 \
  -storepass "$PW" \
  -alias server_public \
  -file demo/server_public.crt << EOF
ja
EOF



echo;
echo "####################################################################################################";
echo "Content of AES-KeyStore";
echo "####################################################################################################";
keytool -list -v \
  -keystore demo/aes_keystore.p12 \
  -storepass "$PW"

echo;
echo "####################################################################################################";
echo "Content of Server-KeyStore";
echo "####################################################################################################";
keytool -list -v \
  -keystore demo/server_keystore.p12 \
  -storepass "$PW"

echo;
echo "####################################################################################################";
echo "Content of Client-KeyStore";
echo "####################################################################################################";
keytool -list -v \
  -keystore demo/client_keystore.p12 \
  -storepass "$PW"
