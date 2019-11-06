package org.apache.maven.artifact.versioning;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import java.util.Locale;

import junit.framework.TestCase;
import java.util.*;

/**
 * Test ComparableVersion.
 *
 * @author <a href="mailto:hboutemy@apache.org">Hervé Boutemy</a>
 */
@SuppressWarnings( "unchecked" )
public class ComparableVersionTest
    extends TestCase
{
    private ComparableVersion newComparable( String version )
    {
        ComparableVersion ret = new ComparableVersion( version );
        String canonical = ret.getCanonical();
        String parsedCanonical = new ComparableVersion( canonical ).getCanonical();

        System.out.println( "canonical( " + version + " ) = " + canonical );
        assertEquals( "canonical( " + version + " ) = " + canonical + " -> canonical: " + parsedCanonical, canonical,
                      parsedCanonical );

        return ret;
    }

    private static final String[] VERSIONS_QUALIFIER =
        { "1-alpha2snapshot", "1-alpha2", "1-alpha-123", "1-beta-2", "1-beta123", "1-m2", "1-m11", "1-rc", "1-cr2",
            "1-rc123", "1-SNAPSHOT", "1", "1-sp", "1-sp2", "1-sp123", "1-abc", "1-def", "1-pom-1", "1-1-snapshot",
            "1-1", "1-2", "1-123" };

    private static final String[] VERSIONS_NUMBER =
        { "2.0", "2-1", "2.0.a", "2.0.0.a", "2.0.2", "2.0.123", "2.1.0", "2.1-a", "2.1b", "2.1-c", "2.1-1", "2.1.0.1",
            "2.2", "2.123", "11.a2", "11.a11", "11.b2", "11.b11", "11.m2", "11.m11", "11", "11.a", "11b", "11c", "11m" };

    private void checkVersionsOrder( String[] versions )
    {
        Comparable[] c = new Comparable[versions.length];
        for ( int i = 0; i < versions.length; i++ )
        {
            c[i] = newComparable( versions[i] );
        }

        for ( int i = 1; i < versions.length; i++ )
        {
            Comparable low = c[i - 1];
            for ( int j = i; j < versions.length; j++ )
            {
                Comparable high = c[j];
                assertTrue( "expected " + low + " < " + high, low.compareTo( high ) < 0 );
                assertTrue( "expected " + high + " > " + low, high.compareTo( low ) > 0 );
            }
        }
    }

    private void checkVersionsEqual( String v1, String v2 )
    {
        Comparable c1 = newComparable( v1 );
        Comparable c2 = newComparable( v2 );
        assertTrue( "expected " + v1 + " == " + v2, c1.compareTo( c2 ) == 0 );
        assertTrue( "expected " + v2 + " == " + v1, c2.compareTo( c1 ) == 0 );
        assertTrue( "expected same hashcode for " + v1 + " and " + v2, c1.hashCode() == c2.hashCode() );
        assertTrue( "expected " + v1 + ".equals( " + v2 + " )", c1.equals( c2 ) );
        assertTrue( "expected " + v2 + ".equals( " + v1 + " )", c2.equals( c1 ) );
    }

    private void checkVersionsArrayEqual( String[] array )
    {
        // compare against each other (including itself)
        for ( int i = 0; i < array.length; ++i )
            for ( int j = i; j < array.length; ++j )
                checkVersionsEqual( array[i], array[j] );
    }

    private void checkVersionsOrder( String v1, String v2 )
    {
        Comparable c1 = newComparable( v1 );
        Comparable c2 = newComparable( v2 );
        assertTrue( "expected " + v1 + " < " + v2, c1.compareTo( c2 ) < 0 );
        assertTrue( "expected " + v2 + " > " + v1, c2.compareTo( c1 ) > 0 );
    }

    public void testVersionsQualifier()
    {
        checkVersionsOrder( VERSIONS_QUALIFIER );
    }

    public void testVersionsNumber()
    {
        checkVersionsOrder( VERSIONS_NUMBER );
    }

    public void testVersionsEqual()
    {
        newComparable( "1.0-alpha" );
        checkVersionsEqual( "1", "1" );
        checkVersionsEqual( "1", "1.0" );
        checkVersionsEqual( "1", "1.0.0" );
        checkVersionsEqual( "1.0", "1.0.0" );
        checkVersionsEqual( "1", "1-0" );
        checkVersionsEqual( "1", "1.0-0" );
        checkVersionsEqual( "1.0", "1.0-0" );
        // no separator between number and character
        checkVersionsEqual( "1a", "1-a" );
        checkVersionsEqual( "1a", "1.0-a" );
        checkVersionsEqual( "1a", "1.0.0-a" );
        checkVersionsEqual( "1.0a", "1-a" );
        checkVersionsEqual( "1.0.0a", "1-a" );
        checkVersionsEqual( "1x", "1-x" );
        checkVersionsEqual( "1x", "1.0-x" );
        checkVersionsEqual( "1x", "1.0.0-x" );
        checkVersionsEqual( "1.0x", "1-x" );
        checkVersionsEqual( "1.0.0x", "1-x" );

        // aliases
        checkVersionsEqual( "1ga", "1" );
        checkVersionsEqual( "1release", "1" );
        checkVersionsEqual( "1final", "1" );
        checkVersionsEqual( "1cr", "1rc" );

        // special "aliases" a, b and m for alpha, beta and milestone
        checkVersionsEqual( "1a1", "1-alpha-1" );
        checkVersionsEqual( "1b2", "1-beta-2" );
        checkVersionsEqual( "1m3", "1-milestone-3" );

        // case insensitive
        checkVersionsEqual( "1X", "1x" );
        checkVersionsEqual( "1A", "1a" );
        checkVersionsEqual( "1B", "1b" );
        checkVersionsEqual( "1M", "1m" );
        checkVersionsEqual( "1Ga", "1" );
        checkVersionsEqual( "1GA", "1" );
        checkVersionsEqual( "1RELEASE", "1" );
        checkVersionsEqual( "1release", "1" );
        checkVersionsEqual( "1RELeaSE", "1" );
        checkVersionsEqual( "1Final", "1" );
        checkVersionsEqual( "1FinaL", "1" );
        checkVersionsEqual( "1FINAL", "1" );
        checkVersionsEqual( "1Cr", "1Rc" );
        checkVersionsEqual( "1cR", "1rC" );
        checkVersionsEqual( "1m3", "1Milestone3" );
        checkVersionsEqual( "1m3", "1MileStone3" );
        checkVersionsEqual( "1m3", "1MILESTONE3" );
    }

    public void testVersionComparing()
    {
        checkVersionsOrder( "1", "2" );
        checkVersionsOrder( "1.5", "2" );
        checkVersionsOrder( "1", "2.5" );
        checkVersionsOrder( "1.0", "1.1" );
        checkVersionsOrder( "1.1", "1.2" );
        checkVersionsOrder( "1.0.0", "1.1" );
        checkVersionsOrder( "1.0.1", "1.1" );
        checkVersionsOrder( "1.1", "1.2.0" );

        checkVersionsOrder( "1.0-alpha-1", "1.0" );
        checkVersionsOrder( "1.0-alpha-1", "1.0-alpha-2" );
        checkVersionsOrder( "1.0-alpha-1", "1.0-beta-1" );

        checkVersionsOrder( "1.0-beta-1", "1.0-SNAPSHOT" );
        checkVersionsOrder( "1.0-SNAPSHOT", "1.0" );
        checkVersionsOrder( "1.0-alpha-1-SNAPSHOT", "1.0-alpha-1" );

        checkVersionsOrder( "1.0", "1.0-1" );
        checkVersionsOrder( "1.0-1", "1.0-2" );
        checkVersionsOrder( "1.0.0", "1.0-1" );

        checkVersionsOrder( "2.0-1", "2.0.1" );
        checkVersionsOrder( "2.0.1-klm", "2.0.1-lmn" );
        checkVersionsOrder( "2.0.1", "2.0.1-xyz" );

        checkVersionsOrder( "2.0.1", "2.0.1-123" );
        checkVersionsOrder( "2.0.1-xyz", "2.0.1-123" );
    }

    public void testPeriods()
    {
        String[] test = {
            "1.0.0-alpha", "1.0.0-a",
            "1.0.0-alpha1", "1.0.0-alpha2", "1.0.0-alpha10",
            "1.0.0-alpha-1", "1.0.0-alpha-2", "1.0.0-alpha-10",
            "1.0.0-alpha.1", "1.0.0-alpha.2", "1.0.0-alpha.10",
            "1.0.0-a-1", "1.0.0-a-2", "1.0.0-a-10",
            "1.0.0-a.1", "1.0.0-a.2", "1.0.0-a.10",
            "1.0.0-a1", "1.0.0-a2", "1.0.0-a10",
            "1.0.0-beta", "1.0.0-b",
            "1.0.0-beta1", "1.0.0-beta2", "1.0.0-beta10",
            "1.0.0-beta-1", "1.0.0-beta-2", "1.0.0-beta-10",
            "1.0.0-beta.1", "1.0.0-beta.2", "1.0.0-beta.10",
            "1.0.0-b-1", "1.0.0-b-2", "1.0.0-b-10",
            "1.0.0-b.1", "1.0.0-b.2", "1.0.0-b.10",
            "1.0.0-b1", "1.0.0-b2", "1.0.0-b10",
            "1.0.0-milestone", "1.0.0-m",
            "1.0.0-milestone-1", "1.0.0-milestone-2", "1.0.0-milestone-10",
            "1.0.0-milestone.1", "1.0.0-milestone.2", "1.0.0-milestone.10",
            "1.0.0-m1", "1.0.0-m2", "1.0.0-m10",
            "1.0.0-m-1", "1.0.0-m-2", "1.0.0-m-10",
            "1.0.0-m.1", "1.0.0-m.2", "1.0.0-m.10",
            "1.0.0-rc1", "1.0.0-rc2", "1.0.0-rc10",
            "1.0.0-rc-1", "1.0.0-rc-2", "1.0.0-rc-10",
            "1.0.0-rc.1", "1.0.0-rc.2", "1.0.0-rc.10",
            "1.0.0-snapshot",
            "1.0.0-snapshot1", "1.0.0-snapshot2", "1.0.0-snapshot10",
            "1.0.0-snapshot-1", "1.0.0-snapshot-2", "1.0.0-snapshot-10",
            "1.0.0-snapshot.1", "1.0.0-snapshot.2", "1.0.0-snapshot.10",
            "1.0.0",
            "1.0.0-ga", "1.0.0-final",
            "1.0.0-ga1", "1.0.0-ga2", "1.0.0-ga10",
            "1.0.0-ga-1", "1.0.0-ga-2", "1.0.0-ga-10",
            "1.0.0-ga.1", "1.0.0-ga.2", "1.0.0-ga.10",
            "1.0.0-final-1", "1.0.0-final-2", "1.0.0-final-10",
            "1.0.0-final.1", "1.0.0-final.2", "1.0.0-final.10",
            "1.0.0-dev", "1.0.0-preview",
            "1.0.0-dev1", "1.0.0-dev2", "1.0.0-dev10",
            "1.0.0-dev-1", "1.0.0-dev-2", "1.0.0-dev-10",
            "1.0.0-dev.1", "1.0.0-dev.2", "1.0.0-dev.10",
            "1.0.0-dev-20191106.1", "1.0.0-20191106.1",
            "1.0.0-preview1", "1.0.0-preview2", "1.0.0-preview10",
            "1.0.0-preview-1", "1.0.0-preview-2", "1.0.0-preview-10",
            "1.0.0-preview.1", "1.0.0-preview.2", "1.0.0-preview.10",
            "1.0.0-1", "1.0.0-2", "1.0.0-10",
            "1.0.1",
            "1.1.0-beta.1", "1.1.0", "1.1.0-dev-1",
            "2.0.0-beta.1", "2.0.0", "2.0.0-dev-1"
        };

        ArrayList<ComparableVersion> comparables = new ArrayList<ComparableVersion>();
        System.out.println("Before Sorting...");
        for(int i = 0; i < test.length; i++)
        {
            ComparableVersion v = new ComparableVersion( test[i] );
            comparables.add(v);
            System.out.println(v + " - canonical(" + v.getCanonical() + ")");
        }
        System.out.println("----------------");

        Collections.sort(comparables);

        System.out.println("After Sorting...");
        System.out.println("----------------");
        for(ComparableVersion v : comparables) {
            System.out.println(v + " - canonical(" + v.getCanonical() + ")");
        }
        System.out.println("----------------");

        /* Output from running above code:
After Sorting...
----------------
1.0.0-alpha - canonical(1-alpha)
1.0.0-alpha1 - canonical(1-alpha-1)
1.0.0-alpha-1 - canonical(1-alpha-1)
1.0.0-a1 - canonical(1-alpha-1)
1.0.0-alpha2 - canonical(1-alpha-2)
1.0.0-alpha-2 - canonical(1-alpha-2)
1.0.0-a2 - canonical(1-alpha-2)
1.0.0-alpha10 - canonical(1-alpha-10)
1.0.0-alpha-10 - canonical(1-alpha-10)
1.0.0-a10 - canonical(1-alpha-10)
1.0.0-alpha.1 - canonical(1-alpha.1)
1.0.0-alpha.2 - canonical(1-alpha.2)
1.0.0-alpha.10 - canonical(1-alpha.10)
1.0.0-beta - canonical(1-beta)
1.0.0-beta1 - canonical(1-beta-1)
1.0.0-beta-1 - canonical(1-beta-1)
1.0.0-b1 - canonical(1-beta-1)
1.0.0-beta2 - canonical(1-beta-2)
1.0.0-beta-2 - canonical(1-beta-2)
1.0.0-b2 - canonical(1-beta-2)
1.0.0-beta10 - canonical(1-beta-10)
1.0.0-beta-10 - canonical(1-beta-10)
1.0.0-b10 - canonical(1-beta-10)
1.0.0-beta.1 - canonical(1-beta.1)
1.0.0-beta.2 - canonical(1-beta.2)
1.0.0-beta.10 - canonical(1-beta.10)
1.0.0-milestone - canonical(1-milestone)
1.0.0-milestone-1 - canonical(1-milestone-1)
1.0.0-m1 - canonical(1-milestone-1)
1.0.0-milestone-2 - canonical(1-milestone-2)
1.0.0-m2 - canonical(1-milestone-2)
1.0.0-milestone-10 - canonical(1-milestone-10)
1.0.0-m10 - canonical(1-milestone-10)
1.0.0-milestone.1 - canonical(1-milestone.1)
1.0.0-milestone.2 - canonical(1-milestone.2)
1.0.0-milestone.10 - canonical(1-milestone.10)
1.0.0-rc1 - canonical(1-rc-1)
1.0.0-rc-1 - canonical(1-rc-1)
1.0.0-rc2 - canonical(1-rc-2)
1.0.0-rc-2 - canonical(1-rc-2)
1.0.0-rc10 - canonical(1-rc-10)
1.0.0-rc-10 - canonical(1-rc-10)
1.0.0-rc.1 - canonical(1-rc.1)
1.0.0-rc.2 - canonical(1-rc.2)
1.0.0-rc.10 - canonical(1-rc.10)
1.0.0-snapshot - canonical(1-snapshot)
1.0.0-snapshot1 - canonical(1-snapshot-1)
1.0.0-snapshot-1 - canonical(1-snapshot-1)
1.0.0-snapshot2 - canonical(1-snapshot-2)
1.0.0-snapshot-2 - canonical(1-snapshot-2)
1.0.0-snapshot10 - canonical(1-snapshot-10)
1.0.0-snapshot-10 - canonical(1-snapshot-10)
1.0.0-snapshot.1 - canonical(1-snapshot.1)
1.0.0-snapshot.2 - canonical(1-snapshot.2)
1.0.0-snapshot.10 - canonical(1-snapshot.10)
1.0.0 - canonical(1)
1.0.0-ga - canonical(1)
1.0.0-final - canonical(1)
1.0.0-ga.1 - canonical(1-1)
1.0.0-final.1 - canonical(1-1)
1.0.0-ga.2 - canonical(1-2)
1.0.0-final.2 - canonical(1-2)
1.0.0-ga.10 - canonical(1-10)
1.0.0-final.10 - canonical(1-10)
1.0.0-a - canonical(1-a)
1.0.0-a-1 - canonical(1-a-1)
1.0.0-a-2 - canonical(1-a-2)
1.0.0-a-10 - canonical(1-a-10)
1.0.0-a.1 - canonical(1-a.1)
1.0.0-a.2 - canonical(1-a.2)
1.0.0-a.10 - canonical(1-a.10)
1.0.0-b - canonical(1-b)
1.0.0-b-1 - canonical(1-b-1)
1.0.0-b-2 - canonical(1-b-2)
1.0.0-b-10 - canonical(1-b-10)
1.0.0-b.1 - canonical(1-b.1)
1.0.0-b.2 - canonical(1-b.2)
1.0.0-b.10 - canonical(1-b.10)
1.0.0-dev - canonical(1-dev)
1.0.0-dev1 - canonical(1-dev-1)
1.0.0-dev-1 - canonical(1-dev-1)
1.0.0-dev2 - canonical(1-dev-2)
1.0.0-dev-2 - canonical(1-dev-2)
1.0.0-dev10 - canonical(1-dev-10)
1.0.0-dev-10 - canonical(1-dev-10)
1.0.0-dev-20191106.1 - canonical(1-dev-20191106.1)
1.0.0-dev.1 - canonical(1-dev.1)
1.0.0-dev.2 - canonical(1-dev.2)
1.0.0-dev.10 - canonical(1-dev.10)
1.0.0-m - canonical(1-m)
1.0.0-m-1 - canonical(1-m-1)
1.0.0-m-2 - canonical(1-m-2)
1.0.0-m-10 - canonical(1-m-10)
1.0.0-m.1 - canonical(1-m.1)
1.0.0-m.2 - canonical(1-m.2)
1.0.0-m.10 - canonical(1-m.10)
1.0.0-preview - canonical(1-preview)
1.0.0-preview1 - canonical(1-preview-1)
1.0.0-preview-1 - canonical(1-preview-1)
1.0.0-preview2 - canonical(1-preview-2)
1.0.0-preview-2 - canonical(1-preview-2)
1.0.0-preview10 - canonical(1-preview-10)
1.0.0-preview-10 - canonical(1-preview-10)
1.0.0-preview.1 - canonical(1-preview.1)
1.0.0-preview.2 - canonical(1-preview.2)
1.0.0-preview.10 - canonical(1-preview.10)
1.0.0-ga1 - canonical(1-1)
1.0.0-ga-1 - canonical(1-1)
1.0.0-final-1 - canonical(1-1)
1.0.0-ga2 - canonical(1-2)
1.0.0-ga-2 - canonical(1-2)
1.0.0-final-2 - canonical(1-2)
1.0.0-ga10 - canonical(1-10)
1.0.0-ga-10 - canonical(1-10)
1.0.0-final-10 - canonical(1-10)
1.0.0-1 - canonical(1-1)
1.0.0-2 - canonical(1-2)
1.0.0-10 - canonical(1-10)
1.0.0-20191106.1 - canonical(1-20191106.1)
1.0.1 - canonical(1.0.1)
1.1.0-beta.1 - canonical(1.1-beta.1)
1.1.0 - canonical(1.1)
1.1.0-dev-1 - canonical(1.1-dev-1)
2.0.0-beta.1 - canonical(2-beta.1)
2.0.0 - canonical(2)
2.0.0-dev-1 - canonical(2-dev-1)
        */
    }

    /**
     * Test <a href="https://issues.apache.org/jira/browse/MNG-5568">MNG-5568</a> edge case
     * which was showing transitive inconsistency: since A &gt; B and B &gt; C then we should have A &gt; C
     * otherwise sorting a list of ComparableVersions() will in some cases throw runtime exception;
     * see Netbeans issues <a href="https://netbeans.org/bugzilla/show_bug.cgi?id=240845">240845</a> and
     * <a href="https://netbeans.org/bugzilla/show_bug.cgi?id=226100">226100</a>
     */
    public void testMng5568()
    {
        String a = "6.1.0";
        String b = "6.1.0rc3";
        String c = "6.1H.5-beta"; // this is the unusual version string, with 'H' in the middle

        checkVersionsOrder( b, a ); // classical
        checkVersionsOrder( b, c ); // now b < c, but before MNG-5568, we had b > c
        checkVersionsOrder( a, c );
    }

    /**
     * Test <a href="https://jira.apache.org/jira/browse/MNG-6572">MNG-6572</a> optimization.
     */
    public void testMng6572()
    {
        String a = "20190126.230843"; // resembles a SNAPSHOT
        String b = "1234567890.12345"; // 10 digit number
        String c = "123456789012345.1H.5-beta"; // 15 digit number
        String d = "12345678901234567890.1H.5-beta"; // 20 digit number

        checkVersionsOrder( a, b );
        checkVersionsOrder( b, c );
        checkVersionsOrder( a, c );
        checkVersionsOrder( c, d );
        checkVersionsOrder( b, d );
        checkVersionsOrder( a, d );
    }

    /**
     * Test all versions are equal when starting with many leading zeroes regardless of string length
     * (related to MNG-6572 optimization)
     */
    public void testVersionEqualWithLeadingZeroes()
    {
        // versions with string lengths from 1 to 19
        String[] arr = new String[] {
            "0000000000000000001",
            "000000000000000001",
            "00000000000000001",
            "0000000000000001",
            "000000000000001",
            "00000000000001",
            "0000000000001",
            "000000000001",
            "00000000001",
            "0000000001",
            "000000001",
            "00000001",
            "0000001",
            "000001",
            "00001",
            "0001",
            "001",
            "01",
            "1"
        };

        checkVersionsArrayEqual( arr );
    }

    /**
     * Test all "0" versions are equal when starting with many leading zeroes regardless of string length
     * (related to MNG-6572 optimization)
     */
    public void testVersionZeroEqualWithLeadingZeroes()
    {
        // versions with string lengths from 1 to 19
        String[] arr = new String[] {
            "0000000000000000000",
            "000000000000000000",
            "00000000000000000",
            "0000000000000000",
            "000000000000000",
            "00000000000000",
            "0000000000000",
            "000000000000",
            "00000000000",
            "0000000000",
            "000000000",
            "00000000",
            "0000000",
            "000000",
            "00000",
            "0000",
            "000",
            "00",
            "0"
        };

        checkVersionsArrayEqual( arr );
    }

    public void testLocaleIndependent()
    {
        Locale orig = Locale.getDefault();
        Locale[] locales = { Locale.ENGLISH, new Locale( "tr" ), Locale.getDefault() };
        try
        {
            for ( Locale locale : locales )
            {
                Locale.setDefault( locale );
                checkVersionsEqual( "1-abcdefghijklmnopqrstuvwxyz", "1-ABCDEFGHIJKLMNOPQRSTUVWXYZ" );
            }
        }
        finally
        {
            Locale.setDefault( orig );
        }
    }

    public void testReuse()
    {
        ComparableVersion c1 = new ComparableVersion( "1" );
        c1.parseVersion( "2" );

        Comparable c2 = newComparable( "2" );

        assertEquals( "reused instance should be equivalent to new instance", c1, c2 );
    }
}
