/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.alopezc.myapp.demo.impl;

import com.alopezc.myapp.demo.dao.AutorDao;
import com.alopezc.myapp.demo.dao.SQLCloseable;
import com.alopezc.myapp.demo.model.Autor;
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
public class AutorDaoImpl implements AutorDao {

    private static final Logger LOG = Logger.getLogger(AutorDaoImpl.class.getName());

    private final DataSource pool;

    public AutorDaoImpl(DataSource pool) {
        this.pool = pool;
    }

    @Override
    public BEAN_PAGINATION getPagination(HashMap<String, Object> parameters, Connection conn) throws SQLException {
        BEAN_PAGINATION beanpagination = new BEAN_PAGINATION();
        List<Autor> list = new ArrayList<>();
        PreparedStatement pst;
        ResultSet rs;
        try {
            pst = conn.prepareStatement("SELECT COUNT (IDAUTOR) FROM AUTOR WHERE NOMBRE LIKE CONCAT ('%',?,'%') ");
            pst.setString(1, String.valueOf(parameters.get("FILTER")));
            LOG.info(parameters.toString());
            LOG.info(pst.toString());
            rs = pst.executeQuery();
            while (rs.next()) {
                if (rs.getInt("COUNT") > 0) {
                    beanpagination.setCOUNT_FILTER(rs.getInt("COUNT"));
                    pst = conn.prepareStatement("SELECT * FROM AUTOR WHERE NOMBRE LIKE CONCAT ('%',?,'%')"
                            + " ORDER BY " + String.valueOf(parameters.get("SQL_ORDER_BY")) + " " + String.valueOf(parameters.get("SQL_LIMIT")));
                    pst.setString(1, String.valueOf(parameters.get("FILTER")));
                    LOG.info(pst.toString());
                    rs = pst.executeQuery();
                    while (rs.next()) {
                        Autor autor = new Autor();
                        autor.setIdautor(rs.getInt("IDAUTOR"));
                        autor.setNombre(rs.getString("NOMBRE"));
                        autor.setNombre2(rs.getString("NOMBRE2"));
                        autor.setDocumento(rs.getString("DOCUMENTO"));
                        autor.setTelefono(rs.getString("TELEFONO"));
                        autor.setDireccion(rs.getString("DIRECCION"));
                        list.add(autor);
                    }
                }

            }
            beanpagination.setList(list);
            rs.close();
            pst.close();
        } catch (SQLException e) {
            throw e;
        }
        return beanpagination;
    }

    @Override
    public BEAN_PAGINATION getPagination(HashMap<String, Object> parameters) throws SQLException {
        BEAN_PAGINATION beanpagination = null;
        try (Connection conn = this.pool.getConnection()) {
            beanpagination = getPagination(parameters, conn);
        } catch (SQLException e) {
            throw e;
        }
        return beanpagination;
    }

    @Override
    public BEAN_CRUD add(Autor obj, HashMap<String, Object> parameters) throws SQLException {
        BEAN_CRUD beancrud = new BEAN_CRUD();
        PreparedStatement pst;
        ResultSet rs;
        try (Connection conn = this.pool.getConnection();
                SQLCloseable finish = conn::rollback;) {
            conn.setAutoCommit(false);
            pst = conn.prepareStatement("SELECT COUNT (IDAUTOR) FROM AUTOR WHERE NOMBRE = ? ");
            pst.setString(1, obj.getNombre());
            LOG.info(pst.toString());
            rs = pst.executeQuery();
            while (rs.next()) {
                if (rs.getInt("COUNT") == 0) {
                    pst = conn.prepareCall("INSERT INTO AUTOR (NOMBRE,NOMBRE2,DOCUMENTO,TELEFONO,DIRECCION) VALUES(?,?,?,?,?)");
                    pst.setString(1, obj.getNombre());
                    pst.setString(2, obj.getNombre2());
                    pst.setString(3, obj.getDocumento());
                    pst.setString(4, obj.getTelefono());
                    pst.setString(5, obj.getDireccion());
                    LOG.info(pst.toString());
                    pst.executeUpdate();
                    conn.commit();
                    beancrud.setMENSSAGE_SERVER("ok");
                    beancrud.setBEAN_PAGINATION(getPagination(parameters, conn));

                } else {
                    beancrud.setMENSSAGE_SERVER("No se registro, ya existe una Cliente con el nombre ingresado");
                }
            }
            pst.close();
            rs.close();
        } catch (SQLException e) {
            throw e;
        }
        return beancrud;
    }

    @Override
    public BEAN_CRUD update(Autor obj, HashMap<String, Object> parameters) throws SQLException {
        BEAN_CRUD beancrud = new BEAN_CRUD();
        PreparedStatement pst;
        ResultSet rs;
        try (Connection conn = this.pool.getConnection();
                SQLCloseable finish = conn::rollback;) {
            conn.setAutoCommit(false);
            pst = conn.prepareStatement("SELECT COUNT (IDAUTOR) FROM AUTOR WHERE NOMBRE = ? AND IDAUTOR= ?");
            pst.setString(1, obj.getNombre());
            pst.setInt(2, obj.getIdautor());
            LOG.info(pst.toString());
            rs = pst.executeQuery();
            while (rs.next()) {
                if (rs.getInt("COUNT") == 0) {
                    pst = conn.prepareCall("UPDATE AUTOR SET NOMBRE = ?, NOMBRE2 = ? , DOCUMENTO = ? , TELEFONO = ? , DIRECCION = ?  WHERE IDAUTOR=?");
                    pst.setString(1, obj.getNombre());
                    pst.setString(2, obj.getNombre2());
                    pst.setString(3, obj.getDocumento());
                    pst.setString(4, obj.getTelefono());
                    pst.setString(5, obj.getDireccion());
                    pst.setInt(6, obj.getIdautor());
                    LOG.info(pst.toString());
                    pst.executeUpdate();
                    conn.commit();
                    beancrud.setMENSSAGE_SERVER("ok");
                    beancrud.setBEAN_PAGINATION(getPagination(parameters, conn));

                } else {
                    beancrud.setMENSSAGE_SERVER("No se modifico, ya existe una Cliente con el nombre ingresado");
                }
            }
            pst.close();
            rs.close();
        } catch (SQLException e) {
            throw e;
        }
        return beancrud;
    }

    @Override
    public BEAN_CRUD delete(Integer id, HashMap<String, Object> parameters) throws SQLException {
        BEAN_CRUD beancrud = new BEAN_CRUD();
        try (Connection conn = this.pool.getConnection();
                SQLCloseable finish = conn::rollback;) {
            conn.setAutoCommit(false);
            try (PreparedStatement pst = conn.prepareStatement("delete from autor where idautor = ? ")) {
                pst.setInt(1, id);
                LOG.info(pst.toString());
                pst.executeUpdate();
                conn.commit();
                beancrud.setMENSSAGE_SERVER("ok");
                beancrud.setBEAN_PAGINATION(getPagination(parameters, conn));
            }
        } catch (SQLException e) {
            throw e;
        }
        return beancrud;
    }

}
