package co.edu.ue.cryptocurrencyV2.data.network;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import co.edu.ue.cryptocurrencyV2.data.models.CryptoCurrency;

/**
 * Servicio para obtener datos de criptomonedas
 */
public class CryptoService {

    private static final String TAG = "CryptoService";
    private static final String BASE_URL = "https://api.coingecko.com/api/v3";

    /**
     * Obtiene las 10 principales criptomonedas por capitalización de mercado
     */
    public static List<CryptoCurrency> getTopCryptocurrencies() throws IOException, JSONException {
        String url = BASE_URL + "/coins/markets?vs_currency=usd&order=market_cap_desc&per_page=10&page=1";
        String jsonResponse = ApiClient.getCryptoData(url);
        return parseCryptoCurrencies(jsonResponse);
    }

    /**
     * Obtiene información de una criptomoneda específica por su ID
     */
    public static CryptoCurrency getCryptoCurrencyById(String id) throws IOException, JSONException {
        String url = BASE_URL + "/coins/markets?vs_currency=usd&ids=" + id;
        String jsonResponse = ApiClient.getCryptoData(url);
        List<CryptoCurrency> cryptos = parseCryptoCurrencies(jsonResponse);

        if (cryptos.isEmpty()) {
            return null;
        }

        return cryptos.get(0);
    }

    /**
     * Convierte el resultado JSON en una lista de objetos CryptoCurrency
     */
    public static List<CryptoCurrency> parseCryptoCurrencies(String jsonResponse) throws JSONException {
        List<CryptoCurrency> cryptoList = new ArrayList<>();
        JSONArray jsonArray = new JSONArray(jsonResponse);

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonCrypto = jsonArray.getJSONObject(i);

            String id = jsonCrypto.getString("id");
            String name = jsonCrypto.getString("name");
            String symbol = jsonCrypto.getString("symbol").toUpperCase();
            double price = jsonCrypto.getDouble("current_price");
            String imageUrl = jsonCrypto.getString("image");

            cryptoList.add(new CryptoCurrency(id, name, symbol, price, imageUrl));
        }

        return cryptoList;
    }
}