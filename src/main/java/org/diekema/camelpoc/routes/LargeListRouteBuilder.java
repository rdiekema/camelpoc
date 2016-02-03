package org.diekema.camelpoc.routes;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rdiekema on 1/15/16.
 */

@Component
public class LargeListRouteBuilder extends RouteBuilder {

    List<String> locations = new ArrayList<>();
    {
        locations.add("target/in");
    }

    @Override
    public void configure() throws Exception {

        // File processing pipeline
        from("file:target/in").pipeline(Endpoints.Direct.HASH_PROCESSOR, Endpoints.Direct.ENTITY_REFERENCE_PROCESSOR, Endpoints.Direct.TOKEN_SPLITTER_PROCESSOR);
        from(Endpoints.Direct.HASH_PROCESSOR).process("sha256HashProcessor");
        from(Endpoints.Direct.ENTITY_REFERENCE_PROCESSOR).process("xmlReferenceProcessor");
        from(Endpoints.Direct.TOKEN_SPLITTER_PROCESSOR).process("pfaTokenSplitter");


        // Entity Processing pipeline
        from(Endpoints.MessageQueue.ENTITY_LOADER_PROCESSOR).throttle(100).threads(2).process("entityLoaderProcessor");
    }


}
