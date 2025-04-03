package com.unicesumar;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;

import com.unicesumar.entities.Product;
import com.unicesumar.entities.User;
import com.unicesumar.repository.ProductRepository;
import com.unicesumar.repository.UserRepository;

public class Main {
    public static void main(String[] args) {
        ProductRepository listaDeProdutos = null;
        UserRepository listaDeUsuarios = null;
        Connection conn = null;

        // Parâmetros de conexão
        String url = "jdbc:sqlite:database.sqlite";

        // Tentativa de conexão
        try {
            conn = DriverManager.getConnection(url);
            if (conn != null) {
                listaDeProdutos = new ProductRepository(conn);
                listaDeUsuarios = new UserRepository(conn);
            } else {
                System.out.println("Falha na conexão.");
                System.exit(1);
            }
        } catch (SQLException e) {
            System.out.println("Erro ao conectar: " + e.getMessage());
            System.exit(1);
        }

        Scanner scanner = new Scanner(System.in);
        int option;

        do {
            System.out.println("\n---MENU---");
            System.out.println("1 - Cadastrar Produto");
            System.out.println("2 - Listar Produtos");
            System.out.println("3 - Cadastrar Usuário");
            System.out.println("4 - Listar Usuários");
            System.out.println("5 - Sair");
            System.out.print("Escolha uma opção: ");
            option = scanner.nextInt();
            scanner.nextLine(); 

            switch (option) {
                case 1:
                    System.out.println("Cadastrar Produto");
                    listaDeProdutos.save(new Product("Teste", 10));
                    listaDeProdutos.save(new Product("Computador", 3000));
                    break;

                case 2:
                    System.out.println("Listar Produtos");
                    List<Product> products = listaDeProdutos.findAll();
                    products.forEach(System.out::println);
                    break;

                case 3:
                    System.out.println("\nCadastrar Usuário");
                    System.out.print("Nome: ");
                    String name = scanner.nextLine();
                    System.out.print("E-mail: ");
                    String email = scanner.nextLine();
                    System.out.print("Senha: ");
                    String password = scanner.nextLine();

                    UUID uuid = UUID.randomUUID();
                    User user = new User(uuid, name, email, password);
                    listaDeUsuarios.save(user);
                    System.out.println("Usuário cadastrado");
                    break;

                case 4:
                    System.out.println("\nLista de Usuários Cadastrados:");

                    List<User> users = listaDeUsuarios.findAll();
                    if (users.isEmpty()) {
                        System.out.println("Nenhum usuário cadastrado.");
                    } else {
                        for (User u : users) {
                            System.out.println("UUID: " + u.getUuid());
                            System.out.println("Nome: " + u.getName());
                            System.out.println("E-mail: " + u.getEmail());
                            System.out.println("---");
                        }
                    }
                    break;

                case 5:
                    System.out.println("Saindo...");
                    break;

                default:
                    System.out.println("Opção inválida. Tente novamente.");
            }

        } while (option != 5);

        scanner.close();
        try {
            conn.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
