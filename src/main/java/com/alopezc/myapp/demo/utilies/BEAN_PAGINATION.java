/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.alopezc.myapp.demo.utilies;

import java.util.List;

/**
 *
 * @author AlopezCarrillo2500
 */
public class BEAN_PAGINATION {

    private Integer COUNT_FILTER;
    private List<?> List;

    public Integer getCOUNT_FILTER() {
        return COUNT_FILTER;
    }

    public void setCOUNT_FILTER(Integer COUNT_FILTER) {
        this.COUNT_FILTER = COUNT_FILTER;
    }

    public List<?> getList() {
        return List;
    }

    public void setList(List<?> List) {
        this.List = List;
    }
    
    
}
