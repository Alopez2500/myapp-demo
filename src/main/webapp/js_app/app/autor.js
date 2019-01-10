$(document).ready(function () {
    $('#btnAbrirNAutor').click(function () {
        $('#txtNombreAutorER').val("");
        $('.error-validation').fadeOut();
        $('#accionAutor').val("addAutor");
        $('#tituloModalManAutor').html("Registrar Cliente");
        $('#ventanaModalManAutor').modal("show");
    });

    $('#FrmAutor').submit(function () {
        $("#accionAutor").val("paginarAutor");
        $("#nameFormAutor").val("FrmAutor");
        $("#numberPageAutor").val("1");
        $('#modalCargandoAutor').modal("show");
        return false;
    });
    $('#FrmAutorModal').submit(function () {
       // console.log("entro")
        if (validarFormularioAutor()) {
            $('#nameFormAutor').val("FrmAutorModal");
            $('#modalCargandoAutor').modal('show');
        }
        return  false;
    });
    $('#modalCargandoAutor').on('show.bs.modal', function () {
        processAjaxAutor();
    });

    addEventsCombosPaginar();
    addValidacionesFormularioAutor();

    $('#modalCargandoAutor').modal("show");

});


function processAjaxAutor() {
    var datosSerealizadosCompletos = $('#' + $('#nameFormAutor').val()).serialize();
    if ($('#nameFormAutor').val().toLowerCase() !== "frmAutor") {
        datosSerealizadosCompletos += "&txtNombreAutor=" + $('#txtNombreAutor').val();
    }
    datosSerealizadosCompletos += "&numberPageAutor=" + $('#numberPageAutor').val();
    datosSerealizadosCompletos += "&sizePageAutor=" + $('#sizePageAutor').val();
    datosSerealizadosCompletos += "&accion=" + $('#accionAutor').val();
    $.ajax({
        url: 'autor',
        type: 'POST',
        data: datosSerealizadosCompletos,
        dataType: 'json',
        success: function (json_respose) {
            $('#modalCargandoAutor').modal("hide");
            if ($('#accionAutor').val().toLowerCase() === "paginarautor") {
                listarAutor(json_respose.BEAN_PAGINATION);
            } else {
                if (json_respose.MENSSAGE_SERVER.toLowerCase() === "ok") {
                    $('#ventanaModalManAutor').modal('hide');
                     listarAutor(json_respose.BEAN_PAGINATION);
                    viewAlert('Operacion realizada correctamente', 'success');
                } else {
                    viewAlert(json_respose.MENSSAGE_SERVER, 'warning');
                }
            }
            console.log(json_respose);


        },
        error: function (jqXHR, textStatus, errorThrown) {
              $('#modalCargandoAutor').modal("hide");
            viewAlert('error interno en el servidor', 'error');
        }

    });
}
function listarAutor(BEAN_PAGINATION) {
    var $pagination = $('#paginationAutor');
    $('#tbodyAutor').empty();
    $pagination.twbsPagination('destroy');
    $('#nameCrudAutor').html("[ " + BEAN_PAGINATION.COUNT_FILTER + " ] Cliente");
    if (BEAN_PAGINATION.COUNT_FILTER > 0) {
        var fila;
        var atributos;
        $(BEAN_PAGINATION.List).each(function (index, value) {
            fila = "<tr ";
            atributos = "idautor='" + value.idautor + "' ";
            atributos += "nombre='" + value.nombre + "' ";
            atributos += "nombre2='" + value.nombre2 + "' ";
            atributos += "documento='" + value.documento + "' ";
            atributos += "telefono='" + value.telefono + "' ";
            atributos += "direccion='" + value.direccion + "' ";
            fila += atributos;
            fila += ">";
            fila += "<td>" + value.nombre + "</td>";
            fila += "<td>" + value.nombre2 + "</td>";
            fila += "<td>" + value.documento + "</td>";
            fila += "<td>" + value.telefono + "</td>";
            fila += "<td>" + value.direccion + "</td>";
            fila += "<td class='text-center'><button class='btn btn-secondary btn-xs editar-Autor'><i class='fa fa-edit'></i></button></td>";
            fila += "<td class='text-center'><button class='btn btn-secondary btn-xs eliminar-Autor'><i class='fa fa-trash'></i></button></td>";
            fila += "</tr>";
            $('#tbodyAutor').append(fila);
        });
        //PAGINACION
        var defaultOptions = getDefaultOptionsPagination();
        var options = getOptionsPagination(BEAN_PAGINATION.COUNT_FILTER, $('#sizePageAutor'),
                $('#numberPageAutor'), $('#actionAutor'), "paginarAutor",
                $('#nameFormAutor'), 'FrmAutor', $('#modalCargandoAutor'));
        $pagination.twbsPagination($.extend({}, defaultOptions, options));
        addEventsButtons();
        $('#txtNombreAutor').focus();

    } else {
        
        viewAlert("No se encontraron Registro", "warning");
    }
}

function addValidacionesFormularioAutor() {
    $('#txtNombreAutorER').on('change', function () {
        $(this).val() === "" ? $('#validarNombreAutorER').fadeIn('slow') : $('#validarNombreAutorER').fadeOut();
    });
     $('#txtNombre2AutorER').on('change', function () {
        $(this).val() === "" ? $('#validarNombre2AutorER').fadeIn('slow') : $('#validarNombre2AutorER').fadeOut();
    });
     $('#txtDocumentoAutorER').on('change', function () {
        $(this).val() === "" ? $('#validarDocumentoAutorER').fadeIn('slow') : $('#validarDocumentoAutorER').fadeOut();
    });
     $('#txtTelefonoAutorER').on('change', function () {
        $(this).val() === "" ? $('#validarTelefonoAutorER').fadeIn('slow') : $('#validarTelefonoAutorER').fadeOut();
    });
     $('#txtDireccionAutorER').on('change', function () {
        $(this).val() === "" ? $('#validarDireccionAutorER').fadeIn('slow') : $('#validarDireccionAutorER').fadeOut();
    });
}

function validarFormularioAutor() {
    if ($('#txtNombreAutorER').val() === "") {
        $('#validarNombreAutorER').fadeIn('slow');
        return false;
    } else {
        $('#validarNombre2AutorER').fadeOut();
    }
     if ($('#txtNombre2AutorER').val() === "") {
        $('#validarNombre2AutorER').fadeIn('slow');
        return false;
    } else {
        $('#validarNombre2AutorER').fadeOut();
    }
     if ($('#txtDocumentoAutorER').val() === "") {
        $('#validarDocumentoAutorER').fadeIn('slow');
        return false;
    } else {
        $('#validarDocumentoAutorER').fadeOut();
    }
     if ($('#txtTelefonoAutorER').val() === "") {
        $('#validarTelefonoAutorER').fadeIn('slow');
        return false;
    } else {
        $('#validarTelefonoAutorER').fadeOut();
    }
     if ($('#txtDireccionAutorER').val() === "") {
        $('#validarDireccionAutorER').fadeIn('slow');
        return false;
    } else {
        $('#validarDireccionAutorER').fadeOut();
    }
    return true;
}

function addEventsButtons(){
    $('.editar-Autor').each(function(index,value){
        $(this).click(function (){
           $('#txtIdAutorER').val($(this.parentElement.parentElement).attr('idAutor'));
            $('#txtNombreAutorER').val($(this.parentElement.parentElement).attr('nombre'));
            $('#txtNombre2AutorER').val($(this.parentElement.parentElement).attr('nombre2'));
            $('#txtDocumentoAutorER').val($(this.parentElement.parentElement).attr('documento'));
            $('#txtTelefonoAutorER').val($(this.parentElement.parentElement).attr('telefono'));
            $('#txtDireccionAutorER').val($(this.parentElement.parentElement).attr('direccion'));
            $('#tituloModalManAutor').html("EDITAR CLIENTE");
            $('#accionAutor').val("updateAutor");
            $('#ventanaModalManAutor').modal("show");
        });
    });
    
    $('.eliminar-Autor').each(function (index, value) {
        $(this).click(function () {
            $('#txtIdAutorER').val($(this.parentElement.parentElement).attr('idAutor'));
            viewAlertDelete('Autor');
        });
    });
}

