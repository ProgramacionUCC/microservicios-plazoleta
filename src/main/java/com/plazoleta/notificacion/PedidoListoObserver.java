package com.plazoleta.notificacion;

import com.plazoleta.entity.Pedido;

/**
 * PATRON OBSERVER (HU-14).
 *
 * Un "observador" es una clase que queda pendiente de que pase algo.
 * Aqui lo que pasa es: "un pedido quedo LISTO".
 * PedidoServiceImpl no sabe que se hace con ese aviso (mandar SMS, escribir en consola...);
 * solo avisa a todos los observadores y cada uno hace lo suyo.
 *
 * Para agregar otra reaccion mas, se crea otra clase que implemente esta interfaz
 * (con @Component) y no hay que tocar PedidoServiceImpl.
 */
public interface PedidoListoObserver {

    // Se llama cuando el pedido va a quedar LISTO. El pin es el codigo que el cliente
    // debera decir al recoger su pedido (HU-15).
    void pedidoListo(Pedido pedido, String pin);
}
