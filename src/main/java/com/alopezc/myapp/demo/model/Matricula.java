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
public class Matricula {
    private Integer idmatricula;
    private String codigo;
    private String ciclo;
    private Alumno alumno;

    @Override
    public String toString() {
        return "Matricula{" + "idmatricula=" + idmatricula + ", codigo=" + codigo + ", ciclo=" + ciclo + ", alumno=" + alumno + '}';
    }

    
    public Integer getIdmatricula() {
        return idmatricula;
    }

    public void setIdmatricula(Integer idmatricula) {
        this.idmatricula = idmatricula;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getCiclo() {
        return ciclo;
    }

    public void setCiclo(String ciclo) {
        this.ciclo = ciclo;
    }

    public Alumno getAlumno() {
        return alumno;
    }

    public void setAlumno(Alumno alumno) {
        this.alumno = alumno;
    }
}
