/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.alopezc.myapp.demo.utilies;

import java.text.SimpleDateFormat;

/**
 *
 * @author AlopezCarrillo2500
 */
public class ParceDate {

    public static java.util.Date getDate(String cadenaFecha, String formato) throws Exception {
        java.util.Date date = null;
        try {
            //formato = dd/MM/yyyy
            formato = "dd/MM/yyyy";
            SimpleDateFormat format = new SimpleDateFormat(formato);
            date = format.parse(cadenaFecha);
        } catch (Exception e) {
            throw e;
        }
        return date;
    }
    
}
