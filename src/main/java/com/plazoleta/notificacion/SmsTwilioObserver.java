package com.plazoleta.notificacion;

import com.plazoleta.entity.Pedido;
import com.plazoleta.exception.ReglaNegocioException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

/**
 * Observador que manda el SMS real con el PIN usando Twilio.
 *
 * Solo existe si en la configuracion esta twilio.activo=true
 * (en application-local.properties, que no se sube a git).
 * Si esta en false, este observador no se crea y solo funciona ConsolaObserver.
 *
 * Twilio es un servicio por internet: le mandamos una peticion con el numero de
 * destino, el numero de origen y el texto, y el se encarga de enviar el SMS.
 */
@Component
@ConditionalOnProperty(name = "twilio.activo", havingValue = "true")
public class SmsTwilioObserver implements PedidoListoObserver {

    // Estos datos vienen de application-local.properties. Nunca se escriben en el codigo.
    @Value("${twilio.account-sid}")
    private String accountSid;

    @Value("${twilio.auth-token}")
    private String authToken;

    @Value("${twilio.numero-origen}")
    private String numeroOrigen;

    // Herramienta para llamar a otra API por internet (se crea en config/AppConfig)
    private final RestTemplate restTemplate;

    public SmsTwilioObserver(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public void pedidoListo(Pedido pedido, String pin) {
        // 1. Direccion de Twilio para enviar mensajes de nuestra cuenta
        String url = "https://api.twilio.com/2010-04-01/Accounts/" + accountSid + "/Messages.json";

        // 2. Twilio nos identifica con el usuario (SID) y la clave (token)
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(accountSid, authToken);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        // 3. Los datos del SMS: a quien, desde donde y que dice
        MultiValueMap<String, String> datos = new LinkedMultiValueMap<>();
        datos.add("To", pedido.getCliente().getCelular());
        datos.add("From", numeroOrigen);
        datos.add("Body", "Tu pedido #" + pedido.getId() + " esta listo. Tu PIN para recogerlo es: " + pin);

        // 4. Se envia. Si Twilio rechaza el mensaje, se lanza error y el pedido NO queda LISTO.
        try {
            restTemplate.postForEntity(url, new HttpEntity<>(datos, headers), String.class);
        } catch (RestClientException e) {
            throw new ReglaNegocioException("No se pudo enviar el SMS al cliente, el pedido no se marco como listo");
        }
    }
}
