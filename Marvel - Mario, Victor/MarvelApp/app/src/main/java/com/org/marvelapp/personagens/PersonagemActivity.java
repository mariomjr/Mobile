package com.org.marvelapp.personagens;

import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.org.marvelapp.R;
import com.org.marvelapp.model.Personagem;
import com.org.marvelapp.services.PersonagemService;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by MarioJr on 02/06/2015.
 */
public class PersonagemActivity extends ActionBarActivity {

    private int previousTotal = 0;

    private boolean loading = true;

    private int visivel = 5;

    int primeiroItemVisivel;
    int visivelCount;
    int totalItem;

    RecyclerView rv;

    PersonagensAdapter personagensAdapter;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personagens);
        rv = (RecyclerView)findViewById(R.id.recyclerPersonagens);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        final LinearLayoutManager llm = new LinearLayoutManager(this);
        rv.setLayoutManager(llm);
        
        final PersonagemService personagemService = new PersonagemService();
        List<Personagem> listPersonagens = new ArrayList<Personagem>();
        try {
            listPersonagens = personagemService.listPersonagens(0);
        }catch (Exception e){
            e.printStackTrace();
        }

        personagensAdapter = new PersonagensAdapter(listPersonagens);
        rv.setOnScrollListener(new RecyclerView.OnScrollListener(){
            public void onScrolled(RecyclerView recylerView, int dx, int dy){
                super.onScrolled(recylerView,dx,dy);
                visivelCount = rv.getChildCount();
                totalItem = llm.getItemCount();
                primeiroItemVisivel = llm.findFirstVisibleItemPosition();

                if(loading){
                    if(totalItem > previousTotal){
                        loading = false;
                        previousTotal = totalItem;
                      /*  List<Personagem> listPersonagems = new ArrayList<Personagem>();
                        try {
                            listPersonagems = personagemService.listPersonagens(previousTotal);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        personagensAdapter.getListPersonagens().addAll(listPersonagems);*/
                    }
                }
                if(!loading && (totalItem - visivelCount)<=(primeiroItemVisivel+visivel)){
                    loading = true;
                }

            }
        });
        rv.setAdapter(personagensAdapter);

    }
}
