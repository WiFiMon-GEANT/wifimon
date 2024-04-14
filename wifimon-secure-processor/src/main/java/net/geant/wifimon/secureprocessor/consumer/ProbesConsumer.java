package net.geant.wifimon.secureprocessor.consumer;

import net.geant.wifimon.model.dto.ProbesMeasurement;
import net.geant.wifimon.secureprocessor.resource.AggregatorResource;
import org.springframework.stereotype.Service;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import jakarta.ws.rs.core.Response;

@Service
public class ProbesConsumer extends AggregatorResource {

    @RabbitListener(queues = {"${rabbitmq.probesQueue.name}"})
    public Response probesConsume(ProbesMeasurement probesMeasurement) {
        return handleProbesMeasurements(probesMeasurement);
    }
}
