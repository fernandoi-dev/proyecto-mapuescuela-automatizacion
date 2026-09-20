package com.mapuescuela.config;

import com.mapuescuela.model.Cliente;
import com.mapuescuela.model.Producto;
import com.mapuescuela.repository.ClienteRepository;
import com.mapuescuela.repository.ProductoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final ClienteRepository clienteRepository;
    private final ProductoRepository productoRepository;

    @Override
    public void run(String... args) {
        if (clienteRepository.count() == 0) {
            Cliente cliente = new Cliente();
            cliente.setNombre("Juan Perez");
            cliente.setCorreo("juan.perez@mail.com");
            cliente.setTelefono("912345678");
            cliente.setDireccion("Av. Principal 123");
            clienteRepository.save(cliente);
        }

        asegurarProducto("Libro usado", "Libro en buen estado", "Libros", 5000, 10);
        asegurarProducto("Uniforme escolar", "Prenda usada en buen estado", "Uniformes", 8000, 5);
        asegurarProducto("Mochila", "Mochila reutilizada lista para el colegio", "Utiles", 6000, 8);
    }

    private void asegurarProducto(String nombre, String descripcion, String categoria, int precio, int stock) {
        boolean existe = productoRepository.findAll().stream()
                .anyMatch(producto -> nombre.equalsIgnoreCase(producto.getNombre()));
        if (existe) {
            return;
        }
        Producto producto = new Producto();
        producto.setNombre(nombre);
        producto.setDescripcion(descripcion);
        producto.setCategoria(categoria);
        producto.setPrecio(precio);
        producto.setStock(stock);
        producto.setEstado("Disponible");
        productoRepository.save(producto);
    }
}
