package com.plazoleta.notificacion;

import com.plazoleta.entity.Pedido;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Observador que solo escribe el aviso en la consola.
 * Sirve para ver que el flujo funciona sin gastar SMS.
 */
@Slf4j
@Component
public class ConsolaObserver implements PedidoListoObserver {

    @Override
    public void pedidoListo(Pedido pedido, String pin) {
        log.info("Pedido {} listo. Aviso para el cliente {} (celular {}). PIN: {}",
                pedido.getId(),
                pedido.getCliente().getCorreo(),
                pedido.getCliente().getCelular(),
                pin);
    }
}
