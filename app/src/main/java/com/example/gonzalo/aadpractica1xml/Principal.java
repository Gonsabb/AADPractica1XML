package com.example.gonzalo.aadpractica1xml;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Xml;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.gonzalo.aadpractica1xml.contacto.Contacto;
import com.example.gonzalo.aadpractica1xml.contacto.GestionContactos;
import com.example.gonzalo.aadpractica1xml.contacto.OrdenarLista;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;


public class Principal extends AppCompatActivity {

    public ArrayList<Contacto> contactos;
    public Adaptador adap;
    private SharedPreferences sp;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);
        try {
            iniciar();
        } catch (IOException e) {
        } catch (XmlPullParserException e) {
        }
    }

    private void iniciar() throws IOException, XmlPullParserException {

        sp = PreferenceManager.getDefaultSharedPreferences(
                getApplicationContext());

        GestionContactos ga= new GestionContactos(this);
        contactos = ga.getGestion();

        for(Contacto aux:contactos){
            aux.setTelefonos((ArrayList<String>) GestionContactos.getListaTelefonos(this,aux.getId()));
        }

        for(int z =0; z<contactos.size(); z++ ){
            contactos.get(z).setId(z);
        }

        ListView lv= (ListView)findViewById(R.id.lvMostrar);
        String aux = sp.getString("start","no");
        if(aux.equals("no")){
            SharedPreferences.Editor ed = sp.edit();
            ed.putString("start", "si");
            ed.commit();
            contactos = new ArrayList<>();
            contactos = ga.getGestion();
            escribir(contactos);
            Log.v("Error", "No se puede crear");

        }else{
            contactos = new ArrayList<>();
            contactos = leer();
            Log.v("Error","No se puede leer");
        }

        adap= new Adaptador(this, R.layout.listact, contactos);
        lv.setAdapter(adap);
        lv.setTag(contactos);
        registerForContextMenu(lv);
        Collections.sort(contactos);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.menucontextual, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo vistaInfo=(AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        int posicion=vistaInfo.position;

        switch(item.getItemId()){
            case R.id.menu_editar:
                editar(posicion);
                adap.notifyDataSetChanged();
                return true;

            case R.id.menu_borrar:
                Toast.makeText(Principal.this, "El contacto "+contactos.get(posicion).getId() +"ha sido eliminado", Toast.LENGTH_SHORT).show();
                contactos.remove(posicion);
                adap.notifyDataSetChanged();
                return true;

            default:
                return super.onContextItemSelected(item);
        }
    }

    public void editar(final int posicion){

        AlertDialog.Builder alert= new AlertDialog.Builder(this);
        alert.setTitle("Editar");
        LayoutInflater inflater= LayoutInflater.from(this);

        final View vista = inflater.inflate(R.layout.editar, null);

        String ap=""+posicion;
        final int p =Integer.parseInt(ap);

        EditText et1, et2, et3, et4;
        et1 = (EditText) vista.findViewById(R.id.etNombre);
        et2 = (EditText) vista.findViewById(R.id.editText2);
        et3 = (EditText) vista.findViewById(R.id.editText3);
        et4 = (EditText) vista.findViewById(R.id.editText4);
        et1.setText(contactos.get(p).getNombre());

        if(contactos.get(p).size()==1) {
            et2.setText(contactos.get(p).getTelefono(0));
        }else if(contactos.get(p).size()==2){
            et2.setText(contactos.get(p).getTelefono(0));
            et3.setText(contactos.get(p).getTelefono(1));
        }else if(contactos.get(p).size()==3){
            et2.setText(contactos.get(p).getTelefono(0));
            et3.setText(contactos.get(p).getTelefono(1));
            et4.setText(contactos.get(p).getTelefono(2));
        }else{
            et2.setText("");
            et3.setText("");
            et4.setText("");
        }

        alert.setPositiveButton("Actualizar", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int whichButton) {

                EditText et1, et2, et3, et4;
                et1 = (EditText) vista.findViewById(R.id.etNombre);
                et2 = (EditText) vista.findViewById(R.id.editText2);
                et3 = (EditText) vista.findViewById(R.id.editText3);
                et4 = (EditText) vista.findViewById(R.id.editText4);

                contactos.get(p).setNombre(et1.getText().toString());
                ArrayList<String> telf = new ArrayList<>();
                telf.add(et2.getText().toString());
                telf.add(et3.getText().toString());
                telf.add(et4.getText().toString());

                contactos.get(p).setTelefonos(telf);
                adap.notifyDataSetChanged();

                Collections.sort(contactos, new OrdenarLista());
                adap = new Adaptador(Principal.this, R.layout.listact, contactos);
                ListView lv = (ListView) findViewById(R.id.lvMostrar);
                lv.setAdapter(adap);
            }
        });
        alert.setView(vista);
        alert.setNegativeButton("cerrar", null);
        alert.show();
    }

    public void nuevoCont(View v){
        AlertDialog.Builder alert= new AlertDialog.Builder(this);
        alert.setTitle("Añadir");
        LayoutInflater inflater= LayoutInflater.from(this);
        final View vista = inflater.inflate(R.layout.editar, null);

        alert.setPositiveButton("Añadir", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int whichButton) {

                int id = contactos.size();

                EditText et1, et2, et3, et4;

                et1 = (EditText) vista.findViewById(R.id.etNombre);
                et2 = (EditText) vista.findViewById(R.id.editText2);
                et3 = (EditText) vista.findViewById(R.id.editText3);
                et4 = (EditText) vista.findViewById(R.id.editText4);


                String nombre = et1.getText().toString();
                ArrayList<String> telf = new ArrayList<>();
                telf.add(et2.getText().toString());
                telf.add(et3.getText().toString());
                telf.add(et4.getText().toString());

                Contacto c = new Contacto(id, nombre, telf);

                contactos.add(c);

                adap.notifyDataSetChanged();

                Collections.sort(contactos, new OrdenarLista());
                adap = new Adaptador(Principal.this, R.layout.listact, contactos);
                ListView lv = (ListView) findViewById(R.id.lvMostrar);
                lv.setAdapter(adap);
            }
        });
        alert.setView(vista);
        alert.setNegativeButton("cerrar", null);
        alert.show();
    }


    /*******************************************************************************************************/

    public void sincronizar (View v) throws IOException, XmlPullParserException {

        ListView lv=(ListView)findViewById(R.id.lvMostrar);
        try {
            escribir(contactos);
            escribir2(contactos);
        } catch (IOException e) {

        }
        adap= new Adaptador(this, R.layout.listact, contactos);
        lv.setAdapter(adap);
        lv.setTag(contactos);
        registerForContextMenu(lv);
    }

    /****************************************************************************************************/

    public void escribir(ArrayList<Contacto> x) throws IOException {
        FileOutputStream fosxml = new FileOutputStream(new File(getExternalFilesDir(null),"archivo.xml"));
        XmlSerializer docxml = Xml.newSerializer();
        docxml.setOutput(fosxml, "UTF-8");
        docxml.startDocument(null, Boolean.valueOf(true));
        docxml.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);
        docxml.startTag(null, "contactos");
        for(int i = 0; i<x.size();i++){
            docxml.startTag(null, "contacto");
            docxml.startTag(null, "nombre");
            docxml.attribute(null, "id", String.valueOf(x.get(i).getId()));
            docxml.text(x.get(i).getNombre().toString());
            docxml.endTag(null, "nombre");
            for(int j=0; j<x.get(i).getTelefonos().size(); j++) {
                docxml.startTag(null, "telefono");
                docxml.text(x.get(i).getTelefono(j).toString());
                docxml.endTag(null, "telefono");
            }
            docxml.endTag(null, "contacto");
        }
        docxml.endDocument();
        docxml.flush();
        fosxml.close();
    }

    public ArrayList<Contacto> leer() throws IOException, XmlPullParserException {
        ArrayList<Contacto> contact=new ArrayList();
        Contacto c;
        int id=0;
        ArrayList <String> telf= new ArrayList<>();

        XmlPullParser lectorxml = Xml.newPullParser();
        lectorxml.setInput(new FileInputStream(new File(getExternalFilesDir(null), "archivo.xml")), "utf-8");
        int evento = lectorxml.getEventType();
        String nombre= "";
        String telefono;
        while (evento != XmlPullParser.END_DOCUMENT){
            if(evento == XmlPullParser.START_TAG){
                String etiqueta = lectorxml.getName();
                Log.v("etiqueta",etiqueta);
                if(etiqueta.compareTo("contacto")==0){
                    telf=new ArrayList<>();
                    c=null;
                    nombre="";
                }
                if(etiqueta.compareTo("nombre")==0){
                    id= Integer.parseInt(lectorxml.getAttributeValue(null,"id"));
                    nombre=lectorxml.nextText();

                } else if(etiqueta.compareTo("telefono")==0){
                    telefono = lectorxml.nextText();
                    telf.add(telefono);

                }
            }
            if(evento==XmlPullParser.END_TAG){
                String etiqueta = lectorxml.getName();
                if(etiqueta.compareTo("contacto")==0){
                    c = new Contacto(id, nombre, telf);
                    Log.v("Contacto",c.getNombre()+c.getId());
                    contact.add(c);
                }
            }
            evento = lectorxml.next();
        }
        return contact;
    }

    public void recuperarOriginal(View v){
        GestionContactos ga= new GestionContactos(this);
        contactos = ga.getGestion();

        for(Contacto aux:contactos){
            aux.setTelefonos((ArrayList<String>) GestionContactos.getListaTelefonos(this,aux.getId()));
        }

        for(int z =0; z<contactos.size(); z++ ){
            contactos.get(z).setId(z);
        }

        ListView lv=(ListView)findViewById(R.id.lvMostrar);


        try{
            escribir(contactos);
        }catch (IOException e){
        }

        adap= new Adaptador(this, R.layout.listact, contactos);
        lv.setAdapter(adap);
        lv.setTag(contactos);
        registerForContextMenu(lv);
    }

    /**********************************************************************************************************/

    public void escribir2(ArrayList<Contacto> x) throws IOException {
        FileOutputStream fosxml = new FileOutputStream(new File(getExternalFilesDir(null),"archivo2.xml"));
        XmlSerializer docxml = Xml.newSerializer();
        docxml.setOutput(fosxml, "UTF-8");
        docxml.startDocument(null, Boolean.valueOf(true));
        docxml.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);
        docxml.startTag(null, "contactos");
        for(int i = 0; i<x.size();i++){
            docxml.startTag(null, "contacto");
            docxml.startTag(null, "nombre");
            docxml.attribute(null, "id", String.valueOf(x.get(i).getId()));
            docxml.text(x.get(i).getNombre().toString());
            docxml.endTag(null, "nombre");
            for(int j=0; j<x.get(i).getTelefonos().size(); j++) {
                docxml.startTag(null, "telefono");
                docxml.text(x.get(i).getTelefono(j).toString());
                docxml.endTag(null, "telefono");
            }
            docxml.endTag(null, "contacto");
        }
        docxml.endDocument();
        docxml.flush();
        fosxml.close();
    }

    public ArrayList<Contacto> leer2() throws IOException, XmlPullParserException {
        ArrayList<Contacto> contact=new ArrayList();
        Contacto c;
        int id=0;
        ArrayList <String> telf= new ArrayList<>();

        XmlPullParser lectorxml = Xml.newPullParser();
        lectorxml.setInput(new FileInputStream(new File(getExternalFilesDir(null), "archivo2.xml")), "utf-8");
        int evento = lectorxml.getEventType();
        String nombre= "";
        String telefono;
        while (evento != XmlPullParser.END_DOCUMENT){
            if(evento == XmlPullParser.START_TAG){
                String etiqueta = lectorxml.getName();
                Log.v("etiqueta",etiqueta);
                if(etiqueta.compareTo("contacto")==0){
                    telf=new ArrayList<>();
                    c=null;
                    nombre="";
                }
                if(etiqueta.compareTo("nombre")==0){
                    id= Integer.parseInt(lectorxml.getAttributeValue(null,"id"));
                    nombre=lectorxml.nextText();

                } else if(etiqueta.compareTo("telefono")==0){
                    telefono = lectorxml.nextText();
                    telf.add(telefono);

                }
            }
            if(evento==XmlPullParser.END_TAG){
                String etiqueta = lectorxml.getName();
                if(etiqueta.compareTo("contacto")==0){
                    c = new Contacto(id, nombre, telf);
                    Log.v("Contacto",c.getNombre()+c.getId());
                    contact.add(c);
                }
            }
            evento = lectorxml.next();
        }
        return contact;
    }

    public void recuperarModificada(View v){
        GestionContactos ga= new GestionContactos(this);
        contactos = ga.getGestion();

        for(Contacto aux:contactos){
            aux.setTelefonos((ArrayList<String>) GestionContactos.getListaTelefonos(this,aux.getId()));
        }

        for(int z =0; z<contactos.size(); z++ ){
            contactos.get(z).setId(z);
        }

        ListView lv=(ListView)findViewById(R.id.lvMostrar);


        try{
            contactos=leer2();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        adap= new Adaptador(this, R.layout.listact, contactos);
        lv.setAdapter(adap);
        lv.setTag(contactos);
        registerForContextMenu(lv);
    }

}
