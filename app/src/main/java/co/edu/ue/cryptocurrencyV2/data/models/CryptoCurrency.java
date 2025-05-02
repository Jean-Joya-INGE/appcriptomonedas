package co.edu.ue.cryptocurrencyV2.data.models;

public class CryptoCurrency {
    private String id;            // ID de la criptomoneda (ej: "bitcoin")
    private String name;          // Nombre completo (ej: "Bitcoin")
    private String symbol;        // Símbolo (ej: "BTC")
    private double currentPrice;  // Precio actual en USD
    private String imageUrl;      // URL de la imagen/logo

    // Constructor
    public CryptoCurrency(String id, String name, String symbol, double currentPrice, String imageUrl) {
        this.id = id;
        this.name = name;
        this.symbol = symbol;
        this.currentPrice = currentPrice;
        this.imageUrl = imageUrl;
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSymbol() {
        return symbol;
    }

    public double getCurrentPrice() {
        return currentPrice;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    @Override
    public String toString() {
        return name + " (" + symbol + ") - $" + currentPrice;
    }
} 