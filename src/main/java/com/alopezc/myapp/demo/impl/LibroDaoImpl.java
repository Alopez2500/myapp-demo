/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.alopezc.myapp.demo.impl;

import com.alopezc.myapp.demo.dao.LibroDao;
import com.alopezc.myapp.demo.dao.SQLCloseable;
import com.alopezc.myapp.demo.model.Autor;
import com.alopezc.myapp.demo.model.Libro;
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
public class LibroDaoImpl implements LibroDao {

    private static final Logger LOG = Logger.getLogger(LibroDaoImpl.class.getName());

    private final DataSource pool;

    public LibroDaoImpl(DataSource pool) {
        this.pool = pool;
    }

    @Override
    public BEAN_PAGINATION getPagination(HashMap<String, Object> parameters, Connection conn) throws SQLException {
        BEAN_PAGINATION beanpagination = new BEAN_PAGINATION();
        List<Libro> list = new ArrayList<>();
        PreparedStatement pst;
        ResultSet rs;
        try {
            pst = conn.prepareStatement("SELECT COUNT(IDLIBRO) AS COUNT FROM LIBRO WHERE "
                    + "LOWER(NOMBRE) LIKE CONCAT('%',?,'%')");
            pst.setString(1, String.valueOf(parameters.get("FILTER")));
            LOG.info(pst.toString());
            rs = pst.executeQuery();
            while (rs.next()) {
                beanpagination.setCOUNT_FILTER(rs.getInt("COUNT"));
                if (rs.getInt("COUNT") > 0) {
                    pst = conn.prepareStatement("select li.idlibro,li.nombre,li.fecha_publicacion,li.genero,li.edicion,au.idautor,au.nombre as nombreAutor ,au.nombre2 as apellido from libro li inner join autor au on li.idautor= au.idautor WHERE "
                            + "LOWER(li.NOMBRE) LIKE CONCAT('%',?,'%') "
                            + "ORDER BY " + String.valueOf(parameters.get("SQL_ORDER_BY")) + " " + parameters.get("SQL_LIMIT"));
                    pst.setString(1, String.valueOf(parameters.get("FILTER")));
                    LOG.info(pst.toString());
                    rs = pst.executeQuery();
                    while (rs.next()) {
                        Libro libro = new Libro();
                        libro.setIdlibro(rs.getInt("IDLIBRO"));
                        libro.setNombre(rs.getString("NOMBRE"));
                        libro.setFechaPublicacion(rs.getDate("fecha_publicacion"));
                        libro.setGenero(rs.getString("GENERO"));
                        libro.setEdicion(rs.getString("EDICION"));
                        Autor autor = new Autor();
                        autor.setIdautor(rs.getInt("IDAUTOR"));
                        autor.setNombre(rs.getString("NOMBREAUTOR"));
                        autor.setNombre2(rs.getString("APELLIDO"));
                        libro.setAutor(autor);
                        list.add(libro);
                    }
                }
            }
            beanpagination.setList(list);
            rs.close();
            pst.close();
        } catch (Exception e) {
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
    public BEAN_CRUD add(Libro obj, HashMap<String, Object> parameters) throws SQLException {
        BEAN_CRUD beancrud = new BEAN_CRUD();
        PreparedStatement pst;
        ResultSet rs;
        try (Connection conn = this.pool.getConnection();
                SQLCloseable finish = conn::rollback;) {
            conn.setAutoCommit(false);
            pst = conn.prepareStatement("SELECT COUNT (IDLIBRO) AS COUNT FROM LIBRO WHERE NOMBRE = ? ");
            pst.setString(1, obj.getNombre());
            rs = pst.executeQuery();
            while (rs.next()) {
                if (rs.getInt("COUNT") == 0) {
                    pst = conn.prepareStatement("insert into libro(nombre,fecha_publicacion,genero,edicion,idautor) values (?,?,?,?,?)");
                    pst.setString(1, obj.getNombre());
                    pst.setDate(2, new java.sql.Date(obj.getFechaPublicacion().getTime()));
                    pst.setString(3, obj.getGenero());
                    pst.setString(4, obj.getEdicion());
                    pst.setInt(5, obj.getAutor().getIdautor());
                    pst.executeUpdate();
                    conn.commit();
                    beancrud.setMENSSAGE_SERVER("ok");
                    beancrud.setBEAN_PAGINATION(getPagination(parameters, conn));
                } else {
                    beancrud.setMENSSAGE_SERVER("No se registró, ya existe un Libro con el nombre ingresado");
                }
            }
            pst.close();
            rs.close();
        } catch (Exception e) {
            throw e;
        }
        return beancrud;
    }

    @Override
    public BEAN_CRUD update(Libro obj, HashMap<String, Object> parameters) throws SQLException {
        BEAN_CRUD beancrud = new BEAN_CRUD();
        PreparedStatement pst;
        ResultSet rs;
        try (Connection conn = this.pool.getConnection();
                SQLCloseable finish = conn::rollback;) {
            conn.setAutoCommit(false);
            pst = conn.prepareStatement("SELECT COUNT(IDLIBRO) AS COUNT FROM LIBRO WHERE NOMBRE = ? AND IDLIBRO != ?");
            pst.setString(1, obj.getNombre());
            pst.setInt(2, obj.getIdlibro());
            rs = pst.executeQuery();
            while (rs.next()) {
                if (rs.getInt("COUNT") == 0) {
                    pst = conn.prepareStatement("UPDATE LIBRO SET NOMBRE = ?, FECHA_PUBLICACION = ?, GENERO = ?, EDICION= ?, "
                            + " IDAUTOR= ? WHERE IDLIBRO= ?");
                    pst.setString(1, obj.getNombre());
                    pst.setDate(2, new java.sql.Date(obj.getFechaPublicacion().getTime()));
                    pst.setString(3, obj.getGenero());
                    pst.setString(4, obj.getEdicion());
                    pst.setInt(5, obj.getAutor().getIdautor());
                    pst.setInt(6, obj.getIdlibro());
                    LOG.info(pst.toString());
                    pst.executeUpdate();
                    conn.commit();
                    beancrud.setMENSSAGE_SERVER("ok");
                    beancrud.setBEAN_PAGINATION(getPagination(parameters, conn));
                } else {
                    beancrud.setMENSSAGE_SERVER("No se modificó, ya existe un Libro con el nombre ingresado");
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
            try (PreparedStatement pst = conn.prepareStatement("delete from libro where idlibro= ? ")) {
                pst.setInt(1, id);
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
