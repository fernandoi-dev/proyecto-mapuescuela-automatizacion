package com.mapuescuela.service;

import com.mapuescuela.dto.ClienteRequest;
import com.mapuescuela.exception.RecursoNoEncontradoException;
import com.mapuescuela.model.Cliente;
import com.mapuescuela.repository.ClienteRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ClienteService {

    private final ClienteRepository clienteRepository;

    public List<Cliente> listar() {
        return clienteRepository.findAll();
    }

    public Cliente obtener(Long id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Cliente no encontrado: " + id));
    }

    @Transactional
    public Cliente crear(ClienteRequest request) {
        return clienteRepository.findFirstByCorreo(request.getCorreo())
                .map(existente -> {
                    existente.setNombre(request.getNombre());
                    if (request.getTelefono() != null) {
                        existente.setTelefono(request.getTelefono());
                    }
                    if (request.getDireccion() != null) {
                        existente.setDireccion(request.getDireccion());
                    }
                    return clienteRepository.save(existente);
                })
                .orElseGet(() -> {
                    Cliente cliente = new Cliente();
                    cliente.setNombre(request.getNombre());
                    cliente.setCorreo(request.getCorreo());
                    cliente.setTelefono(request.getTelefono());
                    cliente.setDireccion(request.getDireccion());
                    return clienteRepository.save(cliente);
                });
    }
}
