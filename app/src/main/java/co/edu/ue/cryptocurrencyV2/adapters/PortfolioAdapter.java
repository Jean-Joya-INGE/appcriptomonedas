package co.edu.ue.cryptocurrencyV2.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
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
        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_portfolio, parent, false);

            holder = new ViewHolder();
            holder.ivCryptoLogo = convertView.findViewById(R.id.ivCryptoLogo);
            holder.tvCryptoName = convertView.findViewById(R.id.tvCryptoName);
            holder.tvCryptoAmount = convertView.findViewById(R.id.tvCryptoAmount);
            holder.tvCryptoValue = convertView.findViewById(R.id.tvCryptoValue);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        PortfolioItem item = portfolioItems.get(position);

        // Construir el nombre completo con el símbolo si está disponible
        String fullName = item.getCryptoName();
        if (item.getCryptoSymbol() != null && !item.getCryptoSymbol().isEmpty()) {
            fullName += " (" + item.getCryptoSymbol() + ")";
        }

        holder.tvCryptoName.setText(fullName);
        holder.tvCryptoAmount.setText(String.format("Cantidad: %.6f", item.getCryptoAmount()));
        holder.tvCryptoValue.setText(String.format("Valor: $%.2f", item.getTotalValue()));

        return convertView;
    }

    // ViewHolder pattern para mejor rendimiento
    private static class ViewHolder {
        ImageView ivCryptoLogo;
        TextView tvCryptoName;
        TextView tvCryptoAmount;
        TextView tvCryptoValue;
    }
}
