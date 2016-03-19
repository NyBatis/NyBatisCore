package org.nybatis.core.file.handler;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.ArchiveOutputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.nybatis.core.exception.unchecked.IoException;
import org.nybatis.core.file.FileUtil;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Zip File Handler
 *
 * @author 1002159
 * @since 2015-11-02
 */
public class ZipFileHandler {

    private void uncompress( ArchiveInputStream inputstreamToUncompress, File outputDirectory ) {

        ArchiveEntry entry ;

        try (
            ArchiveInputStream zis = inputstreamToUncompress

        ) {

            while ( (entry = zis.getNextEntry()) != null ) {

                File target = new File ( outputDirectory, entry.getName() );
                target.getParentFile().mkdirs();

                if ( entry.isDirectory() ){
                    target.mkdirs();

                } else {

                    int     length = 0;
                    byte [] buffer = new byte[ 1024 * 8 ];

                    BufferedOutputStream bos = new BufferedOutputStream( new FileOutputStream(target) );

                    while ( (length = zis.read(buffer)) >= 0 ){
                        bos.write(buffer, 0, length);
                    }

                    bos.close();
                }

            }
        } catch( IOException e ) {
            throw new IoException( e );
        }

    }

    public void unzip( File fileToUnzip, File targetDirectory, Charset charset ) {

        try {

            ArchiveInputStream stream = new ZipArchiveInputStream( new FileInputStream(fileToUnzip), charset.name(), true );

            uncompress( stream, targetDirectory );

        } catch( IOException e ) {

            throw new IoException( e );
        }

    }

    private void compress( File fileOrDirectoryToCompress, ArchiveOutputStream archiveOutpuStream ) {

        List<File> fileList = new ArrayList<>();

        String basePath = "";

        if( fileOrDirectoryToCompress.isDirectory() ) {

            basePath = fileOrDirectoryToCompress.getPath();

            for( Path path : FileUtil.search( fileOrDirectoryToCompress.getPath(), true, false, -1, "**.*" ) ) {
                fileList.add( path.toFile() );
            }

        } else {
            basePath = fileOrDirectoryToCompress.getParent();
            fileList.add( fileOrDirectoryToCompress );

        }


        FileInputStream     fis = null;
        ArchiveOutputStream zos = null;

        try {

            zos = archiveOutpuStream;

            for( File file : fileList ) {

                String name = FileUtil.convertToRelativePath( basePath, file.getPath() );

                ArchiveEntry entry = zos.createArchiveEntry( file, name );

                zos.putArchiveEntry( entry );

                int    length;
                byte[] buf     = new byte[ 1024 * 8 ];

                fis = new FileInputStream( file );

                while ( (length = fis.read(buf, 0, buf.length)) >= 0 ){
                    zos.write(buf, 0, length);
                }

                fis.close();
                zos.closeArchiveEntry();

            }

        } catch( IOException e ) {
            throw new IoException( e );
        } finally {
            if( fis != null ) try { fis.close(); } catch( IOException e ) {}
            if( zos != null ) try { zos.closeArchiveEntry(); } catch( IOException e ) {}
        }

    }

    public void zip( File fileOrDirectoryToZip, File targetFile, Charset charset ) {

        try {

            ZipArchiveOutputStream stream = new ZipArchiveOutputStream( targetFile );

            stream.setEncoding( charset.name() );

            stream.setCreateUnicodeExtraFields( ZipArchiveOutputStream.UnicodeExtraFieldPolicy.ALWAYS );

            compress( fileOrDirectoryToZip, stream );

        } catch( IOException e ) {

            throw new IoException( e );
        }

    }

}
