# StockSim - Stock Market Trading Simulator

A JavaFX-based stock trading simulator with bot traders using different strategies.

## Features

- Browse stocks by category (Technology, Finance, Healthcare, etc.)
- Place limit and market orders (buy/sell)
- View your portfolio with positions and balance
- Track order history and completed trades
- Real-time price charts for each stock
- Market simulation with 8 bot strategies competing against you
- Automatic order matching engine

## Build & Run

**Build:**

```bash
mvn clean install
```

**Run Simulation (generate data):**

```bash
mvn exec:java -D exec.args="-sim"
```

**Run GUI:**

```bash
mvn exec:java -D exec.args="-display"
```

## Tech Stack

- Java 25
- JavaFX 25
- Maven
- Gson (JSON persistence)

## Architecture

MVC pattern with Observer, Strategy, Factory, DTO, and Repository patterns.
