package org.diekema.camelpoc.routes;

/**
 * Created by rdiekema on 1/15/16.
 */
public interface Endpoints {

    interface Direct {
        static final String QUEUE_TYPE = "direct:";
        static final String HASH_PROCESSOR = QUEUE_TYPE + "metadata";
        static final String TOKEN_SPLITTER_PROCESSOR = QUEUE_TYPE + "tokenSplitter";
        static final String ENTITY_REFERENCE_PROCESSOR = QUEUE_TYPE + "entityReference";
    }

    interface MessageQueue {
        static final String QUEUE_TYPE = "activemq:";
        static final String ENTITY_LOADER_PROCESSOR = QUEUE_TYPE + "entityLoader";
    }
}
