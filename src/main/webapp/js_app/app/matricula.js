$(document).ready(function () {
    $('#btnAbrirNMatricula').click(function () {
        $('#txtIdMatriculaER').val("");
        $('#txtCodigoMatriculaER').val("");
        $('#txtCicloMatriculaER').val("");
        $('#cboAlumnoMatriculaER').val("-1");
        $('.error-validation').fadeOut();
        $('#accionMatricula').val("addMatricula");
        $('#tituloModalManMatricula').html("REGISTRAR ALUMNO");
        $('#ventanaModalManMatricula').modal('show');
    });

    $('#FrmMatricula').submit(function () {
        $('#accionMatricula').val("paginarMatricula");
        $('#nameFormMatricula').val("FrmMatricula");
        $('#numberPageMatricula').val("1");
        $('#modalCargandoMatricula').modal('show');
        return false;
    });

    $('#FrmMatriculaModal').submit(function () {
        if (validarFormularioMatricula()) {
            $('#nameFormMatricula').val("FrmMatriculaModal");
            $('#modalCargandoMatricula').modal('show');
        }
        return false;
    });

    $("#modalCargandoMatricula").on('shown.bs.modal', function () {
        processAjaxMatricula();
    });

    $("#ventanaModalManMatricula").on('hidden.bs.modal', function () {
        $("#accionMatricula").val("paginarMatricula");
    });

    addValicacionesCamposMatricula();
    addComboAlumno();
    validarFormularioMatricula();
    $('#modalCargandoMatricula').modal('show');

});

function processAjaxMatricula() {
    var datosSerializadosCompletos = $('#' + $('#nameFormMatricula').val()).serialize();
    if ($('#nameFormMatricula').val().toLowerCase() !== "frmmatricula") {
        datosSerializadosCompletos += "&txtCodigoMatricula=" + $('#txtCodigoMatricula').val();
    }
    datosSerializadosCompletos += "&numberPageMatricula=" + $('#numberPageMatricula').val();
    datosSerializadosCompletos += "&sizePageMatricula=" + $('#sizePageMatricula').val();
    datosSerializadosCompletos += "&accion=" + $('#accionMatricula').val();
    console.log(datosSerializadosCompletos);
    $.ajax({
        url: 'matricula',
        type: 'POST',
        data: datosSerializadosCompletos,
        dataType: 'json',
        success: function (jsonResponse) {
            console.log(jsonResponse);
            $('#modalCargandoMatricula').modal("hide");
            if ($('#accionMatricula').val().toLowerCase() === "paginarmatricula") {
                listarMatricula(jsonResponse.BEAN_PAGINATION);
            } else {
                if (jsonResponse.MENSSAGE_SERVER.toLowerCase() === "ok") {
                    $("#ventanaModalManMatricula").modal("hide");
                    viewAlert(getMessageServerTransaction($('#accionMatricula').val(), 'Matricula', 'o'), 'success');
                    listarMatricula(jsonResponse.BEAN_PAGINATION);
                } else {
                    viewAlert(jsonResponse.MENSSAGE_SERVER, 'warning');
                }
            }

        },
        error: function () {
            $('#modalCargandoMatricula').modal("hide");
            $("#ventanaModalManMatricula").modal("hide");
            viewAlert('Error interno en el Servidor', 'error');
        }
    });
    return false;
}

function listarMatricula(BEAN_PAGINATION) {
    /*PAGINATION*/
    var $pagination = $('#paginationMatricula');
    $('#tbodyMatricula').empty();
    $pagination.twbsPagination('destroy');
    $('#nameCrudMatricula').html("[ " + BEAN_PAGINATION.COUNT_FILTER + " ] MATRICULAS");
    if (BEAN_PAGINATION.COUNT_FILTER > 0) {
        var fila;
        var atributos;
        $.each(BEAN_PAGINATION.List, function (index, value) {
            fila = "<tr ";
            atributos = "";
            atributos += "idmatricula='" + value.idmatricula + "' ";
            atributos += "codigo='" + value.codigo + "' ";
            atributos += "ciclo='" + value.ciclo + "' ";
            atributos += "idalumno='" + value.alumno.idalumno + "' ";
            fila += atributos;
            fila += ">";
            fila += "<td class='align-middle'>" + value.codigo + "</td>";
            fila += "<td class='align-middle'>" + value.ciclo + "</td>";
            fila += "<td class='align-middle'>" + value.alumno.nombre + " " + value.alumno.nombre2 + "</td>";
            fila += "<td class='align-middle'>" + value.alumno.telefono + "</td>";
            fila += "<td class='align-middle'>" + value.alumno.direccion + "</td>";
            fila += "<td class='text-center align-middle'><button class='btn btn-secondary btn-xs editar-Matricula'><i class='fa fa-edit'></i></button></td>";
            fila += "<td class='text-center align-middle'><button class='btn btn-secondary btn-xs eliminar-Matricula'><i class='fa fa-trash'></i></button></td>";
            fila += "</tr>";
            $('#tbodyMatricula').append(fila);
        });
        var defaultOptions = getDefaultOptionsPagination();
        var options = getOptionsPagination(BEAN_PAGINATION.COUNT_FILTER, $('#sizePageMatricula'),
                $('#numberPageMatricula'), $('#accionMatricula'), 'paginarMatricula',
                $('#nameForm'), 'FrmMatricula', $('#modalCargandoMatricula'));
        $pagination.twbsPagination('destroy');
        $pagination.twbsPagination($.extend({}, defaultOptions, options));
        addEventosMatricula();
        $('#txtNombreMatricula').focus();
    } else {
        $pagination.twbsPagination('destroy');
        viewAlert('No se enconntraron resultados', 'warning');
    }
}

function addEventosMatricula() {
    $('.editar-Matricula').each(function () {
        $(this).click(function () {
            $('#txtIdMatriculaER').val($(this.parentElement.parentElement).attr('idmatricula'));
            $('#txtCodigoMatriculaER').val($(this.parentElement.parentElement).attr('codigo'));
            $('#txtCicloMatriculaER').val($(this.parentElement.parentElement).attr('ciclo'));
            $('#cboAlumnoMatriculaER').val($(this.parentElement.parentElement).attr('idalumno'));
            $('#accionMatricula').val("updateMatricula");
            $('#tituloModalManMatricula').html("EDITAR ALUMNO");
            $('#ventanaModalManMatricula').modal("show");
            document.getElementsByTagName("body")[0].style.paddingRight = "0";
        });
    });
    $('.eliminar-Matricula').each(function () {
        $(this).click(function () {
            $('#txtIdMatriculaER').val($(this.parentElement.parentElement).attr('idmatricula'));
            viewAlertDelete("Matricula");
            document.getElementsByTagName("body")[0].style.paddingRight = "0";
        });
    });
}

function addValicacionesCamposMatricula() {
    $('#txtCodigoMatriculaER').on('change', function () {
        $(this).val() === "" ? $("#validarCodigoMatriculaER").fadeIn("slow") : $("#validarCodigoMatriculaER").fadeOut();
    });
    $('txtCicloMatriculaER').on('change', function () {
        $(this).val() === "" ? $("#validarCicloMatriculaER").fadeIn("slow") : $("#validarCicloMatriculaER").fadeOut();
    });
    $('#cboAlumnoMatriculaER').on('change', function () {
        $(this).val() === "-1" ? $("#validarAlumnoMatriculaER").fadeIn("slow") : $("#validarAlumnoMatriculaER").fadeOut();
    });
}

function validarFormularioMatricula() {
    if ($('#txtCodigoMatriculaER').val() === "") {
        $("#validarCodigoMatriculaER").fadeIn("slow");
        return false;
    } else {
        $("#validarCodigoMatriculaER").fadeOut();
    }
    if ($('#txtCicloMatriculaER').val() === "") {
        $("#validarCicloMatriculaER").fadeIn("slow");
        return false;
    } else {
        $("#validarCicloMatriculaER").fadeOut();
    }
    if ($('#cboAlumnoMatriculaER').val() === "-1") {
        $("#validarcboAlumnoMatriculaER").fadeIn("slow");
        return false;
    } else {
        $("#validarcboAlumnoMatriculaER").fadeOut();
    }
    return true;
}

function addComboAlumno() {
    var datosSerializadosCompletos;
    datosSerializadosCompletos = "txtNombreAlumno=";
    datosSerializadosCompletos += "&numberPageAlumno=";
    datosSerializadosCompletos += "&sizePageAlumno=ALL";
    datosSerializadosCompletos += "&accion=paginarAlumno";
    $('#cboAlumnoMatriculaER').empty();
    $.ajax({
        url: 'alumno',
        type: 'POST',
        data: datosSerializadosCompletos,
        dataType: 'json',
        success: function (jsonResponse) {
            $('#cboAlumnoMatriculaER').append("<option value='-1'>Seleccione...</option>");
            $(jsonResponse.BEAN_PAGINATION.List).each(function (index, value) {
                $('#cboAlumnoMatriculaER').append("<option value='" + value.idalumno + "'>" + value.nombre + " " + value.nombre2 + "</option>");
            });
        },
        error: function () {
            console.log("error interno al cargar alumno");
        }
    });
    return false;
}

