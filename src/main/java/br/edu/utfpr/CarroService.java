package br.edu.utfpr;

import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Service
public class CarroService {
    private static final String URL = "jdbc:sqlite:teste.db";

    public CarroService(){
        initializeDB();
    }

    public void initializeDB(){
        String sql = "CREATE TABLE IF NOT EXISTS carros(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "marca TEXT NOT NULL," +
                "modelo TEXT NOT NULL," +
                "ano INTEGER NOT NULL," +
                "cambio TEXT NOT NULL," +
                "tipo TEXT NOT NULL)";
        try (Connection conn = DriverManager.getConnection(URL);
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            System.err.println("Erro ao criar tabela: " + e.getMessage());
        }
    }

    public void insert(Carro carro){
        String sql = "INSERT INTO carros(marca, modelo, ano, cambio, tipo) VALUES(?,?,?,?,?)";
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, carro.marca());
            pstmt.setString(2, carro.modelo());
            pstmt.setInt(3, carro.ano());
            pstmt.setString(4, carro.cambio());
            pstmt.setString(5, carro.tipo());

            int affectedRows = pstmt.executeUpdate();
            ResultSet rs = pstmt.getGeneratedKeys();
            System.out.printf("Veículo inserido com o id: %s%n", rs.getInt(1));
        } catch (SQLException e) {
            System.err.println("Erro ao adicionar carro: " + e.getMessage());
        }
    }

    public CarroComId read(int id){
        String sql = "SELECT * FROM carros WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                Carro carro = new Carro(
                        rs.getString("marca"),
                        rs.getString("modelo"),
                        rs.getInt("ano"),
                        rs.getString("cambio"),
                        rs.getString("tipo")
                );
                return (new CarroComId(rs.getInt("id"), carro));
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar carro: " + e.getMessage());
        }
        return null;
    }

    public List<CarroComId> listAll(){
        List<CarroComId> lista = new ArrayList<>();
        String sql = "SELECT * FROM carros";
        try (Connection conn = DriverManager.getConnection(URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Carro carro = new Carro(
                        rs.getString("marca"),
                        rs.getString("modelo"),
                        rs.getInt("ano"),
                        rs.getString("cambio"),
                        rs.getString("tipo")
                );
                lista.add(new CarroComId(rs.getInt("id"), carro));
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar todos os carros: " + e.getMessage());
        }
        return lista;
    }

    public boolean delete(int id){
        String sql = "DELETE FROM carros WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.printf("DB: Successfully deleted item with id: %s%n", id);
                return true;
            } else {
                System.out.printf("DB Warning: Delete did not affect any rows for item id: %s%n", id);
                return false;
            }
        } catch (SQLException e) {
            System.err.println("Erro ao deletar carro: " + e.getMessage());
            return false;
        }
    }

    public boolean update(int id, Carro carro){
        String sql = "UPDATE carros SET marca = ?, modelo = ?, ano = ?, cambio = ?, tipo = ? WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, carro.marca());
            pstmt.setString(2, carro.modelo());
            pstmt.setInt(3, carro.ano());
            pstmt.setString(4, carro.cambio());
            pstmt.setString(5, carro.tipo());

            pstmt.setInt(6, id);

            return pstmt.executeUpdate() > 0;
        } catch (SQLException | NumberFormatException e) {
            System.err.println("Erro ao atualizar carro: " + e.getMessage());
            return false;
        }
    }

}
