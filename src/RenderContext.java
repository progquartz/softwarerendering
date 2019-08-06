import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class RenderContext extends Bitmap
{
    private float[] m_zBuffer;

    public RenderContext(int width, int height){
        super(width, height);
        m_zBuffer = new float[width*height];
    }

    public void ClearDepthBuffer(){
        for(int i = 0 ; i < m_zBuffer.length ; i++){
            m_zBuffer[i] = Float.MAX_VALUE;
        }
    }
    public void DrawTriangle(Vertex v1 , Vertex v2, Vertex v3, Bitmap texture) {
        List<Vertex> vertices = new ArrayList<>();
        List<Vertex> auxillaryList = new ArrayList<>();

        vertices.add(v1);
        vertices.add(v2);
        vertices.add(v3);

        if(ClipPolygonAxis(vertices,auxillaryList,0) &&
                ClipPolygonAxis(vertices,auxillaryList,1) &&
                ClipPolygonAxis(vertices,auxillaryList,2) )
        {
            Vertex initialVertex = vertices.get(0);

            for(int i = 0 ; i < vertices.size() -1; i++){
                FillTriangle(initialVertex,vertices.get(i), vertices.get(i+1), texture);
            }
        }
    }
        private boolean ClipPolygonAxis(List<Vertex> vertices , List<Vertex> auxillaryList, int componentIndex){
        ClipPolygonComponent(vertices, componentIndex, 1.0f, auxillaryList);
        vertices.clear();

        if(auxillaryList.isEmpty()){
            return false;
        }
        ClipPolygonComponent(auxillaryList,componentIndex, -1.0f, vertices);
        auxillaryList.clear();

        return !vertices.isEmpty();
    }
    public void DrawMesh(Mesh mesh, Matrix4f transform , Bitmap texture){
        for(int i = 0 ; i <  mesh.GetNumIndices(); i+=3){
            FillTriangle(
                    mesh.GetVertex(mesh.GetIndex(i)).Transform(transform),
                    mesh.GetVertex(mesh.GetIndex(i + 1)).Transform(transform),
                    mesh.GetVertex(mesh.GetIndex(i + 2)).Transform(transform),
                    texture);

        }
    }

    private void ClipPolygonComponent(List<Vertex> vertices, int componentIndex, float componentFactor, List<Vertex> result){
        Vertex previousVertex = vertices.get(vertices.size() - 1);
        float previousComponent = previousVertex.Get(componentIndex)* componentFactor;
        boolean previousInside = previousComponent <= previousVertex.GetPosition().GetW();

        Iterator<Vertex> it = vertices.iterator();
        while(it.hasNext()){
            Vertex currentVertex = it.next();
            float currentComponent = currentVertex.Get(componentIndex)* componentFactor;
            boolean currentInside = currentComponent <=currentVertex.GetPosition().GetW();

            if(currentInside ^ previousInside){
                float lerpAmt =
                        (previousVertex.GetPosition().GetW() - previousComponent) /
                        ((previousVertex.GetPosition().GetW() - previousComponent) -
                        (currentVertex.GetPosition().GetW()- currentComponent));

                result.add(previousVertex.Lerp(currentVertex,lerpAmt));
            }
            if(currentInside){
                result.add(currentVertex);
            }
            previousVertex = currentVertex;
            previousComponent = currentComponent;
            previousInside = currentInside;
        }
    }

    private void FillTriangle(Vertex v1 , Vertex v2, Vertex v3, Bitmap texture){
        Matrix4f screenSpaceTransform = new Matrix4f().InitScreenSpaceTransform(GetWidth()/2 , GetHeight()/2);
        Vertex minYvert = v1.Transform(screenSpaceTransform).PerspectiveDivide();
        Vertex midYvert = v2.Transform(screenSpaceTransform).PerspectiveDivide();
        Vertex maxYvert = v3.Transform(screenSpaceTransform).PerspectiveDivide();

        if(minYvert.TriangleAreaTimesTwo(maxYvert,midYvert) >= 0){
            return ;
        }

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
        ScanTriangle(minYvert, midYvert, maxYvert, minYvert.TriangleAreaTimesTwo(maxYvert, midYvert) >= 0, texture);

//        float area = minYvert.TriangleAreaTimesTwo(maxYvert,midYvert);
//        int handedness = area >= 0 ? 1 : 0;
//
//        ScanConvertTriangle(minYvert , midYvert, maxYvert , handedness);
//        FillShape((int)Math.ceil(minYvert.GetY()),(int)Math.ceil(maxYvert.GetY()));
    }

    private void ScanTriangle(Vertex minYVert, Vertex midYVert, Vertex maxYVert, boolean handedness, Bitmap texture)
    {
        Gradients gradients = new Gradients(minYVert, midYVert, maxYVert);
        Edge topToBottom    = new Edge(gradients, minYVert, maxYVert, 0);
        Edge topToMiddle    = new Edge(gradients, minYVert, midYVert, 0);
        Edge middleToBottom = new Edge(gradients, midYVert, maxYVert, 1);

        ScanEdges(gradients, topToBottom, topToMiddle, handedness, texture);
        ScanEdges(gradients, topToBottom, middleToBottom, handedness, texture);;
    }

    private void ScanEdges(Gradients gradients, Edge a, Edge b, boolean handedness, Bitmap texture)
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
            DrawScanLine(gradients, left, right, j, texture);
            left.Step();
            right.Step();
        }
    }

    private void DrawScanLine(Gradients gradients, Edge left, Edge right, int j , Bitmap texture)
    {
        int xMin = (int)Math.ceil(left.GetX());
        int xMax = (int)Math.ceil(right.GetX());
        float xPrestep = xMin - left.GetX();


        float xDist = right.GetX() - left.GetX();
        float texCoordXXStep = (right.GetTexCoordX() - left.GetTexCoordX())/xDist;
        float texCoordYXStep = (right.GetTexCoordY() - left.GetTexCoordY())/xDist;
        float oneOverZXStep = (right.GetOneOverZ() - left.GetOneOverZ())/xDist;
        float depthXStep = (right.GetDepth() - left.GetDepth())/xDist;


        float texCoordX = left.GetTexCoordX() + texCoordXXStep * xPrestep;
        float texCoordY = left.GetTexCoordY() + texCoordYXStep * xPrestep;
        float oneOverZ = left.GetOneOverZ() + oneOverZXStep * xPrestep;
        float depth = left.GetDepth() + depthXStep * xPrestep;

        //Vector4f color = left.GetColor().Add(gradients.GetColorXStep().Mul(xPrestep));

        for(int i = xMin; i < xMax; i++)
        {
            int index = i + j * GetWidth();
            if(depth < m_zBuffer[index]) {
                m_zBuffer[index] = depth;
                float z = 1.0f / oneOverZ;
                int srcX = (int) ((texCoordX * z) * ((texture.GetWidth() - 1) + 0.5f));
                int srcY = (int) ((texCoordY * z) * ((texture.GetHeight() - 1) + 0.5f));

                CopyPixel(i, j, srcX, srcY, texture);
            }
            oneOverZ += oneOverZXStep;
            texCoordX += texCoordXXStep;
            texCoordY += texCoordYXStep;
            depth += depthXStep;
            //color = color.Add(gradients.GetColorXStep());
        }
    }
}
