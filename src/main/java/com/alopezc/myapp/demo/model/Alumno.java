/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.alopezc.myapp.demo.model;

import java.util.Date;

/**
 *
 * @author AlopezCarrillo2500
 */
public class Alumno {
    private Integer idalumno;
    private String nombre;
    private String nombre2;
    private Date fecha_nacimiento;
    private String direccion;
    private String telefono;

    public Alumno(Integer idalumno) {
        this.idalumno = idalumno;
    }

    public Alumno() {
    }
    

    @Override
    public String toString() {
        return "Alumno{" + "idalumno=" + idalumno + ", nombre=" + nombre + ", nombre2=" + nombre2 + ", fecha_nacimiento=" + fecha_nacimiento + ", direccion=" + direccion + ", telefono=" + telefono + '}';
    }
    
    public Integer getIdalumno() {
        return idalumno;
    }

    public void setIdalumno(Integer idalumno) {
        this.idalumno = idalumno;
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

    public Date getFecha_nacimiento() {
        return fecha_nacimiento;
    }

    public void setFecha_nacimiento(Date fecha_nacimiento) {
        this.fecha_nacimiento = fecha_nacimiento;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }
    
    
}
