# Step 1: Create a Certificate Authority (CA)

## Generate the CA Private Key:

```bash
openssl genrsa -out ca.key 2048
```
## Create the CA Certificate (Self-Signed):
```bash
openssl req -x509 -new -nodes -key ca.key -sha256 -days 3650 \
-subj "/C=US/ST=YourState/L=YourCity/O=YourOrganization/OU=YourUnit/CN=DevSchool-CA" \
-out ca.crt
```
# Step 2: Create a Server Certificate

## Generate the Server Private Key:

```bash
openssl genrsa -out server.key 2048
```

## Create a Certificate Signing Request (CSR):

openssl genrsa -out server.key 2048
```bash
openssl req -new -key server.key \
-subj "/C=US/ST=YourState/L=YourCity/O=YourOrganization/OU=YourUnit/CN=localhost" \
-out server.csr
```

## Sign the Server Certificate with the CA:

```bash
openssl x509 -req -in server.csr -CA ca.crt -CAkey ca.key -CAcreateserial \
-out server.crt -days 365 -sha256
```

# Convert the certificate to .p12 format

```bash
openssl pkcs12 -export -inkey server.key -in server.crt -out server.p12 -name "server-alias" \
-passout pass:server-pass
```

# Step 3: Add the Spring Boot properties
```properties
server.port=8443
server.ssl.key-store=classpath:server.p12
server.ssl.key-store-password=server-pass
server.ssl.key-store-type=PKCS12
server.ssl.key-alias=server-alias
```

# Step 4: check if HTTP request works
Start the app then check if the following script works:

```bash
curl --show-headers --location 'https://localhost:8443/users' \
--header 'Content-Type: application/json' \
--cacert ca.crt \
--data '{
    "username": "Ragnar",
    "password": "1234",
    "cnp": "12345678"
}'
```