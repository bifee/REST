# Dependecias:
 - Java 21
 - Maven 3.X
# Executar:
Execute cada um dos comandos em um terminal próprio
## Servidor
``mvn clean package exec:java -Dexec.mainClass="br.edu.utfpr.Main"``
## Cliente
``mvn exec:java -Dexec.mainClass="br.edu.utfpr.RestClientMain"``