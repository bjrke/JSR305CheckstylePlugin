package de.bjrke.checkstyle.jsr305;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.testng.Assert;

import com.puppycrawl.tools.checkstyle.Checker;
import com.puppycrawl.tools.checkstyle.DefaultConfiguration;
import com.puppycrawl.tools.checkstyle.DefaultLogger;
import com.puppycrawl.tools.checkstyle.api.AuditEvent;
import com.puppycrawl.tools.checkstyle.api.AuditListener;
import com.puppycrawl.tools.checkstyle.api.CheckstyleException;

public class Jsr305AnnationsTestUtil {

    private static final String GRADLE_BUILD_PATH = "/build/classes";

    public static final class ExpectedWarning {

        private final int _column;
        private final int _line;
        private final Class<?> _clz;

        ExpectedWarning( final Class<?> clz, final int line, final int column ) {
            _clz = clz;
            _line = line;
            _column = column;
        }

        ExpectedWarning( final Class<?> clz ) {
            this( clz, -1, -1 );
        }

        public boolean isNoWarning() {
            return _column < 0 || _line < 0;
        }

        @Override
        public String toString() {
            return _clz.getSimpleName() + " (" + _line + ":" + _column + ")";
        }
    }

    private static final class AuditListenerImplementation implements AuditListener {

        private final Map<String, List<ExpectedWarning>> _fileNameWarningMap;
        private Iterator<ExpectedWarning> _warningIterator;

        public AuditListenerImplementation( final Map<String, List<ExpectedWarning>> fileNameWarningMap ) {
            _fileNameWarningMap = fileNameWarningMap;
        }

        @Override
        public void fileStarted( final AuditEvent event ) {
            final List<ExpectedWarning> list = _fileNameWarningMap.remove( event.getFileName() );
            _warningIterator = list.iterator();
        }

        @Override
        public void fileFinished( final AuditEvent event ) {
            if ( _warningIterator.hasNext() ) { // can't use Assert.assertFalse because next can not be evaluated
                Assert.fail( "There is a warnings left: " + _warningIterator.next() );
            }
        }

        @Override
        public void auditStarted( final AuditEvent event ) {
            return;
        }

        @Override
        public void auditFinished( final AuditEvent event ) {
            Assert.assertTrue( _fileNameWarningMap.isEmpty(), "There are Files left: " + _fileNameWarningMap.keySet() );
        }

        @Override
        public void addException( final AuditEvent event, final Throwable throwable ) {
            Assert.fail( "Exception during check of file " + event.getFileName(), throwable );
        }

        @Override
        public void addError( final AuditEvent event ) {
            Assert.assertTrue( _warningIterator.hasNext(), "There should be an expeceted warning for file \"" + event.getFileName()
                    + "\", but none found" );
            final ExpectedWarning warning = _warningIterator.next();
            final String error = "Position (" + event.getLine() + ":" + event.getColumn() + ") didn't match: " + warning;
            Assert.assertEquals( event.getLine(), warning._line, error );
            Assert.assertEquals( event.getColumn(), warning._column, error );
        }

    }

    public static void check( final ExpectedWarning... warnings ) throws CheckstyleException {
        final Checker checker = createChecker();

        checker.addListener( new DefaultLogger( System.out, false ) );

        final Map<String, List<ExpectedWarning>> fileNameWarningMap = new HashMap<String, List<ExpectedWarning>>();
        final List<File> files = new ArrayList<File>();

        final Map<Class<?>, List<ExpectedWarning>> mappedWarnings = new HashMap<Class<?>, List<ExpectedWarning>>();
        for ( final ExpectedWarning warning : warnings ) {
            final List<ExpectedWarning> existing = mappedWarnings.get( warning._clz );
            final List<ExpectedWarning> warningsInMap;
            if ( existing != null ) {
                warningsInMap = existing;
            } else {
                final File file = getFileFromClass( warning._clz );
                files.add( file );
                warningsInMap = new ArrayList<ExpectedWarning>();
                mappedWarnings.put( warning._clz, warningsInMap );
                fileNameWarningMap.put( file.getPath(), warningsInMap );
            }
            if ( !warning.isNoWarning() ) {
                warningsInMap.add( warning );
            }
        }

        checker.addListener( new AuditListenerImplementation( fileNameWarningMap ) );
        checker.process( files );
    }

    private static File getFileFromClass( final Class<?> clz ) {
        try {
            final String clzFilename = clz.getSimpleName() + ".class";
            final URL url = clz.getResource( clzFilename );
            final String filename = url.toString();

            final String path = filename.replace(resolveName(clz, ".class"), "");
            final int lastSlash = path.lastIndexOf("/", path.length() - 2);
            final String lsPath = path.substring(0, lastSlash);
            final String buildPath = lsPath.endsWith(GRADLE_BUILD_PATH) ? lsPath.substring(0, lsPath.length() - GRADLE_BUILD_PATH.length()) : lsPath;

            final String newPath = buildPath + "/src/test/java/" + resolveName(clz, ".java");
            return new File( new URI( newPath ) );
        } catch ( final URISyntaxException e ) {
            throw new RuntimeException( e );
        }
    }

    private static String resolveName( final Class<?> clz, final String extension ) {
        Class<?> c = clz;
        while ( c.isArray() ) {
            c = c.getComponentType();
        }
        final String baseName = c.getName();
        final int index = baseName.lastIndexOf( '.' );

        final String name = c.getSimpleName() + extension;
        if ( index != -1 ) {
            return baseName.substring( 0, index ).replace( '.', '/' ) + "/" + name;
        }
        return name;
    }

    private static Checker createChecker() throws CheckstyleException {
        final Checker checker = new Checker();
        checker.setModuleClassLoader( Checker.class.getClassLoader() );
        checker.configure( createCheckerConfiguration() );
        return checker;
    }

    private static DefaultConfiguration createCheckerConfiguration() {
        final DefaultConfiguration checkerConfiguration = new DefaultConfiguration( "Checker" );
        checkerConfiguration.addChild( createTreeWalkerConfiguration() );
        return checkerConfiguration;
    }

    private static DefaultConfiguration createTreeWalkerConfiguration() {
        final DefaultConfiguration treeWalkerConfiguration = new DefaultConfiguration( "TreeWalker" );
        treeWalkerConfiguration.addChild( createJsr305AnnotationsConfiguration() );
        return treeWalkerConfiguration;
    }

    private static DefaultConfiguration createJsr305AnnotationsConfiguration() {
        final DefaultConfiguration jsr305AnnotaionsConfiguration = new DefaultConfiguration( "Jsr305Annotations" );
        jsr305AnnotaionsConfiguration.addAttribute( "packages", "de.bjrke.checkstyle.jsr305.test" );
        return jsr305AnnotaionsConfiguration;
    }
}
