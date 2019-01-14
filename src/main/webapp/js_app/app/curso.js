$(document).ready(function () {
    $('#btnAbrirNCurso').click(function () {
        $('#txtNombreCursoER').val("");
        $('#txtEstadoCursoER').val("");
        $('.error-validation').fadeOut();
        $('#accionCurso').val("addCurso");
        $('#tituloModalManCurso').html("Registrar Curso");
        $('#ventanaModalManCurso').modal("show");
    });

    $('#FrmCurso').submit(function () {
        $("#accionCurso").val("paginarCurso");
        $("#nameFormCurso").val("FrmCurso");
        $("#numberPageCurso").val("1");
        $('#modalCargandoCurso').modal("show");
        return false;
    });
    $('#FrmCursoModal').submit(function () {
       // console.log("entro")
        if (validarFormularioCurso()) {
            $('#nameFormCurso').val("FrmCursoModal");
            $('#modalCargandoCurso').modal('show');
        }
        return  false;
    });
    $('#modalCargandoCurso').on('show.bs.modal', function () {
        processAjaxCurso();
    });
    
    $('#cboEstadoCurso').on('change', function (){
        $("#btnBuscarCurso").trigger("click");
    });

    addEventsCombosPaginar();
    addValidacionesFormularioCurso();
    $('#modalCargandoCurso').modal("show");
});

function processAjaxCurso() {
    var datosSerealizadosCompletos = $('#' + $('#nameFormCurso').val()).serialize();
    if ($('#nameFormCurso').val().toLowerCase() !== "frmcurso") {
        datosSerealizadosCompletos += "&txtNombreCurso=" + $('#txtNombreCurso').val();
        datosSerealizadosCompletos += "&cboEstadoCurso=" + $('#cboEstadoCurso').val();
    }
    datosSerealizadosCompletos += "&numberPageCurso=" + $('#numberPageCurso').val();
    datosSerealizadosCompletos += "&sizePageCurso=" + $('#sizePageCurso').val();
    datosSerealizadosCompletos += "&accion=" + $('#accionCurso').val();
    $.ajax({
        url: 'curso',
        type: 'POST',
        data: datosSerealizadosCompletos,
        dataType: 'json',
        success: function (json_respose) {
            $('#modalCargandoCurso').modal("hide");
            if ($('#accionCurso').val().toLowerCase() === "paginarcurso") {
                listarCurso(json_respose.BEAN_PAGINATION);
            } else {
                if (json_respose.MENSSAGE_SERVER.toLowerCase() === "ok") {
                    $('#ventanaModalManCurso').modal('hide');
                     listarCurso(json_respose.BEAN_PAGINATION);
                    viewAlert('Operacion realizada correctamente', 'success');
                } else {
                    viewAlert(json_respose.MENSSAGE_SERVER, 'warning');
                }
            }
            console.log(json_respose);


        },
        error: function (jqXHR, textStatus, errorThrown) {
             $('#modalCargandoCurso').modal("hide");
            viewAlert('error interno en el servidor', 'error');
        }

    });
}
function listarCurso(BEAN_PAGINATION) {
    var $pagination = $('#paginationCurso');
    var estado;
    $('#tbodyCurso').empty();
    $pagination.twbsPagination('destroy');
    $('#nameCrudCurso').html("[ " + BEAN_PAGINATION.COUNT_FILTER + " ] CURSOS");
    if (BEAN_PAGINATION.COUNT_FILTER > 0) {
        var fila;
        var atributos;
        $(BEAN_PAGINATION.List).each(function (index, value) {
            fila = "<tr ";
            atributos = "idcurso='" + value.idcurso + "' ";
            atributos += "nombre='" + value.nombre + "' ";
            atributos += "estado='" + value.estado + "' ";
            fila += atributos;
            fila += ">";
            fila += "<td>" + value.nombre + "</td>";
            if(value.estado === 'a'){
                estado = 'Activo';
            }else{
                estado = 'Inactivo';
            }
            fila += "<td>" + estado + "</td>";
            fila += "<td class='text-center'><button class='btn btn-secondary btn-xs editar-curso'><i class='fa fa-edit'></i></button></td>";
            fila += "<td class='text-center'><button class='btn btn-secondary btn-xs eliminar-curso'><i class='fa fa-trash'></i></button></td>";
            fila += "</tr>";
            $('#tbodyCurso').append(fila);
        });
        //PAGINACION
        var defaultOptions = getDefaultOptionsPagination();
        var options = getOptionsPagination(BEAN_PAGINATION.COUNT_FILTER, $('#sizePageCurso'),
                $('#numberPageCurso'), $('#actionCurso'), "paginarCurso",
                $('#nameFormCurso'), 'FrmCurso', $('#modalCargandoCurso'));
        $pagination.twbsPagination($.extend({}, defaultOptions, options));
        addEventsButtons();
        $('#txtNombreCurso').focus();
    } else {
        viewAlert("No se encontraron Registro", "warning");
    }
}

function addValidacionesFormularioCurso() {
    $('#txtNombreCursoER').on('change', function () {
        $(this).val() === "" ? $('#validarNombreCursoER').fadeIn('slow') : $('#validarNombreCursoER').fadeOut();
    });
     $('#cboEstadoCursoER').on('change', function () {
        $(this).val() === "" ? $('#validarEstadoCursoER').fadeIn('slow') : $('#validarEstadoCursoER').fadeOut();
    });
}

function validarFormularioCurso() {
    if ($('#txtNombreCursoER').val() === "") {
        $('#validarNombreCursoER').fadeIn('slow');
        return false;
    } else {
        $('#validarNombreCursoER').fadeOut();
    }
      if ($('#cboEstadoCursoER').val() === "-1") {
        $('#validarEstadoCursoER').fadeIn('slow');
        return false;
    } else {
        $('#validarEstadoCursoER').fadeOut();
    }
    return true;
}

function addEventsButtons(){
    $('.editar-curso').each(function(index,value){
        $(this).click(function (){
           $('#txtIdCursoER').val($(this.parentElement.parentElement).attr('idcurso'));
            $('#txtNombreCursoER').val($(this.parentElement.parentElement).attr('nombre'));
            $('#cboEstadoCursoER').val($(this.parentElement.parentElement).attr('estado'));
            $('#tituloModalManCurso').html("EDITAR ALUMNO");
            $('#accionCurso').val("updateCurso");
            $('#ventanaModalManCurso').modal("show");
        });
    });
    
    $('.eliminar-curso').each(function (index, value) {
        $(this).click(function () {
            $('#txtIdCursoER').val($(this.parentElement.parentElement).attr('idcurso'));
            viewAlertDelete('Curso');
        });
    });
}





