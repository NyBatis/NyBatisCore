package org.nybatis.core.file.handler;

import org.nybatis.core.util.StringUtil;

import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 파일을 검색하는 Visitor 클래스
 *
 * @author nayasis
 */
public class FileFinder extends SimpleFileVisitor<Path> {

    private boolean                 checkPattern = true;
    private boolean                 includeDir   = true;
    private boolean                 includeFile  = true;
    private final Set<PathMatcher>  matchers     = new HashSet<>();
    private final List<Path>        result       = new ArrayList<>();

    /**
     * 기본 생성자
     *
     * @param includeFile   파일 포함여부
     * @param includeDir    디렉토리 포함여부
     * @param pattern       이름을 검사할 패턴식
     */
    public FileFinder( boolean includeFile, boolean includeDir, String... pattern ) {

        for( String singlePattern : new HashSet<>(Arrays.asList(pattern)) ) {
        	if( StringUtil.isEmpty( singlePattern ) ) continue;

            if( ! singlePattern.contains("/") && ! singlePattern.contains("\\") ) {
                singlePattern = "**/" + singlePattern;
            }

            matchers.add( FileSystems.getDefault().getPathMatcher( "glob:" + singlePattern ) );
        }

        this.checkPattern = ( matchers.size() != 0 );
        this.includeFile  = includeFile;
        this.includeDir   = includeDir;

        // 1. *.java when given path is java , we will get true by PathMatcher.matches(path).
        // 2. *.* if file contains a dot, pattern will be matched.
        // 3. *.{java,txt} If file is either java or txt, path will be matched.
        // 4. abc.? matches a file which start with abc and it has extension with only single character.

    }

    /**
     * 파일명칭의 패턴을 검사한다.
     *
     * @param file  검사할 파일명
     */
    private void find( Path file ) {
//        NLogger.debug( "check file : {}", file );
        if( checkPattern ) {
            for( PathMatcher matcher : matchers ) {
        		if( matcher.matches( file ) ) {
                    add( file );
        			return;
        	    }
            }
        } else {
        	add( file );
        }
    }

    private void add( Path path ) {

    	boolean isDir = Files.isDirectory( path );

    	if( (includeFile && ! isDir) || (includeDir && isDir) ) {
    		result.add( path );
    	}

    }

    /**
     * 파일 검색결과를 얻는다.
     *
     * @return 검색결과
     */
    public List<Path> getFindResult() {
        return result;
    }

    /*
     * (non-Javadoc)
     * @see java.nio.file.SimpleFileVisitor#preVisitDirectory(java.lang.Object,
     * java.nio.file.attribute.BasicFileAttributes)
     */
    @Override
    public FileVisitResult preVisitDirectory( Path dir, BasicFileAttributes attrs ) {
//        NLogger.debug( "visitDir : {}", dir );
        if( includeDir ) find( dir );
        return FileVisitResult.CONTINUE;
    }

    /*
     * (non-Javadoc)
     * @see java.nio.file.SimpleFileVisitor#visitFile(java.lang.Object, java.nio.file.attribute.BasicFileAttributes)
     */
    @Override
    public FileVisitResult visitFile( Path file, BasicFileAttributes attrs ) {
//        NLogger.debug( "visitFile : {}", file );
        if( includeFile ) find( file );
        return FileVisitResult.CONTINUE;
    }

//    /*
//     * (non-Javadoc)
//     * @see java.nio.file.SimpleFileVisitor#visitFileFailed(java.lang.Object, java.io.IOException)
//     */
//    @Override
//    public FileVisitResult visitFileFailed( Path file, IOException exception ) throws IOException {
//    	if( exception != null ) throw exception;
//        return FileVisitResult.CONTINUE;
//    }

}
