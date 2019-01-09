/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.alopezc.myapp.demo.api;

import com.alopezc.myapp.demo.impl.CategoriaDaoImpl;
import com.alopezc.myapp.demo.dao.CategoriaDao;
import com.alopezc.myapp.demo.model.Categoria;
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
import javax.ws.rs.core.Request;

/**
 *
 * @author AlopezCarrillo2500
 */
@WebServlet(name = "CategoriasAPI", urlPatterns = {"/categoria"})
public class CategoriasAPI extends HttpServlet {

    private static final Logger LOG = Logger.getLogger(CategoriasAPI.class.getName());

    //instanciando el pool para que no este basio
    @Resource(name = "jdbc/dbmyapp")
    private DataSource pool;
    private Gson jsonParse;
    private HashMap<String, Object> parameters;
    private String json_respose;
    private String accion;
    private CategoriaDao categoriaDAO;

    // inicializamos todo lo definido a nivel de clase
    @Override
    public void init() throws ServletException {
        this.jsonParse = new Gson();
        this.parameters = new HashMap<>();
        this.categoriaDAO = new CategoriaDaoImpl(pool);
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
                case "paginarCategoria":
                    BEAN_PAGINATION beanpagination = this.categoriaDAO.getPagination(getParameters(request));
                    BEAN_CRUD  beancrud=  new BEAN_CRUD(beanpagination);
                    procesarCategoria(beancrud, response);
                    break;
                case "addCategoria":
                    procesarCategoria(this.categoriaDAO.add(getCategoria(request), getParameters(request)), response);
                    break;
                case "updateCategoria":
                    procesarCategoria(this.categoriaDAO.update(getCategoria(request), getParameters(request)), response);
                    break;
                case "deleteCategoria":
                    procesarCategoria(this.categoriaDAO.delete(Integer.parseInt(request.getParameter("txtIdCategoriaER")), getParameters(request)), response);
                    break;
                default:
                    request.getRequestDispatcher("/jsp_app/mantenimiento/categoria.jsp").forward(request, response);
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

    private void procesarCategoria(BEAN_CRUD beancrud, HttpServletResponse response) {
        try {
            this.json_respose = this.jsonParse.toJson(beancrud);
            LOG.info(this.json_respose);
            response.setContentType("application/json");
            response.getWriter().write(this.json_respose);
        } catch (IOException ex) {
            Logger.getLogger(CategoriasAPI.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private HashMap<String, Object> getParameters(HttpServletRequest request) {
        this.parameters.clear();
        this.parameters.put("FILTER", request.getParameter("txtNombreCategoria"));
        this.parameters.put("SQL_ORDER_BY", "NOMBRE ASC");
        this.parameters.put("SQL_LIMIT", " LIMIT " + request.getParameter("sizePageCategoria") + " offset "
                + (Integer.parseInt(request.getParameter("numberPageCategoria")) - 1) * Integer.parseInt(request.getParameter("sizePageCategoria")));
        return this.parameters;

    }
    private Categoria getCategoria(HttpServletRequest request){
        Categoria categoria = new Categoria();
        if (request.getParameter("accion").equals("updateCategoria")) {
            categoria.setIdcategoria(Integer.parseInt(request.getParameter("txtIdCategoriaER")));
        }
        categoria.setNombre(request.getParameter("txtNombreCategoriaER"));
        return categoria;
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
