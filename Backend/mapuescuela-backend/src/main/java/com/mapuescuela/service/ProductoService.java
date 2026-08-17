package com.mapuescuela.service;

import com.mapuescuela.dto.ProductoRequest;
import com.mapuescuela.exception.RecursoNoEncontradoException;
import com.mapuescuela.model.Producto;
import com.mapuescuela.repository.ProductoRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductoService {

    private final ProductoRepository productoRepository;

    public List<Producto> listar() {
        return productoRepository.findAll();
    }

    public Producto obtener(Long id) {
        return productoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Producto no encontrado: " + id));
    }

    @Transactional
    public Producto crear(ProductoRequest request) {
        Producto producto = new Producto();
        copiar(request, producto);
        return productoRepository.save(producto);
    }

    @Transactional
    public Producto actualizar(Long id, ProductoRequest request) {
        Producto producto = obtener(id);
        copiar(request, producto);
        return productoRepository.save(producto);
    }

    @Transactional
    public void retirar(Long id) {
        Producto producto = obtener(id);
        producto.setEstado("Retirado");
        producto.setStock(0);
        productoRepository.save(producto);
    }

    private void copiar(ProductoRequest request, Producto producto) {
        producto.setNombre(request.getNombre());
        producto.setDescripcion(request.getDescripcion());
        producto.setCategoria(request.getCategoria());
        producto.setPrecio(request.getPrecio());
        producto.setFotografia(request.getFotografia());
        producto.setStock(request.getStock());
        producto.setEstado(request.getEstado());
    }
}
