package org.team27.stocksim.model.portfolio;

public class Portfolio {

    private double balance;

    public Portfolio(double traderBalance) {
        this.balance = traderBalance;
        System.out.println("new Portfolio created");
    }

    public double getBalance() {
        return balance;
    }

    public void deposit(double amount) {
        this.balance += amount;
    }

    public boolean withdraw(double amount) {
        double newBalance = this.balance - amount;
        if(newBalance >= 0) {
            balance = newBalance;
            return true; // succesfull withdrawal
        }
        return false; // unsuccessfull withdrawal (not enough balance)
    }
}
