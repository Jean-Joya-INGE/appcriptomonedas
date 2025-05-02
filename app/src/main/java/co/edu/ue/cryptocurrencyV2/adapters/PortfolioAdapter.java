package co.edu.ue.cryptocurrencyV2.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import co.edu.ue.cryptocurrencyV2.data.models.PortfolioItem;
import co.edu.ue.cryptocurrencyV2.R;

public class PortfolioAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<PortfolioItem> portfolioItems;

    public PortfolioAdapter(Context context, ArrayList<PortfolioItem> portfolioItems) {
        this.context = context;
        this.portfolioItems = portfolioItems;
    }

    @Override
    public int getCount() {
        return portfolioItems.size();
    }

    @Override
    public Object getItem(int position) {
        return portfolioItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return portfolioItems.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_portfolio, parent, false);
        }

        PortfolioItem item = portfolioItems.get(position);

        TextView tvName = convertView.findViewById(R.id.tvCryptoName);
        TextView tvAmount = convertView.findViewById(R.id.tvCryptoAmount);
        TextView tvValue = convertView.findViewById(R.id.tvCryptoValue);

        // Construir el nombre completo con el símbolo si está disponible
        String fullName = item.getCryptoName();
        if (item.getCryptoSymbol() != null && !item.getCryptoSymbol().isEmpty()) {
            fullName += " (" + item.getCryptoSymbol() + ")";
        }

        tvName.setText(fullName);
        tvAmount.setText(String.format("Cantidad: %.6f", item.getCryptoAmount()));
        tvValue.setText(String.format("Valor: $%.2f", item.getTotalValue()));

        return convertView;
    }
}
