package org.emu.docmanagment.integration.config;


import org.emu.common.events.DocumentEvent;
import org.emu.common.events.NotificationEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.util.function.Supplier;

@Configuration
public class DocumentConfig {



    @Bean
    public Sinks.Many<DocumentEvent> documentSink(){
        return Sinks.many().unicast().onBackpressureBuffer();
    }

    @Bean
    public Supplier<Flux<DocumentEvent>> documentSupplier(Sinks.Many<DocumentEvent> sink){
        return sink::asFlux;
    }

}
