import java.io.*;
import java.time.LocalDate;
import java.util.*;

class Expense {
    int id;
    String description;
    int amount;
    LocalDate date;

    public Expense(int id, String description, int amount, LocalDate date) {
        this.id = id;
        this.description = description;
        this.amount = amount;
        this.date = date;
    }

    @Override
    public String toString() {
        return id + "   " + date + "   " + description + "   $" + amount;
    }
}

public class ExpenseTracker {
    private static final String FILE = "expenses.txt";

    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Usage: expense-tracker <command> [options]");
            return;
        }

        String command = args[0];
        List<Expense> expenses = loadExpenses();

        switch (command) {
            case "add":
                String description = null;
                int amount = 0;
                for (int i = 1; i < args.length; i++) {
                    if (args[i].equals("--description")) {
                        description = args[++i];
                    } else if (args[i].equals("--amount")) {
                        amount = Integer.parseInt(args[++i]);
                    }
                }
                if (description == null || amount == 0) {
                    System.out.println("Usage: expense-tracker add --description <desc> --amount <amt>");
                    return;
                }
                addExpense(expenses, description, amount);
                break;

            case "list":
                listExpenses(expenses);
                break;

            case "summary":
                if (args.length == 3 && args[1].equals("--month")) {
                    int month = Integer.parseInt(args[2]);
                    summaryExpenses(expenses, month);
                } else {
                    summaryExpenses(expenses, null);
                }
                break;

            case "delete":
                int id = 0;
                for (int i = 1; i < args.length; i++) {
                    if (args[i].equals("--id")) {
                        id = Integer.parseInt(args[++i]);
                    }
                }
                if (id == 0) {
                    System.out.println("Usage: expense-tracker delete --id <id>");
                    return;
                }
                deleteExpense(expenses, id);
                break;

            default:
                System.out.println("Invalid command");
        }

        saveExpenses(expenses);
    }

    // Load expenses from file
    private static List<Expense> loadExpenses() {
        List<Expense> expenses = new ArrayList<>();
        try {
            File file = new File(FILE);
            if (!file.exists()) return expenses;

            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split("\\|");
                expenses.add(new Expense(
                        Integer.parseInt(parts[0]),
                        parts[1],
                        Integer.parseInt(parts[2]),
                        LocalDate.parse(parts[3])
                ));
            }
            br.close();
        } catch (Exception e) {
            System.out.println("Error loading expenses: " + e.getMessage());
        }
        return expenses;
    }

    // Save expenses to file
    private static void saveExpenses(List<Expense> expenses) {
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(FILE));
            for (Expense e : expenses) {
                bw.write(e.id + "|" + e.description + "|" + e.amount + "|" + e.date);
                bw.newLine();
            }
            bw.close();
        } catch (Exception e) {
            System.out.println("Error saving expenses: " + e.getMessage());
        }
    }

    // Add expense
    private static void addExpense(List<Expense> expenses, String description, int amount) {
        int id = expenses.size() + 1;
        Expense e = new Expense(id, description, amount, LocalDate.now());
        expenses.add(e);
        System.out.println("Expense added successfully (ID: " + id + ")");
    }

    // List expenses
    private static void listExpenses(List<Expense> expenses) {
        System.out.println("ID   Date       Description   Amount");
        for (Expense e : expenses) {
            System.out.println(e);
        }
    }

    // Summary (optionally filter by month)
    private static void summaryExpenses(List<Expense> expenses, Integer monthFilter) {
        int total = 0;
        for (Expense e : expenses) {
            if (monthFilter == null || e.date.getMonthValue() == monthFilter) {
                total += e.amount;
            }
        }
        if (monthFilter == null) {
            System.out.println("Total expenses: $" + total);
        } else {
            System.out.println("Total expenses for month " + monthFilter + ": $" + total);
        }
    }

    // Delete expense
    private static void deleteExpense(List<Expense> expenses, int id) {
        boolean removed = expenses.removeIf(e -> e.id == id);
        if (removed) {
            System.out.println("Expense deleted successfully");
        } else {
            System.out.println("Expense not found");
        }
    }
}