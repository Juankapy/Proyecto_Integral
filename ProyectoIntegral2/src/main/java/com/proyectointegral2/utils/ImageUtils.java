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

            double widthRatio = (double) targetWidth / originalWidth;
            double heightRatio = (double) targetHeight / originalHeight;
            double scaleRatio = Math.min(widthRatio, heightRatio);

            int newWidth = (int) (originalWidth * scaleRatio);
            int newHeight = (int) (originalHeight * scaleRatio);

            if (newWidth < 1) newWidth = 1;
            if (newHeight < 1) newHeight = 1;

            java.awt.Image scaledAwtImage = originalImage.getScaledInstance(newWidth, newHeight, java.awt.Image.SCALE_SMOOTH);

            BufferedImage outputImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = outputImage.createGraphics();

            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2d.drawImage(scaledAwtImage, 0, 0, null);
            g2d.dispose();


            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(outputImage, "png", baos);
            InputStream resizedInputStream = new ByteArrayInputStream(baos.toByteArray());

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
                    inputStream.close();
                } catch (IOException e) {

                }
            }
        }
    }
}