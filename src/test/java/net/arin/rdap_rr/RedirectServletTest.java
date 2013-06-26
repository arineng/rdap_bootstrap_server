/*
 * Copyright (C) 2013 American Registry for Internet Numbers (ARIN)
 */
package net.arin.rdap_rr;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;

/**
 * @version $Rev$, $Date$
 */
public class RedirectServletTest
{
    @Test
    public void testMakeAutNumInt() throws Exception
    {
        RedirectServlet servlet = new RedirectServlet();

        assertEquals( 10, servlet.makeAutNumLong( "/autnum/10" ) );
        assertEquals( 42222, servlet.makeAutNumLong( "/autnum/42222" ) );
    }
}
