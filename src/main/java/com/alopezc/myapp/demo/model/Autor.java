/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.alopezc.myapp.demo.model;

/**
 *
 * @author AlopezCarrillo2500
 */
public class Autor {
    private Integer idautor;
    private String nombre;
    private String nombre2;
    private String documento;
    private String telefono;
    private String direccion;

    public Autor(Integer idautor) {
        this.idautor = idautor;
    }

    
    public Autor() {
    }

 
    @Override
    public String toString() {
        return "Autor{" + "idautor=" + idautor + ", nombre=" + nombre + ", nombre2=" + nombre2 + ", documento=" + documento + ", telefono=" + telefono + ", direccion=" + direccion + '}';
    }

    
    public Integer getIdautor() {
        return idautor;
    }

    public void setIdautor(Integer idautor) {
        this.idautor = idautor;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getNombre2() {
        return nombre2;
    }

    public void setNombre2(String nombre2) {
        this.nombre2 = nombre2;
    }

    public String getDocumento() {
        return documento;
    }

    public void setDocumento(String documento) {
        this.documento = documento;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }
    
    
            
}
