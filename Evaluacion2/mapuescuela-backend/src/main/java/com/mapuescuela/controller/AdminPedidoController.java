package com.mapuescuela.controller;

import com.mapuescuela.dto.PedidoDetalleDto;
import com.mapuescuela.dto.PedidoResumenDto;
import com.mapuescuela.dto.PrepararPedidoRequest;
import com.mapuescuela.service.AdminPedidoService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/pedidos")
@RequiredArgsConstructor
public class AdminPedidoController {

    private final AdminPedidoService adminPedidoService;

    @GetMapping
    public List<PedidoResumenDto> listar() {
        return adminPedidoService.listar();
    }

    @GetMapping("/{id}")
    public PedidoDetalleDto obtener(@PathVariable Long id) {
        return adminPedidoService.obtener(id);
    }

    @GetMapping("/{id}/comprobante")
    public ResponseEntity<Resource> descargarComprobante(@PathVariable Long id) {
        var archivo = adminPedidoService.archivoComprobante(id);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header("Content-Disposition", "attachment; filename=\"" + archivo.getFileName() + "\"")
                .body(new FileSystemResource(archivo));
    }

    @PostMapping("/{id}/aprobar-pago")
    public PedidoDetalleDto aprobarPago(@PathVariable Long id) {
        return adminPedidoService.aprobarPago(id);
    }

    @PostMapping("/{id}/rechazar-pago")
    public PedidoDetalleDto rechazarPago(@PathVariable Long id) {
        return adminPedidoService.rechazarPago(id);
    }

    @PostMapping("/{id}/preparar")
    public PedidoDetalleDto preparar(
            @PathVariable Long id,
            @RequestBody(required = false) PrepararPedidoRequest request
    ) {
        return adminPedidoService.preparar(id, request);
    }

    @PostMapping("/{id}/finalizar")
    public PedidoDetalleDto finalizar(@PathVariable Long id) {
        return adminPedidoService.finalizar(id);
    }
}
