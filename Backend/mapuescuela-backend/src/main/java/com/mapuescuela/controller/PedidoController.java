package com.mapuescuela.controller;

import com.mapuescuela.dto.ComprobanteDto;
import com.mapuescuela.dto.CrearPedidoRequest;
import com.mapuescuela.dto.PedidoCreadoResponse;
import com.mapuescuela.service.PedidoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/pedidos")
@RequiredArgsConstructor
public class PedidoController {

    private final PedidoService pedidoService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PedidoCreadoResponse crear(@Valid @RequestBody CrearPedidoRequest request) {
        return pedidoService.crear(request);
    }

    @PostMapping("/{id}/comprobante")
    @ResponseStatus(HttpStatus.CREATED)
    public ComprobanteDto subirComprobante(
            @PathVariable Long id,
            @RequestParam("archivo") MultipartFile archivo
    ) {
        return pedidoService.subirComprobante(id, archivo);
    }
}
