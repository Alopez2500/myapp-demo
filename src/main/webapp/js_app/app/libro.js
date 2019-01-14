$(document).ready(function () {
    $('#btnAbrirNLibro').click(function () {
        $('#txtIdLibroER').val("");
        $('#txtNombreLibroER').val("");
        $('#txtFecha-PublicacionLibroER').val("");
        $('#txtGeneroLibroER').val("");
        $('#txtEdicionLibroER').val("");
        $('#cboAutorLibroER').val("-1");
        $('.error-validation').fadeOut();
        $('#accionLibro').val("addLibro");
        $('#tituloModalManLibro').html("REGISTRAR LIBRO");
        $('#ventanaModalManLibro').modal('show');
    });

    $('#FrmLibro').submit(function () {
        $('#accionLibro').val("paginarLibro");
        $('#nameFormLibro').val("FrmLibro");
        $('#numberPageLibro').val("1");
        $('#modalCargandoLibro').modal('show');
        return false;
    });

    $('#FrmLibroModal').submit(function () {
        if (validarFormularioLibro()) {
            $('#nameFormLibro').val("FrmLibroModal");
            $('#modalCargandoLibro').modal('show');
        }
        return false;
    });

    $("#modalCargandoLibro").on('shown.bs.modal', function () {
        processAjaxLibro();
    });

    $("#ventanaModalManLibro").on('hidden.bs.modal', function () {
        $("#accionLibro").val("paginarLibro");
    });

    addValicacionesCamposLibro();
    addComboAutor();
    validarFormularioLibro();
    $('#modalCargandoLibro').modal('show');

});

function processAjaxLibro() {
    var datosSerializadosCompletos = $('#' + $('#nameFormLibro').val()).serialize();
    if ($('#nameFormLibro').val().toLowerCase() !== "frmlibro") {
        datosSerializadosCompletos += "&txtNombreLibro=" + $('#txtNombreLibro').val();
    }
    datosSerializadosCompletos += "&numberPageLibro=" + $('#numberPageLibro').val();
    datosSerializadosCompletos += "&sizePageLibro=" + $('#sizePageLibro').val();
    datosSerializadosCompletos += "&accion=" + $('#accionLibro').val();
    console.log(datosSerializadosCompletos)
    $.ajax({
        url: 'libro',
        type: 'POST',
        data: datosSerializadosCompletos,
        dataType: 'json',
        success: function (jsonResponse) {
            console.log(jsonResponse);
            $('#modalCargandoLibro').modal("hide");
            if ($('#accionLibro').val().toLowerCase() === "paginarlibro") {
                listarLibro(jsonResponse.BEAN_PAGINATION);
            } else {
                if (jsonResponse.MENSSAGE_SERVER.toLowerCase() === "ok") {
                    $("#ventanaModalManLibro").modal("hide");
                    viewAlert(getMessageServerTransaction($('#accionLibro').val(), 'Libro', 'o'), 'success');
                    listarLibro(jsonResponse.BEAN_PAGINATION);
                } else {
                    viewAlert(jsonResponse.MENSSAGE_SERVER, 'warning');
                }
            }

        },
        error: function () {
            $('#modalCargandoLibro').modal("hide");
            $("#ventanaModalManLibro").modal("hide");
            viewAlert('Error interno en el Servidor', 'error');
        }
    });
    return false;
}

function listarLibro(BEAN_PAGINATION) {
    /*PAGINATION*/
    var $pagination = $('#paginationLibro');
    $('#tbodyLibro').empty();
    $pagination.twbsPagination('destroy');
    $('#nameCrudLibro').html("[ " + BEAN_PAGINATION.COUNT_FILTER + " ] LIBROS");
    if (BEAN_PAGINATION.COUNT_FILTER > 0) {
        var fila;
        var atributos;
        $.each(BEAN_PAGINATION.List, function (index, value) {
            fila = "<tr ";
            atributos = "";
            atributos += "idlibro='" + value.idlibro + "' ";
            atributos += "nombre='" + value.nombre + "' ";
            atributos += "fecha-publicacion='" + value.fechaPublicacion + "' ";
            atributos += "genero='" + value.genero + "' ";
            atributos += "edicion='" + value.edicion + "' ";
            atributos += "idautor='" + value.autor.idautor + "' ";
            fila += atributos;
            fila += ">";
            fila += "<td class='align-middle'>" + value.nombre + "</td>";
            fila += "<td class='align-middle'>" + value.fechaPublicacion + "</td>";
            fila += "<td class='align-middle'>" + value.genero + "</td>";
            fila += "<td class='align-middle'>" + value.edicion + "</td>";
            fila += "<td class='align-middle'>" + value.autor.nombre + " " + value.autor.nombre2 + "</td>";
            fila += "<td class='text-center align-middle'><button class='btn btn-secondary btn-xs editar-Libro'><i class='fa fa-edit'></i></button></td>";
            fila += "<td class='text-center align-middle'><button class='btn btn-secondary btn-xs eliminar-Libro'><i class='fa fa-trash'></i></button></td>";
            fila += "</tr>";
            $('#tbodyLibro').append(fila);
        });
        var defaultOptions = getDefaultOptionsPagination();
        var options = getOptionsPagination(BEAN_PAGINATION.COUNT_FILTER, $('#sizePageLibro'),
                $('#numberPageLibro'), $('#accionLibro'), 'paginarLibro',
                $('#nameForm'), 'FrmLibro', $('#modalCargandoLibro'));
        $pagination.twbsPagination('destroy');
        $pagination.twbsPagination($.extend({}, defaultOptions, options));
        addEventosLibro();
        $('#txtNombreLibro').focus();
    } else {
        $pagination.twbsPagination('destroy');
        viewAlert('No se enconntraron resultados', 'warning');
    }
}

function addEventosLibro() {
    $('.editar-Libro').each(function () {
        $(this).click(function () {
            $('#txtIdLibroER').val($(this.parentElement.parentElement).attr('idlibro'));
            $('#txtNombreLibroER').val($(this.parentElement.parentElement).attr('nombre'));
            $('#txtFecha-PublicacionLibroER').val($(this.parentElement.parentElement).attr('fecha-publicacion'));
            $('#txtGeneroLibroER').val($(this.parentElement.parentElement).attr('genero'));
            $('#txtEdicionLibroER').val($(this.parentElement.parentElement).attr('edicion'));
            $('#cboAutorLibroER').val($(this.parentElement.parentElement).attr('idautor'));
            $('#accionLibro').val("updateLibro");
            $('#tituloModalManLibro').html("EDITAR LIBRO");
            $('#ventanaModalManLibro').modal("show");
            document.getElementsByTagName("body")[0].style.paddingRight = "0";
        });
    });
    $('.eliminar-Libro').each(function () {
        $(this).click(function () {
            $('#txtIdLibroER').val($(this.parentElement.parentElement).attr('idlibro'));
            viewAlertDelete("Libro");
            document.getElementsByTagName("body")[0].style.paddingRight = "0";
        });
    });
}

function addValicacionesCamposLibro() {
    $('#txtNombreLibroER').on('change', function () {
        $(this).val() === "" ? $("#validarNombreLibroER").fadeIn("slow") : $("#validarNombreLibroER").fadeOut();
    });
    $('txtFecha-PublicacionLibroER').on('change', function () {
        $(this).val() === "" ? $("#validarFecha-PublicacionLibroER").fadeIn("slow") : $("#validarFecha-PublicacionLibroER").fadeOut();
    });
    $('#txtGeneroLibroER').on('change', function () {
        $(this).val() === "" ? $("#validarGeneroLibroER").fadeIn("slow") : $("#validarGeneroLibroER").fadeOut();
    });
    $('#txtEdicionLibroER').on('change', function () {
        $(this).val() === "" ? $("#validarEdicionLibroER").fadeIn("slow") : $("#validarEdicionLibroER").fadeOut();
    });
    $('#cboAutorLibroER').on('change', function () {
        $(this).val() === "-1" ? $("#validarAutorLibroER").fadeIn("slow") : $("#validarAutorLibroER").fadeOut();
    });
}

function validarFormularioLibro() {
    if ($('#txtNombreLibroER').val() === "") {
        $("#validarNombreLibroER").fadeIn("slow");
        return false;
    } else {
        $("#validarNombreLibroER").fadeOut();
    }
    if ($('#txtFecha-PublicacionLibroER').val() === "") {
        $("#validarFecha-PublicacionLibroER").fadeIn("slow");
        return false;
    } else {
        $("#validarFecha-PublicacionLibroER").fadeOut();
    }
    if ($('#txtGeneroLibroER').val() === "") {
        $("#validarGeneroLibroER").fadeIn("slow");
        return false;
    } else {
        $("#validarGeneroLibroER").fadeOut();
    }
    if ($('#txtEdicionLibroER').val() === "") {
        $("#validarEdicionLibroER").fadeIn("slow");
        return false;
    } else {
        $("#validarEdicionLibroER").fadeOut();
    }
    if ($('#cboAutorLibroER').val() === "-1") {
        $("#validarAutorLibroER").fadeIn("slow");
        return false;
    } else {
        $("#validarAutorLibroER").fadeOut();
    }
    return true;
}

function addComboAutor() {
    var datosSerializadosCompletos;
    datosSerializadosCompletos = "txtNombreAutor=";
    datosSerializadosCompletos += "&numberPageAutor=";
    datosSerializadosCompletos += "&sizePageAutor=ALL";
    datosSerializadosCompletos += "&accion=paginarAutor";
    $('#cboAutorLibroER').empty();
    $.ajax({
        url: 'autor',
        type: 'POST',
        data: datosSerializadosCompletos,
        dataType: 'json',
        success: function (jsonResponse) {
            $('#cboAutorLibroER').append("<option value='-1'>Seleccione...</option>");
            $(jsonResponse.BEAN_PAGINATION.List).each(function (index, value) {
                $('#cboAutorLibroER').append("<option value='" + value.idautor + "'>" + value.nombre + " " + value.nombre2 + "</option>");
            });
        },
        error: function () {
            console.log("error interno al cargar autor");
        }
    });
    return false;
}

