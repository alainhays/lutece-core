/*
 * Copyright (c) 2002-2008, Mairie de Paris
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice
 *     and the following disclaimer.
 *
 *  2. Redistributions in binary form must reproduce the above copyright notice
 *     and the following disclaimer in the documentation and/or other materials
 *     provided with the distribution.
 *
 *  3. Neither the name of 'Mairie de Paris' nor 'Lutece' nor the names of its
 *     contributors may be used to endorse or promote products derived from
 *     this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * License 1.0
 */
package fr.paris.lutece.portal.service.filter;

import fr.paris.lutece.portal.service.util.AppLogService;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;


/**
 * MainFilter
 */
public class MainFilter implements Filter
{
    /**
     * {@inheritDoc}
     */
    public void init( FilterConfig config ) throws ServletException
    {
        for ( LuteceFilter filter : FilterService.getInstance(  ).getFilters(  ) )
        {
            if ( filter.getPlugin(  ).isInstalled(  ) )
            {
                filter.getFilter(  ).init( config );
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public void doFilter( ServletRequest requestServlet, ServletResponse response, FilterChain chain )
        throws IOException, ServletException
    {
        HttpServletRequest request = (HttpServletRequest) requestServlet;
        AppLogService.info( "MainFilter - doFilter : " + request.getContextPath(  ) );
        AppLogService.info( "MainFilter - doFilter : " + request.getRequestURI(  ) );
        AppLogService.info( "MainFilter - doFilter : " + request.getServletPath(  ) );
        AppLogService.info( "MainFilter - doFilter : " + request.getPathInfo(  ) );
        AppLogService.info( "MainFilter - doFilter : " + request.getPathTranslated(  ) );

        for ( LuteceFilter filter : FilterService.getInstance(  ).getFilters(  ) )
        {
            // Checks mapping and plugin status
            if ( matchMapping( filter, request ) && filter.getPlugin(  ).isInstalled(  ) )
            {
                filter.getFilter(  ).doFilter( request, response, chain );
            }
        }

        chain.doFilter( request, response );
    }

    /**
     * {@inheritDoc}
     */
    public void destroy(  )
    {
        for ( LuteceFilter filter : FilterService.getInstance(  ).getFilters(  ) )
        {
            // Checks mapping and plugin status
            if ( filter.getPlugin(  ).isInstalled(  ) )
            {
                filter.getFilter(  ).destroy(  );
            }
        }
    }

    private boolean matchMapping( LuteceFilter filter, HttpServletRequest request )
    {
        String strPath = request.getContextPath(  );

        return strPath.startsWith( filter.getMapping(  ) );
    }
}
