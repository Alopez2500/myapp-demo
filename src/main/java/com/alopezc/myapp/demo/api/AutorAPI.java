/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.alopezc.myapp.demo.api;

import com.alopezc.myapp.demo.dao.AutorDao;
import com.alopezc.myapp.demo.impl.AutorDaoImpl;
import com.alopezc.myapp.demo.model.Autor;
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
@WebServlet(name = "AutorAPI", urlPatterns = {"/autor"})
public class AutorAPI extends HttpServlet {

    private static final Logger LOG = Logger.getLogger(AutorAPI.class.getName());
    @Resource(name = "jdbc/dbmyapp")
    private DataSource pool;
    private Gson jsonParse;
    private HashMap<String, Object> parameters;
    private String json_respose;
    private String accion;
    private AutorDao autorDao;

    @Override
    public void init() throws ServletException {
        this.jsonParse = new Gson();
        this.parameters = new HashMap<>();
        this.autorDao = new AutorDaoImpl(pool);
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
                case "paginarAutor":
                    BEAN_PAGINATION beanpagination = this.autorDao.getPagination(getParameters(request));
                    BEAN_CRUD beancrud = new BEAN_CRUD(beanpagination);
                    procesarAutor(beancrud, response);
                    break;
                case "addAutor":
                    procesarAutor(this.autorDao.add(getAutor(request), getParameters(request)), response);
                    break;
                case "updateAutor":
                    procesarAutor(this.autorDao.update(getAutor(request), getParameters(request)), response);
                    break;
                case "deleteAutor":
                    procesarAutor(this.autorDao.delete(Integer.parseInt(request.getParameter("txtIdAutorER")), getParameters(request)), response);
                    break;
                default:
                    request.getRequestDispatcher("/jsp_app/mantenimiento/autor.jsp").forward(request, response);
                    break;
                    
            }
        } catch (SQLException ex) {
            
            Logger.getLogger(AutorAPI.class.getName()).log(Level.SEVERE, null, ex);
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

    private Autor getAutor(HttpServletRequest request) {
        Autor autor = new Autor();
        if (request.getParameter("accion").equals("updateAutor")) {
            autor.setIdautor(Integer.parseInt(request.getParameter("txtIdAutorER")));
        }
        autor.setNombre(request.getParameter("txtNombreAutorER"));
        autor.setNombre2(request.getParameter("txtNombre2AutorER"));
        autor.setDocumento(request.getParameter("txtDocumentoAutorER"));
        autor.setTelefono(request.getParameter("txtTelefonoAutorER"));
        autor.setDireccion(request.getParameter("txtDireccionAutorER"));
        return autor;
    }

    private void procesarAutor(BEAN_CRUD beancrud, HttpServletResponse response) {
        try {
            this.json_respose = this.jsonParse.toJson(beancrud);
            LOG.info(this.json_respose);
            response.setContentType("application/json");
            response.getWriter().write(this.json_respose);
        } catch (IOException ex) {
            Logger.getLogger(AutorAPI.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private HashMap<String, Object> getParameters(HttpServletRequest request) {
        this.parameters.clear();
        this.parameters.put("FILTER", request.getParameter("txtNombreAutor"));
        this.parameters.put("SQL_ORDER_BY", " NOMBRE ASC ");
        this.parameters.put("SQL_LIMIT", " LIMIT " + request.getParameter("sizePageAutor") + " offset "
                + (Integer.parseInt(request.getParameter("numberPageAutor")) - 1) * Integer.parseInt(request.getParameter("sizePageAutor")));
        return this.parameters;

    }

}
