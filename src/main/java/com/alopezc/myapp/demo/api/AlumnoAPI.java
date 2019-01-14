/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.alopezc.myapp.demo.api;

import com.alopezc.myapp.demo.dao.AlumnoDao;
import com.alopezc.myapp.demo.impl.AlumnoDaoImpl;
import com.alopezc.myapp.demo.model.Alumno;
import com.alopezc.myapp.demo.utilies.BEAN_CRUD;
import com.alopezc.myapp.demo.utilies.BEAN_PAGINATION;
import com.alopezc.myapp.demo.utilies.ParceDate;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

/**
 *
 * @author AlopezCarrillo2500
 */
@WebServlet(name = "AlumnoAPI", urlPatterns = {"/alumno"})
public class AlumnoAPI extends HttpServlet {

    private static final Logger LOG = Logger.getLogger(AlumnoAPI.class.getName());

    
    
    @Resource(name = "jdbc/dbmyapp")
    private DataSource pool;
    private Gson jsonParse;
    private HashMap<String, Object> parameters;
    private String json_respose;
    private String accion;
    private AlumnoDao alumnodao;

    @Override
    public void init() throws ServletException {
        this.jsonParse = new GsonBuilder().setDateFormat("dd/MM/yyyy").create();
        this.parameters = new HashMap<>();
        this.alumnodao = new AlumnoDaoImpl(pool); 
    }
    
    
    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            this.accion = request.getParameter("accion") == null ? "" : request.getParameter("accion");
            LOG.info(accion);
            switch (this.accion) {
                case "paginarAlumno":
                    BEAN_PAGINATION beanpagination = this.alumnodao.getPagination(getParameters(request));
                    BEAN_CRUD beancrud = new BEAN_CRUD(beanpagination);
                    procesarAlumno(beancrud, response);
                    break;
                case "addAlumno":
                    procesarAlumno(this.alumnodao.add(getAlumno(request), getParameters(request)), response);
                    break;
                case "updateAlumno":
                    procesarAlumno(this.alumnodao.update(getAlumno(request), getParameters(request)), response);
                    break;
                case "deleteAlumno":
                    procesarAlumno(this.alumnodao.delete(Integer.parseInt(request.getParameter("txtIdAlumnoER")), getParameters(request)), response);
                    break;
                default:
                    request.getRequestDispatcher("/jsp_app/mantenimiento/alumno.jsp").forward(request, response);
                    break;

            }
        } catch (SQLException ex) {
            Logger.getLogger(CategoriasAPI.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>
    
    private void procesarAlumno(BEAN_CRUD beancrud, HttpServletResponse response) {
        try {
            this.json_respose = this.jsonParse.toJson(beancrud);
            LOG.info(this.json_respose);
            response.setContentType("application/json");
            response.getWriter().write(this.json_respose);
        } catch (IOException ex) {
            Logger.getLogger(AlumnoAPI.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private HashMap<String, Object> getParameters(HttpServletRequest request) {
        this.parameters.clear();
        this.parameters.put("FILTER", request.getParameter("txtNombreAlumno"));
        this.parameters.put("SQL_ORDER_BY", "NOMBRE ASC");
        if (request.getParameter("sizePageAlumno").equals("ALL")) {
            this.parameters.put("SQL_LIMIT", "");
        } else {
            this.parameters.put("SQL_LIMIT", " LIMIT " + request.getParameter("sizePageAlumno") + " offset "
                    + (Integer.parseInt(request.getParameter("numberPageAlumno")) - 1) * Integer.parseInt(request.getParameter("sizePageAlumno")));
        }

        return this.parameters;

    }

    private Alumno getAlumno(HttpServletRequest request) {
           Alumno alumno = new Alumno();
        try {
        
            if (request.getParameter("accion").equals("updateAlumno")) {
                alumno.setIdalumno(Integer.parseInt(request.getParameter("txtIdAlumnoER")));
            }
            alumno.setNombre(request.getParameter("txtNombreAlumnoER"));
            alumno.setNombre2(request.getParameter("txtNombre2AlumnoER"));
            alumno.setFecha_nacimiento(ParceDate.getDate(request.getParameter("txtFecha_nacimientoAlumnoER"), "dd/MM/yyyy"));
            alumno.setDireccion(request.getParameter("txtDireccionAlumnoER"));
            alumno.setTelefono(request.getParameter("txtTelefonoAlumnoER"));
            
            
        } catch (Exception ex) {
            Logger.getLogger(AlumnoAPI.class.getName()).log(Level.SEVERE, null, ex);
        }
        return alumno;
    }
}
