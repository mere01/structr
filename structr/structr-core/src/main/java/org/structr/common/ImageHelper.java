/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.structr.common;

import com.mortennobel.imagescaling.ResampleOp;
import java.awt.image.BufferedImage;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.commons.lang.StringUtils;
import org.structr.core.entity.Image;

/**
 *
 * @author axel
 */
public abstract class ImageHelper {

    private static final Logger logger = Logger.getLogger(ImageHelper.class.getName());
    private static Thumbnail tn = new Thumbnail();

    public static class Thumbnail {

        public static final String FORMAT = "png";

        private byte[] bytes;
        private int width;
        private int height;

        public Thumbnail() {
        }

        public Thumbnail(final byte[] bytes) {
            this.bytes = bytes;
        }

        public Thumbnail(final int width, final int height) {
            this.width = width;
            this.height = height;
        }

        public Thumbnail(final byte[] bytes, final int width, final int height) {
            this.bytes = bytes;
            this.width = width;
            this.height = height;
        }

        public byte[] getBytes() {
            return bytes;
        }

        public void setBytes(final byte[] bytes) {
            this.bytes = bytes;
        }

        public int getWidth() {
            return width;
        }

        public void setWidth(final int width) {
            this.width = width;
        }

        public int getHeight() {
            return height;
        }

        public void setHeight(final int height) {
            this.height = height;
        }
    }

    public static Thumbnail createThumbnail(final Image originalImage, final int maxWidth, final int maxHeight) {

        //String contentType = (String) originalImage.getProperty(Image.CONTENT_TYPE_KEY);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try {
            // read image
            long start = System.nanoTime();

            BufferedImage source = ImageIO.read(originalImage.getInputStream());

            if (source != null) {

                int sourceWidth = source.getWidth();
                int sourceHeight = source.getHeight();

                // Update image dimensions
                originalImage.setWidth(sourceWidth);
                originalImage.setHeight(sourceHeight);

                //float aspectRatio = sourceWidth/sourceHeight;

                float scaleX = 1.0f * sourceWidth / maxWidth;
                float scaleY = 1.0f * sourceHeight / maxHeight;

                float scale = Math.max(scaleX, scaleY);

//            System.out.println("Source (w,h): " + sourceWidth + ", " + sourceHeight + ", Scale (x,y,res): " + scaleX + ", " + scaleY + ", " + scale);

                // Don't scale up
                if (scale > 1.0000f) {

                    int destWidth = Math.max(3, Math.round(sourceWidth / scale));
                    int destHeight = Math.max(3, Math.round(sourceHeight / scale));

                    tn.setWidth(destWidth);
                    tn.setHeight(destHeight);

//                System.out.println("Dest (w,h): " + destWidth + ", " + destHeight);

                    ResampleOp resampleOp = new ResampleOp(destWidth, destHeight);
                    //resampleOp.setUnsharpenMask(AdvancedResizeOp.UnsharpenMask.Soft);
                    BufferedImage dest = resampleOp.filter(source, null);

                    ImageIO.write(dest, Thumbnail.FORMAT, baos);
                } else {
                    ImageIO.write(source, Thumbnail.FORMAT, baos);
                }
            } else {
                logger.log(Level.SEVERE, "Thumbnail could not be created");
                return null;
            }

            long end = System.nanoTime();
            long time = (end - start) / 1000000;
            logger.log(Level.FINE, "Thumbnail created. Reading, scaling and writing took {0} ms", time);

            tn.setBytes(baos.toByteArray());
            return tn;

        } catch (Throwable t) {
            logger.log(Level.SEVERE, "Error creating thumbnail", t);
        }

        return null;
    }


    /**
     * Check if url points to an image by extension
     *
     * TODO: Improve method to check file type by peeping at the
     * content
     *
     * @param urlString
     * @return
     */
    public static boolean isImageType(final String urlString) {

        if (urlString == null || StringUtils.isBlank(urlString)) return false;
        String extension = urlString.toLowerCase().substring(urlString.lastIndexOf(".")+1);
        String[] imageExtensions = {"png", "gif", "jpg", "jpeg", "bmp", "tif", "tiff"};
        for (String ext : imageExtensions) {
            if (ext.equals(extension)) {
                return true;
            }
        }
        return false;
    }


    /**
     * Check if url points to an Flash object by extension
     *
     * TODO: Improve method to check file type by peeping at the
     * content
     *
     * @param urlString
     * @return
     */
    public static boolean isSwfType(final String urlString) {

        if (urlString == null || StringUtils.isBlank(urlString)) return false;
        String extension = urlString.toLowerCase().substring(urlString.lastIndexOf(".")+1);
        String[] imageExtensions = {"swf"};
        for (String ext : imageExtensions) {
            if (ext.equals(extension)) {
                return true;
            }
        }
        return false;
    }
}
