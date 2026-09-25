$projectName = "albaraka-bank-analyzer"
$baseDir = Join-Path (Get-Location) $projectName
$srcMainJava = Join-Path $baseDir "src/main/java/com/solubank"
$srcMainRes = Join-Path $baseDir "src/main/resources"

$dirs = @(
    $srcMainRes,
    "$srcMainJava/entity",
    "$srcMainJava/dao/impl",
    "$srcMainJava/service",
    "$srcMainJava/ui",
    "$srcMainJava/util",
    "$srcMainJava/exception"
)

foreach ($dir in $dirs) {
    New-Item -ItemType Directory -Force -Path $dir | Out-Null
}

$files = @{
    "$srcMainJava/entity/Client.java" = "package com.solubank.entity;`n`npublic record Client(int id, String nom, String email) {}"
    "$srcMainJava/entity/TypeCompte.java" = "package com.solubank.entity;`n`npublic enum TypeCompte {`n    COURANT, EPARGNE`n}"
    "$srcMainJava/entity/TypeTransaction.java" = "package com.solubank.entity;`n`npublic enum TypeTransaction {`n    VERSEMENT, RETRAIT, VIREMENT`n}"
    "$srcMainJava/entity/Compte.java" = "package com.solubank.entity;`n`npublic abstract sealed class Compte permits CompteCourant, CompteEpargne {`n}"
    "$srcMainJava/entity/CompteCourant.java" = "package com.solubank.entity;`n`npublic final class CompteCourant extends Compte {`n}"
    "$srcMainJava/entity/CompteEpargne.java" = "package com.solubank.entity;`n`npublic final class CompteEpargne extends Compte {`n}"
    "$srcMainJava/entity/Transaction.java" = "package com.solubank.entity;`n`nimport java.time.LocalDateTime;`n`npublic record Transaction(int id, LocalDateTime date, double montant, TypeTransaction type, String lieu, int idCompte) {}"
    "$srcMainJava/dao/ClientDAO.java" = "package com.solubank.dao;`n`npublic interface ClientDAO {`n}"
    "$srcMainJava/dao/CompteDAO.java" = "package com.solubank.dao;`n`npublic interface CompteDAO {`n}"
    "$srcMainJava/dao/TransactionDAO.java" = "package com.solubank.dao;`n`npublic interface TransactionDAO {`n}"
    "$srcMainJava/dao/impl/ClientDAOImpl.java" = "package com.solubank.dao.impl;`n`nimport com.solubank.dao.ClientDAO;`n`npublic class ClientDAOImpl implements ClientDAO {`n}"
    "$srcMainJava/dao/impl/CompteDAOImpl.java" = "package com.solubank.dao.impl;`n`nimport com.solubank.dao.CompteDAO;`n`npublic class CompteDAOImpl implements CompteDAO {`n}"
    "$srcMainJava/dao/impl/TransactionDAOImpl.java" = "package com.solubank.dao.impl;`n`nimport com.solubank.dao.TransactionDAO;`n`npublic class TransactionDAOImpl implements TransactionDAO {`n}"
    "$srcMainJava/service/ClientService.java" = "package com.solubank.service;`n`npublic class ClientService {`n}"
    "$srcMainJava/service/CompteService.java" = "package com.solubank.service;`n`npublic class CompteService {`n}"
    "$srcMainJava/service/TransactionService.java" = "package com.solubank.service;`n`npublic class TransactionService {`n}"
    "$srcMainJava/service/RapportService.java" = "package com.solubank.service;`n`npublic class RapportService {`n}"
    "$srcMainJava/ui/ConsoleUI.java" = "package com.solubank.ui;`n`npublic class ConsoleUI {`n}"
    "$srcMainJava/util/DatabaseConnection.java" = "package com.solubank.util;`n`npublic class DatabaseConnection {`n}"
    "$srcMainJava/util/ValidationUtils.java" = "package com.solubank.util;`n`npublic class ValidationUtils {`n}"
    "$srcMainJava/util/DateUtils.java" = "package com.solubank.util;`n`npublic class DateUtils {`n}"
    "$srcMainJava/exception/SoldeInsuffisantException.java" = "package com.solubank.exception;`n`npublic class SoldeInsuffisantException extends RuntimeException {`n}"
    "$srcMainJava/exception/ResourceNotFoundException.java" = "package com.solubank.exception;`n`npublic class ResourceNotFoundException extends RuntimeException {`n}"
    "$srcMainJava/exception/BusinessRuleException.java" = "package com.solubank.exception;`n`npublic class BusinessRuleException extends RuntimeException {`n}"
    "$srcMainJava/Main.java" = "package com.solubank;`n`nimport com.solubank.ui.ConsoleUI;`n`npublic class Main {`n    public static void main(String[] args) {`n        System.out.println(`"Starting Albaraka Bank Analyzer...`");`n    }`n}"
    "$srcMainRes/schema.sql" = "-- DDL for Client, Compte, Transaction tables`nCREATE TABLE Client (`n    id SERIAL PRIMARY KEY,`n    nom VARCHAR(100),`n    email VARCHAR(100)`n);`n`nCREATE TABLE Compte (`n    id SERIAL PRIMARY KEY,`n    type_compte VARCHAR(50),`n    solde DECIMAL(15,2),`n    client_id INT REFERENCES Client(id)`n);`n`nCREATE TABLE Transaction (`n    id SERIAL PRIMARY KEY,`n    date TIMESTAMP,`n    montant DECIMAL(15,2),`n    type VARCHAR(50),`n    lieu VARCHAR(100),`n    compte_id INT REFERENCES Compte(id)`n);"
    "$srcMainRes/application.properties" = "db.url=jdbc:postgresql://localhost:5432/albaraka`ndb.user=postgres`ndb.password=root"
}

foreach ($filePath in $files.Keys) {
    Set-Content -Path $filePath -Value $files[$filePath]
}

$pom = @"
<?xml version=`"1.0`" encoding=`"UTF-8`"?>
<project xmlns=`"http://maven.apache.org/POM/4.0.0`"
         xmlns:xsi=`"http://www.w3.org/2001/XMLSchema-instance`"
         xsi:schemaLocation=`"http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd`">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.solubank</groupId>
    <artifactId>albaraka-bank-analyzer</artifactId>
    <version>1.0-SNAPSHOT</version>

    <properties>
        <maven.compiler.source>17</maven.compiler.source>
        <maven.compiler.target>17</maven.compiler.target>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    </properties>

    <dependencies>
        <dependency>
            <groupId>org.postgresql</groupId>
            <artifactId>postgresql</artifactId>
            <version>42.7.2</version>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-shade-plugin</artifactId>
                <version>3.5.2</version>
                <executions>
                    <execution>
                        <phase>package</phase>
                        <goals>
                            <goal>shade</goal>
                        </goals>
                        <configuration>
                            <transformers>
                                <transformer implementation=`"org.apache.maven.plugins.shade.resource.ManifestResourceTransformer`">
                                    <mainClass>com.solubank.Main</mainClass>
                                </transformer>
                            </transformers>
                        </configuration>
                    </execution>
                </executions>
            </plugin>
        </plugins>
    </build>
</project>
"@

Set-Content -Path "$baseDir/pom.xml" -Value $pom
Write-Host "Bootstrap completed in $baseDir"

