
[![](https://github.com/wutsi/wutsi-login-bff/actions/workflows/master.yml/badge.svg)](https://github.com/wutsi/wutsi-login-bff/actions/workflows/master.yml)

[![JDK](https://img.shields.io/badge/jdk-11-brightgreen.svg)](https://jdk.java.net/11/)
[![](https://img.shields.io/badge/maven-3.6-brightgreen.svg)](https://maven.apache.org/download.cgi)
![](https://img.shields.io/badge/language-kotlin-blue.svg)


# wutsi-login-bff
BFF for the login flow, for authenticating users via their PIN.
Once user are authenticated:
- The access token is returned to client via the header ``x-access-token``
- The onboarding flag is returned to client via the response header ``x-onboarded``, with the value ``true``.

