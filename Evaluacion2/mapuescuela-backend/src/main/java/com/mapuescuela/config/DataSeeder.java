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

        if (productoRepository.count() == 0) {
            Producto producto = new Producto();
            producto.setNombre("Libro usado");
            producto.setDescripcion("Libro en buen estado");
            producto.setCategoria("Libros");
            producto.setPrecio(5000);
            producto.setStock(1);
            producto.setEstado("Disponible");
            productoRepository.save(producto);
        }
    }
}
