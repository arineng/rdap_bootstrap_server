package net.arin.rdap_bootstrap;

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
            throw new IllegalArgumentException( "application context cannot be null" );
        }
        else
        {
            SpringUtils.ctx = ctx;
        }
    }
}
