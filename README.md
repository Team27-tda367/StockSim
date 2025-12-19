# StockSim - Stock Market Trading Simulator

A JavaFX-based stock trading simulator with bot traders using different strategies. The application supports both a **simulation mode** for generating market data and a **display mode** for visualizing and interacting with that data.

## Features

* Browse stocks by category (Technology, Finance, Consumer, etc.)
* Place limit and market orders (buy/sell)
* View your portfolio with positions and balance
* Track order history and completed trades
* Real-time price charts for each stock
* Market simulation with 8 bot strategies competing against you
* Automatic order matching engine

## Bot Strategies
StockSim includes multiple automated trading bots that compete in the market alongside the user. Each bot follows a predefined strategy that determines how it places orders.

### Available Strategies

- **DayTraderStrategy**  
  Actively buys and sells throughout the trading day to capture short-term price movements, never holding positions for long.

- **PanicSellerStrategy**  
  Closely monitors prices and immediately sells at the first sign of a price drop, rarely buying and preferring to hold cash.

- **MomentumTraderStrategy**  
  Buys stocks with upward price momentum and quickly sells those that begin to decline, following market trends.

- **InstitutionalInvestorStrategy**  
  Receives periodic capital injections and makes diversified, long-term investments with relatively infrequent trades.

- **HodlerStrategy**  
  Purchases stocks occasionally and almost never sells, only taking profits after substantial gains.

- **FocusedTraderStrategy**  
  Trades actively within a small, curated watchlist of preferred stocks rather than the entire market.

- **RandomStrategy**  
  Makes random buy and sell decisions with a slight bias toward buying, without a coherent trading strategy.

## Configuration

The application uses **two configuration files**, located in:

```
src/main/resources/config
```

* **Initial Stock Configuration**
  Defines the initial stock universe, including symbols, categories, starting prices, and other stock-related parameters.

* **Initial Bot Configuration**
  Defines the bot traders, including their strategies, starting capital, and starting positions.

These configuration files are used as the **source of truth for initial data**, especially when no previously generated simulation data exists.

## Simulation & Data Flow

StockSim supports two run modes: **simulation** and **display**.

### Simulation Mode (`-sim`)

Simulation mode is used to **generate market data**.

* The simulation runs for **8 seconds of real time**.
* During this time, the market runs at **3600× speed**.
* This results in a total simulated duration of **8 hours** of market activity, roughly equivalent to a full trading day.
* Bot traders actively place and match orders throughout the simulation.

At the end of the simulation:

* Final **bot positions** are saved to disk.
* Final **stock price histories** are saved to disk.

All generated data is written to:

```
src/main/resources/data
```

These files persist the results of the simulation and can be reused later.

### Display Mode (`-display`)

Display mode is used to **visualize and interact with market data**.

When starting in display mode:

1. The application first checks for existing data files in:

   ```
   src/main/resources/data
   ```
2. If data files exist:

   * Bot positions and stock prices are loaded from these files.
3. If data files do **not** exist:

   * Initial stock data is loaded from the stock config file.
   * Initial bot data is loaded from the bot config file.

This allows you to:

* Run a simulation once and reuse its results
* Or skip simulation entirely and start directly from configured defaults

## Build & Run

### Build

```bash
mvn clean install
```

### Run Simulation (generate data)

```bash
mvn exec:java -D exec.args="-sim"
```

### Run GUI (display mode)

```bash
mvn exec:java -D exec.args="-display"
```

## Tech Stack

* Java 25
* JavaFX 25
* Maven
* Gson (JSON persistence)
