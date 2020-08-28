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
package net.arin.rdap_bootstrap.service;

import javax.servlet.Servlet;

import net.arin.rdap_bootstrap.SpringUtils;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;

@PropertySource( { "classpath:rdap_bootstrap.properties" } )
@SpringBootApplication
public class RdapBootstrapApp
{
    public static void main( String[] args )
    {
        SpringApplication.run( RdapBootstrapApp.class, args );
    }

    @Bean
    public static BeanPostProcessor postProcessor( ConfigurableApplicationContext ctx )
    {
        return SpringUtils.createInitBean( ctx );
    }

    @Bean
    public static ServletRegistrationBean<Servlet> rdapBootstrapRedirectServlet() throws Exception
    {
        ServletRegistrationBean<Servlet> registrationBean = new ServletRegistrationBean<>();
        registrationBean.setServlet( ( Servlet ) Class.forName( "net.arin.rdap_bootstrap.service.RedirectServlet" ).getConstructor().newInstance() );
        registrationBean.addUrlMappings( "/rdapbootstrap/*" );
        registrationBean.setLoadOnStartup( 1 );
        return registrationBean;
    }
}
