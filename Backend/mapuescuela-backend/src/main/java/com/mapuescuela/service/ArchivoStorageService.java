package com.mapuescuela.service;

import com.mapuescuela.exception.ReglaNegocioException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ArchivoStorageService {

    private final Path directorio;

    public ArchivoStorageService(@Value("${app.upload.dir}") String uploadDir) {
        this.directorio = Paths.get(uploadDir).toAbsolutePath().normalize();
    }

    public String guardar(Long pedidoId, MultipartFile archivo) {
        if (archivo == null || archivo.isEmpty()) {
            throw new ReglaNegocioException("Debe adjuntar un archivo");
        }

        try {
            Files.createDirectories(directorio);
            String original = archivo.getOriginalFilename() == null ? "comprobante" : archivo.getOriginalFilename();
            String nombre = pedidoId + "_" + UUID.randomUUID() + "_" + original.replaceAll("[^a-zA-Z0-9._-]", "_");
            Path destino = directorio.resolve(nombre);
            archivo.transferTo(destino);
            return nombre;
        } catch (IOException e) {
            throw new ReglaNegocioException("No se pudo guardar el comprobante");
        }
    }

    public Path resolver(String nombreArchivo) {
        return directorio.resolve(nombreArchivo);
    }
}
