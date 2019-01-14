/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.alopezc.myapp.demo.api;

import com.alopezc.myapp.demo.dao.MatriculaDao;
import com.alopezc.myapp.demo.impl.MatriculaDaoImpl;
import com.alopezc.myapp.demo.model.Alumno;
import com.alopezc.myapp.demo.model.Matricula;
import com.alopezc.myapp.demo.utilies.BEAN_CRUD;
import com.google.gson.Gson;
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
@WebServlet(name = "MatriculaAPI", urlPatterns = {"/matricula"})
public class MatriculaAPI extends HttpServlet {

    private static final Logger LOG = Logger.getLogger(MatriculaAPI.class.getName());

         @Resource(name = "jdbc/dbmyapp")
    private DataSource pool;
    private Gson jsonParse;
    private HashMap<String, Object> parameters;
    private String json_respose;
    private String accion;
    private MatriculaDao matriculaDao;

    @Override
    public void init() throws ServletException {
         this.jsonParse = new Gson();
        this.parameters = new HashMap<>();
        this.matriculaDao = new MatriculaDaoImpl(pool); 
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
            switch (this.accion) {
                case "paginarMatricula":
                    procesarMatricula(new BEAN_CRUD(this.matriculaDao.getPagination(getParameters(request))), response);
                    break;
                case "addMatricula":
                    procesarMatricula(this.matriculaDao.add(getMatricula(request), getParameters(request)), response);
                    break;
                case "updateMatricula":
                    procesarMatricula(this.matriculaDao.update(getMatricula(request), getParameters(request)), response);
                    break;
                case "deleteMatricula":
                    procesarMatricula(this.matriculaDao.delete(Integer.parseInt(request.getParameter("txtIdMatriculaER")), getParameters(request)), response);
                    break;
                default:
                    request.getRequestDispatcher("/jsp_app/mantenimiento/matricula.jsp").forward(request, response);
                    break;
            }
        } catch (SQLException ex) {
            Logger.getLogger(MatriculaAPI.class.getName()).log(Level.SEVERE, null, ex);
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
   private void procesarMatricula(BEAN_CRUD beancrud, HttpServletResponse response) {
        try {
            this.json_respose = this.jsonParse.toJson(beancrud);
            LOG.info(this.json_respose);
            response.setContentType("application/json");
            response.getWriter().write(this.json_respose);
        } catch (IOException ex) {
            Logger.getLogger(MatriculaAPI.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private HashMap<String, Object> getParameters(HttpServletRequest request) {
        this.parameters.clear();
        this.parameters.put("FILTER", request.getParameter("txtCodigoMatricula"));
        this.parameters.put("SQL_ORDER_BY", "NOMBRE ASC");
        if (request.getParameter("sizePageMatricula").equals("ALL")) {
            this.parameters.put("SQL_LIMIT", "");
        } else {
            this.parameters.put("SQL_LIMIT", " LIMIT " + request.getParameter("sizePageMatricula") + " offset "
                    + (Integer.parseInt(request.getParameter("numberPageMatricula")) - 1) * Integer.parseInt(request.getParameter("sizePageMatricula")));
        }

        return this.parameters;

    }

    private Matricula getMatricula(HttpServletRequest request) {
        Matricula matricula = new Matricula();
        if (request.getParameter("accion").equals("updateMatricula")) {
            matricula.setIdmatricula(Integer.parseInt(request.getParameter("txtIdMatriculaER")));
        }
        matricula.setCodigo(request.getParameter("txtCodigoMatriculaER"));
        matricula.setCiclo(request.getParameter("txtCicloMatriculaER"));
        matricula.setAlumno( new Alumno(Integer.parseInt(request.getParameter("cboAlumnoMatriculaER"))));
        return matricula;
    }
}
