public class Edge
{
    // all information to load a triangle.
    private float m_x;
    private float m_xStep;
    private int m_yStart;
    private int m_yEnd;

    public float GetX() { return m_x; }
    public int GetYStart() { return m_yStart; }
    public int GetYEnd() { return m_yEnd; }

    public Edge(Vertex minYVert, Vertex maxYVert)
    {
        m_yStart = (int)Math.ceil(minYVert.GetY());
        m_yEnd = (int)Math.ceil(maxYVert.GetY());

        float yDist = maxYVert.GetY() - minYVert.GetY();
        float xDist = maxYVert.GetX() - minYVert.GetX();

        float yPrestep = m_yStart - minYVert.GetY();
        m_xStep = (float)xDist/(float)yDist;
        m_x = minYVert.GetX() + yPrestep * m_xStep;
    }

    public void Step()
    {
        m_x += m_xStep;
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
