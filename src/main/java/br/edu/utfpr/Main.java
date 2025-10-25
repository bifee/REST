package br.edu.utfpr;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.rmi.RemoteException;
import java.util.Scanner;
@SpringBootApplication
public class Main {
    public static void main(String[] args){
        System.setProperty("server.port", "9090");
        SpringApplication.run(Main.class, args);
        System.out.println("Servidor REST iniciado em http://localhost:8080");
    }
}