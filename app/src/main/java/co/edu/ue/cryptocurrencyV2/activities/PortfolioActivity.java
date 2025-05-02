package co.edu.ue.cryptocurrencyV2.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import co.edu.ue.cryptocurrencyV2.adapters.CryptoListAdapter;
import co.edu.ue.cryptocurrencyV2.adapters.PortfolioAdapter;
import co.edu.ue.cryptocurrencyV2.R;
import co.edu.ue.cryptocurrencyV2.data.db.DatabaseHelper;
import co.edu.ue.cryptocurrencyV2.data.models.CryptoCurrency;
import co.edu.ue.cryptocurrencyV2.data.models.PortfolioItem;
import co.edu.ue.cryptocurrencyV2.data.network.ApiClient;
import co.edu.ue.cryptocurrencyV2.data.network.CryptoService;

public class PortfolioActivity extends AppCompatActivity {

    private static final String TAG = "PortfolioActivity";

    private ListView lvPortfolio;
    private TextView tvTotalValue;
    private DatabaseHelper db;
    private int userId; // ID del usuario actual
    private ArrayList<PortfolioItem> portfolioItems;
    private PortfolioAdapter adapter;

    // Variables para añadir criptomonedas
    private CryptoCurrency selectedCrypto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_portfolio);

        db = new DatabaseHelper(this);
        lvPortfolio = findViewById(R.id.lvPortfolio);
        tvTotalValue = findViewById(R.id.tvTotalValue);
        portfolioItems = new ArrayList<>();

        // Recuperamos el ID del usuario enviado por la Activity anterior
        userId = getIntent().getIntExtra("USER_ID", -1);

        if (userId == -1) {
            Toast.makeText(this, "Error: Usuario no identificado", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Cargamos el portafolio de este usuario
        loadPortfolio();

        // Configuramos el listener para manejar clics en los elementos del portafolio
        // (editar/eliminar)
        lvPortfolio.setOnItemClickListener((parent, view, position, id) -> {
            showEditDeleteDialog(portfolioItems.get(position));
        });

        // Botón para agregar nueva criptomoneda
        findViewById(R.id.btnAddCrypto).setOnClickListener(v -> {
            showAddCryptoDialog();
        });
    }

    /**
     * Muestra un diálogo para añadir una nueva criptomoneda al portafolio
     */
    private void showAddCryptoDialog() {
        selectedCrypto = null;

        // Inflamos el layout del diálogo
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_crypto, null);

        // Referenciamos las vistas
        TextView tvSelectedCrypto = dialogView.findViewById(R.id.tvSelectedCrypto);
        TextView tvCurrentPrice = dialogView.findViewById(R.id.tvCurrentPrice);
        Button btnSelectCrypto = dialogView.findViewById(R.id.btnSelectCrypto);
        EditText etAmount = dialogView.findViewById(R.id.etCryptoAmount);

        // Creamos el diálogo
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Añadir Criptomoneda")
                .setView(dialogView)
                .setPositiveButton("Añadir", null) // Lo configuraremos después para evitar cierre automático
                .setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();

        // Configuramos el botón para seleccionar criptomoneda
        btnSelectCrypto.setOnClickListener(v -> {
            // Mostramos el diálogo para seleccionar criptomoneda
            showSelectCryptoDialog(crypto -> {
                selectedCrypto = crypto;
                tvSelectedCrypto.setText(crypto.getName() + " (" + crypto.getSymbol() + ")");
                tvCurrentPrice.setText(String.format("$%.2f", crypto.getCurrentPrice()));
            });
        });

        dialog.show();

        // Reemplazamos el listener del botón positivo para validar antes de cerrar
        Button positiveButton = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
        positiveButton.setOnClickListener(v -> {
            // Validamos que haya seleccionado una criptomoneda y una cantidad
            if (selectedCrypto == null) {
                Toast.makeText(PortfolioActivity.this, "Debes seleccionar una criptomoneda", Toast.LENGTH_SHORT).show();
                return;
            }

            String amountStr = etAmount.getText().toString().trim();
            if (amountStr.isEmpty()) {
                Toast.makeText(PortfolioActivity.this, "Debes ingresar una cantidad", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                double amount = Double.parseDouble(amountStr);
                if (amount <= 0) {
                    Toast.makeText(PortfolioActivity.this, "La cantidad debe ser mayor a 0", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Añadimos la criptomoneda al portafolio
                boolean success = db.addToPortfolio(
                        userId,
                        selectedCrypto.getId(),
                        selectedCrypto.getName(),
                        selectedCrypto.getSymbol(),
                        amount,
                        selectedCrypto.getCurrentPrice());

                if (success) {
                    Toast.makeText(PortfolioActivity.this, "Criptomoneda añadida con éxito", Toast.LENGTH_SHORT).show();
                    loadPortfolio(); // Recargamos el portafolio
                    dialog.dismiss();
                } else {
                    Toast.makeText(PortfolioActivity.this, "Error al añadir la criptomoneda", Toast.LENGTH_SHORT)
                            .show();
                }

            } catch (NumberFormatException e) {
                Toast.makeText(PortfolioActivity.this, "Cantidad inválida", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Interfaz para manejar la selección de criptomonedas
     */
    interface OnCryptoSelectedListener {
        void onCryptoSelected(CryptoCurrency crypto);
    }

    /**
     * Muestra un diálogo para seleccionar una criptomoneda de la lista
     */
    private void showSelectCryptoDialog(OnCryptoSelectedListener listener) {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_select_crypto, null);
        ListView lvCryptoList = dialogView.findViewById(R.id.lvCryptoList);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();

        // Configuramos el adaptador
        CryptoListAdapter adapter = new CryptoListAdapter(this);
        lvCryptoList.setAdapter(adapter);

        // Cargamos las criptomonedas en un hilo separado
        new Thread(() -> {
            try {
                List<CryptoCurrency> cryptoList = CryptoService.getTopCryptocurrencies();

                runOnUiThread(() -> {
                    // Actualizamos el adaptador con las criptomonedas
                    adapter.setCryptoList(cryptoList);

                    // Configuramos el listener para manejar clics
                    lvCryptoList.setOnItemClickListener((parent, view, position, id) -> {
                        CryptoCurrency crypto = adapter.getItem(position);
                        listener.onCryptoSelected(crypto);
                        dialog.dismiss();
                    });
                });

            } catch (IOException | JSONException e) {
                runOnUiThread(() -> {
                    Toast.makeText(PortfolioActivity.this,
                            "Error al cargar criptomonedas: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                });
            }
        }).start();

        dialog.show();
    }

    /**
     * Muestra un diálogo para editar o eliminar una criptomoneda del portafolio
     */
    private void showEditDeleteDialog(PortfolioItem item) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Opciones")
                .setItems(new String[] { "Editar", "Eliminar" }, (dialog, which) -> {
                    switch (which) {
                        case 0: // Editar
                            showEditCryptoDialog(item);
                            break;
                        case 1: // Eliminar
                            showDeleteConfirmDialog(item);
                            break;
                    }
                })
                .show();
    }

    /**
     * Muestra un diálogo para editar la cantidad de una criptomoneda
     */
    private void showEditCryptoDialog(PortfolioItem item) {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_crypto, null);

        TextView tvSelectedCrypto = dialogView.findViewById(R.id.tvSelectedCrypto);
        TextView tvCurrentPrice = dialogView.findViewById(R.id.tvCurrentPrice);
        Button btnSelectCrypto = dialogView.findViewById(R.id.btnSelectCrypto);
        EditText etAmount = dialogView.findViewById(R.id.etCryptoAmount);

        // Ocultamos el botón de seleccionar criptomoneda, ya que solo permitimos editar
        // la cantidad
        btnSelectCrypto.setVisibility(View.GONE);

        // Configuramos los valores actuales
        tvSelectedCrypto.setText(
                item.getCryptoName() + (item.getCryptoSymbol() != null ? " (" + item.getCryptoSymbol() + ")" : ""));
        tvCurrentPrice.setText(String.format("$%.2f", item.getCryptoPrice()));
        etAmount.setText(String.valueOf(item.getCryptoAmount()));

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Editar Criptomoneda")
                .setView(dialogView)
                .setPositiveButton("Guardar", null)
                .setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();

        // Reemplazamos el listener para validar antes de cerrar
        Button positiveButton = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
        positiveButton.setOnClickListener(v -> {
            String amountStr = etAmount.getText().toString().trim();
            if (amountStr.isEmpty()) {
                Toast.makeText(PortfolioActivity.this, "Debes ingresar una cantidad", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                double amount = Double.parseDouble(amountStr);
                if (amount <= 0) {
                    Toast.makeText(PortfolioActivity.this, "La cantidad debe ser mayor a 0", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Actualizamos la criptomoneda en el portafolio
                boolean success = db.updatePortfolio(item.getId(), amount, item.getCryptoPrice());

                if (success) {
                    Toast.makeText(PortfolioActivity.this, "Criptomoneda actualizada con éxito", Toast.LENGTH_SHORT)
                            .show();
                    loadPortfolio(); // Recargamos el portafolio
                    dialog.dismiss();
                } else {
                    Toast.makeText(PortfolioActivity.this, "Error al actualizar la criptomoneda", Toast.LENGTH_SHORT)
                            .show();
                }

            } catch (NumberFormatException e) {
                Toast.makeText(PortfolioActivity.this, "Cantidad inválida", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Muestra un diálogo de confirmación para eliminar una criptomoneda
     */
    private void showDeleteConfirmDialog(PortfolioItem item) {
        new AlertDialog.Builder(this)
                .setTitle("Eliminar Criptomoneda")
                .setMessage("¿Estás seguro de que deseas eliminar " + item.getCryptoName() + " de tu portafolio?")
                .setPositiveButton("Eliminar", (dialog, which) -> {
                    boolean success = db.removeFromPortfolio(item.getId());

                    if (success) {
                        Toast.makeText(PortfolioActivity.this, "Criptomoneda eliminada con éxito", Toast.LENGTH_SHORT)
                                .show();
                        loadPortfolio(); // Recargamos el portafolio
                    } else {
                        Toast.makeText(PortfolioActivity.this, "Error al eliminar la criptomoneda", Toast.LENGTH_SHORT)
                                .show();
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    /**
     * Función que carga el portafolio del usuario desde la base de datos
     */
    private void loadPortfolio() {
        try (Cursor cursor = db.getPortfolio(userId)) {
            portfolioItems.clear();
            double totalValue = 0;

            if (cursor != null) {
                // Obtenemos los índices de las columnas
                int idIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_PORTFOLIO_ID);
                int cryptoIdIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_CRYPTO_ID);
                int cryptoNameIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_CRYPTO_NAME);
                int cryptoSymbolIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_CRYPTO_SYMBOL);
                int cryptoAmountIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_CRYPTO_AMOUNT);
                int cryptoPriceIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_CRYPTO_PRICE);
                int purchaseDateIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_PURCHASE_DATE);

                while (cursor.moveToNext()) {
                    int id = cursor.getInt(idIndex);
                    String cryptoId = cryptoIdIndex != -1 ? cursor.getString(cryptoIdIndex) : "";
                    String cryptoName = cursor.getString(cryptoNameIndex);
                    String cryptoSymbol = cryptoSymbolIndex != -1 ? cursor.getString(cryptoSymbolIndex) : "";
                    double cryptoAmount = cursor.getDouble(cryptoAmountIndex);
                    double cryptoPrice = cursor.getDouble(cryptoPriceIndex);
                    String purchaseDate = purchaseDateIndex != -1 ? cursor.getString(purchaseDateIndex) : "";

                    double totalCryptoValue = cryptoAmount * cryptoPrice;
                    totalValue += totalCryptoValue;

                    portfolioItems.add(new PortfolioItem(
                            id,
                            cryptoId,
                            cryptoName,
                            cryptoSymbol,
                            cryptoAmount,
                            cryptoPrice,
                            cryptoPrice, // Usamos el mismo precio como precio actual por ahora
                            totalCryptoValue,
                            purchaseDate));
                }
            }

            // Actualizamos el total en dólares
            tvTotalValue.setText(String.format("Total en dólares: $%.2f", totalValue));

            // Creamos o actualizamos el adaptador de la lista
            adapter = new PortfolioAdapter(this, portfolioItems);
            lvPortfolio.setAdapter(adapter);

            // Si queremos actualizar los precios en tiempo real, podríamos llamar:
            // updateCryptoPricesInPortfolio();

        } catch (Exception e) {
            Log.e(TAG, "Error al cargar el portafolio", e);
            Toast.makeText(this, "Error al cargar el portafolio", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Actualiza los precios actuales de las criptomonedas en el portafolio
     */
    private void updateCryptoPricesInPortfolio() {
        if (portfolioItems.isEmpty()) {
            return;
        }

        // Construimos una lista de IDs de criptomonedas
        StringBuilder cryptoIds = new StringBuilder();
        for (PortfolioItem item : portfolioItems) {
            if (item.getCryptoId() != null && !item.getCryptoId().isEmpty()) {
                if (cryptoIds.length() > 0) {
                    cryptoIds.append(",");
                }
                cryptoIds.append(item.getCryptoId());
            }
        }

        // Si no hay IDs válidos, salimos
        if (cryptoIds.length() == 0) {
            return;
        }

        // Ejecutamos en un hilo separado
        new Thread(() -> {
            try {
                // Obtenemos los precios actualizados
                String url = "https://api.coingecko.com/api/v3/coins/markets?vs_currency=usd&ids="
                        + cryptoIds.toString();
                List<CryptoCurrency> updatedCryptos = CryptoService.parseCryptoCurrencies(
                        ApiClient.getCryptoData(url));

                // Creamos un mapa para acceder fácilmente a los precios por ID
                java.util.Map<String, Double> priceMap = new java.util.HashMap<>();
                for (CryptoCurrency crypto : updatedCryptos) {
                    priceMap.put(crypto.getId(), crypto.getCurrentPrice());
                }

                // Actualizamos los precios en el portafolio
                double totalValue = 0;
                for (PortfolioItem item : portfolioItems) {
                    Double newPrice = priceMap.get(item.getCryptoId());
                    if (newPrice != null) {
                        item = new PortfolioItem(
                                item.getId(),
                                item.getCryptoId(),
                                item.getCryptoName(),
                                item.getCryptoSymbol(),
                                item.getCryptoAmount(),
                                item.getCryptoPrice(),
                                newPrice,
                                item.getCryptoAmount() * newPrice,
                                item.getPurchaseDate());
                    }

                    totalValue += item.getTotalValue();
                }

                // Actualizamos la UI
                final double finalTotalValue = totalValue;
                runOnUiThread(() -> {
                    adapter.notifyDataSetChanged();
                    tvTotalValue.setText(String.format("Total en dólares: $%.2f", finalTotalValue));
                });

            } catch (Exception e) {
                Log.e(TAG, "Error al actualizar precios", e);
            }
        }).start();
    }
}
