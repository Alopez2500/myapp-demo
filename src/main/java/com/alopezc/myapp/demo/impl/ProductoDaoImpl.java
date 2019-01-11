/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.alopezc.myapp.demo.impl;

import com.alopezc.myapp.demo.dao.ProductoDao;
import com.alopezc.myapp.demo.dao.SQLCloseable;
import com.alopezc.myapp.demo.model.Categoria;
import com.alopezc.myapp.demo.model.Producto;
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
public class ProductoDaoImpl implements ProductoDao {

    private static final Logger LOG = Logger.getLogger(ProductoDaoImpl.class.getName());

    private final DataSource pool;

    public ProductoDaoImpl(DataSource pool) {
        this.pool = pool;
    }

    @Override
    public BEAN_PAGINATION getPagination(HashMap<String, Object> parameters, Connection conn) throws SQLException {
        BEAN_PAGINATION beanpagination = new BEAN_PAGINATION();
        List<Producto> list = new ArrayList<>();
        PreparedStatement pst;
        ResultSet rs;
        try {
            pst = conn.prepareStatement("SELECT COUNT(IDPRODUCTO) AS COUNT FROM PRODUCTO WHERE "
                    + "LOWER(NOMBRE) LIKE CONCAT('%',?,'%')");
            pst.setString(1, String.valueOf(parameters.get("FILTER")));
            LOG.info(pst.toString());
            rs = pst.executeQuery();
            while (rs.next()) {
                beanpagination.setCOUNT_FILTER(rs.getInt("COUNT"));
                if (rs.getInt("COUNT") > 0) {
                    pst = conn.prepareStatement("SELECT pro.IDPRODUCTO, pro.nombre,pro.precio,pro.stock,pro.stock_min,pro.stock_max,ca.idcategoria,ca.nombre as nombreCategoria FROM PRODUCTO pro inner join CATEGORIA ca on pro.idcategoria = ca.idcategoria WHERE "
                            + "LOWER(PRO.NOMBRE) LIKE CONCAT('%',?,'%') "
                            + "ORDER BY " + String.valueOf(parameters.get("SQL_ORDER_BY")) + " " + parameters.get("SQL_LIMIT"));
                    pst.setString(1, String.valueOf(parameters.get("FILTER")));
                    LOG.info(pst.toString());
                    rs = pst.executeQuery();
                    while (rs.next()) {
                        Producto producto = new Producto();
                        producto.setIdproducto(rs.getInt("IDPRODUCTO"));
                        producto.setNombre(rs.getString("NOMBRE"));
                        producto.setPrecio(rs.getDouble("PRECIO"));
                        producto.setStock(rs.getInt("STOCK"));
                        producto.setStock_min(rs.getInt("STOCK_MIN"));
                        producto.setStock_max(rs.getInt("STOCK_MAX"));
                        producto.setCategoria(new Categoria(rs.getInt("IDCATEGORIA"),rs.getString("nombreCategoria")));
                        list.add(producto);
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
    public BEAN_CRUD add(Producto obj, HashMap<String, Object> parameters) throws SQLException {
        BEAN_CRUD beancrud = new BEAN_CRUD();
        PreparedStatement pst;
        ResultSet rs;
        try (Connection conn = this.pool.getConnection();
                SQLCloseable finish = conn::rollback;) {
            conn.setAutoCommit(false);
            pst = conn.prepareStatement("SELECT COUNT (IDPRODUCTO) AS COUNT FROM PRODUCTO WHERE NOMBRE = ? ");
            pst.setString(1, obj.getNombre());
            rs = pst.executeQuery();
            while (rs.next()) {
                if (rs.getInt("COUNT") == 0) {
                    pst = conn.prepareStatement("INSERT INTO PRODUCTO(NOMBRE,PRECIO,STOCK,STOCK_MIN,STOCK_MAX,IDCATEGORIA) VALUES(?,?,?,?,?,?)");
                    pst.setString(1, obj.getNombre());
                    pst.setDouble(2, obj.getPrecio());
                    pst.setInt(3, obj.getStock());
                    pst.setInt(4, obj.getStock_min());
                    pst.setInt(5, obj.getStock_max());
                    pst.setInt(6, obj.getCategoria().getIdcategoria());
                    pst.executeUpdate();
                    conn.commit();
                    beancrud.setMENSSAGE_SERVER("ok");
                    beancrud.setBEAN_PAGINATION(getPagination(parameters, conn));
                } else {
                    beancrud.setMENSSAGE_SERVER("No se registró, ya existe un Producto con el nombre ingresado");
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
    public BEAN_CRUD update(Producto obj, HashMap<String, Object> parameters) throws SQLException {
        BEAN_CRUD beancrud = new BEAN_CRUD();
        PreparedStatement pst;
        ResultSet rs;
        try (Connection conn = this.pool.getConnection();
                SQLCloseable finish = conn::rollback;) {
            conn.setAutoCommit(false);
            pst = conn.prepareStatement("SELECT COUNT(IDPRODUCTO) AS COUNT FROM PRODUCTO WHERE NOMBRE = ? AND IDPRODUCTO != ?");
            pst.setString(1, obj.getNombre());
            pst.setInt(2, obj.getIdproducto());
            rs = pst.executeQuery();
            while (rs.next()) {
                if (rs.getInt("COUNT") == 0) {
                    pst = conn.prepareStatement("UPDATE PRODUCTO SET NOMBRE = ?, PRECIO = ?, STOCK = ?, STOCK_MIN = ?, "
                            + "STOCK_MAX = ?, IDCATEGORIA = ? WHERE IDPRODUCTO = ?");
                    pst.setString(1, obj.getNombre());
                    pst.setDouble(2, obj.getPrecio());
                    pst.setInt(3, obj.getStock());
                    pst.setInt(4, obj.getStock_min());
                    pst.setInt(5, obj.getStock_max());
                    pst.setInt(6, obj.getCategoria().getIdcategoria());
                    pst.setInt(7, obj.getIdproducto());
                    LOG.info(pst.toString());
                    pst.executeUpdate();
                    conn.commit();
                    beancrud.setMENSSAGE_SERVER("ok");
                    beancrud.setBEAN_PAGINATION(getPagination(parameters, conn));
                } else {
                    beancrud.setMENSSAGE_SERVER("No se modificó, ya existe un Producto con el nombre ingresado");
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
            try (PreparedStatement pst = conn.prepareStatement("delete from producto where idproducto = ? ")) {
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
