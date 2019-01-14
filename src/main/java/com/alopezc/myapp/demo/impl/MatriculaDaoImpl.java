/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.alopezc.myapp.demo.impl;

import com.alopezc.myapp.demo.dao.MatriculaDao;
import com.alopezc.myapp.demo.dao.SQLCloseable;
import com.alopezc.myapp.demo.model.Alumno;
import com.alopezc.myapp.demo.model.Matricula;
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
public class MatriculaDaoImpl implements MatriculaDao {

    private static final Logger LOG = Logger.getLogger(MatriculaDaoImpl.class.getName());

    private final DataSource pool;

    public MatriculaDaoImpl(DataSource pool) {
        this.pool = pool;
    }

    @Override
    public BEAN_PAGINATION getPagination(HashMap<String, Object> parameters, Connection conn) throws SQLException {
        BEAN_PAGINATION beanpagination = new BEAN_PAGINATION();
            List<Matricula> list = new ArrayList<>();
        PreparedStatement pst;
        ResultSet rs;
        try {
            pst = conn.prepareStatement("SELECT COUNT(IDMATRICULA) AS COUNT FROM MATRICULA WHERE "
                    + "LOWER(CODIGO) LIKE CONCAT('%',?,'%')");
            pst.setString(1, String.valueOf(parameters.get("FILTER")));
            LOG.info(pst.toString());
            rs = pst.executeQuery();
            while (rs.next()) {
                beanpagination.setCOUNT_FILTER(rs.getInt("COUNT"));
                if (rs.getInt("COUNT") > 0) {
                    pst = conn.prepareStatement("select  ma.idmatricula, ma.codigo,ma.ciclo,al.idalumno,al.nombre,al.nombre2,al.telefono,al.direccion from matricula ma inner join alumno al on ma.idalumno = al.idalumno WHERE "
                            + "LOWER(CODIGO) LIKE CONCAT('%',?,'%') "
                            + "ORDER BY " + String.valueOf(parameters.get("SQL_ORDER_BY")) + " " + parameters.get("SQL_LIMIT"));
                    pst.setString(1, String.valueOf(parameters.get("FILTER")));
                    LOG.info(pst.toString());
                    rs = pst.executeQuery();
                    while (rs.next()) {
                        Matricula matricula = new Matricula();
                        matricula.setIdmatricula(rs.getInt("IDMATRICULA"));
                        matricula.setCodigo(rs.getString("CODIGO"));
                        matricula.setCiclo(rs.getString("CICLO"));
                        Alumno alumno = new Alumno();
                        alumno.setIdalumno(rs.getInt("IDALUMNO"));
                        alumno.setNombre(rs.getString("NOMBRE"));
                        alumno.setNombre2(rs.getString("NOMBRE2"));
                        alumno.setTelefono(rs.getString("TELEFONO"));
                        alumno.setDireccion(rs.getString("DIRECCION"));
                        matricula.setAlumno(alumno);
                        list.add(matricula);
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
    public BEAN_CRUD add(Matricula obj, HashMap<String, Object> parameters) throws SQLException {
       
 BEAN_CRUD beancrud = new BEAN_CRUD();
        PreparedStatement pst;
        ResultSet rs;
        try (Connection conn = this.pool.getConnection();
                SQLCloseable finish = conn::rollback;) {
            conn.setAutoCommit(false);
            pst = conn.prepareStatement("SELECT COUNT (IDMATRICULA) AS COUNT FROM MATRICULA WHERE CODIGO= ? ");
            pst.setString(1, obj.getCodigo());
            rs = pst.executeQuery();
            while (rs.next()) {
                if (rs.getInt("COUNT") == 0) {
                    pst = conn.prepareStatement("insert into MATRICULA(codigo,ciclo,idalumno) values (?,?,?)");
                    pst.setString(1, obj.getCodigo());
                    pst.setString(2, obj.getCiclo());
                    pst.setInt(3, obj.getAlumno().getIdalumno());
                    pst.executeUpdate();
                    conn.commit();
                    beancrud.setMENSSAGE_SERVER("ok");
                    beancrud.setBEAN_PAGINATION(getPagination(parameters, conn));
                } else {
                    beancrud.setMENSSAGE_SERVER("No se registró, ya existe una Matricula con el codigo ingresado");
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
    public BEAN_CRUD update(Matricula obj, HashMap<String, Object> parameters) throws SQLException {
        BEAN_CRUD beancrud = new BEAN_CRUD();
        PreparedStatement pst;
        ResultSet rs;
        try (Connection conn = this.pool.getConnection();
                SQLCloseable finish = conn::rollback;) {
            conn.setAutoCommit(false);
            pst = conn.prepareStatement("SELECT COUNT(IDMATRICULA) AS COUNT FROM MATRICULA WHERE CODIGO = ? AND IDMATRICULA != ?");
            pst.setString(1, obj.getCodigo());
            pst.setInt(2, obj.getIdmatricula());
            rs = pst.executeQuery();
            while (rs.next()) {
                if (rs.getInt("COUNT") == 0) {
                    pst = conn.prepareStatement("UPDATE MATRICULA SET CODIGO = ?, CICLO = ?,IDALUMNO= ? WHERE IDMATRICULA= ?");
                    pst.setString(1, obj.getCodigo());
                    pst.setString(2, obj.getCiclo());
                    pst.setInt(3, obj.getAlumno().getIdalumno());
                    pst.setInt(4, obj.getIdmatricula());
                    LOG.info(pst.toString());
                    pst.executeUpdate();
                    conn.commit();
                    beancrud.setMENSSAGE_SERVER("ok");
                    beancrud.setBEAN_PAGINATION(getPagination(parameters, conn));
                } else {
                    beancrud.setMENSSAGE_SERVER("No se modificó, ya existe una Matricula con el codigo ingresado");
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
            try (PreparedStatement pst = conn.prepareStatement("delete from Matricula where idmatricula= ? ")) {
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
