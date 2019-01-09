$(document).ready(function () {
    $('#btnAbrirNCategoria').click(function () {
        $('#txtNombreCategoriaER').val("");
        $('.error-validation').fadeOut();
        $('#accionCategoria').val("addCategoria");
        $('#tituloModalManCategoria').html("Registrar Categoria");
        $('#ventanaModalManCategoria').modal("show");
    });

    $('#FrmCategoria').submit(function () {
        $("#accionCategoria").val("paginarCategoria");
        $("#nameFormCategoria").val("FrmCategoria");
        $("#numberPageCategoria").val("1");
        $('#modalCargandoCategoria').modal("show");
        return false;
    });
    $('#FrmCategoriaModal').submit(function () {
       // console.log("entro")
        if (validarFormularioCategoria()) {
            $('#nameFormCategoria').val("FrmCategoriaModal");
            $('#modalCargandoCategoria').modal('show');
        }
        return  false;
    });
    $('#modalCargandoCategoria').on('show.bs.modal', function () {
        processAjaxCategoria();
    });

    addEventsCombosPaginar();
    addValidacionesFormularioCategoria();

    $('#modalCargandoCategoria').modal("show");
});

function processAjaxCategoria() {
    var datosSerealizadosCompletos = $('#' + $('#nameFormCategoria').val()).serialize();
    if ($('#nameFormCategoria').val().toLowerCase() !== "frmcategoria") {
        datosSerealizadosCompletos += "&txtNombreCategoria=" + $('#txtNombreCategoria').val();
    }
    datosSerealizadosCompletos += "&numberPageCategoria=" + $('#numberPageCategoria').val();
    datosSerealizadosCompletos += "&sizePageCategoria=" + $('#sizePageCategoria').val();
    datosSerealizadosCompletos += "&accion=" + $('#accionCategoria').val();
    $.ajax({
        url: 'categoria',
        type: 'POST',
        data: datosSerealizadosCompletos,
        dataType: 'json',
        success: function (json_respose) {
            $('#modalCargandoCategoria').modal("hide");
            if ($('#accionCategoria').val().toLowerCase() === "paginarcategoria") {
                listarCategoria(json_respose.BEAN_PAGINATION);
            } else {
                if (json_respose.MENSSAGE_SERVER.toLowerCase() === "ok") {
                    $('#ventanaModalManCategoria').modal('hide');
                     listarCategoria(json_respose.BEAN_PAGINATION);
                    viewAlert('Operacion realizada correctamente', 'success');
                } else {
                    viewAlert(json_respose.MENSSAGE_SERVER, 'warning');
                }
            }
            console.log(json_respose);


        },
        error: function (jqXHR, textStatus, errorThrown) {
              $('#modalCargandoCategoria').modal("hide");
            viewAlert('error interno en el servidor', 'error');
        }

    });
}
function listarCategoria(BEAN_PAGINATION) {
    var $pagination = $('#paginationCategoria');
    $('#tbodyCategoria').empty();
    $pagination.twbsPagination('destroy');
    $('#nameCrudCategoria').html("[ " + BEAN_PAGINATION.COUNT_FILTER + " ] CATEGORIAS");
    if (BEAN_PAGINATION.COUNT_FILTER > 0) {
        var fila;
        var atributos;
        $(BEAN_PAGINATION.List).each(function (index, value) {
            fila = "<tr ";
            atributos = "idcategoria='" + value.idcategoria + "' ";
            atributos += "nombre='" + value.nombre + "' ";
            fila += atributos;
            fila += ">";
            fila += "<td>" + value.nombre + "</td>";
            fila += "<td class='text-center'><button class='btn btn-secondary btn-xs editar-categoria'><i class='fa fa-edit'></i></button></td>";
            fila += "<td class='text-center'><button class='btn btn-secondary btn-xs eliminar-categoria'><i class='fa fa-trash'></i></button></td>";
            fila += "</tr>";
            $('#tbodyCategoria').append(fila);
        });
        //PAGINACION
        var defaultOptions = getDefaultOptionsPagination();
        var options = getOptionsPagination(BEAN_PAGINATION.COUNT_FILTER, $('#sizePageCategoria'),
                $('#numberPageCategoria'), $('#actionCategoria'), "paginarCategoria",
                $('#nameFormCategoria'), 'FrmCategoria', $('#modalCargandoCategoria'));
        $pagination.twbsPagination($.extend({}, defaultOptions, options));
        addEventsButtons();
        $('#txtNombreCategoria').focus();

    } else {
        
        viewAlert("No se encontraron Registro", "warning");
    }
}

function addValidacionesFormularioCategoria() {
    $('#txtNombreCategoriaER').on('change', function () {
        $(this).val() === "" ? $('#validarNombreCategoriaER').fadeIn('slow') : $('#validarNombreCategoriaER').fadeOut();
    });
}

function validarFormularioCategoria() {
    if ($('#txtNombreCategoriaER').val() === "") {
        $('#validarNombreCategoriaER').fadeIn('slow');
        return false;
    } else {
        $('#validarNombreCategoriaER').fadeOut();
    }
    return true;
}

function addEventsButtons(){
    $('.editar-categoria').each(function(index,value){
        $(this).click(function (){
           $('#txtIdCategoriaER').val($(this.parentElement.parentElement).attr('idcategoria'));
            $('#txtNombreCategoriaER').val($(this.parentElement.parentElement).attr('nombre'));
            $('#tituloModalManCategoria').html("EDITAR CATEGORÍA");
            $('#accionCategoria').val("updateCategoria");
            $('#ventanaModalManCategoria').modal("show");
        });
    });
    
    $('.eliminar-categoria').each(function (index, value) {
        $(this).click(function () {
            $('#txtIdCategoriaER').val($(this.parentElement.parentElement).attr('idcategoria'));
            viewAlertDelete('Categoria');
        });
    });
}
