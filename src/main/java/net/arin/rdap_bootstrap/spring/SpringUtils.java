/*
 * Copyright (C) 2020 American Registry for Internet Numbers (ARIN)
 *
 * Permission to use, copy, modify, and/or distribute this software for any
 * purpose with or without fee is hereby granted, provided that the above
 * copyright notice and this permission notice appear in all copies.
 *
 * THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
 * WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR
 * ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
 * WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
 * ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF OR
 * IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 */
package net.arin.rdap_bootstrap.spring;

import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;

public abstract class SpringUtils
{
    private static ApplicationContext ctx;

    public static BeanPostProcessor createInitBean( ApplicationContext ctx )
    {
        init( ctx );
        AppProperties.updateResolver( ctx.getEnvironment() );
        return new BeanPostProcessor()
        {
        };
    }

    public static void init( ApplicationContext ctx )
    {
        if ( ctx == null )
        {
            throw new IllegalArgumentException( "Application context cannot be null" );
        }
        else
        {
            SpringUtils.ctx = ctx;
        }
    }
}
