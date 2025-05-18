package com.proyectointegral2.Controller;


import com.proyectointegral2.utils.UtilidadesVentana;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

import java.time.LocalDate;

public class FormularioPerroController {
    @FXML
    private Button BtnCancelar;

    @FXML
    private TextField TxtNombrePerro;

    @FXML
    private ImageView ImgPreviewPerro;

    @FXML
    private DatePicker DateFechaNacimiento;

    @FXML
    private ComboBox<?> CmbEstado;

    @FXML
    private ImageView imgIconoVolver;

    @FXML
    private TextField TxtRazaPerro;

    @FXML
    private Button btnSeleccionarImagen;

    @FXML
    private ComboBox<?> CmbSexo;

    @FXML
    private TextArea TxtAreaPatologia;

    @FXML
    private Button BtnAnadirPerro;

    @FXML
    private TextArea TxtAreaDescripcion;

@FXML
void Cancelar(ActionEvent event) {
    // Vuelve a la pantalla principal de la protectora
    String mainProtectoraFxml = "/com/proyectointegral2/Vista/MainProtectora.fxml";
    String mainProtectoraTitle = "Inicio de Sesión - Dogpuccino";
    UtilidadesVentana.cambiarEscena(mainProtectoraFxml, mainProtectoraTitle, false);
}

@FXML
void AnadirPerro(ActionEvent event) {
    String nombre = TxtNombrePerro.getText();
    String razaNombre = TxtRazaPerro.getText();
    String sexo = (CmbSexo.getValue() != null) ? CmbSexo.getValue().toString() : "";
    String estado = (CmbEstado.getValue() != null) ? CmbEstado.getValue().toString() : "";
    String descripcion = TxtAreaDescripcion.getText();
    String patologia = TxtAreaPatologia.getText();
    LocalDate fechaNacimiento = DateFechaNacimiento.getValue();

    // Validación básica
    if (nombre.isEmpty() || razaNombre.isEmpty() || sexo.isEmpty() || estado.isEmpty() || fechaNacimiento == null) {
        UtilidadesVentana.mostrarAlertaError("Campos obligatorios", "Por favor, completa todos los campos obligatorios.");
        return;
    }

    try {
        // Buscar o crear la raza
        //com.proyectointegral2.Model.Raza raza = new com.proyectointegral2.Model.Raza();
        //raza.setNombre(razaNombre);
        // Si tienes un RazaDao, aquí podrías buscar la raza por nombre y obtener el ID

        // Crear el objeto Perro
        com.proyectointegral2.Model.Perro perro = new com.proyectointegral2.Model.Perro();
        perro.setNombre(nombre);
        perro.setSexo(sexo);
        perro.setFechaNacimiento(fechaNacimiento);
        perro.setAdoptadoChar(estado.equalsIgnoreCase("Adoptado") ? "S" : "N");
        perro.setFoto(""); // Implementa la lógica para la foto si aplica
        //perro.setRaza(raza);
        //perro.setDescripcion(descripcion);
       // perro.setPatologia(patologia);
        // Si tienes el idProtectora, asígnalo aquí

        // Guardar en la base de datos
        com.proyectointegral2.dao.PerroDao perroDao = new com.proyectointegral2.dao.PerroDao();
        perroDao.insertarPerro(perro);

        UtilidadesVentana.mostrarAlertaInformacion("Perro añadido", "El perro ha sido añadido correctamente.");
        Cancelar(event);
    } catch (Exception e) {
        UtilidadesVentana.mostrarAlertaError("Error", "No se pudo añadir el perro: " + e.getMessage());
    }
}


    @FXML
    private void Volver(MouseEvent event) {
        String mainProtectoraFxml = "/com/proyectointegral2/Vista/MainProtectora.fxml";
        String mainProtectoraTitle = "Inicio de Sesión - Dogpuccino";
        UtilidadesVentana.cambiarEscena(mainProtectoraFxml, mainProtectoraTitle, false);

    }
}
