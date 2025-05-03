package co.edu.ue.cryptocurrencyV2.data.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;

import co.edu.ue.cryptocurrencyV2.data.repositories.PortfolioRepository;
import co.edu.ue.cryptocurrencyV2.data.repositories.UserRepository;

/**
 * Esta clase actúa como adaptador para mantener compatibilidad con el código
 * existente,
 * pero delega la funcionalidad a los repositorios apropiados
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    // Usamos las constantes de CryptoDatabase
    private static final String DATABASE_NAME = CryptoDatabase.DATABASE_NAME;
    private static final int DATABASE_VERSION = CryptoDatabase.DATABASE_VERSION;

    // Constantes públicas que necesita PortfolioActivity (referencian a
    // CryptoDatabase)
    public static final String COLUMN_PORTFOLIO_ID = CryptoDatabase.COLUMN_PORTFOLIO_ID;
    public static final String COLUMN_USER_ID = CryptoDatabase.COLUMN_USER_ID;
    public static final String COLUMN_CRYPTO_ID = CryptoDatabase.COLUMN_CRYPTO_ID;
    public static final String COLUMN_CRYPTO_NAME = CryptoDatabase.COLUMN_CRYPTO_NAME;
    public static final String COLUMN_CRYPTO_SYMBOL = CryptoDatabase.COLUMN_CRYPTO_SYMBOL;
    public static final String COLUMN_CRYPTO_AMOUNT = CryptoDatabase.COLUMN_CRYPTO_AMOUNT;
    public static final String COLUMN_CRYPTO_PRICE = CryptoDatabase.COLUMN_CRYPTO_PRICE;
    public static final String COLUMN_PURCHASE_DATE = CryptoDatabase.COLUMN_PURCHASE_DATE;

    // Repositorios
    private UserRepository userRepository;
    private PortfolioRepository portfolioRepository;
    private Context context;

    // Constructor de la clase
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
        this.userRepository = new UserRepository(context);
        this.portfolioRepository = new PortfolioRepository(context);
    }

    // Se llama cuando se crea la base de datos por primera vez
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Delegamos la creación a CryptoDatabase
        new CryptoDatabase(context).onCreate(db);
    }

    // Se llama cuando la versión de la base de datos cambia
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Delegamos la actualización a CryptoDatabase
        new CryptoDatabase(context).onUpgrade(db, oldVersion, newVersion);
    }

    // Método para verificar si un correo electrónico ya está registrado
    public boolean isEmailExists(String email) {
        return userRepository.isEmailExists(email);
    }

    // Método para registrar un nuevo usuario
    public boolean registerUser(String username, String email, String password) {
        return userRepository.registerUser(username, email, password);
    }

    // Método para verificar las credenciales de un usuario (inicio de sesión)
    public boolean checkUser(String email, String password) {
        return userRepository.checkUser(email, password);
    }

    // Método para agregar una criptomoneda al portafolio
    public boolean addToPortfolio(int userId, String cryptoId, String cryptoName, String cryptoSymbol,
            double amount, double price) {
        return portfolioRepository.addToPortfolio(userId, cryptoId, cryptoName, cryptoSymbol, amount, price);
    }

    // Método para obtener todas las criptomonedas del portafolio de un usuario
    public Cursor getPortfolio(int userId) {
        return portfolioRepository.getPortfolio(userId);
    }

    // Método para actualizar una criptomoneda en el portafolio
    public boolean updatePortfolio(int portfolioId, double newAmount, double newPrice) {
        return portfolioRepository.updatePortfolio(portfolioId, newAmount, newPrice);
    }

    // Método para eliminar una criptomoneda del portafolio
    public boolean removeFromPortfolio(int portfolioId) {
        return portfolioRepository.removeFromPortfolio(portfolioId);
    }

    // Método para obtener el ID de un usuario a partir de su email
    public int getUserId(String email) {
        return userRepository.getUserId(email);
    }

    // Método para cerrar la base de datos cuando ya no se necesite
    @Override
    public void close() {
        // Primero cerramos los repositorios
        if (userRepository != null) {
            userRepository.closeDatabase();
        }
        if (portfolioRepository != null) {
            portfolioRepository.closeDatabase();
        }
        // Luego cerramos la base de datos del adaptador
        super.close();
    }
}
