package org.team27.stocksim.ui.terminal;

import org.team27.stocksim.controller.ISimController;
import org.team27.stocksim.model.market.Instrument;
import org.team27.stocksim.observer.ModelObserver;
import org.team27.stocksim.ui.IViewInit;

import java.util.HashMap;
import java.util.Scanner;

/**
 * Terminal-based view implementation for the StockSim application.
 * Provides a command-line interface for interacting with the stock simulation.
 */
public class TerminalView implements IViewInit {
    private ISimController controller;
    private Scanner scanner;
    private volatile boolean running;
    private HashMap<String, Object> stocks;

    public TerminalView() {
        this.scanner = new Scanner(System.in);
        this.running = false;
        this.stocks = new HashMap<>();
    }

    @Override
    public void setController(ISimController controller) {
        this.controller = controller;
    }

    @Override
    public void show() {
        running = true;
        displayWelcome();
        runMainLoop();
    }

    private void displayWelcome() {
        clearScreen();
        System.out.println("╔════════════════════════════════════════╗");
        System.out.println("║         STOCKSIM TERMINAL v1.0         ║");
        System.out.println("║    Stock Market Simulation System      ║");
        System.out.println("╚════════════════════════════════════════╝");
        System.out.println();
    }

    private void runMainLoop() {
        while (running) {
            displayMainMenu();
            String choice = getUserInput("Enter your choice: ");
            handleMenuChoice(choice);
        }
        scanner.close();
    }

    private void displayMainMenu() {
        System.out.println("\n" + "=".repeat(40));
        System.out.println("MAIN MENU");
        System.out.println("=".repeat(40));
        System.out.println("1. Create Stock");
        System.out.println("2. View All Stocks");
        System.out.println("3. View Market Status");
        System.out.println("4. Help");
        System.out.println("5. Exit");
        System.out.println("=".repeat(40));
    }

    private void handleMenuChoice(String choice) {
        switch (choice.trim()) {
            case "1":
                createStockDialog();
                break;
            case "2":
                viewAllStocks();
                break;
            case "3":
                viewMarketStatus();
                break;
            case "4":
                displayHelp();
                break;
            case "5":
                exitApplication();
                break;
            default:
                System.out.println("❌ Invalid choice. Please enter a number between 1 and 5.");
                pause();
        }
    }

    private void createStockDialog() {
        clearScreen();
        System.out.println("\n╔════════════════════════════════════════╗");
        System.out.println("║          CREATE NEW STOCK              ║");
        System.out.println("╚════════════════════════════════════════╝\n");

        String symbol = getUserInput("Enter stock symbol (e.g., AAPL): ");
        if (symbol.isEmpty()) {
            System.out.println("❌ Stock symbol cannot be empty.");
            pause();
            return;
        }

        String name = getUserInput("Enter stock name (e.g., Apple Inc.): ");
        if (name.isEmpty()) {
            System.out.println("❌ Stock name cannot be empty.");
            pause();
            return;
        }

        String tickSize = getUserInput("Enter tick size (e.g., 0.01): ");
        if (!isValidNumber(tickSize)) {
            System.out.println("❌ Invalid tick size. Must be a valid number.");
            pause();
            return;
        }

        String lotSize = getUserInput("Enter lot size (e.g., 100): ");
        if (!isValidNumber(lotSize)) {
            System.out.println("❌ Invalid lot size. Must be a valid number.");
            pause();
            return;
        }

        // Confirm creation
        System.out.println("\n" + "-".repeat(40));
        System.out.println("Stock Details:");
        System.out.println("  Symbol:    " + symbol);
        System.out.println("  Name:      " + name);
        System.out.println("  Tick Size: " + tickSize);
        System.out.println("  Lot Size:  " + lotSize);
        System.out.println("-".repeat(40));

        String confirm = getUserInput("Create this stock? (y/n): ");
        if (confirm.equalsIgnoreCase("y") || confirm.equalsIgnoreCase("yes")) {
            try {
                controller.createStock(symbol, name, tickSize, lotSize);
                // The newStockCreated callback will display success message
            } catch (Exception e) {
                System.out.println("❌ Error creating stock: " + e.getMessage());
            }
        } else {
            System.out.println("Stock creation cancelled.");
        }
        pause();
    }

    private void viewAllStocks() {
        clearScreen();
        System.out.println("\n╔════════════════════════════════════════╗");
        System.out.println("║           ALL STOCKS                   ║");
        System.out.println("╚════════════════════════════════════════╝\n");

        if (stocks.isEmpty()) {
            System.out.println("No stocks available. Create a stock to get started!");
        } else {
            System.out.println(String.format("%-10s | %-25s", "SYMBOL", "NAME"));
            System.out.println("-".repeat(40));
            stocks.forEach((key, value) -> {
                System.out.println(String.format("%-10s | %-25s", key, value));
            });
            System.out.println("\nTotal stocks: " + stocks.size());
        }
        pause();
    }

    private void viewMarketStatus() {
        clearScreen();
        System.out.println("\n╔════════════════════════════════════════╗");
        System.out.println("║         MARKET STATUS                  ║");
        System.out.println("╚════════════════════════════════════════╝\n");

        System.out.println("Market Status: OPEN");
        System.out.println("Total Stocks: " + stocks.size());
        System.out.println("Active Traders: 0");
        System.out.println("Total Volume: 0");
        System.out.println("\nNote: Extended market data coming soon!");

        pause();
    }

    private void displayHelp() {
        clearScreen();
        System.out.println("\n╔════════════════════════════════════════╗");
        System.out.println("║              HELP GUIDE                ║");
        System.out.println("╚════════════════════════════════════════╝\n");

        System.out.println("Available Commands:");
        System.out.println("  1. Create Stock    - Add a new stock to the market");
        System.out.println("  2. View All Stocks - List all available stocks");
        System.out.println("  3. Market Status   - View current market information");
        System.out.println("  4. Help            - Display this help guide");
        System.out.println("  5. Exit            - Quit the application");
        System.out.println("\nStock Creation:");
        System.out.println("  - Symbol: Short identifier (e.g., AAPL, MSFT)");
        System.out.println("  - Name: Full company name");
        System.out.println("  - Tick Size: Minimum price increment");
        System.out.println("  - Lot Size: Minimum trading quantity");

        pause();
    }

    private void exitApplication() {
        System.out.println("\nThank you for using StockSim Terminal!");
        System.out.println("Goodbye! 👋\n");
        running = false;
    }

    private String getUserInput(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine();
    }

    private boolean isValidNumber(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private void clearScreen() {
        // For cross-platform compatibility, print multiple newlines
        // A more sophisticated version could use ANSI codes or ProcessBuilder
        System.out.print("\n".repeat(2));
    }

    private void pause() {
        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }
}
