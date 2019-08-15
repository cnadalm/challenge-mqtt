1) Create a key pair for the CA

	openssl genrsa -des3 -out ca.key 2048
	==> pwd: bigfinite

2) Create a certificate for the CA using the CA key generated before
	
	openssl req -new -x509 -days 1826 -key ca.key -out ca.crt
	==> CN: cabigfinite

3) Create server key pair:

	openssl genrsa -out server.key 2048

4) Create a certificate request .csr

	openssl req -new -out server.csr -key server.key

5) Create the server certification using the CA key and certificate 

	openssl x509 -req -in server.csr -CA ca.crt -CAkey ca.key -CAcreateserial -out server.crt -days 360
