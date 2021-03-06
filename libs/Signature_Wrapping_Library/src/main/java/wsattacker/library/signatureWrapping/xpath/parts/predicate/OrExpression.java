/**
 * WS-Attacker - A Modular Web Services Penetration Testing Framework Copyright
 * (C) 2013 Christian Mainka
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
package wsattacker.library.signatureWrapping.xpath.parts.predicate;

import java.util.*;
import wsattacker.library.signatureWrapping.xpath.interfaces.ExpressionInterface;
import wsattacker.library.signatureWrapping.xpath.interfaces.XPathPartInterface;
import wsattacker.library.signatureWrapping.xpath.parts.predicate.factory.AndExpressionFactory;
import wsattacker.library.signatureWrapping.xpath.parts.predicate.factory.AndExpressionFactoryInterface;
import wsattacker.library.signatureWrapping.xpath.parts.util.XPathInspectorTools;

/**
 * An OrExpression is mainly a container for AndExpressions.
 */
public class OrExpression
    implements XPathPartInterface, ExpressionInterface
{

    /**
     * Factory for creating special andExpressions. Global access.. can be changed to another implementation.
     */
    public static AndExpressionFactoryInterface andFactory = new AndExpressionFactory();

    private final String expression;

    private final List<AndExpression> andExpressions;

    public OrExpression( String expression )
    {
        this.expression = expression;
        this.andExpressions = new ArrayList<AndExpression>();
        eval();
    }

    public List<AndExpression> getAndExpressions()
    {
        return andExpressions;
    }

    @Override
    public String getExpression()
    {
        return expression;
    }

    @Override
    public String toString()
    {
        return expression;
    }

    @Override
    public String toFullString()
    {
        return XPathInspectorTools.implodeList( andExpressions, " " );
    }

    @Override
    public boolean equals( Object o )
    {
        if ( o instanceof String )
        {
            return equals( new OrExpression( (String) o ) );
        }
        if ( o instanceof OrExpression )
        {
            return expression.equals( ( (ExpressionInterface) o ).getExpression() );
        }
        return false;
    }

    @Override
    public int hashCode()
    {
        int hash = 7;
        hash = 47 * hash + ( this.expression != null ? this.expression.hashCode() : 0 );
        return hash;
    }

    private void eval()
    {
        int prevAnd = 0;
        int nextAnd = XPathInspectorTools.nextString( expression, " and ", prevAnd );
        String andString;
        while ( nextAnd > 0 )
        {
            andString = expression.substring( prevAnd, nextAnd ).trim();
            if ( !andString.isEmpty() )
            {
                andExpressions.add( andFactory.createAndExpression( andString ) );
            }
            prevAnd = nextAnd + 5; // = nextOr + " and ".length()
            nextAnd = XPathInspectorTools.nextString( expression, " and ", prevAnd );
        }
        andString = expression.substring( prevAnd ).trim();
        if ( !andString.isEmpty() )
        {
            andExpressions.add( andFactory.createAndExpression( andString ) );
        }
    }
}
