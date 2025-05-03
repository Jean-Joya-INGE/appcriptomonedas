package co.edu.ue.cryptocurrencyV2.data.models;

public class PortfolioItem {
    private int id; // ID del elemento en la base de datos
    private String cryptoId; // ID de la criptomoneda (ej: "bitcoin")
    private String cryptoName; // Nombre de la criptomoneda
    private String cryptoSymbol; // Símbolo de la criptomoneda
    private double cryptoAmount; // Cantidad
    private double cryptoPrice; // Precio de compra
    private double currentPrice; // Precio actual
    private double totalValue; // Valor total (cantidad * precio actual)
    private String purchaseDate; // Fecha de compra
    private String imageUrl; // URL de la imagen/logo

    // Constructor completo
    public PortfolioItem(int id, String cryptoId, String cryptoName, String cryptoSymbol,
            double cryptoAmount, double cryptoPrice, double currentPrice,
            double totalValue, String purchaseDate, String imageUrl) {
        this.id = id;
        this.cryptoId = cryptoId;
        this.cryptoName = cryptoName;
        this.cryptoSymbol = cryptoSymbol;
        this.cryptoAmount = cryptoAmount;
        this.cryptoPrice = cryptoPrice;
        this.currentPrice = currentPrice;
        this.totalValue = totalValue;
        this.purchaseDate = purchaseDate;
        this.imageUrl = imageUrl;
    }

    // Constructor completo sin imagen (compatible con código existente)
    public PortfolioItem(int id, String cryptoId, String cryptoName, String cryptoSymbol,
            double cryptoAmount, double cryptoPrice, double currentPrice,
            double totalValue, String purchaseDate) {
        this(id, cryptoId, cryptoName, cryptoSymbol, cryptoAmount, cryptoPrice,
                currentPrice, totalValue, purchaseDate, null);
    }

    // Constructor simplificado (compatible con el código existente)
    public PortfolioItem(String cryptoName, double cryptoAmount, double cryptoPrice, double totalValue) {
        this.cryptoName = cryptoName;
        this.cryptoAmount = cryptoAmount;
        this.cryptoPrice = cryptoPrice;
        this.totalValue = totalValue;
        this.imageUrl = null;
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getCryptoId() {
        return cryptoId;
    }

    public String getCryptoName() {
        return cryptoName;
    }

    public String getCryptoSymbol() {
        return cryptoSymbol;
    }

    public double getCryptoAmount() {
        return cryptoAmount;
    }

    public double getCryptoPrice() {
        return cryptoPrice;
    }

    public double getCurrentPrice() {
        return currentPrice;
    }

    public double getTotalValue() {
        return totalValue;
    }

    public String getPurchaseDate() {
        return purchaseDate;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
