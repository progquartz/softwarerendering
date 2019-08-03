public class RenderContext extends Bitmap
{

    public RenderContext(int width, int height){
        super(width, height);
    }

    public void FillTriangle(Vertex v1 , Vertex v2, Vertex v3){
        Matrix4f screenSpaceTransform = new Matrix4f().InitScreenSpaceTransform(GetWidth()/2 , GetHeight()/2);
        Vertex minYvert = v1.Transform(screenSpaceTransform).PerspectiveDivide();
        Vertex midYvert = v2.Transform(screenSpaceTransform).PerspectiveDivide();
        Vertex maxYvert = v3.Transform(screenSpaceTransform).PerspectiveDivide();

        if(maxYvert.GetY() < midYvert.GetY()){
            Vertex temp = maxYvert;
            maxYvert = midYvert;
            midYvert = temp;
        }

        if(midYvert.GetY() < minYvert.GetY()){
            Vertex temp = midYvert;
            midYvert = minYvert;
            minYvert = temp;
        }

        if(maxYvert.GetY() < midYvert.GetY()){
            Vertex temp = maxYvert;
            maxYvert = midYvert;
            midYvert = temp;
        }
        ScanTriangle(minYvert , midYvert, maxYvert ,minYvert.TriangleAreaTimesTwo(maxYvert,midYvert) >= 0);

//        float area = minYvert.TriangleAreaTimesTwo(maxYvert,midYvert);
//        int handedness = area >= 0 ? 1 : 0;
//
//        ScanConvertTriangle(minYvert , midYvert, maxYvert , handedness);
//        FillShape((int)Math.ceil(minYvert.GetY()),(int)Math.ceil(maxYvert.GetY()));
    }

    public void ScanTriangle(Vertex minYVert, Vertex midYVert, Vertex maxYVert, boolean handedness)
    {
        Edge topToBotton = new Edge(minYVert,maxYVert);
        Edge topToMiddle = new Edge(minYVert,midYVert);
        Edge middleToBotton = new Edge(midYVert,maxYVert);


        ScanEdges(topToBotton, topToMiddle , handedness);
        ScanEdges(topToBotton,middleToBotton , handedness);
    }

    private void ScanEdges(Edge a, Edge b, boolean handedness)
    {
        // if the handedness is 0, then the top to the bottom is left edge.
        Edge left = a;
        Edge right = b;
        if(handedness)
        {
            Edge temp = left;
            left = right;
            right = temp;
        }

        int yStart = b.GetYStart();
        int yEnd   = b.GetYEnd();
        for(int j = yStart; j < yEnd; j++)
        {
            DrawScanLine(left, right, j);
            left.Step();
            right.Step();
        }
    }

    private void DrawScanLine ( Edge left, Edge right , int j)
    {
        int xMin = (int)Math.ceil(left.GetX());
        int xMax = (int)Math.ceil(right.GetX());

        for(int i = xMin ; i < xMax ; i++) {
            DrawPixel(i, j, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF);
        }
    }
}
