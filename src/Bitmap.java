import java.util.Arrays;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Bitmap
{
    // The width, in pixels of the image
    private final int m_width;
    // The height, in pixels of the image
    private final int m_height;
    // Every Pixel components in the image
    private final byte m_components[];

    public int GetWidth(){return m_width; }
    public int GetHeight(){return m_height; }
    public byte GetComponent(int index) { return m_components[index]; }

    // ARGB(RGBA)를 사용할것임.
    // Create and initialize Bitmap
    /**
     * @param width The width, in pixels of the image
     * @param height The height, in pixels of the image
     * **/
    public Bitmap(int width, int height)
    {
        m_width = width;
        m_height = height;
        m_components = new byte[width * height * 4];

    }

    public Bitmap(String fileName) throws IOException
    {
        int width = 0;
        int height = 0;
        byte[] components = null;

        BufferedImage image = ImageIO.read(new File(fileName));

        width = image.getWidth();
        height = image.getHeight();

        int imgPixels[] = new int[width * height];
        image.getRGB(0, 0, width, height, imgPixels, 0, width);
        components = new byte[width * height * 4];

        for(int i = 0; i < width * height; i++)
        {
            int pixel = imgPixels[i];

            components[i * 4]     = (byte)((pixel >> 24) & 0xFF); // A
            components[i * 4 + 1] = (byte)((pixel      ) & 0xFF); // B
            components[i * 4 + 2] = (byte)((pixel >> 8 ) & 0xFF); // G
            components[i * 4 + 3] = (byte)((pixel >> 16) & 0xFF); // R
        }

        m_width = width;
        m_height = height;
        m_components = components;
    }
    //
    public void Clear(byte shade)
    {
        Arrays.fill(m_components,shade);
    }
    public void DrawPixel(int x, int y, byte a, byte b, byte g, byte r)
    {
        int index = (x + y * m_width) * 4;
        m_components[index    ] = a;
        m_components[index + 1] = b;
        m_components[index + 2] = g;
        m_components[index + 3] = r;
    }
    public void CopyPixel(int destX, int destY, int srcX, int srcY, Bitmap src)
    {
        int destIndex = (destX + destY * m_width) * 4;
        int srcIndex = (srcX + srcY * src.GetWidth()) * 4;
        m_components[destIndex    ] = src.GetComponent(srcIndex);
        m_components[destIndex + 1] = src.GetComponent(srcIndex + 1);
        m_components[destIndex + 2] = src.GetComponent(srcIndex + 2);
        m_components[destIndex + 3] = src.GetComponent(srcIndex + 3);
    }


    public void CopyToByteArray(byte[] dest)
    {
        for(int i = 0 ; i < m_width * m_height ; i++) {
            dest[i*3    ] = m_components[i*4 + 1];
            dest[i*3 + 1] = m_components[i*4 + 2];
            dest[i*3 + 2] = m_components[i*4 + 3];
        }
    }



/*    public void CopyToIntArray(int[] dest)
    {
        for(int i = 0 ; i < m_width * m_height ; i++) {
            int a = (int) m_components[i * 4] << 24;
            int r = (int) m_components[i * 4 + 1] << 16;
            int g = (int) m_components[i * 4 + 2] << 8;
            int b = (int) m_components[i * 4 + 3];

            dest[i] = a | r | g | b;
        }
    }*/

}

