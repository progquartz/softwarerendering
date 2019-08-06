public class Edge
{
    private float m_x;
    private float m_xStep;
    private int m_yStart;
    private int m_yEnd;
    private float m_texCoordX;
    private float m_texCoordXStep;
    private float m_texCoordY;
    private float m_texCoordYStep;
    private float m_oneOverZ;
    private float m_oneOverZStep;
    private float m_depth;
    private float m_depthStep;

    public float GetX() { return m_x; }
    public int GetYStart() { return m_yStart; }
    public int GetYEnd() { return m_yEnd; }
    public float GetTexCoordX() { return m_texCoordX; }
    public float GetTexCoordY() { return m_texCoordY; }
    public float GetOneOverZ() { return m_oneOverZ; }
    public float GetDepth() { return m_depth; }

    public Edge(Gradients gradients, Vertex minYVert, Vertex maxYVert, int minYVertIndex)
    {
        m_yStart = (int)Math.ceil(minYVert.GetY());
        m_yEnd = (int)Math.ceil(maxYVert.GetY());

        float yDist = maxYVert.GetY() - minYVert.GetY();
        float xDist = maxYVert.GetX() - minYVert.GetX();

        float yPrestep = m_yStart - minYVert.GetY();
        m_xStep = (float)xDist/(float)yDist;
        m_x = minYVert.GetX() + yPrestep * m_xStep;
        float xPrestep = m_x - minYVert.GetX();

        m_texCoordX = gradients.GetTexCoordX(minYVertIndex) +
                gradients.GetTexCoordXXStep() * xPrestep +
                gradients.GetTexCoordXYStep() * yPrestep;
        m_texCoordXStep = gradients.GetTexCoordXYStep() + gradients.GetTexCoordXXStep() * m_xStep;

        m_texCoordY = gradients.GetTexCoordY(minYVertIndex) +
                gradients.GetTexCoordYXStep() * xPrestep +
                gradients.GetTexCoordYYStep() * yPrestep;
        m_texCoordYStep = gradients.GetTexCoordYYStep() + gradients.GetTexCoordYXStep() * m_xStep;

        m_oneOverZ = gradients.GetOneOverZ(minYVertIndex) +
                gradients.GetOneOverZXStep() * xPrestep +
                gradients.GetOneOverZYStep() * yPrestep;
        m_oneOverZStep = gradients.GetOneOverZYStep() + gradients.GetOneOverZXStep() * m_xStep;

        m_depth = gradients.GetDepth(minYVertIndex) +
                gradients.GetDepthXStep() * xPrestep +
                gradients.GetDepthYStep() * yPrestep;
        m_depthStep = gradients.GetDepthYStep() + gradients.GetDepthXStep() * m_xStep;
    }

    public void Step()
    {
        m_x += m_xStep;
        m_texCoordX += m_texCoordXStep;
        m_texCoordY += m_texCoordYStep;
        m_oneOverZ += m_oneOverZStep;
        m_depth += m_depthStep;
    }
}
    /*
        int yStart = (int)Math.ceil(minYVert.GetY());
        int yEnd   = (int)Math.ceil(maxYVert.GetY());
        int xStart = (int)Math.ceil(minYVert.GetX());
        int xEnd   = (int)Math.ceil(maxYVert.GetX());

        float yDist = maxYVert.GetY() - minYVert.GetY();
        float xDist = maxYVert.GetX() - minYVert.GetX();

        if(yDist <= 0)
        {
            return;
        }

        float xStep = (float)xDist/(float)yDist;
        float yPrestep = yStart - minYVert.GetY();
        float curX = minYVert.GetX() + yPrestep * xStep;

        for(int j = yStart; j < yEnd; j++)
        {
            m_scanBuffer[j * 2 + whichSide] = (int)Math.ceil(curX);
            curX += xStep;
        }

     */
