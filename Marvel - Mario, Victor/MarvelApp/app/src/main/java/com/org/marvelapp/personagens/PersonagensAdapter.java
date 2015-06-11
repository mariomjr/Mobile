package com.org.marvelapp.personagens;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.org.marvelapp.R;
import com.org.marvelapp.model.Personagem;

import java.util.List;

/**
 * Created by MarioJr on 02/06/2015.
 */
public class PersonagensAdapter extends RecyclerView.Adapter<PersonagensAdapter.ColorViewHolder> {

    private List<Personagem> listPersonagens;

    public PersonagensAdapter(List<Personagem> listPersonagens){
        this.listPersonagens = listPersonagens;
    }

    @Override
    public ColorViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_personagem, viewGroup, false);
        ColorViewHolder pvh = new ColorViewHolder(v);
        return pvh;
    }

    @Override
    public void onBindViewHolder(ColorViewHolder holder, final int position) {
        holder.personagemName.setText(listPersonagens.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return listPersonagens.size();
    }

    public static class ColorViewHolder extends RecyclerView.ViewHolder {

        CardView cv;
        TextView personagemName;

        ColorViewHolder(View itemView) {
            super(itemView);
            cv = (CardView)itemView.findViewById(R.id.cardPersonagem);
            personagemName = (TextView)itemView.findViewById(R.id.personagemName);
        }
    }

    public List<Personagem> getListPersonagens(){
        return this.listPersonagens;
    }
    public void setListPersonagens(List<Personagem> listPersonagens){
        this.listPersonagens = listPersonagens;
    }
}
