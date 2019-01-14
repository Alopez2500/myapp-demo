$(document).ready(function () {
    $('#btnAbrirNAlumno').click(function () {
        $('#txtNombreAlumnoER').val("");
        $('#txtNombre2AlumnoER').val("");
        $('#txtFecha_nacimientoAlumnoER').val("");
        $('#txtDireccionAlumnoER').val("");
        $('#txtTelefonoAlumnoER').val("");
        $('.error-validation').fadeOut();
        $('#accionAlumno').val("addAlumno");
        $('#tituloModalManAlumno').html("Registrar Alumno");
        $('#ventanaModalManAlumno').modal("show");
    });

    $('#FrmAlumno').submit(function () {
        $("#accionAlumno").val("paginarAlumno");
        $("#nameFormAlumno").val("FrmAlumno");
        $("#numberPageAlumno").val("1");
        $('#modalCargandoAlumno').modal("show");
        return false;
    });
    $('#FrmAlumnoModal').submit(function () {
       // console.log("entro")
        if (validarFormularioAlumno()) {
            $('#nameFormAlumno').val("FrmAlumnoModal");
            $('#modalCargandoAlumno').modal('show');
        }
        return  false;
    });
    $('#modalCargandoAlumno').on('show.bs.modal', function () {
        processAjaxAlumno();
    });

    addEventsCombosPaginar();
    addValidacionesFormularioAlumno();
    $('#modalCargandoAlumno').modal("show");
});

function processAjaxAlumno() {
    var datosSerealizadosCompletos = $('#' + $('#nameFormAlumno').val()).serialize();
    if ($('#nameFormAlumno').val().toLowerCase() !== "frmalumno") {
        datosSerealizadosCompletos += "&txtNombreAlumno=" + $('#txtNombreAlumno').val();
    }
    datosSerealizadosCompletos += "&numberPageAlumno=" + $('#numberPageAlumno').val();
    datosSerealizadosCompletos += "&sizePageAlumno=" + $('#sizePageAlumno').val();
    datosSerealizadosCompletos += "&accion=" + $('#accionAlumno').val();
    $.ajax({
        url: 'alumno',
        type: 'POST',
        data: datosSerealizadosCompletos,
        dataType: 'json',
        success: function (json_respose) {
            $('#modalCargandoAlumno').modal("hide");
            if ($('#accionAlumno').val().toLowerCase() === "paginaralumno") {
                listarAlumno(json_respose.BEAN_PAGINATION);
            } else {
                if (json_respose.MENSSAGE_SERVER.toLowerCase() === "ok") {
                    $('#ventanaModalManAlumno').modal('hide');
                     listarAlumno(json_respose.BEAN_PAGINATION);
                    viewAlert('Operacion realizada correctamente', 'success');
                } else {
                    viewAlert(json_respose.MENSSAGE_SERVER, 'warning');
                }
            }
            console.log(json_respose);


        },
        error: function (jqXHR, textStatus, errorThrown) {
             $('#modalCargandoAlumno').modal("hide");
            viewAlert('error interno en el servidor', 'error');
        }

    });
}
function listarAlumno(BEAN_PAGINATION) {
    var $pagination = $('#paginationAlumno');
    $('#tbodyAlumno').empty();
    $pagination.twbsPagination('destroy');
    $('#nameCrudAlumno').html("[ " + BEAN_PAGINATION.COUNT_FILTER + " ] ALUMNOS");
    if (BEAN_PAGINATION.COUNT_FILTER > 0) {
        var fila;
        var atributos;
        $(BEAN_PAGINATION.List).each(function (index, value) {
            fila = "<tr ";
            atributos = "idalumno='" + value.idalumno + "' ";
            atributos += "nombre='" + value.nombre + "' ";
            atributos += "apellido='" + value.nombre2 + "' ";
            atributos += "fecha_nacimiento='" + value.fecha_nacimiento + "' ";
            atributos += "direccion='" + value.direccion + "' ";
            atributos += "telefono='" + value.telefono + "' ";
            fila += atributos;
            fila += ">";
            fila += "<td>" + value.nombre + "</td>";
            fila += "<td>" + value.nombre2 + "</td>";
            fila += "<td>" + value.fecha_nacimiento + "</td>";
            fila += "<td>" + value.direccion + "</td>";
            fila += "<td>" + value.telefono + "</td>";
            fila += "<td class='text-center'><button class='btn btn-secondary btn-xs editar-alumno'><i class='fa fa-edit'></i></button></td>";
            fila += "<td class='text-center'><button class='btn btn-secondary btn-xs eliminar-alumno'><i class='fa fa-trash'></i></button></td>";
            fila += "</tr>";
            $('#tbodyAlumno').append(fila);
        });
        //PAGINACION
        var defaultOptions = getDefaultOptionsPagination();
        var options = getOptionsPagination(BEAN_PAGINATION.COUNT_FILTER, $('#sizePageAlumno'),
                $('#numberPageAlumno'), $('#actionAlumno'), "paginarAlumno",
                $('#nameFormAlumno'), 'FrmAlumno', $('#modalCargandoAlumno'));
        $pagination.twbsPagination($.extend({}, defaultOptions, options));
        addEventsButtons();
        $('#txtNombreAlumno').focus();

    } else {
        
        viewAlert("No se encontraron Registro", "warning");
    }
}

function addValidacionesFormularioAlumno() {
    $('#txtNombreAlumnoER').on('change', function () {
        $(this).val() === "" ? $('#validarNombreAlumnoER').fadeIn('slow') : $('#validarNombreAlumnoER').fadeOut();
    });
     $('#txtNombre2AlumnoER').on('change', function () {
        $(this).val() === "" ? $('#validarNombre2AlumnoER').fadeIn('slow') : $('#validarNombre2AlumnoER').fadeOut();
    });
     $('#txtFecha_nacimeintoAlumnoER').on('change', function () {
        $(this).val() === "" ? $('#validarFecha_nacimeintoAlumnoER').fadeIn('slow') : $('#validarFecha_nacimeintoAlumnoER').fadeOut();
    });
     $('#txtDireccionAlumnoER').on('change', function () {
        $(this).val() === "" ? $('#validarDireccionAlumnoER').fadeIn('slow') : $('#validarDireccionAlumnoER').fadeOut();
    });
     $('#txtTelefonoAlumnoER').on('change', function () {
        $(this).val() === "" ? $('#validarTelefonoAlumnoER').fadeIn('slow') : $('#validarTelefonoAlumnoER').fadeOut();
    });
}

function validarFormularioAlumno() {
    if ($('#txtNombreAlumnoER').val() === "") {
        $('#validarNombreAlumnoER').fadeIn('slow');
        return false;
    } else {
        $('#validarNombreAlumnoER').fadeOut();
    }
      if ($('#txtNombre2AlumnoER').val() === "") {
        $('#validarNombre2AlumnoER').fadeIn('slow');
        return false;
    } else {
        $('#validarNombre2AlumnoER').fadeOut();
    }
      if ($('#txtFecha_nacimeintoAlumnoER').val() === "") {
        $('#validarFecha_nacimeintoAlumnoER').fadeIn('slow');
        return false;
    } else {
        $('#validarFecha_nacimientoAlumnoER').fadeOut();
    }
      if ($('#txtDireccionAlumnoER').val() === "") {
        $('#validarDireccionAlumnoER').fadeIn('slow');
        return false;
    } else {
        $('#validarDireccionAlumnoER').fadeOut();
    }
      if ($('#txtTelefonoAlumnoER').val() === "") {
        $('#validarTelefonoAlumnoER').fadeIn('slow');
        return false;
    } else {
        $('#validarTelefonoAlumnoER').fadeOut();
    }
    return true;
}

function addEventsButtons(){
    $('.editar-alumno').each(function(index,value){
        $(this).click(function (){
           $('#txtIdAlumnoER').val($(this.parentElement.parentElement).attr('idalumno'));
            $('#txtNombreAlumnoER').val($(this.parentElement.parentElement).attr('nombre'));
            $('#txtNombre2AlumnoER').val($(this.parentElement.parentElement).attr('apellido'));
            $('#txtFecha_nacimientoAlumnoER').val($(this.parentElement.parentElement).attr('fecha_nacimiento'));
            $('#txtDireccionAlumnoER').val($(this.parentElement.parentElement).attr('direccion'));
            $('#txtTelefonoAlumnoER').val($(this.parentElement.parentElement).attr('telefono'));
            $('#tituloModalManAlumno').html("EDITAR ALUMNO");
            $('#accionAlumno').val("updateAlumno");
            $('#ventanaModalManAlumno').modal("show");
        });
    });
    
    $('.eliminar-alumno').each(function (index, value) {
        $(this).click(function () {
            $('#txtIdAlumnoER').val($(this.parentElement.parentElement).attr('idalumno'));
            viewAlertDelete('Alumno');
        });
    });
}



