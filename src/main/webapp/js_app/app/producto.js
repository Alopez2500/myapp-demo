$(document).ready(function () {

    $('#btnAbrirNProducto').click(function () {
        $('#txtIdProductoER').val("");
        $('#txtNombreProductoER').val("");
        $('#txtPrecioProductoER').val("");
        $('#txtStockProductoER').val("");
        $('#txtStock_minProductoER').val("");
        $('#txtStock_maxProductoER').val("");
        $('#cboCategoriaProductoER').val("-1");
        $('.error-validation').fadeOut();
        $('#accionProducto').val("addProducto");
        $('#tituloModalManProducto').html("REGISTRAR PRODUCTO");
        $('#ventanaModalManProducto').modal('show');
    });

    $('#FrmProducto').submit(function () {
        $('#accionProducto').val("paginarProducto");
        $('#nameFormProducto').val("FrmProducto");
        $('#numberPageProducto').val("1");
        $('#modalCargandoProducto').modal('show');
        return false;
    });

    $('#FrmProductoModal').submit(function () {
        if (validarFormularioProducto()) {
            $('#nameFormProducto').val("FrmProductoModal");
            $('#modalCargandoProducto').modal('show');
        }
        return false;
    });

    $("#modalCargandoProducto").on('shown.bs.modal', function () {
        processAjaxProducto();
    });

    $("#ventanaModalManProducto").on('hidden.bs.modal', function () {
        $("#accionProducto").val("paginarProducto");
    });

    addValicacionesCamposProducto();
    addComboCategoria();
    validarFormularioProducto();
    $('#modalCargandoProducto').modal('show');

});
function processAjaxProducto() {
    var datosSerializadosCompletos = $('#' + $('#nameFormProducto').val()).serialize();
    if ($('#nameFormProducto').val().toLowerCase() !== "frmproducto") {
        datosSerializadosCompletos += "&txtNombreProducto=" + $('#txtNombreProducto').val();
    }
    datosSerializadosCompletos += "&numberPageProducto=" + $('#numberPageProducto').val();
    datosSerializadosCompletos += "&sizePageProducto=" + $('#sizePageProducto').val();
    datosSerializadosCompletos += "&accion=" + $('#accionProducto').val();
    $.ajax({
        url: 'producto',
        type: 'POST',
        data: datosSerializadosCompletos,
        dataType: 'json',
        success: function (jsonResponse) {
            console.log(jsonResponse);
            $('#modalCargandoProducto').modal("hide");
            if ($('#accionProducto').val().toLowerCase() === "paginarproducto") {
                listarProducto(jsonResponse.BEAN_PAGINATION);
            } else {
                if (jsonResponse.MENSSAGE_SERVER.toLowerCase() === "ok") {
                    viewAlert(getMessageServerTransaction($('#accionProducto').val(), 'Producto', 'o'), 'success');
                    listarProducto(jsonResponse.BEAN_PAGINATION);
                } else {
                    viewAlert(jsonResponse.MENSSAGE_SERVER, 'warning');
                }
            }
            $("#ventanaModalManProducto").modal("hide");
        },
        error: function () {
            $('#modalCargandoProducto').modal("hide");
            $("#ventanaModalManProducto").modal("hide");
            viewAlert('Error interno en el Servidor', 'error');
        }
    });
    return false;
}

function listarProducto(BEAN_PAGINATION) {
    /*PAGINATION*/
    var $pagination = $('#paginationProducto');
    $('#tbodyProducto').empty();
    $pagination.twbsPagination('destroy');
    $('#nameCrudProducto').html("[ " + BEAN_PAGINATION.COUNT_FILTER + " ] PRODUCTOS");
    if (BEAN_PAGINATION.COUNT_FILTER > 0) {
        var fila;
        var atributos;
        $.each(BEAN_PAGINATION.List, function (index, value) {
            fila = "<tr ";
            atributos = "";
            atributos += "idproducto='" + value.idproducto + "' ";
            atributos += "nombre='" + value.nombre + "' ";
            atributos += "precio='" + value.precio + "' ";
            atributos += "stock='" + value.stock + "' ";
            atributos += "stock_min='" + value.stock_min + "' ";
            atributos += "stock_max='" + value.stock_max + "' ";
            atributos += "idcategoria='" + value.categoria.idcategoria + "' ";
            atributos += "nombrecategoria='" + value.categoria.nombre + "' ";
            fila += atributos;
            fila += ">";
            fila += "<td class='align-middle'>" + value.nombre + "</td>";
            fila += "<td class='align-middle'>" + value.precio + "</td>";
            fila += "<td class='align-middle'>" + value.stock + "</td>";
            fila += "<td class='align-middle'>" + value.stock_min + "</td>";
            fila += "<td class='align-middle'>" + value.stock_max + "</td>";
            fila += "<td class='align-middle'>" + value.categoria.nombre + "</td>";
            fila += "<td class='text-center align-middle'><button class='btn btn-secondary btn-xs editar-Producto'><i class='fa fa-edit'></i></button></td>";
            fila += "<td class='text-center align-middle'><button class='btn btn-secondary btn-xs eliminar-Producto'><i class='fa fa-trash'></i></button></td>";
            fila += "</tr>";
            console.log(fila);
            $('#tbodyProducto').append(fila);
        });
        var defaultOptions = getDefaultOptionsPagination();
        var options = getOptionsPagination(BEAN_PAGINATION.COUNT_FILTER, $('#sizePageProducto'),
                $('#numberPageProducto'), $('#accionProducto'), 'paginarProducto',
                $('#nameForm'), 'FrmProducto', $('#modalCargandoProducto'));
        $pagination.twbsPagination('destroy');
        $pagination.twbsPagination($.extend({}, defaultOptions, options));
        addEventosProducto();
        $('#txtNombreProducto').focus();
    } else {
        $pagination.twbsPagination('destroy');
        viewAlert('No se enconntraron resultados', 'warning');
    }
}

function addEventosProducto() {
    $('.editar-Producto').each(function () {
        $(this).click(function () {
            $('#txtIdProductoER').val($(this.parentElement.parentElement).attr('idproducto'));
            $('#txtNombreProductoER').val($(this.parentElement.parentElement).attr('nombre'));
            $('#txtPrecioProductoER').val($(this.parentElement.parentElement).attr('precio'));
            $('#txtStockProductoER').val($(this.parentElement.parentElement).attr('stock'));
            $('#txtStock_minProductoER').val($(this.parentElement.parentElement).attr('stock_min'));
            $('#txtStock_maxProductoER').val($(this.parentElement.parentElement).attr('stock_max'));
            $('#cboCategoriaProductoER').val($(this.parentElement.parentElement).attr('idcategoria'));
            $('#accionProducto').val("updateProducto");
            $('#tituloModalManProducto').html("EDITAR PRODUCTO");
            $('#ventanaModalManProducto').modal("show");
            document.getElementsByTagName("body")[0].style.paddingRight = "0";
        });
    });
    $('.eliminar-Producto').each(function () {
        $(this).click(function () {
            $('#txtIdProductoER').val($(this.parentElement.parentElement).attr('idproducto'));
            viewAlertDelete("Producto");
            document.getElementsByTagName("body")[0].style.paddingRight = "0";
        });
    });
}

function addValicacionesCamposProducto() {
    $('#txtNombreProductoER').on('change', function () {
        $(this).val() === "" ? $("#validarNombreProductoER").fadeIn("slow") : $("#validarNombreProductoER").fadeOut();
    });
    $('#txtPrecioProductoER').on('change', function () {
        $(this).val() === "" ? $("#validarPrecioProductoER").fadeIn("slow") : $("#validarPrecioProductoER").fadeOut();
    });
    $('#txtStockProductoER').on('change', function () {
        $(this).val() === "" ? $("#validarStockProductoER").fadeIn("slow") : $("#validarStockProductoER").fadeOut();
    });
    $('#txtStock_minProductoER').on('change', function () {
        $(this).val() === "" ? $("#validarStock_minProductoER").fadeIn("slow") : $("#validarStock_minProductoER").fadeOut();
    });
    $('#txtStock_maxProductoER').on('change', function () {
        $(this).val() === "" ? $("#validarStock_maxProductoER").fadeIn("slow") : $("#validarStock_maxProductoER").fadeOut();
    });
    $('#cboCategoriaProductoER').on('change', function () {
        $(this).val() === "-1" ? $("#validarCategoriaProductoER").fadeIn("slow") : $("#validarCategoriaProductoER").fadeOut();
    });
}

function validarFormularioProducto() {
    if ($('#txtNombreProductoER').val() === "") {
        $("#validarNombreProductoER").fadeIn("slow");
        return false;
    } else {
        $("#validarNombreProductoER").fadeOut();
    }
    if ($('#txtPrecioProductoER').val() === "") {
        $("#validarPrecioProductoER").fadeIn("slow");
        return false;
    } else {
        $("#validarPrecioProductoER").fadeOut();
    }
    if ($('#txtStockProductoER').val() === "") {
        $("#validarStockProductoER").fadeIn("slow");
        return false;
    } else {
        $("#validarStockProductoER").fadeOut();
    }
    if ($('#txtStock_minProductoER').val() === "") {
        $("#validarStock_minProductoER").fadeIn("slow");
        return false;
    } else {
        $("#validarStock_minProductoER").fadeOut();
    }
    if ($('#txtStock_maxProductoER').val() === "") {
        $("#validarStock_maxProductoER").fadeIn("slow");
        return false;
    } else {
        $("#validarStock_maxProductoER").fadeOut();
    }
    if ($('#cboCategoriaProductoER').val() === "-1") {
        $("#validarCategoriaProductoER").fadeIn("slow");
        return false;
    } else {
        $("#validarCategoriaProductoER").fadeOut();
    }
    return true;
}

function addComboCategoria() {
    var datosSerializadosCompletos;
    datosSerializadosCompletos = "txtNombreCategoria=";
    datosSerializadosCompletos += "&numberPageCategoria=";
    datosSerializadosCompletos += "&sizePageCategoria=ALL";
    datosSerializadosCompletos += "&accion=paginarCategoria";
    $('#cboCategoriaProductoER').empty();
    $.ajax({
        url: 'categoria',
        type: 'POST',
        data: datosSerializadosCompletos,
        dataType: 'json',
        success: function (jsonResponse) {
            $('#cboCategoriaProductoER').append("<option value='-1'>Seleccione...</option>");
            $(jsonResponse.BEAN_PAGINATION.List).each(function (index, value) {
                $('#cboCategoriaProductoER').append("<option value='" + value.idcategoria + "'>" + value.nombre + "</option>");
            });
        },
        error: function () {
            console.log("error interno al cargar categorias");
        }
    });
    return false;
}