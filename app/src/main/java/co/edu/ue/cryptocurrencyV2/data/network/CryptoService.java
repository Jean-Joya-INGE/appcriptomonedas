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
        String url = BASE_URL
                + "/coins/markets?vs_currency=usd&order=market_cap_desc&per_page=10&page=1&sparkline=false&price_change_percentage=24h";
        String jsonResponse = ApiClient.getCryptoData(url);
        return parseCryptoCurrencies(jsonResponse);
    }

    /**
     * Obtiene información de una criptomoneda específica por su ID
     */
    public static CryptoCurrency getCryptoCurrencyById(String id) throws IOException, JSONException {
        String url = BASE_URL + "/coins/markets?vs_currency=usd&ids=" + id
                + "&sparkline=false&price_change_percentage=24h";
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

            // Obtener el cambio de precio en 24h (podría no estar presente en la respuesta)
            double priceChange = 0.0;
            if (!jsonCrypto.isNull("price_change_percentage_24h")) {
                priceChange = jsonCrypto.getDouble("price_change_percentage_24h");
            }

            cryptoList.add(new CryptoCurrency(id, name, symbol, price, imageUrl, priceChange));
        }

        return cryptoList;
    }

    /**
     * Extrae la URL de la imagen de la respuesta JSON de la API de detalle de una
     * criptomoneda
     */
    public static String extractImageUrl(String jsonResponse) throws JSONException {
        JSONObject jsonObject = new JSONObject(jsonResponse);

        if (jsonObject.has("image")) {
            JSONObject imageObj = jsonObject.getJSONObject("image");
            if (imageObj.has("small")) {
                return imageObj.getString("small");
            } else if (imageObj.has("thumb")) {
                return imageObj.getString("thumb");
            } else if (imageObj.has("large")) {
                return imageObj.getString("large");
            }
        }

        return null;
    }
}