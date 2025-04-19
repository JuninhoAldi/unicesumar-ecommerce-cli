package com.unicesumar.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.UUID;

import com.unicesumar.entities.Product;
import com.unicesumar.entities.Sale;
import com.unicesumar.entities.SaleProduct;
import com.unicesumar.entities.User;

public class SaleRepository implements EntityRepository<Sale> {
    private final Connection connection;

    public SaleRepository(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void save(Sale sale) {
        String query = "INSERT INTO sales (id, user_id, payment_method, sale_date) VALUES (?, ?, ?, ?)";
        try {
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, sale.getUuid().toString());
            stmt.setString(2, sale.getUserId().toString());
            stmt.setString(3, sale.getPaymentMethod());
            stmt.setString(4, sale.getSaleDate().toString());
            stmt.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void saveSaleProduct(SaleProduct saleProduct) {
        String query = "INSERT INTO sale_products (sale_id, product_id) VALUES (?, ?)";
        try {
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, saleProduct.getSaleId().toString());
            stmt.setString(2, saleProduct.getProductId().toString());
            stmt.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Sale> findById(UUID id) {
        return Optional.empty(); 
    }

    @Override
    public List<Sale> findAll() {
        return new ArrayList<>(); 
    }

    @Override
    public void deleteById(UUID id) {
        String query = "DELETE FROM sales WHERE id = ?";
        try {
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, id.toString());
            stmt.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void registerSale(UserRepository userRepo, ProductRepository productRepo) {
        Scanner scanner = new Scanner(System.in);

        
        System.out.print("Digite o Email do usuário: ");
        String email = scanner.nextLine();

        Optional<User> optionalUser = userRepo.findAll().stream()
                .filter(u -> u.getEmail().equalsIgnoreCase(email))
                .findFirst();

        if (optionalUser.isEmpty()) {
            System.out.println("Usuário não encontrado.");
            return;
        }

        User user = optionalUser.get();
        System.out.println("Usuário encontrado: " + user.getName());

        
        System.out.print("Digite os IDs dos produtos (separados por vírgula): ");
        String[] idStrings = scanner.nextLine().split(",");
        List<Product> selectedProducts = new ArrayList<>();
        List<Product> allProducts = productRepo.findAll();

        for (String idStr : idStrings) {
            try {
                int id = Integer.parseInt(idStr.trim());
                if (id >= 0 && id < allProducts.size()) {
                    selectedProducts.add(allProducts.get(id));
                } else {
                    System.out.println("Produto com ID " + id + " não encontrado.");
                }
            } catch (NumberFormatException e) {
                System.out.println("ID inválido: " + idStr);
            }
        }

        if (selectedProducts.isEmpty()) {
            System.out.println("Nenhum produto válido foi selecionado.");
            return;
        }

        System.out.println("Produtos encontrados:");
        selectedProducts.forEach(p -> System.out.printf("- %s (R$ %.2f)%n", p.getName(), p.getPrice()));

        
        System.out.println("Escolha a forma de pagamento:");
        System.out.println("1 - Cartão de Crédito");
        System.out.println("2 - Boleto");
        System.out.println("3 - PIX");
        System.out.print("Opção: ");
        int opcao = Integer.parseInt(scanner.nextLine());

        String formaPagamento;
        switch (opcao) {
            case 1: formaPagamento = "Cartão de Crédito"; break;
            case 2: formaPagamento = "Boleto"; break;
            case 3: formaPagamento = "PIX"; break;
            default:
                System.out.println("Forma de pagamento inválida.");
                return;
        }

        System.out.println("Aguarde, efetuando pagamento...");
        System.out.println("Pagamento confirmado com sucesso via " + formaPagamento + 
                           ". Chave de Autenticação: " + UUID.randomUUID());

        
        UUID saleId = UUID.randomUUID();
        Sale sale = new Sale(saleId, user.getUuid(), formaPagamento, LocalDateTime.now());
        this.save(sale);
        selectedProducts.forEach(p -> this.saveSaleProduct(new SaleProduct(saleId, p.getUuid())));

        
        double total = selectedProducts.stream().mapToDouble(Product::getPrice).sum();
        System.out.println("\nResumo da venda:");
        System.out.println("Cliente: " + user.getName());
        selectedProducts.forEach(p -> System.out.println("- " + p.getName()));
        System.out.printf("Valor total: R$ %.2f%n", total);
        System.out.println("Pagamento: " + formaPagamento);
        System.out.println("Venda registrada com sucesso!");
    }
}
