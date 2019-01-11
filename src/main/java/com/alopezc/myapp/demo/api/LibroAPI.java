/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.alopezc.myapp.demo.api;

import com.alopezc.myapp.demo.dao.LibroDao;
import com.alopezc.myapp.demo.impl.LibroDaoImpl;
import com.alopezc.myapp.demo.model.Autor;
import com.alopezc.myapp.demo.model.Libro;
import com.alopezc.myapp.demo.utilies.BEAN_CRUD;
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
@WebServlet(name = "LibroAPI", urlPatterns = {"/libro"})
public class LibroAPI extends HttpServlet {

    private static final Logger LOG = Logger.getLogger(LibroAPI.class.getName());

    @Resource(name = "jdbc/dbmyapp")
    private DataSource pool;
    private Gson jsonParse;
    private HashMap<String, Object> parameters;
    private String json_respose;
    private String accion;
    private LibroDao libroDao;

    @Override
    public void init() throws ServletException {
        this.jsonParse = new GsonBuilder().setDateFormat("dd/MM/yyyy").create();
        this.parameters = new HashMap<>();
        this.libroDao = new LibroDaoImpl(pool);
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
                case "paginarLibro":
                    procesarLibro(new BEAN_CRUD(this.libroDao.getPagination(getParameters(request))), response);
                    break;
                case "addLibro":
                    procesarLibro(this.libroDao.add(getLibro(request), getParameters(request)), response);
                    break;
                case "updateLibro":
                    procesarLibro(this.libroDao.update(getLibro(request), getParameters(request)), response);
                    break;
                case "deleteLibro":
                    procesarLibro(this.libroDao.delete(Integer.parseInt(request.getParameter("txtIdLibroER")), getParameters(request)), response);
                    break;
                default:
                    request.getRequestDispatcher("/jsp_app/mantenimiento/libro.jsp").forward(request, response);
                    break;
            }
        } catch (SQLException ex) {
            Logger.getLogger(LibroAPI.class.getName()).log(Level.SEVERE, null, ex);
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

    private Libro getLibro(HttpServletRequest request) {
       Libro libro = new Libro();
        try {
            if (request.getParameter("accion").equals("updateLibro")) {
                libro.setIdlibro(Integer.parseInt(request.getParameter("txtIdLibroER")));
            }
            libro.setNombre(request.getParameter("txtNombreLibroER"));
            libro.setFechaPublicacion(ParceDate.getDate(request.getParameter("txtFecha-PublicacionLibroER"), "dd/MM/yyyy"));
            libro.setGenero(request.getParameter("txtGeneroLibroER"));
            libro.setEdicion(request.getParameter("txtEdicionLibroER"));
            libro.setAutor(new Autor(Integer.parseInt(request.getParameter("cboAutorLibroER"))));
        } catch (Exception ex) {
            Logger.getLogger(LibroAPI.class.getName()).log(Level.SEVERE, null, ex);
        }
        return libro;
    }

    private void procesarLibro(BEAN_CRUD beancrud, HttpServletResponse response) {
        try {
            this.json_respose = this.jsonParse.toJson(beancrud);
            LOG.info(this.json_respose);
            response.setContentType("application/json");
            response.getWriter().write(this.json_respose);
        } catch (IOException ex) {
            Logger.getLogger(LibroAPI.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private HashMap<String, Object> getParameters(HttpServletRequest request) {
        this.parameters.clear();
        this.parameters.put("FILTER", request.getParameter("txtNombreLibro"));
        this.parameters.put("SQL_ORDER_BY", " NOMBRE ASC ");
        this.parameters.put("SQL_LIMIT", " LIMIT " + request.getParameter("sizePageLibro") + " offset "
                + (Integer.parseInt(request.getParameter("numberPageLibro")) - 1) * Integer.parseInt(request.getParameter("sizePageLibro")));
        return this.parameters;

    }
}
