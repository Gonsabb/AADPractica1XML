package com.example.gonzalo.aadpractica1xml.contacto;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Gonzalo on 01/11/2015.
 */
public class Contacto implements Serializable, Comparable<Contacto> {
    private long id;
    private String nombre;
    private ArrayList<String> telefonos;

    public Contacto(long id, String nombre, ArrayList<String> telefonos) {
        this.id = id;
        this.nombre = nombre;
        this.telefonos = telefonos;
    }

    public Contacto() {
        this(0,"",new ArrayList<String>());
    }

    public String getTelefono(int location) {
        return telefonos.get(location);
    }

    public ArrayList<String> getTelefonos() {
        return telefonos;
    }

    public void setTelefono(int location, String telefono){
        this.telefonos.set(location,telefono);
    }

    public boolean addTelefono(String object) {
        return getTelefonos().add(object);
    }

    public int size() {
        return telefonos.size();
    }

    public boolean isEmpty() {
        return telefonos.isEmpty();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setTelefonos(ArrayList<String> telefonos) {
        this.telefonos = telefonos;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Contacto contacto = (Contacto) o;

        return id == contacto.id;
    }

    @Override
    public int hashCode() {
        return (int) (id ^ (id >>> 32));
    }


    @Override
    public int compareTo(Contacto contacto) {
        int r = this.nombre.compareTo(contacto.nombre);
        if ( r == 0){
            r =(int)(this.id - contacto.id);
        }
        return r;
    }

    @Override
    public String toString() {
        return "Contacto{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", telefonos=" + telefonos +
                '}';
    }
}
