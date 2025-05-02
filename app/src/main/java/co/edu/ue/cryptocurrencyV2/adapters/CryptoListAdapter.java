package co.edu.ue.cryptocurrencyV2.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import co.edu.ue.cryptocurrencyV2.R;
import co.edu.ue.cryptocurrencyV2.data.models.CryptoCurrency;

public class CryptoListAdapter extends BaseAdapter {

    private Context context;
    private List<CryptoCurrency> cryptoList;

    public CryptoListAdapter(Context context) {
        this.context = context;
        this.cryptoList = new ArrayList<>();
    }

    public void setCryptoList(List<CryptoCurrency> cryptoList) {
        this.cryptoList = cryptoList;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return cryptoList.size();
    }

    @Override
    public CryptoCurrency getItem(int position) {
        return cryptoList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_crypto, parent, false);
        }

        CryptoCurrency crypto = getItem(position);

        TextView tvCryptoName = convertView.findViewById(R.id.tvCryptoName);
        TextView tvCryptoSymbol = convertView.findViewById(R.id.tvCryptoSymbol);
        TextView tvCryptoPrice = convertView.findViewById(R.id.tvCryptoPrice);

        tvCryptoName.setText(crypto.getName());
        tvCryptoSymbol.setText(crypto.getSymbol());
        tvCryptoPrice.setText(String.format("$%.2f", crypto.getCurrentPrice()));

        return convertView;
    }
}