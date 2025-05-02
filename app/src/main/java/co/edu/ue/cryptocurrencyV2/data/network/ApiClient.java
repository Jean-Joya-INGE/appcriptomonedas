package co.edu.ue.cryptocurrencyV2.data.network;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import java.io.IOException;

/**
 * ApiClient centraliza todas las peticiones HTTP de la app.
 */
public class ApiClient {
    private static final OkHttpClient client = new OkHttpClient();

    // Método estático para obtener datos de una URL
    public static String getCryptoData(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .build();

        Response response = client.newCall(request).execute();

        if (!response.isSuccessful()) {
            throw new IOException("Error de servidor: " + response.code());
        }

        return response.body() != null ? response.body().string() : "[]";
    }
}

