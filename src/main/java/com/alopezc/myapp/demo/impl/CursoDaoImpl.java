/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.alopezc.myapp.demo.impl;

import com.alopezc.myapp.demo.dao.CursoDao;
import com.alopezc.myapp.demo.dao.SQLCloseable;
import com.alopezc.myapp.demo.model.Curso;
import com.alopezc.myapp.demo.utilies.BEAN_CRUD;
import com.alopezc.myapp.demo.utilies.BEAN_PAGINATION;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;
import javax.sql.DataSource;

/**
 *
 * @author AlopezCarrillo2500
 */
public class CursoDaoImpl implements CursoDao {

    private static final Logger LOG = Logger.getLogger(CursoDaoImpl.class.getName());

    private final DataSource pool;

    public CursoDaoImpl(DataSource pool) {
        this.pool = pool;
    }

    @Override
    public BEAN_PAGINATION getPagination(HashMap<String, Object> parameters, Connection conn) throws SQLException {
        BEAN_PAGINATION bean_pagination = new BEAN_PAGINATION();
        List<Curso> list = new ArrayList<>();
        PreparedStatement pst;
        ResultSet rs;
        try {
            pst = conn.prepareStatement("SELECT COUNT (IDCURSO) AS COUNT  FROM CURSO WHERE NOMBRE LIKE CONCAT ('%',?,'%')"
                    + parameters.get("SQL_ESTADO"));
            pst.setString(1, String.valueOf(parameters.get("FILTER")));
            LOG.info(pst.toString());
            rs = pst.executeQuery();
            while (rs.next()) {
                LOG.info(String.valueOf(rs.getInt("COUNT")));
                if (rs.getInt("COUNT") > 0) {
                    bean_pagination.setCOUNT_FILTER(rs.getInt("COUNT"));
                    pst = conn.prepareStatement("SELECT * FROM CURSO WHERE NOMBRE LIKE CONCAT ('%',?,'%')" 
                            + parameters.get("SQL_ESTADO")
                            + " ORDER BY "  + String.valueOf(parameters.get("SQL_ORDER_BY")) + " " + String.valueOf(parameters.get("SQL_LIMIT")));
                    pst.setString(1, String.valueOf(parameters.get("FILTER")));
                    LOG.info(pst.toString());
                    rs = pst.executeQuery();
                    while (rs.next()) {
                        Curso curso = new Curso();
                        curso.setIdcurso(rs.getInt("IDCURSO"));
                        curso.setNombre(rs.getString("NOMBRE"));
                        curso.setEstado(rs.getString("ESTADO"));
                        list.add(curso);
                    }
                }
            }
            bean_pagination.setList(list);
            rs.close();
            pst.close();
        } catch (SQLException e) {
            throw e;
        }
        return bean_pagination;
    }

    @Override
    public BEAN_PAGINATION getPagination(HashMap<String, Object> parameters) throws SQLException {
        BEAN_PAGINATION bean_pagination = null;
        try (Connection conn = this.pool.getConnection()) {
            bean_pagination = getPagination(parameters, conn);
        } catch (SQLException e) {
            throw e;
        }
        return bean_pagination;
    }

    @Override
    public BEAN_CRUD add(Curso obj, HashMap<String, Object> parameters) throws SQLException {
        BEAN_CRUD bean_crud = new BEAN_CRUD();
        PreparedStatement pst;
        ResultSet rs;
        try (Connection conn = this.pool.getConnection();
                SQLCloseable finish = conn::rollback;) {
            conn.setAutoCommit(false);
            pst = conn.prepareStatement("SELECT COUNT (IDCURSO) FROM CURSO WHERE NOMBRE = ? ");
            pst.setString(1, obj.getNombre());
            LOG.info(pst.toString());
            rs = pst.executeQuery();
            while (rs.next()) {
                if (rs.getInt("COUNT") == 0) {
                    pst = conn.prepareCall("INSERT INTO CURSO (NOMBRE,ESTADO) VALUES(?,?)");
                    pst.setString(1, obj.getNombre());
                     pst.setString(2, obj.getEstado());
                    LOG.info(pst.toString());
                    pst.executeUpdate();
                    conn.commit();
                    bean_crud.setMENSSAGE_SERVER("ok");
                    bean_crud.setBEAN_PAGINATION(getPagination(parameters, conn));

                } else {
                    bean_crud.setMENSSAGE_SERVER("No se registro, ya existe una Curso con el nombre ingresado");
                }
            }
            pst.close();
            rs.close();
        } catch (SQLException e) {
            throw e;
        }
        return bean_crud; 
    }

    @Override
    public BEAN_CRUD update(Curso obj, HashMap<String, Object> parameters) throws SQLException {
       BEAN_CRUD bean_crud = new BEAN_CRUD();
        PreparedStatement pst;
        ResultSet rs;
        try (Connection conn = this.pool.getConnection();
                SQLCloseable finish = conn::rollback;) {
            conn.setAutoCommit(false);
            pst = conn.prepareStatement("SELECT COUNT (IDCURSO) FROM CURSO WHERE NOMBRE = ? AND IDCURSO != ?");
            pst.setString(1, obj.getNombre());
            pst.setInt(2, obj.getIdcurso());
            LOG.info(pst.toString());
            rs = pst.executeQuery();
            while (rs.next()) {
                if (rs.getInt("COUNT") == 0) {
                    pst = conn.prepareCall("UPDATE CURSO SET NOMBRE = ? , ESTADO = ? WHERE IDCURSO=?");
                    pst.setString(1, obj.getNombre());
                    pst.setString(2, obj.getEstado());
                    pst.setInt(3, obj.getIdcurso());
                    LOG.info(pst.toString());
                    pst.executeUpdate();
                    conn.commit();
                    bean_crud.setMENSSAGE_SERVER("ok");
                    bean_crud.setBEAN_PAGINATION(getPagination(parameters, conn));
                    
                } else {
                    bean_crud.setMENSSAGE_SERVER("No se modifico, ya existe una Curso con el nombre ingresado");
                }
            }
            pst.close();
            rs.close();
        } catch (SQLException e) {
            throw e;
        }
        return bean_crud;
    }

    @Override
    public BEAN_CRUD delete(Integer id, HashMap<String, Object> parameters) throws SQLException {
        BEAN_CRUD bean_crud = new BEAN_CRUD();
        try (Connection conn = this.pool.getConnection();
                SQLCloseable finish = conn::rollback;) {
            conn.setAutoCommit(false);
            try (PreparedStatement pst = conn.prepareStatement("delete from cuurso where idcurso = ? ")) {
                pst.setInt(1, id);
                LOG.info(pst.toString());
                pst.executeUpdate();
                conn.commit();
                bean_crud.setMENSSAGE_SERVER("ok");
                bean_crud.setBEAN_PAGINATION(getPagination(parameters, conn));
            }
        } catch (SQLException e) {
            throw e;
        }
        return bean_crud;
    }

}
