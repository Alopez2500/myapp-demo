/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.alopezc.myapp.demo.api;

import com.alopezc.myapp.demo.dao.CursoDao;
import com.alopezc.myapp.demo.impl.CursoDaoImpl;
import com.alopezc.myapp.demo.model.Curso;
import com.alopezc.myapp.demo.utilies.BEAN_CRUD;
import com.alopezc.myapp.demo.utilies.BEAN_PAGINATION;
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
@WebServlet(name = "Curso", urlPatterns = {"/curso"})
public class CursoAPI extends HttpServlet {

    private static final Logger LOG = Logger.getLogger(CursoAPI.class.getName());
    
    @Resource(name = "jdbc/dbmyapp")
    private DataSource pool;
    private Gson jsonParse;
    private HashMap<String, Object> parameters;
    private String json_respose;
    private String accion;
    private CursoDao cursoDao;
    
    @Override
    public void init() throws ServletException {
        this.jsonParse = new Gson();
        this.parameters = new HashMap<>();
        this.cursoDao = new CursoDaoImpl(pool);
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
        response.setContentType("text/html;charset=UTF-8");
            try {
            this.accion = request.getParameter("accion") == null ? "" : request.getParameter("accion");
            LOG.info(accion);
            switch (this.accion) {
                case "paginarCurso":
                    BEAN_PAGINATION beanpagination = this.cursoDao.getPagination(getParameters(request));
                    BEAN_CRUD beancrud = new BEAN_CRUD(beanpagination);
                    procesarCurso(beancrud, response);
                    break;
                case "addCurso":
                    procesarCurso(this.cursoDao.add(getCurso(request), getParameters(request)), response);
                    break;
                case "updateCurso":
                    procesarCurso(this.cursoDao.update(getCurso(request), getParameters(request)), response);
                    break;
                case "deleteCurso":
                    procesarCurso(this.cursoDao.delete(Integer.parseInt(request.getParameter("txtIdCursoER")), getParameters(request)), response);
                    break;
                default:
                    request.getRequestDispatcher("/jsp_app/mantenimiento/curso.jsp").forward(request, response);
                    break;

            }
        } catch (SQLException ex) {
            Logger.getLogger(CursoAPI.class.getName()).log(Level.SEVERE, null, ex);
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
    
     private void procesarCurso(BEAN_CRUD beancrud, HttpServletResponse response) {
        try {
            this.json_respose = this.jsonParse.toJson(beancrud);
            LOG.info(this.json_respose);
            response.setContentType("application/json");
            response.getWriter().write(this.json_respose);
        } catch (IOException ex) {
            Logger.getLogger(CursoAPI.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private HashMap<String, Object> getParameters(HttpServletRequest request) {
        this.parameters.clear();
        this.parameters.put("FILTER", request.getParameter("txtNombreCurso"));
        this.parameters.put("SQL_ORDER_BY", "NOMBRE ASC");
        if (request.getParameter("sizePageCurso").equals("ALL")) {
            this.parameters.put("SQL_LIMIT", "");
        } else {
            this.parameters.put("SQL_LIMIT", " LIMIT " + request.getParameter("sizePageCurso") + " offset "
                    + (Integer.parseInt(request.getParameter("numberPageCurso")) - 1) * Integer.parseInt(request.getParameter("sizePageCurso")));
        }
        if(request.getParameter("cboEstadoCurso").equals("-1")){
            this.parameters.put("SQL_ESTADO", "");
        }else{
            this.parameters.put("SQL_ESTADO"," AND ESTADO = '"+ request.getParameter("cboEstadoCurso") + "' ");
        }
        return this.parameters;
    }

    private Curso getCurso(HttpServletRequest request) {
        Curso curso = new Curso();
        if (request.getParameter("accion").equals("updateCurso")) {
            curso.setIdcurso(Integer.parseInt(request.getParameter("txtIdCursoER")));
        }
        curso.setNombre(request.getParameter("txtNombreCursoER"));
        curso.setEstado(request.getParameter("cboEstadoCursoER"));
        LOG.info(request.getParameter("cboEstadoCursoER"));
        return curso;
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

}
