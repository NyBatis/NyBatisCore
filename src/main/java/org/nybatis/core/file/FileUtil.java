package org.nybatis.core.file;import org.nybatis.core.exception.unchecked.ClassNotExistException;import org.nybatis.core.exception.unchecked.IoException;import org.nybatis.core.file.handler.FileFinder;import org.nybatis.core.file.handler.ZipFileHandler;import org.nybatis.core.log.NLogger;import org.nybatis.core.model.NList;import org.nybatis.core.util.StringUtil;import org.nybatis.core.worker.WorkerReadLine;import org.nybatis.core.worker.WorkerWriteBuffer;import java.io.BufferedReader;import java.io.BufferedWriter;import java.io.File;import java.io.FileInputStream;import java.io.FileNotFoundException;import java.io.FileOutputStream;import java.io.IOException;import java.io.InputStream;import java.io.InputStreamReader;import java.io.ObjectInput;import java.io.ObjectInputStream;import java.io.ObjectOutput;import java.io.ObjectOutputStream;import java.io.OutputStream;import java.io.OutputStreamWriter;import java.nio.charset.Charset;import java.nio.file.CopyOption;import java.nio.file.FileVisitOption;import java.nio.file.FileVisitResult;import java.nio.file.Files;import java.nio.file.Path;import java.nio.file.Paths;import java.nio.file.SimpleFileVisitor;import java.nio.file.StandardCopyOption;import java.nio.file.attribute.BasicFileAttributes;import java.util.ArrayList;import java.util.EnumSet;import java.util.List;import java.util.zip.GZIPInputStream;import java.util.zip.GZIPOutputStream;/** * File Utility * * @author nayasis@gmail.com * */public class FileUtil {    /**     * Delete file or directory     *     * @param filePath  file path or directory path     * @throws IoException  if an I/O error occurs     */	public static void delete( Path filePath ) throws IoException {		if( filePath == null || isNotExist(filePath) ) return;		try {            if( isDirectory(filePath) ) {                Files.walkFileTree( filePath, new SimpleFileVisitor<Path>() {                    @Override                    public FileVisitResult visitFile( Path file, BasicFileAttributes attributes ) throws IOException {                        Files.delete(file);                        return FileVisitResult.CONTINUE;                    }                    @Override                    public FileVisitResult visitFileFailed( Path file, IOException e ) throws IOException {                        Files.delete(file);                        return FileVisitResult.CONTINUE;                    }                    @Override                    public FileVisitResult postVisitDirectory( Path dir, IOException e ) throws IOException {                        if ( e != null) throw e;                        Files.delete( dir );                        return FileVisitResult.CONTINUE;                    }                });            } else {                Files.delete( filePath );            }        } catch( IOException e ) {        	throw new IoException( e );        }	}    /**     * Delete file or directory     *     * @param filePath  file path or directory path     * @throws IoException  if an I/O error occurs     */    public static void delete( String filePath ) throws IoException {    	if( filePath != null ) delete( Paths.get(filePath) );    }    /**     * Delete file or directory     *     * @param filePath  file path or directory path     * @throws IoException  if an I/O error occurs     */    public static void delete( File file ) throws IoException {        if( file != null && file.exists() ) {            delete( file.toPath() );        }    }    /**     * Get file extention     *     * @param filePath  file name or full path     * @return file extension     */    public static String getExtention( String filePath ) {        if( filePath == null ) return "";        int index = filePath.lastIndexOf( '.' );        if( index < 0 ) return "";        String ext = filePath.substring( index + 1 );        if( ext.contains( File.pathSeparator ) ) return "";        return ext;    }    /**     * Get file extention     *     * @param filePath  file name or full path     * @return file extension     */    public static String getExtention( File file ) {    	return ( file == null ) ? "" : getExtention( file.getName() );    }    /**     * Get file extention     *     * @param filePath  file name or full path     * @return file extension     */    public static String getExtention( Path path ) {        return ( path == null ) ? "" : getExtention( path.getFileName().toString() );    }    /**     * Search list of files or directories in sub directory.     *     *     * @param searchDir         root directory to search     * @param includeFile       include file     * @param includeDirectory  include directory     * @param scanDepth         depth to scan     * <pre>     *   -1 : infinite     *    0 : in searchDir itself     *    1 : from searchDir to 1 depth sub directory     *    2 : from searchDir to 2 depth sub directory     *    ...     * </pre>     * @param matchingPattern   path matching pattern (glob 식이며,미입력시 전체검색)     * <pre>     * ** : 디렉토리를 무시     * *  : 파일명 like 검색     *     * 1. **.xml : searchDir 아래의 모든 디렉토리에 있는 파일 중 확장자가 xml로 끝나는 모든 파일     * 2. *.xml  : searchDir 바로 1depth 아래 디렉토리에 있는 파일 중 확장자가 xml로 끝나는 모든 파일     * 3. c:\home\*\*.xml : 'c:\home\ 바로 1 depth 아래의 하위 디렉토리에 있는 확장자가 xml로 끝나는 모든 파일     * 4. c:\home\**\*.xml : 'c:\home\ 아래의 모든 하위 디렉토리에 있는 확장자가 xml로 끝나는 모든 파일     *     * 1. *  It matches zero , one or more than one characters. While matching, it will not cross directories boundaries.     * 2. ** It does the same as * but it crosses the directory boundaries.     * 3. ?  It matches only one character for the given name.     * 4. \  It helps to avoid characters to be interpreted as special characters.     * 5. [] In a set of characters, only single character is matched. If (-) hyphen is used then, it matches a range of characters. Example: [efg] matches "e","f" or "g" . [a-d] matches a range from a to d.     * 6. {} It helps to matches the group of sub patterns.     *     * 1. *.java when given path is java , we will get true by PathMatcher.matches(path).     * 2. *.* if file contains a dot, pattern will be matched.     * 3. *.{java,txt} If file is either java or txt, path will be matched.     * 4. abc.? matches a file which start with abc and it has extension with only single character.     *     * </pre>     *     * @return list of files or directories     * @throws IoException  if an I/O error occurs     */    public static List<Path> getList( String searchDir, boolean includeFile, boolean includeDirectory, int scanDepth, String... matchingPattern ) {        if( StringUtil.isEmpty( searchDir ) ) return new ArrayList<>();        return getList( Paths.get( searchDir ), includeFile, includeDirectory, scanDepth, matchingPattern ) ;    }    /**     * Search list of files or directories in sub directory.     *     *     * @param searchDir         root directory to search     * @param includeFile       include file     * @param includeDirectory  include directory     * @param scanDepth         depth to scan     * <pre>     *   -1 : infinite     *    0 : in searchDir itself     *    1 : from searchDir to 1 depth sub directory     *    2 : from searchDir to 2 depth sub directory     *    ...     * </pre>     * @param matchingPattern   path matching pattern (glob 식이며,미입력시 전체검색)     * <pre>     * ** : 디렉토리를 무시     * *  : 파일명 like 검색     *     * 1. **.xml : searchDir 아래의 모든 디렉토리에 있는 파일 중 확장자가 xml로 끝나는 모든 파일     * 2. *.xml  : searchDir 바로 1depth 아래 디렉토리에 있는 파일 중 확장자가 xml로 끝나는 모든 파일     * 3. c:\home\*\*.xml : 'c:\home\ 바로 1 depth 아래의 하위 디렉토리에 있는 확장자가 xml로 끝나는 모든 파일     * 4. c:\home\**\*.xml : 'c:\home\ 아래의 모든 하위 디렉토리에 있는 확장자가 xml로 끝나는 모든 파일     *     * 1. *  It matches zero , one or more than one characters. While matching, it will not cross directories boundaries.     * 2. ** It does the same as * but it crosses the directory boundaries.     * 3. ?  It matches only one character for the given name.     * 4. \  It helps to avoid characters to be interpreted as special characters.     * 5. [] In a set of characters, only single character is matched. If (-) hyphen is used then, it matches a range of characters. Example: [efg] matches "e","f" or "g" . [a-d] matches a range from a to d.     * 6. {} It helps to matches the group of sub patterns.     *     * 1. *.java when given path is java , we will get true by PathMatcher.matches(path).     * 2. *.* if file contains a dot, pattern will be matched.     * 3. *.{java,txt} If file is either java or txt, path will be matched.     * 4. abc.? matches a file which start with abc and it has extension with only single character.     *     * </pre>     *     * @return list of files or directories     * @throws IoException  if an I/O error occurs     */    public static List<Path> getList( Path searchDir, boolean includeFile, boolean includeDirectory, int scanDepth, String... matchingPattern ) {        if( isNotExist( searchDir ) ) return new ArrayList<>();        Path rootDir = isFile( searchDir ) ? searchDir.getParent() : searchDir;        FileFinder finder = new FileFinder( includeFile, includeDirectory, matchingPattern );        scanDepth = ( scanDepth < 0 ) ? Integer.MAX_VALUE : ++scanDepth;        try {        	Files.walkFileTree( rootDir, EnumSet.noneOf( FileVisitOption.class ), scanDepth, finder );        } catch( IOException e ) {	        throw new IoException( e );        }        return finder.getFindResult();    }    public static boolean isExist( Path path ) {    	return path != null && Files.exists( path );    }    /**     * 경로에 해당하는 File 혹은 Directory가 존재하는지 여부를 확인한다.     *     * @param path  경로     * @return 존재여부     */    public static boolean isExist( String path ) {        return isExist( Paths.get(path) );    }    public static boolean isExist( File file ) {    	return file != null && isExist( file.toPath() );    }    public static boolean isNotExist( Path path ) {    	return path != null && Files.notExists( path );    }    public static boolean isNotExist( String path ) {    	return isNotExist( Paths.get(path) );    }    public static boolean isNotExist( File file ) {    	return file != null && isNotExist( file.toPath() );    }    public static boolean isFile( Path path ) {    	return path != null && Files.isRegularFile( path );    }    public static boolean isFile( String path ) {    	return isFile( Paths.get(path) );    }    public static boolean isFile( File file ) {    	return file != null && isFile( file.toPath() );    }    public static boolean isDirectory( Path path ) {    	return path != null && Files.isDirectory( path );    }    public static boolean isDirectory( String path ) {    	return isDirectory( Paths.get(path) );    }    public static boolean isDirectory( File file ) {    	return file != null && isDirectory( file.toPath() );    }    /**     * Make directory     *     * @param directoryPath directory path     * @throws IoException  if an I/O error occurs     */    public static File makeDir( String directoryPath ) throws IoException {    	if( directoryPath == null ) return null;    	return makeDir( Paths.get( directoryPath ) );    }    /**     * Make directory     *     * @param directory directory path     * @throws IoException  if an I/O error occurs     */    public static File makeDir( File directory ) throws IoException {        if( directory == null ) return null;        return makeDir( directory.toPath() );    }    /**     * Make directory     *     * @param directory directory path     * @throws IoException  if an I/O error occurs     */    public static File makeDir( Path directory ) throws IoException {    	if( directory == null ) return null;    	if( Files.exists(directory) ) return directory.toFile();    	try {    		return Files.createDirectories( directory ).toFile();    	} catch( IOException e ) {    		throw new IoException( e );    	}    }    /**     * Make file (if directory path is not exists, create it additionally.)     *     * @param filePath file path     * @throws IoException  if an I/O error occurs     */    public static File makeFile( String filePath ) throws IoException {    	if( filePath == null ) return null;    	return makeFile( Paths.get(filePath) );    }    /**     * Make file (if directory path is not exists, create it additionally.)     *     * @param filePath file path     * @throws IoException  if an I/O error occurs     */    public static File makeFile( Path filePath ) throws IoException {    	if( filePath == null ) return null;    	if( Files.exists( filePath ) ) {    		return filePath.toFile();    	} else {    		makeDir( filePath.getParent().toString() );    	}    	try {    		return Files.createFile( filePath ).toFile();    	} catch( IOException e ) {    		throw new IoException( e );    	}    }    /**     * Make file (if directory path is not exists, create it additionally.)     *     * @param file file to create     * @throws IoException  if an I/O error occurs     */    public static File makeFile( File file ) throws IoException {    	if( file == null ) return null;    	return makeFile( file.toPath() );    }    /**     * Move file or directory     *     * @param  source     file or directory path to move     * @param  target     file or directory path of target     * @param  overwrite  overwrite if the target file exists     * @throws IoException if an I/O error occurs     */    public static void move( String source, String target, boolean overwrite ) throws IoException {        Path sourcePath = Paths.get( source );        Path targetPath = Paths.get( target );        move( sourcePath, targetPath, overwrite );    }    /**     * Move file or directory     *     * @param  source     file or directory path to move     * @param  target     file or directory path of target     * @param  overwrite  overwrite if the target file exists     * @throws IoException if an I/O error occurs     */    public static void move( Path source, Path target, boolean overwrite ) throws IoException {        CopyOption[] option = overwrite                ? new CopyOption[] { StandardCopyOption.REPLACE_EXISTING }                : new CopyOption[] {};        try {            if( Files.isDirectory(source) ) {                Files.move( source, target, option );            } else {                if( Files.isDirectory(target) ) {                    Files.move( source, target.resolve( source.getFileName() ), option );                } else {                    Files.move( source, target, option );                }            }        } catch( IOException e ) {            throw new IoException( e );        }    }    /**     * Copy file or directory     *     * @param  source     file or directory path to move     * @param  target     file or directory path of target     * @param  overwrite  overwrite if the target file exists     * @throws IoException if an I/O error occurs     */    public static void copy( String source, String target, boolean overwrite ) throws IoException {        Path sourcePath = Paths.get( source );        Path targetPath = Paths.get( target );        copy( sourcePath, targetPath, overwrite );    }    /**     * Copy file or directory     *     * @param  source     file or directory path to copy     * @param  target     file or directory path of target     * @param  overwrite  overwrite if the target file exists     * @throws IoException if an I/O error occurs     */    public static void copy( Path source, Path target, boolean overwrite ) throws IoException {        CopyOption[] option = overwrite                ? new CopyOption[] { StandardCopyOption.COPY_ATTRIBUTES, StandardCopyOption.REPLACE_EXISTING }                : new CopyOption[] { StandardCopyOption.COPY_ATTRIBUTES };        try {            if( Files.isDirectory(source) ) {                for( Path path : getList( source, true, true, -1 ) ) {                    Path targetPath = target.resolve( source.relativize(path) );                    if( FileUtil.isDirectory( targetPath ) ) {                        makeDir( path );                        continue;                    }                    Files.copy( path, targetPath, option );                }            } else {                if( Files.isDirectory(target) ) {                    Files.copy( source, target.resolve( source.getFileName() ), option );                } else {                    Files.copy( source, target, option );                }            }        } catch( IOException e ) {            throw new IoException( e );        }    }    /**     * 파일에 저장된 object를 읽어온다.     *     * @param filePath  object 정보가 담긴 파일 경로     * @return object     * @throws ClassNotExistException   파일에 담긴 object에 해당하는 클래스가 존재하지 않을 경우     * @throws IoException             파일 처리 오류시     */    @SuppressWarnings( "unchecked" )    public static <T> T readObject( String filePath ) throws ClassNotExistException, IoException {        try(                InputStream file   = new FileInputStream( filePath );                InputStream buffer = new GZIPInputStream( file );                ObjectInput input  = new ObjectInputStream( buffer ) ) {            return (T) input.readObject();        } catch( ClassNotFoundException e ) {            throw new ClassNotExistException( e );        } catch( IOException e ) {        	throw new IoException( e );        }    }    /**     * Read serialized objects data stored in file     *     * @param file  file stored serialized objects data.     * @return object     * @throws ClassNotExistException   파일에 담긴 object에 해당하는 클래스가 존재하지 않을 경우     * @throws IoException              파일 처리 오류시     */    public static <T> T readObject( File file ) throws ClassNotExistException, IoException {    	return readObject( file.getPath() );    }    /**     * 파일명에서 확장자를 제거한다.     *     * @param filePath  파일명 (또는 파일경로)     * @return 확장자가 제거된 파일명 (또는 파일경로)     */    public static String removeExtention( String filePath ) {        if( filePath == null ) return filePath;        int index = filePath.lastIndexOf( '.' );        if( index < 0 ) return filePath;        String ext = filePath.substring( index + 1 );        if( ext.contains( File.pathSeparator ) ) return filePath;        return filePath.substring( 0, index );    }    public static String readFrom( String filePath ) throws IoException {    	return readFrom( filePath, "UTF-8" );    }    public static String readFrom( String filePath, String charset ) throws IoException {    	StringBuilder sb = new StringBuilder();    	readFrom( filePath, new WorkerReadLine() {            public void execute( String readLine ) {            	sb.append( readLine ).append( '\n' );            }		}, charset );    	return sb.toString();    }    public static String readFrom( File file, String charset ) throws IoException {    	return readFrom( file.getPath(), charset );    }    public static String readFrom( File file ) throws IoException {    	return readFrom( file.getPath(), "UTF-8" );    }    public static void readFrom( Path filePath, WorkerReadLine worker ) throws IoException {        readFrom( filePath, worker, "UTF-8" );    }    public static void readFrom( Path filePath, WorkerReadLine worker, String charset ) throws IoException {        readFrom( filePath.toString(), worker, charset );    }    public static void readFrom( File filePath, WorkerReadLine worker, String charset ) throws IoException {    	readFrom( filePath.toString(), worker, charset );    }    public static void readFrom( File filePath, WorkerReadLine worker ) throws IoException {    	readFrom( filePath.toString(), worker, "UTF-8" );    }    public static void readFrom( String filePath, WorkerReadLine worker ) throws IoException {        readFrom( filePath, worker, "UTF-8" );    }    public static void readFrom( String filePath, WorkerReadLine worker, String charset ) throws IoException {        try(            FileInputStream fis = new FileInputStream( filePath );            BufferedReader br = new BufferedReader( new InputStreamReader( fis, charset ) )        ) {            String line;            while( ( line = br.readLine() ) != null ) {                worker.execute( line );            }        } catch( IOException e ) {            throw new IoException( e );        }    }    public static void writeTo( String filePath, WorkerWriteBuffer handler, String charset ) throws IoException {        makeFile( filePath );        try(            FileOutputStream fos    = new FileOutputStream( filePath );            BufferedWriter   writer = new BufferedWriter( new OutputStreamWriter( fos, charset ) )        ) {            handler.execute( writer );        } catch( IOException e ) {            throw new IoException(e);        }    }    public static void writeTo( String filePath, WorkerWriteBuffer handler ) throws IoException {        writeTo( filePath, handler, "UTF-8" );    }    /**     * 문자열을 파일로 생성한다.     *     * @param filePath  경로를 포함한 파일명     * @param text      문자열     * @throws IOException  파일 처리 오류시     */    public static void writeTo( String filePath, final String text ) throws IoException {        writeTo( filePath, new WorkerWriteBuffer() {            public void execute( BufferedWriter writer ) throws IOException {                writer.write( text );            }        } );    }    public static void writeTo( File file, final String text ) throws IoException {    	writeTo( file.getAbsolutePath(), text );    }    public static void writeTo( File file, byte[] bytes ) throws IoException {    	makeFile( file );    	FileOutputStream stream = null;    	try {    		stream = new FileOutputStream( file );    	    stream.write(bytes);    	} catch( IOException e ) {	        throw new IoException(e);        } finally {    	    if( stream != null ) try { stream.close(); } catch( IOException e ) {}    	}    }    /**     * Object를 파일로 저장한다.     *     * @param filePath  파일경로     * @param object    저장할 object     * @throws IoException  파일 처리 오류시     */    public static void writeObject( String filePath, Object object ) throws IoException {    	makeFile( filePath );        try(                OutputStream file   = new FileOutputStream( filePath );                OutputStream buffer = new GZIPOutputStream( file );                ObjectOutput output = new ObjectOutputStream( buffer )        ) {            output.writeObject( object );            output.flush();            output.close();        } catch( IOException e ) {            throw new IoException( e );        }    }    /**     * Object를 파일로 저장한다.     *     * @param file  파일     * @param object    저장할 object     * @throws IOException  파일 처리 오류시     */    public static void writeObject( File file, Object object ) throws IoException {    	writeObject( file.getPath(), object );    }    public static void writeToCsv( String file, NList data, String delimiter, String charset ) throws IoException {    	writeTo( file, new WorkerWriteBuffer() {            public void execute( BufferedWriter writer ) throws IOException {                writer.write( StringUtil.join( data.getAliases(), delimiter ) );                writer.write( '\n' );                for( int row = 0, rowCnt = data.size(); row < rowCnt; row++ ) {                    List<String> temp = new ArrayList<>();                    for( int col = 0, colCnt = data.keySize(); col < colCnt; col++ ) {                        temp.add( data.getString( col, row ) );                    }                    writer.write( StringUtil.join( temp, delimiter ) );                    writer.write( '\n' );                }            }        }, charset );    }    /**     * 상대경로를 절대경로로 변환한다.     *     * <pre>     * FileUtil.convertToAbsolutePath( "/home/user/nayasis", "../test/abc" );     *     * -> "/home/user/test/abc"     * </pre>     *     * @param basePath   기준경로     * @param targetPath 변환대상     * @return 변환된 절대경로     * @throws FileNotFoundException 기준경로가 존재하지 않을 경우     */    public static String convertToAbsolutePath( String basePath, String targetPath ) throws FileNotFoundException {    	Path pathBase = getDirectory( Paths.get(basePath) );    	return pathBase.resolve( targetPath ).normalize().toString();    }    /**     * 절대경로를 상대경로로 변환한다.     *     * <pre>     * FileUtil.convertToRelativePath( "/home/user/nayasis", "/home/user/test/abc" );     *     * -> "../test/abc"     * </pre>     *     * @param basePath   기준경로     * @param targetPath 변환대상     * @return 변환된 상대경로     * @throws FileNotFoundException 기준경로가 존재하지 않을 경우     * @throws IllegalArgumentException 기준경로와 대상경로의 Root가 다를 경우 (basePath는 C 드라이브에, targetPath는 D 드라이브에 있을 경우)     */    public static String convertToRelativePath( String basePath, String targetPath ) throws FileNotFoundException {    	Path pathBase = getDirectory( Paths.get(basePath) );    	return pathBase.relativize( Paths.get( targetPath ) ).toString();    }    /**     * Get working directory     *     * @param path File or Directory     * @return return parent directory if path is file, return itself it path is directory.     * @throws FileNotFoundException paht is not invalid     */    public static File getDirectory( File path ) throws FileNotFoundException {        return getDirectory( path.toPath() ).toFile();    }    /**     * Get working directory     *     * @param path File or Directory     * @return return parent directory if path is file, return itself it path is directory.     * @throws FileNotFoundException paht is not invalid     */    public static Path getDirectory( Path path ) throws FileNotFoundException {    	if( ! isExist(path) ) throw new FileNotFoundException( StringUtil.format( "path : {}", path ) );        return isDirectory(path) ? path : path.getParent();    }    /**     * Zip file or directory     *     * @param fileOrDirectoryToZip file or directory to zip     * @param targetFile archive file     * @param charset characterset (default : UTF-8)     */    public static void zip( File fileOrDirectoryToZip, File targetFile, Charset charset ) {        getZipFileHandler().zip( fileOrDirectoryToZip, targetFile, charset );    }    /**     * Zip file or directory     *     * @param fileOrDirectoryToZip file or directory to zip     * @param targetFile archive file     */    public static void zip( File fileOrDirectoryToZip, File targetFile ) {        zip( fileOrDirectoryToZip, targetFile, Charset.defaultCharset() );    }    /**     * Zip file or directory     *     * @param fileOrDirectoryToZip file or directory to zip     * @param targetFile archive file     * @param charset characterset (default : UTF-8)     */    public static void zip( Path fileOrDirectoryToZip, Path targetFile, Charset charset ) {        zip( fileOrDirectoryToZip.toFile(), targetFile.toFile(), charset );    }    /**     * Zip file or directory     *     * @param fileOrDirectoryToZip file or directory to zip     * @param targetFile archive file     */    public static void zip( Path fileOrDirectoryToZip, Path targetFile ) {        zip( fileOrDirectoryToZip.toFile(), targetFile.toFile() );    }    /**     * Unzip file or directory     *     * @param fileToUnzip file to unzip     * @param targetDirectory directory to unzip     * @param charset characterset (default : UTF-8)     */    public static void unzip( File fileToUnzip, File targetDirectory, Charset charset ) {        getZipFileHandler().unzip( fileToUnzip, targetDirectory, charset );    }    /**     * Unzip file or directory     *     * @param fileToUnzip file to unzip     * @param targetDirectory directory to unzip     */    public static void unzip( File fileToUnzip, File targetDirectory ) {        unzip( fileToUnzip, targetDirectory, Charset.defaultCharset() );    }    /**     * Unzip file or directory     *     * @param fileToUnzip file to unzip     * @param targetDirectory directory to unzip     * @param charset characterset (default : UTF-8)     */    public static void unzip( Path fileToUnzip, Path targetDirectory, Charset charset ) {        unzip( fileToUnzip.toFile(), targetDirectory.toFile(), charset );    }    /**     * Unzip file or directory     *     * @param fileToUnzip file to unzip     * @param targetDirectory directory to unzip     */    public static void unzip( Path fileToUnzip, Path targetDirectory ) {        unzip( fileToUnzip.toFile(), targetDirectory.toFile() );    }    private static ZipFileHandler getZipFileHandler() {        try {            return new ZipFileHandler();        } catch( Throwable e ) {            String errorMessage =                    "you must import [Apache Common Compress Library] to handle zip file.\n" +                            "\t- Maven dependency is like below.\n" +                            "\t\t<dependency>\n" +                            "\t\t  <groupId>org.apache.commons</groupId>\n" +                            "\t\t  <artifactId>commons-compress</artifactId>\n" +                            "\t\t  <version>1.8</version>\n" +                            "\t\t</dependency>\n";            throw new NoClassDefFoundError( errorMessage );        }    }//    private String toPath(File root, File dir){//        String path = dir.getAbsolutePath();//        path = path.substring(root.getAbsolutePath().length()).replace(File.separatorChar, '/');//        if ( path.startsWith("/")) path = path.substring(1);//        if ( dir.isDirectory() && !path.endsWith("/")) path += "/" ;//        return path ;//    }}