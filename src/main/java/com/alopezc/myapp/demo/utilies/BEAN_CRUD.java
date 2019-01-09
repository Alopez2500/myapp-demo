/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.alopezc.myapp.demo.utilies;

/**
 *
 * @author AlopezCarrillo2500
 */
public class BEAN_CRUD {
    
    private String MENSSAGE_SERVER;
    private BEAN_PAGINATION BEAN_PAGINATION;

    public BEAN_CRUD() {
    }

    public BEAN_CRUD(BEAN_PAGINATION BEAN_PAGINATION) {
        this.BEAN_PAGINATION = BEAN_PAGINATION;
    }
    

    public String getMENSSAGE_SERVER() {
        return MENSSAGE_SERVER;
    }

    public void setMENSSAGE_SERVER(String MENSSAGE_SERVER) {
        this.MENSSAGE_SERVER = MENSSAGE_SERVER;
    }

    public BEAN_PAGINATION getBEAN_PAGINATION() {
        return BEAN_PAGINATION;
    }

    public void setBEAN_PAGINATION(BEAN_PAGINATION BEAN_PAGINATION) {
        this.BEAN_PAGINATION = BEAN_PAGINATION;
    }
    
}
