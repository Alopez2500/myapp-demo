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
public class Curso {
    private Integer idcurso;
    private String nombre;
    private String estado;

    public Integer getIdcurso() {
        return idcurso;
    }

    public void setIdcurso(Integer idcurso) {
        this.idcurso = idcurso;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    @Override
    public String toString() {
        return "curso{" + "idcurso=" + idcurso + ", nombre=" + nombre + ", estado=" + estado + '}';
    }
    
    
}
