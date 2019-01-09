/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.alopezc.myapp.demo.dao;

import com.alopezc.myapp.demo.utilies.BEAN_CRUD;
import com.alopezc.myapp.demo.utilies.BEAN_PAGINATION;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;

/**
 *
 * @author AlopezCarrillo2500
 */
public interface CRUD<T> {
    BEAN_PAGINATION getPagination(HashMap <String,Object>  parameters, Connection conn) throws SQLException;
    
    BEAN_PAGINATION getPagination(HashMap <String,Object>  parameters) throws SQLException;
    
    BEAN_CRUD add(T obj,HashMap <String,Object>  parameters)throws SQLException;
    BEAN_CRUD update(T obj,HashMap <String,Object>  parameters)throws SQLException;
    BEAN_CRUD delete(Integer id, HashMap <String,Object> parameters)throws SQLException;
   
}
