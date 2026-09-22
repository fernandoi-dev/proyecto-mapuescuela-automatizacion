package com.mapuescuela.controller;

import com.mapuescuela.dto.ComprobanteDto;
import com.mapuescuela.dto.CrearPedidoRequest;
import com.mapuescuela.dto.PedidoCreadoResponse;
import com.mapuescuela.service.PedidoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.mapuescuela.repository.PedidoRepository;
import org.springframework.jdbc.core.JdbcTemplate;

@RestController
@RequestMapping("/pedidos")
public class PedidoController {

    @Autowired
    private PedidoService pedidoService;

    @Autowired
    private PedidoRepository pedidoRepository;

    
    @Autowired
    private JdbcTemplate jdbcTemplate; 

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

    @GetMapping("/admin/lista")
    @CrossOrigin(origins = "*")
    public ResponseEntity<?> listarPedidosAdmin() {
        java.util.List<java.util.Map<String, Object>> listaLimpia = new java.util.ArrayList<>();
        
        for (var pedido : pedidoRepository.findAll()) {
            java.util.Map<String, Object> datos = new java.util.HashMap<>();
            datos.put("id", pedido.getId());
            datos.put("total", pedido.getTotal());
            datos.put("estado", pedido.getEstado());
            listaLimpia.add(datos);
        }
        
        return ResponseEntity.ok(listaLimpia);
    }

    @PutMapping("/admin/estado/{id}")
    @CrossOrigin(origins = "*")
    public ResponseEntity<?> actualizarEstadoAdmin(
            @PathVariable Long id,
            @RequestBody java.util.Map<String, String> body) {
        
        
        jdbcTemplate.update("UPDATE pedido SET estado = ? WHERE id = ?", body.get("estado"), id);
        
        return ResponseEntity.ok().build();
    }
}