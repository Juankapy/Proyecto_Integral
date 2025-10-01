package com.proyectointegral2.utils;

import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

public class UtilidadesExcepciones {

    public static void mostrarError(Exception ex, String titulo, String cabecera) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo != null ? titulo : "Error");
        alert.setHeaderText(cabecera != null ? cabecera : "Ocurrió un error inesperado");
        alert.setContentText(ex.getMessage());

        String stackTrace = obtenerStackTraceComoTexto(ex);
        TextArea textArea = crearTextAreaExpandible(stackTrace);

        GridPane expContent = new GridPane();
        expContent.setMaxWidth(Double.MAX_VALUE);
        expContent.add(textArea, 0, 0);

        alert.getDialogPane().setExpandableContent(expContent);
        alert.showAndWait();
    }

    // 🟠 Mostrar advertencia
    public static void mostrarAdvertencia(String mensaje, String titulo, String cabecera) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(titulo != null ? titulo : "Advertencia");
        alert.setHeaderText(cabecera != null ? cabecera : null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    // 🔵 Mostrar información
    public static void mostrarInformacion(String mensaje, String titulo, String cabecera) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo != null ? titulo : "Información");
        alert.setHeaderText(cabecera != null ? cabecera : null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    // 🔧 Utilidades privadas
    private static String obtenerStackTraceComoTexto(Exception ex) {
        StringBuilder sb = new StringBuilder();
        for (StackTraceElement element : ex.getStackTrace()) {
            sb.append(element.toString()).append("\n");
        }
        return sb.toString();
    }

    private static TextArea crearTextAreaExpandible(String texto) {
        TextArea textArea = new TextArea(texto);
        textArea.setEditable(false);
        textArea.setWrapText(true);
        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);
        GridPane.setVgrow(textArea, Priority.ALWAYS);
        GridPane.setHgrow(textArea, Priority.ALWAYS);
        return textArea;
    }
}
