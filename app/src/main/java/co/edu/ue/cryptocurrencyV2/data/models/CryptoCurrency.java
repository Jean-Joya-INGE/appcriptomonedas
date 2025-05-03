package co.edu.ue.cryptocurrencyV2.data.models;

public class CryptoCurrency {
    private String id; // ID de la criptomoneda (ej: "bitcoin")
    private String name; // Nombre completo (ej: "Bitcoin")
    private String symbol; // Símbolo (ej: "BTC")
    private double currentPrice; // Precio actual en USD
    private String imageUrl; // URL de la imagen/logo
    private double priceChangePercentage24h; // Cambio de precio en las últimas 24h

    // Constructor
    public CryptoCurrency(String id, String name, String symbol, double currentPrice, String imageUrl) {
        this.id = id;
        this.name = name;
        this.symbol = symbol;
        this.currentPrice = currentPrice;
        this.imageUrl = imageUrl;
        this.priceChangePercentage24h = 0.0;
    }

    // Constructor con cambio de precio
    public CryptoCurrency(String id, String name, String symbol, double currentPrice, String imageUrl,
            double priceChangePercentage24h) {
        this.id = id;
        this.name = name;
        this.symbol = symbol;
        this.currentPrice = currentPrice;
        this.imageUrl = imageUrl;
        this.priceChangePercentage24h = priceChangePercentage24h;
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

    public double getPriceChangePercentage24h() {
        return priceChangePercentage24h;
    }

    // Setter para cambio de precio
    public void setPriceChangePercentage24h(double priceChangePercentage24h) {
        this.priceChangePercentage24h = priceChangePercentage24h;
    }

    @Override
    public String toString() {
        return name + " (" + symbol + ") - $" + currentPrice;
    }
}