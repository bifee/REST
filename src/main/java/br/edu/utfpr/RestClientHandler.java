package br.edu.utfpr;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Scanner;

public class RestClientHandler {
    private final HttpClient httpClient;
    private final Gson gson;
    private final String serverUrl = "http://localhost:9090/carro";
    public Scanner sc = new Scanner(System.in);

    public RestClientHandler() {
        this.httpClient = HttpClient.newHttpClient();
        this.gson = new Gson();
    }

    public void handle() {
        String opcao = "";
        while (!"6".equals(opcao)) {
            System.out.println("\nEscolha uma opção:");
            System.out.println("[1] Inserir novo veículo");
            System.out.println("[2] Buscar veículo por ID");
            System.out.println("[3] Listar Todos os Veículos");
            System.out.println("[4] Remover veículo");
            System.out.println("[5] Atualizar informações de um veículo");
            System.out.println("[6] Sair");
            System.out.print("Digite o número da opção desejada: ");
            opcao = sc.nextLine();

            switch (opcao) {
                case "1" -> handleInsert();
                case "2" -> handleRead();
                case "3" -> handleListAll();
                case "4" -> handleRemove();
                case "5" -> handleUpdate();
                case "6" -> System.out.println("Encerrando...");
                default -> System.out.println("Opção inválida. Tente novamente.");
            }
        }
    }

    private void handleInsert(){
        try{
            Carro carro = insertFields();
            String jsonBody = gson.toJson(carro);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(serverUrl))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();
            httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            System.out.println("Veículo inserido com sucesso!");
        } catch (Exception e){
            System.err.println(e.getMessage());
        }
    }

    private void handleRead(){
        try{
            int id = readInt("Digite o id do veículo: ");
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(serverUrl + "/" + id))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 404) {
                System.out.printf("Carro com id %d nao encontrado%n", id);
                return;
            }

            // ------------------------------------

            if (response.statusCode() == 404) {
                System.out.printf("Carro com id %d nao encontrado%n", id);
                return;
            }

            CarroComId carroComId = gson.fromJson(response.body(), CarroComId.class);
            if (carroComId == null) {
                System.out.printf("Carro com id %d nao encontrado%n", id);
                return;
            }
            Carro c = carroComId.carro();
            System.out.printf("ID: %d | Marca: %s | Modelo: %s | Ano: %d | Cambio: %s | Tipo: %s%n",
                    carroComId.id(), c.marca(), c.modelo(),
                    c.ano(), c.cambio(), c.tipo());
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    public void handleListAll(){
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(serverUrl))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            var listType = new TypeToken<List<CarroComId>>() {}.getType();
            List<CarroComId> lista = gson.fromJson(response.body(), listType);

            System.out.println("\n--- Lista de Veículos Cadastrados ---");
            for (CarroComId carroComId : lista) {
                System.out.printf("ID: %d | Marca: %s | Modelo: %s | Ano: %d | Cambio: %s | Tipo: %s%n",
                        carroComId.id(), carroComId.carro().marca(), carroComId.carro().modelo(), carroComId.carro().ano(), carroComId.carro().cambio(), carroComId.carro().tipo());
            }
        } catch (Exception e) {
            System.err.println("Erro ao listar veículos: " + e.getMessage());
        }
    }

    private void handleRemove(){
        handleListAll();
        try{
            int id = readInt("Digite o id do veiculo que deseja remover: ");
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(serverUrl + "/" + id))
                    .DELETE()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e){
            System.err.println(e.getMessage());
        }
    }

    private void handleUpdate(){
        handleListAll();
        try{
            int id = readInt("Digite o id do veiculo que deseja atualizar: ");
            Carro carro = insertFields();
            String jsonBody = gson.toJson(carro);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(serverUrl + "/" + id))
                    .header("Content-Type", "application/json")
                    .PUT(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();
            httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e){
            System.err.println(e.getMessage());
        }
    }

    private Carro insertFields(){
        try{
            System.out.println("Insira as informacoes do veiculo:");
            System.out.print("Marca: ");
            String marca = sc.nextLine();
            System.out.print("Modelo: ");
            String modelo = sc.nextLine();
            int ano = readInt("Ano: ");
            System.out.print("Cambio: ");
            String cambio = sc.nextLine();
            System.out.print("Tipo de carroceria: ");
            String tipo = sc.nextLine();
            return new Carro(marca, modelo, ano, cambio, tipo);
        }catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return null;
    }

    
    private int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = sc.nextLine();
            if (input == null) {
                System.out.println("Entrada inválida. Tente novamente.");
                continue;
            }
            input = input.trim();
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException ex) {
                System.out.println("Entrada inválida. Digite um número inteiro.");
            }
        }
    }
}