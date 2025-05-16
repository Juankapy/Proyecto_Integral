package com.proyectointegral2.utils;

import javafx.scene.image.Image;
import java.io.InputStream;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ImageUtils {

    /**
     * Carga una imagen desde un InputStream, la redimensiona a las dimensiones objetivo
     * manteniendo la proporción (encajando dentro de targetWidth/targetHeight),
     * y devuelve un objeto javafx.scene.image.Image.
     *
     * @param inputStream   El flujo de entrada de la imagen original.
     * @param targetWidth   El ancho máximo deseado para la imagen redimensionada.
     * @param targetHeight  La altura máxima deseada para la imagen redimensionada.
     * @return Un objeto Image de JavaFX redimensionado, o null si ocurre un error.
     */
    public static Image loadAndResizeImage(InputStream inputStream, int targetWidth, int targetHeight) {
        if (inputStream == null || targetWidth <= 0 || targetHeight <= 0) {
            System.err.println("Error en loadAndResizeImage: Parámetros inválidos.");
            return null;
        }

        try {
            BufferedImage originalImage = ImageIO.read(inputStream);
            if (originalImage == null) {
                System.err.println("Error en loadAndResizeImage: No se pudo leer la imagen original desde el stream.");
                return null;
            }

            int originalWidth = originalImage.getWidth();
            int originalHeight = originalImage.getHeight();

            // Calcular nuevas dimensiones manteniendo la proporción
            double widthRatio = (double) targetWidth / originalWidth;
            double heightRatio = (double) targetHeight / originalHeight;
            double scaleRatio = Math.min(widthRatio, heightRatio); // Escalar para que quepa dentro de los límites

            int newWidth = (int) (originalWidth * scaleRatio);
            int newHeight = (int) (originalHeight * scaleRatio);

            // Asegurar dimensiones mínimas
            if (newWidth < 1) newWidth = 1;
            if (newHeight < 1) newHeight = 1;

            // Redimensionar usando java.awt.Image.getScaledInstance (calidad decente)
            java.awt.Image scaledAwtImage = originalImage.getScaledInstance(newWidth, newHeight, java.awt.Image.SCALE_SMOOTH);

            // Convertir de nuevo a BufferedImage para poder escribirlo a un ByteArrayOutputStream
            BufferedImage outputImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = outputImage.createGraphics();

            // Mejorar calidad de renderizado
            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2d.drawImage(scaledAwtImage, 0, 0, null);
            g2d.dispose();

            // Convertir BufferedImage a InputStream para crear el objeto Image de JavaFX
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(outputImage, "png", baos); // Usar PNG para mantener posible transparencia
            InputStream resizedInputStream = new ByteArrayInputStream(baos.toByteArray());

            // Crear la imagen de JavaFX. No necesitamos especificar targetWidth/Height aquí
            // porque la imagen ya está redimensionada a newWidth/newHeight.
            // Pasarle targetWidth/Height aquí podría causar un segundo escalado por JavaFX si son diferentes
            // a newWidth/newHeight, lo cual queremos evitar con esta estrategia.
            // Dejaremos que el ImageView con sus fitWidth/fitHeight haga el ajuste final si es necesario
            // por pequeñas diferencias debido al redondeo de int.
            return new Image(resizedInputStream);

        } catch (IOException e) {
            System.err.println("IOException al redimensionar imagen: " + e.getMessage());
            e.printStackTrace();
            return null;
        } catch (Exception e) {
            System.err.println("Excepción general al redimensionar imagen: " + e.getMessage());
            e.printStackTrace();
            return null;
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close(); // Cerrar el stream original
                } catch (IOException e) {
                    // Ignorar errores al cerrar
                }
            }
        }
    }
}