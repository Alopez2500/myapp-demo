/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.alopezc.myapp.demo.dao;

import java.sql.SQLException;

/**
 *
 * @author AlopezCarrillo2500
 */
public interface SQLCloseable extends AutoCloseable{

    @Override
    public void close() throws SQLException;
    
}
