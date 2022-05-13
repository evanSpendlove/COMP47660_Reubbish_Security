#!/bin/bash
rm -f src/main/resources/hsekey.p12
keytool -genkeypair -alias hsekey -keyalg RSA -keysize 2048 -storetype PKCS12 -keystore src/main/resources/hsekey.p12 -validity 3650 -dname "cn=Unknown, ou=Unknown, o=Unknown, c=Unknown" -keypass 123456 -storepass 123456
