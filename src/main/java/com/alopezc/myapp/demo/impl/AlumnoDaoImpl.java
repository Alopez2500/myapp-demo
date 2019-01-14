/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.alopezc.myapp.demo.impl;

import com.alopezc.myapp.demo.dao.AlumnoDao;
import com.alopezc.myapp.demo.dao.SQLCloseable;
import com.alopezc.myapp.demo.model.Alumno;
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
public class AlumnoDaoImpl implements AlumnoDao {

    private static final Logger LOG = Logger.getLogger(AlumnoDaoImpl.class.getName());

    private DataSource pool;

    public AlumnoDaoImpl(DataSource pool) {
        this.pool = pool;
    }

    @Override
    public BEAN_PAGINATION getPagination(HashMap<String, Object> parameters, Connection conn) throws SQLException {
        BEAN_PAGINATION bean_pagination = new BEAN_PAGINATION();
        List<Alumno> list = new ArrayList<>();
        PreparedStatement pst;
        ResultSet rs;
        try {
            pst = conn.prepareStatement("SELECT COUNT (IDALUMNO) AS COUNT  FROM ALUMNO WHERE NOMBRE LIKE CONCAT ('%',?,'%')");
            pst.setString(1, String.valueOf(parameters.get("FILTER")));
            LOG.info(pst.toString());
            rs = pst.executeQuery();
            while (rs.next()) {
                LOG.info(String.valueOf(rs.getInt("COUNT")));
                if (rs.getInt("COUNT") > 0) {
                    bean_pagination.setCOUNT_FILTER(rs.getInt("COUNT"));
                    pst = conn.prepareStatement("SELECT * FROM ALUMNO WHERE NOMBRE LIKE CONCAT ('%',?,'%')"
                            + " ORDER BY " + String.valueOf(parameters.get("SQL_ORDER_BY")) + " " + String.valueOf(parameters.get("SQL_LIMIT")));
                    pst.setString(1, String.valueOf(parameters.get("FILTER")));
                    LOG.info(pst.toString());
                    rs = pst.executeQuery();
                    while (rs.next()) {
                        Alumno alumno = new Alumno();
                        alumno.setIdalumno(rs.getInt("IDALUMNO"));
                        alumno.setNombre(rs.getString("NOMBRE"));
                        alumno.setNombre2(rs.getString("NOMBRE2"));
                        alumno.setFecha_nacimiento(rs.getDate("FECHA_NACIMIENTO"));
                        alumno.setDireccion(rs.getString("DIRECCION"));
                        alumno.setTelefono(rs.getString("TELEFONO"));
                        list.add(alumno);
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
    public BEAN_CRUD add(Alumno obj, HashMap<String, Object> parameters) throws SQLException {
        BEAN_CRUD bean_crud = new BEAN_CRUD();
        PreparedStatement pst;
        ResultSet rs;
        try (Connection conn = this.pool.getConnection();
                SQLCloseable finish = conn::rollback;) {
            conn.setAutoCommit(false);
            pst = conn.prepareStatement("SELECT COUNT (IDALUMNO) FROM ALUMNO WHERE NOMBRE = ? ");
            pst.setString(1, obj.getNombre());
            LOG.info(pst.toString());
            rs = pst.executeQuery();
            while (rs.next()) {
                if (rs.getInt("COUNT") == 0) {
                    pst = conn.prepareCall("INSERT INTO ALUMNO (NOMBRE,NOMBRE2,FECHA_NACIMIENTO,DIRECCION,TELEFONO) VALUES(?,?,?,?,?)");
                    pst.setString(1, obj.getNombre());
                    pst.setString(2, obj.getNombre2());
                    pst.setDate(3, new java.sql.Date(obj.getFecha_nacimiento().getTime()));
                    pst.setString(4, obj.getDireccion());
                    pst.setString(5, obj.getTelefono());
                    LOG.info(pst.toString());
                    pst.executeUpdate();
                    conn.commit();
                    bean_crud.setMENSSAGE_SERVER("ok");
                    bean_crud.setBEAN_PAGINATION(getPagination(parameters, conn));

                } else {
                    bean_crud.setMENSSAGE_SERVER("No se registro, ya existe una Alumno con el nombre ingresado");
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
    public BEAN_CRUD update(Alumno obj, HashMap<String, Object> parameters) throws SQLException {
        BEAN_CRUD bean_crud = new BEAN_CRUD();
        PreparedStatement pst;
        ResultSet rs;
        try (Connection conn = this.pool.getConnection();
                SQLCloseable finish = conn::rollback;) {
            conn.setAutoCommit(false);
            pst = conn.prepareStatement("SELECT COUNT (IDALUMNO) FROM ALUMNO WHERE NOMBRE = ? AND IDALUMNO != ?");
            pst.setString(1, obj.getNombre());
            pst.setInt(2, obj.getIdalumno());
            LOG.info(pst.toString());
            rs = pst.executeQuery();
            while (rs.next()) {
                if (rs.getInt("COUNT") == 0) {
                    pst = conn.prepareCall("UPDATE ALUMNO SET NOMBRE = ? , NOMBRE2 = ? , FECHA_NACIMIENTO = ? ,DIRECCION = ? ,TELEFONO = ? WHERE IDALUMNO=?");
                    pst.setString(1, obj.getNombre());
                    pst.setString(2, obj.getNombre2());
                    pst.setDate(3, new java.sql.Date(obj.getFecha_nacimiento().getTime()));
                     pst.setString(4, obj.getDireccion());
                     pst.setString(5, obj.getTelefono());
                    pst.setInt(6, obj.getIdalumno());
                    LOG.info(pst.toString());
                    pst.executeUpdate();
                    conn.commit();
                    bean_crud.setMENSSAGE_SERVER("ok");
                    bean_crud.setBEAN_PAGINATION(getPagination(parameters, conn));

                } else {
                    bean_crud.setMENSSAGE_SERVER("No se modifico, ya existe una Alumno con el nombre ingresado");
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
            try (PreparedStatement pst = conn.prepareStatement("delete from alumno where idalumno = ? ")) {
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
