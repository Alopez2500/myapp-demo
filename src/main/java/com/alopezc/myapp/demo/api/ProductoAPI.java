/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.alopezc.myapp.demo.api;

import com.alopezc.myapp.demo.dao.ProductoDao;
import com.alopezc.myapp.demo.impl.ProductoDaoImpl;
import com.alopezc.myapp.demo.model.Categoria;
import com.alopezc.myapp.demo.model.Producto;
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
@WebServlet(name = "ProductoAPI", urlPatterns = {"/producto"})
public class ProductoAPI extends HttpServlet {

    private static final Logger LOG = Logger.getLogger(ProductoAPI.class.getName());
    @Resource(name = "jdbc/dbmyapp")
    private DataSource pool;
    private Gson jsonParse;
    private HashMap<String, Object> parameters;
    private String json_respose;
    private String accion;
    private ProductoDao productoDao;

    @Override
    public void init() throws ServletException {
        this.jsonParse = new Gson();
        this.parameters = new HashMap<>();
        this.productoDao = new ProductoDaoImpl(pool);
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
                case "paginarProducto":
                    procesarProducto(new BEAN_CRUD(this.productoDao.getPagination(getParameters(request))), response);
                    break;
                case "addProducto":
                    procesarProducto(this.productoDao.add(getProducto(request), getParameters(request)), response);
                    break;
                case "updateProducto":
                    procesarProducto(this.productoDao.update(getProducto(request), getParameters(request)), response);
                    break;
                case "deleteProducto":
                    procesarProducto(this.productoDao.delete(Integer.parseInt(request.getParameter("txtIdProductoER")), getParameters(request)), response);
                    break;
                default:
                    request.getRequestDispatcher("/jsp_app/mantenimiento/producto.jsp").forward(request, response);
                    break;
            }
        } catch (SQLException ex) {
            Logger.getLogger(ProductoAPI.class.getName()).log(Level.SEVERE, null, ex);
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

    private Producto getProducto(HttpServletRequest request) {
        Producto producto = new Producto();
        if (request.getParameter("accion").equals("updateProducto")) {
            producto.setIdproducto(Integer.parseInt(request.getParameter("txtIdProductoER")));
        }
        producto.setNombre(request.getParameter("txtNombreProductoER"));
        producto.setPrecio(Double.parseDouble(request.getParameter("txtPrecioProductoER")));
        producto.setStock(Integer.parseInt(request.getParameter("txtStockProductoER")));
        producto.setStock_min(Integer.parseInt(request.getParameter("txtStock_minProductoER")));
        producto.setStock_max(Integer.parseInt(request.getParameter("txtStock_maxProductoER")));
        producto.setCategoria(new Categoria(Integer.parseInt(request.getParameter("cboCategoriaProductoER"))));
        return producto;
    }

    private void procesarProducto(BEAN_CRUD beancrud, HttpServletResponse response) {
        try {
            this.json_respose = this.jsonParse.toJson(beancrud);
            LOG.info(this.json_respose);
            response.setContentType("application/json");
            response.getWriter().write(this.json_respose);
        } catch (IOException ex) {
            Logger.getLogger(ProductoAPI.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private HashMap<String, Object> getParameters(HttpServletRequest request) {
        this.parameters.clear();
        this.parameters.put("FILTER", request.getParameter("txtNombreProducto"));
        this.parameters.put("SQL_ORDER_BY", " NOMBRE ASC ");
        this.parameters.put("SQL_LIMIT", " LIMIT " + request.getParameter("sizePageProducto") + " offset "
                + (Integer.parseInt(request.getParameter("numberPageProducto")) - 1) * Integer.parseInt(request.getParameter("sizePageProducto")));
        return this.parameters;

    }
}
