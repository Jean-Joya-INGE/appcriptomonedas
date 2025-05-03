package co.edu.ue.cryptocurrencyV2.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import co.edu.ue.cryptocurrencyV2.R;
import co.edu.ue.cryptocurrencyV2.data.network.ApiClient;
import co.edu.ue.cryptocurrencyV2.utils.SessionManager;

public class DashboardActivity extends AppCompatActivity {

    private Button btnLogout;
    private LinearLayout cryptoContainer;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        sessionManager = new SessionManager(this);

        // Verificar si el usuario está logueado
        if (!sessionManager.isLoggedIn()) {
            startActivity(new Intent(DashboardActivity.this, MainActivity.class));
            finish();
            return;
        }

        // Inicializamos los elementos de la vista
        btnLogout = findViewById(R.id.btnLogout);
        cryptoContainer = findViewById(R.id.cryptoContainer);

        // Llamamos a la función para traer los precios de criptomonedas
        fetchCryptoPrices();

        // Botón de cerrar sesión
        btnLogout.setOnClickListener(v -> {
            sessionManager.logout();
            startActivity(new Intent(DashboardActivity.this, MainActivity.class));
            finish(); // Cerramos esta Activity
        });

        // Botón para ver el portafolio del usuario
        Button btnViewPortfolio = findViewById(R.id.btnViewPortfolio);
        btnViewPortfolio.setOnClickListener(v -> {
            Intent intent = new Intent(DashboardActivity.this, PortfolioActivity.class);
            intent.putExtra("USER_ID", sessionManager.getUserId()); // Usamos el ID del usuario en sesión
            startActivity(intent);
        });
    }

    /**
     * Función para obtener precios de las principales criptomonedas
     */
    private void fetchCryptoPrices() {
        // Usamos una URL que incluye la imagen de cada criptomoneda
        String url = "https://api.coingecko.com/api/v3/coins/markets?vs_currency=usd&order=market_cap_desc&per_page=10&page=1";

        // Ejecutamos la conexión en un hilo aparte para no bloquear la interfaz
        new Thread(() -> {
            try {
                // Pedimos los datos al servidor usando ApiClient
                String responseData = ApiClient.getCryptoData(url);

                // Actualizamos la UI en el hilo principal
                runOnUiThread(() -> processCryptoData(responseData));

            } catch (Exception e) {
                runOnUiThread(() -> Toast
                        .makeText(DashboardActivity.this, "Error al cargar precios", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }

    /**
     * Función para procesar el JSON recibido y mostrarlo en pantalla
     */
    private void processCryptoData(String jsonData) {
        try {
            JSONArray jsonArray = new JSONArray(jsonData);

            // Actualizamos el TextView con Bitcoin que ya está en el layout
            if (jsonArray.length() > 0) {
                JSONObject bitcoin = jsonArray.getJSONObject(0);
                String btcName = bitcoin.getString("name");
                double btcPrice = bitcoin.getDouble("current_price");
                String formattedBtcPrice = String.format("%.2f", btcPrice);
                String btcImageUrl = bitcoin.getString("image");

                TextView tvBitcoin = findViewById(R.id.tvBitcoin);
                tvBitcoin.setText(btcName + ": $" + formattedBtcPrice);

                // Cargamos el ícono oficial de Bitcoin
                ImageView ivBitcoin = findViewById(R.id.ivBitcoin);
                Picasso.get().load(btcImageUrl).into(ivBitcoin);
            }

            // Limpiamos el contenedor antes de agregar nuevos elementos
            cryptoContainer.removeAllViews();

            // Mostramos el resto de criptomonedas (empezando desde la segunda, índice 1)
            for (int i = 1; i < Math.min(jsonArray.length(), 10); i++) {
                JSONObject crypto = jsonArray.getJSONObject(i);
                String name = crypto.getString("name");
                String symbol = crypto.getString("symbol").toUpperCase();
                double price = crypto.getDouble("current_price");
                String formattedPrice = String.format("%.2f", price);
                String imageUrl = crypto.getString("image");

                // Añadimos cada criptomoneda al contenedor con un CardView
                CardView cryptoCard = (CardView) LayoutInflater.from(this)
                        .inflate(R.layout.item_crypto_card, cryptoContainer, false);

                // Configuramos el nombre y precio
                TextView tvCryptoName = cryptoCard.findViewById(R.id.tvCryptoName);
                tvCryptoName.setText(name + " (" + symbol + "): $" + formattedPrice);

                // Cargamos el ícono oficial
                ImageView ivCryptoIcon = cryptoCard.findViewById(R.id.ivCryptoIcon);
                Picasso.get().load(imageUrl).into(ivCryptoIcon);

                cryptoContainer.addView(cryptoCard);
            }

        } catch (Exception e) {
            Toast.makeText(this, "Error procesando datos: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
