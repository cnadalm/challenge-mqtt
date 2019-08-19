# How the certificates have been created


## Generate an own CA certificate

1) Create a key pair for the CA

	openssl genrsa -des3 -out ca.key 2048
	==> pwd: bigfinite

2) Create a CA certificate using the private key generated above
	
	openssl req -new -x509 -days 1826 -key ca.key -out ca.crt
	==> CN: cabigfinite


## Generate a certificate for MQTT broker, signed/verified by the CA

1) Create a key pair:

	openssl genrsa -out broker.key 2048

2) Create a certificate signing request (.csr) for :: broker

	openssl req -new -out broker.csr -key broker.key

3) Create the broker certification using the certification request, and the CA key and certificate 

	openssl x509 -req -in broker.csr -CA ca.crt -CAkey ca.key -CAcreateserial -out broker.crt -days 360


## Generate a certificate for MQTT client, signed/verified by the CA

1) Create a key pair:

	openssl genrsa -out client.key 2048

2) Create a certificate signing request (.csr) 

	openssl req -new -out client.csr -key client.key

3) Create the broker certification using the certification request, and the CA key and certificate 

	openssl x509 -req -in client.csr -CA ca.crt -CAkey ca.key -set_serial 1 -out client.crt -days 360


## Generate a certificate for HTTPS server, signed/verified by the CA

1) Create a key pair:

	openssl genrsa -out server.key 2048

2) Create a certificate signing request (.csr) for :: server

	openssl req -new -out server.csr -key server.key

3) Create the server certification using the certification request, and the CA key and certificate 

	openssl x509 -req -in server.csr -CA ca.crt -CAkey ca.key -set_serial 2 -out server.crt -days 360

4) Bundle the private key and the certificate

	cat server.key server.crt ca.crt > server.fullchain.pem

5) Bundle client key into a PFX file

	openssl pkcs12 -export -out server.fullchain.pfx -inkey server.key -in server.crt -certfile ca.crt -name server
	==> pwd: bigfinite




## Allow HTTPS connections 

### Server side

6 ) Convert the x.509 cert and key to a pkcs12 file

	openssl pkcs12 -export -in server.pem -inkey server.key -out server.p12 -name server -CAfile ca.crt -caname root
	==> pwd: bigfinite

7 ) Convert the pkcs12 file to a Java keystore

	keytool -importkeystore -deststorepass password -destkeypass password -destkeystore server.keystore -srckeystore server.p12 -srcstoretype PKCS12 -srcstorepass bigfinite -alias server
	==> Note the 'password' destination passwords, because of a bug of Quarkus SSL management
