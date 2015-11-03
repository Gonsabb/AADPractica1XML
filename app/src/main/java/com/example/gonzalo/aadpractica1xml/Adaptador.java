package com.example.gonzalo.aadpractica1xml;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.example.gonzalo.aadpractica1xml.contacto.Contacto;

import java.util.ArrayList;

/**
 * Created by Gonzalo on 01/11/2015.
 */
public class Adaptador extends ArrayAdapter<Contacto>{
    private Context contexto;
    private int recurso;
    private ArrayList<Contacto> lista;
    private LayoutInflater i;
    private RelativeLayout rl;
    private ImageButton ibma, ibme;
    private TextView contact;



    public Adaptador(Context contexto, int recurso, ArrayList<Contacto> lista) {
        super(contexto, recurso, lista);
        this.contexto=contexto;
        this.recurso=recurso;
        this.lista=lista;
        i = (LayoutInflater) contexto.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public class ViewHolder{
        public TextView tvcontact, tvtfn, tvotros;
        public ImageButton ibmas, ibmenos;
        public RelativeLayout r;
    }

    @Override
    public View getView(int posicion, View vista, ViewGroup padre){

        ViewHolder gv= new ViewHolder();
        if(vista==null){
            vista = i.inflate(recurso, null);

            contact=(TextView)vista.findViewById(R.id.tvcontacto);
            TextView tvtfn=(TextView)vista.findViewById(R.id.tvtelefono);
            TextView tvotros=(TextView)vista.findViewById(R.id.tvotros);

            rl=(RelativeLayout)vista.findViewById(R.id.desplegable);


            ibma=(ImageButton)vista.findViewById(R.id.ibmas);
            ibme = (ImageButton)vista.findViewById(R.id.ibmenos);

            rl.setVisibility(View.GONE);
            ibme.setVisibility(View.INVISIBLE);

            gv.tvcontact=contact;
            gv.tvtfn=tvtfn;
            gv.tvotros=tvotros;
            gv.ibmas=ibma;
            gv.ibmenos= ibme;
            gv.r=rl;

            addListener(ibma, ibme, rl);
            addListener2(ibme, ibma, rl);

            vista.setTag(gv);
        }else{
            gv = (ViewHolder)vista.getTag();
        }

        String ab="";
        gv.tvcontact.setText(lista.get(posicion).getNombre());
        gv.tvtfn.setText(lista.get(posicion).getTelefonos().get(0));


        if(lista.get(posicion).getTelefonos().size()>1) {
            for (int a = 1; a < lista.get(posicion).getTelefonos().size(); a++) {
                ab = ab + lista.get(posicion).getTelefonos().get(a) + " \n";
            }
            gv.tvotros.setText(ab);
        }else{
            gv.tvotros.setText("");
        }

        gv.r.setVisibility(View.GONE);
        gv.ibmenos.setVisibility(View.GONE);

        return vista;
    }
    

    private void addListener(final ImageButton ibma, final ImageButton ibme, final RelativeLayout rl) {
        ibma.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                rl.setVisibility(View.VISIBLE);
                ibma.setVisibility(View.INVISIBLE);
                ibme.setVisibility(View.VISIBLE);
            }
        });
    }

    private void addListener2(final ImageButton ibme, final ImageButton ibma, final RelativeLayout rl) {
        ibme.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                rl.setVisibility(View.GONE);
                ibme.setVisibility(View.INVISIBLE);
                ibma.setVisibility(View.VISIBLE);
            }
        });
    }


}
