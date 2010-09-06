package com.github.tlb.balancer;

import com.github.tlb.TlbConstants;
import com.github.tlb.TlbSuiteFile;
import com.github.tlb.TlbSuiteFileImpl;
import com.github.tlb.orderer.TestOrderer;
import com.github.tlb.splitter.TestSplitterCriteria;
import org.restlet.Context;
import org.restlet.data.MediaType;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.resource.*;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @understands balancer process(as in a regular user process with PID)
 */
public class ControlResource extends Resource {
    private static final Logger logger = Logger.getLogger(ControlResource.class.getName());

    public static enum Query {
        status {
            @Override
            Representation act() {
                return new StringRepresentation("RUNNING");
            }
        },
        suicide {
            @Override
            Representation act() {
                System.exit(0);
                return null;
            }
        };
        abstract Representation act();
    }

    public ControlResource(Context context, Request request, Response response) {
        super(context, request, response);
        getVariants().add(new Variant(MediaType.TEXT_PLAIN));
    }

    @Override
    public Representation represent(Variant variant) throws ResourceException {
        return Query.valueOf(query()).act();
    }

    private String query() {
        return (String) getRequest().getAttributes().get(TlbConstants.Balancer.QUERY);
    }
}