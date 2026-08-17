package com.mapuescuela.repository;

import com.mapuescuela.model.Pedido;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    @Query("SELECT p FROM Pedido p JOIN FETCH p.cliente ORDER BY p.fecha DESC")
    List<Pedido> findAllConCliente();

    @Query("""
            SELECT DISTINCT p FROM Pedido p
            JOIN FETCH p.cliente
            LEFT JOIN FETCH p.detalles d
            LEFT JOIN FETCH d.producto
            LEFT JOIN FETCH p.comprobante
            LEFT JOIN FETCH p.despacho
            WHERE p.id = :id
            """)
    Optional<Pedido> findDetalleById(@Param("id") Long id);
}
