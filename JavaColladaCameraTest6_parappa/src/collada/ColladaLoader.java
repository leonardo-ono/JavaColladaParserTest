package collada;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * ColladaLoader class.
 * 
 * @author Leonardo Ono (ono.leo80@gmail.com)
 */
public class ColladaLoader {
    
    private static Collada collada;
    private static final Logger LOG = Logger.getLogger(ColladaLoader.class.getName());

    private ColladaLoader() {
    }
    
    public static Collada load(String res) 
            throws ParserConfigurationException, SAXException, IOException  {
        
        collada = new Collada();
        
        DocumentBuilderFactory factory =
        DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        
        // https://docs.oracle.com/javase/tutorial/essential/exceptions/tryResourceClose.html
        try (InputStream is = ColladaLoader.class.getResourceAsStream(res)) {
            Document doc = builder.parse(is);
            Element root = doc.getDocumentElement();
            NodeList childNodes = root.getChildNodes();
            for (int i = 0; i < childNodes.getLength(); i++) {
                Node child = childNodes.item(i);
                if (child.getNodeType() != Node.ELEMENT_NODE) {
                    continue;
                }
                //System.out.println("root child node: " + child.getNodeName());
                if (child.getNodeName().equals("library_cameras")) {
                    parseCameras(child); 
                }
                else if (child.getNodeName().equals("library_images")) {
                    parseImages(child); 
                }
                else if (child.getNodeName().equals("library_effects")) {
                    parseEffects(child); 
                }
                else if (child.getNodeName().equals("library_materials")) {
                    parseMaterials(child); 
                }
                else if (child.getNodeName().equals("library_geometries")) {
                    parseGeometries(child); 
                }
                else if (child.getNodeName().equals("library_controllers")) {
                    parseControllers(child); 
                }
                else if (child.getNodeName().equals("library_animations")) {
                    parseAnimations(child); 
                }
                else if (child.getNodeName().equals("library_visual_scenes")) {
                    parseVisualScenes(child); 
                }
                else if (child.getNodeName().equals("scene")) {
                    parseScene(child); 
                }
            }
        }
        
        return collada;
    }
    
    public static void main(String[] args) throws Exception {
        ColladaLoader.load("/res/camera.dae");
    }

    private static void parseCameras(Node node) {
        NodeList childNodes = node.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node child = childNodes.item(i);
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                //System.out.println("camera child nodes: " + child.getNodeName() + " content:" + child.getTextContent());
                Element element = (Element) child;
                
                //<camera id="Camera-camera" name="Camera">
                //  <optics>
                //    <technique_common>
                //      <perspective>
                //        <xfov sid="xfov">39.59775</xfov>
                //        <aspect_ratio>1.777778</aspect_ratio>
                //        <znear sid="znear">0.1</znear>
                //        <zfar sid="zfar">100</zfar>
                //      </perspective>
                //    </technique_common>
                //  </optics>
                //  ...
                String cameraId = "#" + element.getAttribute("id");
                Element optics = (Element) element.getElementsByTagName("optics").item(0);
                Element techniqueCommon = (Element) optics.getElementsByTagName("technique_common").item(0);
                Element perspective = (Element) techniqueCommon.getElementsByTagName("perspective").item(0);
                Element xfovElem = (Element) perspective.getElementsByTagName("xfov").item(0);
                Element aspectRatioElem = (Element) perspective.getElementsByTagName("aspect_ratio").item(0);
                Element znearElem = (Element) perspective.getElementsByTagName("znear").item(0);
                Element zfarElem = (Element) perspective.getElementsByTagName("zfar").item(0);
                double xfov = Double.parseDouble(xfovElem.getTextContent()); // in degrees
                double aspectRatio = Double.parseDouble(aspectRatioElem.getTextContent());
                double znear = Double.parseDouble(znearElem.getTextContent());
                double zfar = Double.parseDouble(zfarElem.getTextContent());
                Camera camera = new Camera(cameraId, null, xfov, aspectRatio, znear, zfar);
                collada.getLibraries().addCamera(camera);
            }
        }
    }

    private static void parseImages(Node node) {
        NodeList childNodes = node.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node child = childNodes.item(i);
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                //System.out.println("images child nodes: " + child.getNodeName() + " content:" + child.getTextContent());
                Element element = (Element) child;
                
                //<library_images>
                //  <image id="door_handle_png" name="door_handle_png">
                //    <init_from>door_handle.png</init_from>
                //  </image>
                //  <image id="door_png" name="door_png">
                //    <init_from>door.png</init_from>
                //  </image>
                //</library_images>
              
                String imageId = "#" + element.getAttribute("id");
                Element initFrom = (Element) element.getElementsByTagName("init_from").item(0);
                String imageRes = initFrom.getTextContent();
                collada.getLibraries().addImage(imageId, imageRes);
            }
        }
    }
    
    private static void parseEffects(Node node) {
        NodeList childNodes = node.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node child = childNodes.item(i);
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                //System.out.println("effects child nodes: " + child.getNodeName() + " content:" + child.getTextContent());
                Element element = (Element) child;

                //<effect id="Material_004-effect">
                //  <profile_COMMON>
                //    <newparam sid="door_handle_png-surface">
                //      <surface type="2D">
                //        <init_from>door_handle_png</init_from>
                //      </surface>
                //    </newparam>
                //    <newparam sid="door_handle_png-sampler">
                //      <sampler2D>
                //        <source>door_handle_png-surface</source>
                //      </sampler2D>
                //    </newparam>
                //    <technique sid="common">
                //      <lambert>
                //        <emission>
                //          <color sid="emission">0 0 0 1</color>
                //        </emission>
                //        <diffuse>
                //          <texture texture="door_handle_png-sampler" texcoord="UVMap"/>
                //        </diffuse>
                //        <index_of_refraction>
                //          <float sid="ior">1.45</float>
                //        </index_of_refraction>
                //      </lambert>
                //    </technique>
                //  </profile_COMMON>
                //</effect>

                String effectId = "#" + element.getAttribute("id");
                Element texture = (Element) element.getElementsByTagName("texture").item(0);
                
                if (texture != null) {
                    // TODO get image id correctly
                    String imageId = "#" + texture.getAttribute("texture").replace("-sampler", "");
                    Effect effect = new Effect(effectId);
                    effect.setImageId(imageId);
                    collada.getLibraries().addEffect(effect);
                }
            }
        }
    }

    private static void parseMaterials(Node node) {
        NodeList childNodes = node.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node child = childNodes.item(i);
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                //System.out.println("materials child nodes: " + child.getNodeName() + " content:" + child.getTextContent());
                Element element = (Element) child;

                //<library_materials>
                //  <material id="Material_004-material" name="Material.004">
                //    <instance_effect url="#Material_004-effect"/>
                //  </material>
                //  <material id="Material_003-material" name="Material.003">
                //    <instance_effect url="#Material_003-effect"/>
                //  </material>
                //</library_materials>
                
                String materialId = "#" + element.getAttribute("id");
                Element instanceEffect = (Element) element.getElementsByTagName("instance_effect").item(0);
                String effectId = instanceEffect.getAttribute("url");
                Effect effect = collada.getLibraries().getEffecs().get(effectId);
                
                if (effect != null) {
                    Material material = new Material(materialId);
                    BufferedImage textureImage = collada.getLibraries().getImages().get(effect.getImageId());
                    Texture texture = new Texture(effect.getImageId());
                    texture.setImage(textureImage);
                    material.setTexture(texture);
                    collada.getLibraries().addMaterial(material);
                }
                
            }
        }
    }
    
    private static void parseGeometries(Node node) {
        NodeList childNodes = node.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node child = childNodes.item(i);
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                //System.out.println("geometry child nodes: " + child.getNodeName());// + " content:" + child.getTextContent());
                Element element = (Element) child;

                //<geometry id="Cylinder-mesh" name="Cylinder">
                //  <mesh>
                //    <source id="Cylinder-mesh-positions">
                //      <float_array id="Cylinder-mesh-positions-array" count="240">0 0.00999999 -0.004999995 0 0.00999999 0.004999995 0.007071018 0.007071018 -0.004999995 0.007071018 0.007071018 0.004999995 0.00999999 0 -0.004999995 0.00999999 0 0.004999995 0.007071018 -0.007071018 -0.004999995 0.007071018 -0.007071018 0.004999995 0 -0.00999999 -0.004999995 0 -0.00999999 0.004999995 -0.007071018 -0.007071018 -0.004999995 -0.007071018 -0.007071018 0.004999995 -0.00999999 0 -0.004999995 -0.00999999 0 0.004999995 -0.007071018 0.007071018 -0.004999995 -0.007071018 0.007071018 0.004999995 0 0.003806233 -0.008703112 0.002691388 0.002691388 -0.008703112 0.003806233 0 -0.008703112 0.002691388 -0.002691388 -0.008703112 0 -0.003806233 -0.008703112 -0.002691388 -0.002691388 -0.008703112 -0.003806233 0 -0.008703112 -0.002691388 0.002691388 -0.008703112 0 0.003806233 -0.01564657 0.002691388 0.002691388 -0.01564657 0.003806233 0 -0.01564657 0.002691388 -0.002691388 -0.01564657 0 -0.003806233 -0.01564657 -0.002691388 -0.002691388 -0.01564657 -0.003806233 0 -0.01564657 -0.002691388 0.002691388 -0.01564657 0 0.00999999 -0.03356331 0 0.00999999 -0.0435633 0.007071077 0.007071018 -0.03356331 0.007071077 0.007071018 -0.0435633 0.00999999 0 -0.03356331 0.00999999 0 -0.0435633 0.007071077 -0.007071018 -0.03356331 0.007071077 -0.007071018 -0.0435633 0 -0.00999999 -0.03356331 0 -0.00999999 -0.0435633 -0.007071018 -0.007071018 -0.03356331 -0.007071018 -0.007071018 -0.0435633 -0.00999993 0 -0.03356331 -0.00999993 0 -0.0435633 -0.007071018 0.007071018 -0.03356331 -0.007071018 0.007071018 -0.0435633 0 0.003806233 -0.02986013 0.002691388 0.002691388 -0.02986013 0.003806233 0 -0.02986013 0.002691388 -0.002691388 -0.02986013 0 -0.003806233 -0.02986013 -0.002691388 -0.002691388 -0.02986013 -0.003806173 0 -0.02986013 -0.002691388 0.002691388 -0.02986013 0 0.003806233 -0.02291673 0.002691388 0.002691388 -0.02291673 0.003806233 0 -0.02291673 0.002691388 -0.002691388 -0.02291673 0 -0.003806233 -0.02291673 -0.002691388 -0.002691388 -0.02291673 -0.003806173 0 -0.02291673 -0.002691388 0.002691388 -0.02291673 0.00580728 0.0228424 -0.005375742 0.01120853 0.0206052 -0.005375742 0.01120853 0.0206052 0.002262711 0.00580728 0.0228424 0.002262711 0.00580728 0.0228424 -0.02719372 0.01120853 0.0206052 -0.02719372 0.01120853 0.0206052 -0.03483217 0.00580728 0.0228424 -0.03483217 0.0165292 0.03507226 -0.006057381 0.02004039 0.03361791 -0.006057381 0.02004039 0.03361791 -0.001091778 0.0165292 0.03507226 -0.001091778 0.0165292 0.03507226 -0.02024078 0.02004039 0.03361791 -0.02024078 0.02004039 0.03361791 -0.02520638 0.0165292 0.03507226 -0.02520638</float_array>
                //      <technique_common>
                //        <accessor source="#Cylinder-mesh-positions-array" count="80" stride="3">
                //          <param name="X" type="float"/>
                //          <param name="Y" type="float"/>
                //          <param name="Z" type="float"/>
                //        </accessor>
                //      </technique_common>
                //    </source>
                //    <source id="Cylinder-mesh-normals">
                //      <float_array id="Cylinder-mesh-normals-array" count="432">0.9563115 -0.2923501 0 0.9238795 0.3826836 0 0.9238796 -0.3826833 0 0.3826835 -0.9238796 0 -0.3826836 -0.9238795 0 -0.9238796 -0.3826836 0 0 0 1 -0.9238796 0.3826833 0 -0.3826835 0.9238795 0 0.2079133 -0.5019471 -0.8395364 -0.9238795 0.3826835 0 -0.5019472 0.2079133 -0.8395364 0.5019472 0.2079134 -0.8395363 -0.2079133 -0.5019472 -0.8395364 -0.2079134 0.5019471 -0.8395364 0.2079132 0.5019471 -0.8395364 0.5019472 -0.2079132 -0.8395363 -0.5019471 -0.2079132 -0.8395364 0 0 -1 -0.3826837 -0.9238795 0 0.9238795 -0.3826835 0 0.3826835 0.9238796 0 -0.3826835 0.9238795 0 -0.9238796 -0.3826832 0 0.3826835 -0.9238795 0 0.9238795 0.3826836 0 0.9111711 -0.4120284 0 -0.9238796 -0.3826836 3.08961e-7 -0.9238796 0.3826835 2.07297e-7 -0.3826836 0.9238795 0 0.3826836 0.9238796 -1.9013e-7 0.9238796 0.3826833 -3.8026e-7 2.91038e-7 0 1 0.9238796 -0.3826835 -3.08961e-7 0.3826832 -0.9238797 0 -0.2079137 0.5019469 -0.8395363 0.9238795 -0.3826836 -1.91095e-7 0.5019466 -0.2079133 -0.8395367 -0.5019468 -0.2079133 -0.8395366 0.2079136 0.5019472 -0.8395362 0.2079131 -0.5019472 -0.8395363 -0.2079137 -0.5019472 -0.8395361 -0.5019474 0.2079132 -0.8395362 0.501946 0.2079131 -0.839537 0 -3.46034e-7 -1 0.3826836 0.9238795 -2.5976e-7 -0.9238796 0.3826833 1.91095e-7 -0.3826836 -0.9238795 1.79854e-7 0.3826836 -0.9238795 0 0.9238795 0.3826835 -2.53866e-7 -0.3826833 0.9238796 0 -0.9238796 -0.3826833 2.53866e-7 0.7519438 -0.6592272 0 0.08143842 0.1966097 0.9770939 0.1576642 0.3806334 -0.9111862 0.07299387 0.1762226 0.9816402 -0.9563117 0.2923494 2.57057e-7 -0.2016006 -0.4867085 0.8499836 -0.9111708 0.4120287 0 -0.01020365 -0.02463412 -0.9996445 0.3826832 0.9238796 0 -0.3826841 -0.9238793 3.03667e-7 0.157455 0.380129 -0.9114329 -0.7519435 0.6592275 0 -0.01691997 -0.04084861 -0.9990221 -0.8274221 0.5615807 2.42272e-7 -0.2028157 -0.4896389 0.8480092 0.8274218 -0.561581 0 0.9563114 -0.2923501 0 0.9238796 0.3826833 0 0.9238795 -0.3826837 0 -0.3826836 -0.9238795 0 -0.9238796 -0.3826834 0 -1.31743e-7 0 1 -1.75657e-7 0 1 0 0 1 1.75657e-7 0 1 0 0 1 -0.9238796 0.3826835 0 0.2079131 -0.5019471 -0.8395364 -0.9238796 0.3826835 0 -0.5019469 0.2079132 -0.8395365 0.5019471 0.2079132 -0.8395363 -0.2079133 -0.5019472 -0.8395363 -0.2079131 0.5019471 -0.8395363 0.2079135 0.5019471 -0.8395364 0.5019469 -0.2079134 -0.8395364 -0.5019472 -0.2079133 -0.8395363 -0.3826836 -0.9238795 0 0.9238795 -0.3826836 0 0.3826835 0.9238795 0 -0.3826836 0.9238795 0 -0.9238795 -0.3826838 0 0.3826834 -0.9238796 0 0.9238796 0.3826834 0 0.911171 -0.4120286 -3.09737e-7 -0.9238796 -0.3826834 3.8026e-7 -0.9238796 0.3826833 3.08961e-7 -0.382683 0.9238798 0 0.3826836 0.9238795 -2.44262e-7 0.9238796 0.3826834 -3.08961e-7 1.40526e-6 2.63418e-7 1 0 6.35947e-7 1 0 -6.35947e-7 1 0 -2.63418e-7 1 -2.91038e-7 0 1 0.9238798 -0.382683 -2.07296e-7 0.3826836 -0.9238795 -2.81075e-7 -0.2079137 0.5019474 -0.8395361 0.9238795 -0.3826835 -1.7518e-7 0.5019471 -0.2079129 -0.8395364 -0.5019475 -0.2079131 -0.8395361 0.2079138 0.501947 -0.8395363 0.2079144 -0.5019471 -0.839536 -0.2079138 -0.5019469 -0.8395364 -0.5019468 0.2079133 -0.8395366 0.5019466 0.2079133 -0.8395367 0 8.35399e-7 -1 -4.84989e-6 -3.46034e-7 -1 0 8.35399e-7 -1 -2.42495e-6 -3.46034e-7 -1 1.00445e-6 -3.46034e-7 -1 0.3826836 0.9238795 0 -0.9238795 0.3826835 1.79854e-7 -0.3826832 -0.9238796 1.95769e-7 0.3826836 -0.9238795 0 0.9238796 0.3826832 -2.47299e-7 -0.3826835 0.9238796 0 -0.9238795 -0.3826836 2.47299e-7 0.7519439 -0.6592273 -4.68531e-7 0.08143842 0.1966097 0.9770939 0.1576632 0.3806334 -0.9111865 0.07299381 0.1762226 0.9816402 -0.9563115 0.2923496 3.24737e-7 -0.2016004 -0.4867086 0.8499836 -0.9111709 0.4120287 0 -0.01020389 -0.02463412 -0.9996445 0.3826835 0.9238795 0 -0.3826843 -0.9238792 0 0.1574552 0.3801298 -0.9114325 -0.7519437 0.6592274 0 -0.01691991 -0.04084861 -0.9990221 -0.8274222 0.5615805 4.85638e-7 -0.2028155 -0.4896386 0.8480094</float_array>
                //      <technique_common>
                //        <accessor source="#Cylinder-mesh-normals-array" count="144" stride="3">
                //          <param name="X" type="float"/>
                //          <param name="Y" type="float"/>
                //          <param name="Z" type="float"/>
                //        </accessor>
                //      </technique_common>
                //    </source>
                //    <source id="Cylinder-mesh-map-0">
                //      <float_array id="Cylinder-mesh-map-0-array" count="912">0.8618571 0.03613919 0.8618571 0.4314417 0.8618571 0.4314417 0.8618571 0.4314417 0.7410247 0.03613919 0.8618571 0.03613919 0.7410247 0.4314417 0.6201927 0.03613919 0.7410247 0.03613919 0.6201927 0.4314417 0.4993604 0.03613919 0.6201927 0.03613919 0.4993604 0.4314417 0.3785282 0.03613919 0.4993604 0.03613919 0.3785282 0.4314417 0.257696 0.03613919 0.3785282 0.03613919 0.2901836 0.744344 0.424144 0.5125535 0.5581046 0.744344 0.257696 0.4314417 0.1368638 0.03613919 0.257696 0.03613919 0.1368638 0.4314417 0.01603174 0.03613919 0.1368638 0.03613919 0.4993604 0.03613919 0.6201927 0.03613919 0.6201927 0.03613919 0.1368638 0.03613919 0.257696 0.03613919 0.257696 0.03613919 0.1368638 0.03613919 0.257696 0.03613919 0.257696 0.03613919 0.8618571 0.03613919 0.7410247 0.03613919 0.8618571 0.03613919 0.3785282 0.03613919 0.4993604 0.03613919 0.4993604 0.03613919 0.01603174 0.03613919 0.1368638 0.03613919 0.1368638 0.03613919 0.8618571 0.03613919 0.9826891 0.03613919 0.9826891 0.03613919 0.6201927 0.03613919 0.7410247 0.03613919 0.7410247 0.03613919 0.3785282 0.03613919 0.257696 0.03613919 0.3785282 0.03613919 0.5204725 0.5804435 0.3310238 0.5804435 0.3310238 0.9082446 0.3785282 0.03613919 0.4993604 0.03613919 0.4993604 0.03613919 0.6201927 0.03613919 0.7410247 0.03613919 0.7410247 0.03613919 0.9826891 0.03613919 0.8618571 0.03613919 0.9826891 0.03613919 0.1368638 0.03613919 0.01603174 0.03613919 0.1368638 0.03613919 0.257696 0.03613919 0.3785282 0.03613919 0.3785282 0.03613919 0.4993604 0.03613919 0.6201927 0.03613919 0.6201927 0.03613919 0.8618571 0.03613919 0.7410247 0.03613919 0.8618571 0.03613919 0.01603174 0.03613919 0.01603174 0.4314417 0.01603174 0.03613919 0.8618571 0.4314417 0.7410247 0.03613919 0.8618571 0.03613919 0.7410247 0.03613919 0.6201927 0.4314417 0.6201927 0.03613919 0.6201927 0.4314417 0.4993604 0.03613919 0.6201927 0.03613919 0.4993604 0.4314417 0.3785282 0.03613919 0.4993604 0.03613919 0.3785282 0.03613919 0.257696 0.4314417 0.257696 0.03613919 0.2901836 0.744344 0.424144 0.5125535 0.5581046 0.744344 0.257696 0.4314417 0.1368638 0.03613919 0.257696 0.03613919 0.1368638 0.4314417 0.01603174 0.03613919 0.1368638 0.03613919 0.6201927 0.03613919 0.4993604 0.03613919 0.6201927 0.03613919 0.257696 0.03613919 0.1368638 0.03613919 0.257696 0.03613919 0.1368638 0.03613919 0.257696 0.03613919 0.257696 0.03613919 0.8618571 0.03613919 0.7410247 0.03613919 0.8618571 0.03613919 0.3785282 0.03613919 0.4993604 0.03613919 0.4993604 0.03613919 0.01603174 0.03613919 0.1368638 0.03613919 0.1368638 0.03613919 0.8618571 0.03613919 0.9826891 0.03613919 0.9826891 0.03613919 0.6201927 0.03613919 0.7410247 0.03613919 0.7410247 0.03613919 0.3785282 0.03613919 0.257696 0.03613919 0.3785282 0.03613919 0.5204725 0.5804435 0.3310238 0.5804435 0.3310238 0.9082446 0.3785282 0.03613919 0.4993604 0.03613919 0.4993604 0.03613919 0.6201927 0.03613919 0.7410247 0.03613919 0.7410247 0.03613919 0.9826891 0.03613919 0.8618571 0.03613919 0.9826891 0.03613919 0.01603174 0.03613919 0.1368638 0.03613919 0.1368638 0.03613919 0.257696 0.03613919 0.3785282 0.03613919 0.3785282 0.03613919 0.4993604 0.03613919 0.6201927 0.03613919 0.6201927 0.03613919 0.7410247 0.03613919 0.8618571 0.03613919 0.8618571 0.03613919 0.01603174 0.03613919 0.01603174 0.4314417 0.01603174 0.03613919 0.5188685 0.9082446 0.424144 0.9761345 0.424144 0.9761345 0.9826891 0.03613919 0.8618571 0.03613919 0.8618571 0.03613919 0.424144 0.9761345 0.5188685 0.9082446 0.424144 0.9761345 0.8618571 0.03613919 0.8618571 0.4314417 0.8618571 0.4314417 0.424144 0.9761345 0.5188685 0.9082446 0.424144 0.9761345 0.01603174 0.03613919 0.01603174 0.4314417 0.01603174 0.03613919 0.9826891 0.03613919 0.8618571 0.03613919 0.8618571 0.03613919 0.9826891 0.4314417 0.8618571 0.03613919 0.9826891 0.03613919 0.9826891 0.03613919 0.8618571 0.4314417 0.8618571 0.03613919 0.8618571 0.03613919 0.9826891 0.03613919 0.8618571 0.03613919 0.01603174 0.03613919 0.01603174 0.4314417 0.01603174 0.03613919 0.8618571 0.03613919 0.9826891 0.03613919 0.8618571 0.03613919 0.8618571 0.03613919 0.8618571 0.4314417 0.8618571 0.4314417 0.5188685 0.9082446 0.424144 0.9761345 0.424144 0.9761345 0.8618571 0.03613919 0.8618571 0.4314417 0.8618571 0.4314417 0.8618571 0.03613919 0.8618571 0.03613919 0.8618571 0.4314417 0.8618571 0.4314417 0.7410247 0.4314417 0.7410247 0.03613919 0.7410247 0.4314417 0.6201927 0.4314417 0.6201927 0.03613919 0.6201927 0.4314417 0.4993604 0.4314417 0.4993604 0.03613919 0.4993604 0.4314417 0.3785282 0.4314417 0.3785282 0.03613919 0.3785282 0.4314417 0.257696 0.4314417 0.257696 0.03613919 0.5581046 0.744344 0.5188685 0.9082446 0.424144 0.9761345 0.424144 0.9761345 0.3294197 0.9082446 0.2901836 0.744344 0.2901836 0.744344 0.3294197 0.5804435 0.424144 0.5125535 0.424144 0.5125535 0.5188685 0.5804435 0.5581046 0.744344 0.5581046 0.744344 0.424144 0.9761345 0.2901836 0.744344 0.257696 0.4314417 0.1368638 0.4314417 0.1368638 0.03613919 0.1368638 0.4314417 0.01603174 0.4314417 0.01603174 0.03613919 0.4993604 0.03613919 0.4993604 0.03613919 0.6201927 0.03613919 0.1368638 0.03613919 0.1368638 0.03613919 0.257696 0.03613919 0.1368638 0.03613919 0.1368638 0.03613919 0.257696 0.03613919 0.8618571 0.03613919 0.7410247 0.03613919 0.7410247 0.03613919 0.3785282 0.03613919 0.3785282 0.03613919 0.4993604 0.03613919 0.01603174 0.03613919 0.01603174 0.03613919 0.1368638 0.03613919 0.8618571 0.03613919 0.8618571 0.03613919 0.9826891 0.03613919 0.6201927 0.03613919 0.6201927 0.03613919 0.7410247 0.03613919 0.3785282 0.03613919 0.257696 0.03613919 0.257696 0.03613919 0.3310238 0.9082446 0.4257481 0.9761345 0.5204725 0.9082446 0.5204725 0.9082446 0.5597085 0.744344 0.5204725 0.5804435 0.5204725 0.5804435 0.4257481 0.5125535 0.3310238 0.5804435 0.3310238 0.5804435 0.2917878 0.744344 0.3310238 0.9082446 0.3310238 0.9082446 0.5204725 0.9082446 0.5204725 0.5804435 0.3785282 0.03613919 0.3785282 0.03613919 0.4993604 0.03613919 0.6201927 0.03613919 0.6201927 0.03613919 0.7410247 0.03613919 0.9826891 0.03613919 0.8618571 0.03613919 0.8618571 0.03613919 0.1368638 0.03613919 0.01603174 0.03613919 0.01603174 0.03613919 0.257696 0.03613919 0.257696 0.03613919 0.3785282 0.03613919 0.4993604 0.03613919 0.4993604 0.03613919 0.6201927 0.03613919 0.8618571 0.03613919 0.7410247 0.03613919 0.7410247 0.03613919 0.01603174 0.03613919 0.01603174 0.4314417 0.01603174 0.4314417 0.8618571 0.4314417 0.7410247 0.4314417 0.7410247 0.03613919 0.7410247 0.03613919 0.7410247 0.4314417 0.6201927 0.4314417 0.6201927 0.4314417 0.4993604 0.4314417 0.4993604 0.03613919 0.4993604 0.4314417 0.3785282 0.4314417 0.3785282 0.03613919 0.3785282 0.03613919 0.3785282 0.4314417 0.257696 0.4314417 0.5581046 0.744344 0.5188685 0.9082446 0.424144 0.9761345 0.424144 0.9761345 0.3294197 0.9082446 0.2901836 0.744344 0.2901836 0.744344 0.3294197 0.5804435 0.424144 0.5125535 0.424144 0.5125535 0.5188685 0.5804435 0.5581046 0.744344 0.5581046 0.744344 0.424144 0.9761345 0.2901836 0.744344 0.257696 0.4314417 0.1368638 0.4314417 0.1368638 0.03613919 0.1368638 0.4314417 0.01603174 0.4314417 0.01603174 0.03613919 0.6201927 0.03613919 0.4993604 0.03613919 0.4993604 0.03613919 0.257696 0.03613919 0.1368638 0.03613919 0.1368638 0.03613919 0.1368638 0.03613919 0.1368638 0.03613919 0.257696 0.03613919 0.8618571 0.03613919 0.7410247 0.03613919 0.7410247 0.03613919 0.3785282 0.03613919 0.3785282 0.03613919 0.4993604 0.03613919 0.01603174 0.03613919 0.01603174 0.03613919 0.1368638 0.03613919 0.8618571 0.03613919 0.8618571 0.03613919 0.9826891 0.03613919 0.6201927 0.03613919 0.6201927 0.03613919 0.7410247 0.03613919 0.3785282 0.03613919 0.257696 0.03613919 0.257696 0.03613919 0.3310238 0.9082446 0.4257481 0.9761345 0.5204725 0.9082446 0.5204725 0.9082446 0.5597085 0.744344 0.5204725 0.5804435 0.5204725 0.5804435 0.4257481 0.5125535 0.3310238 0.5804435 0.3310238 0.5804435 0.2917878 0.744344 0.3310238 0.9082446 0.3310238 0.9082446 0.5204725 0.9082446 0.5204725 0.5804435 0.3785282 0.03613919 0.3785282 0.03613919 0.4993604 0.03613919 0.6201927 0.03613919 0.6201927 0.03613919 0.7410247 0.03613919 0.9826891 0.03613919 0.8618571 0.03613919 0.8618571 0.03613919 0.01603174 0.03613919 0.01603174 0.03613919 0.1368638 0.03613919 0.257696 0.03613919 0.257696 0.03613919 0.3785282 0.03613919 0.4993604 0.03613919 0.4993604 0.03613919 0.6201927 0.03613919 0.7410247 0.03613919 0.7410247 0.03613919 0.8618571 0.03613919 0.01603174 0.03613919 0.01603174 0.4314417 0.01603174 0.4314417 0.5188685 0.9082446 0.5188685 0.9082446 0.424144 0.9761345 0.9826891 0.03613919 0.9826891 0.03613919 0.8618571 0.03613919 0.424144 0.9761345 0.5188685 0.9082446 0.5188685 0.9082446 0.8618571 0.03613919 0.8618571 0.03613919 0.8618571 0.4314417 0.424144 0.9761345 0.5188685 0.9082446 0.5188685 0.9082446 0.01603174 0.03613919 0.01603174 0.4314417 0.01603174 0.4314417 0.9826891 0.03613919 0.9826891 0.03613919 0.8618571 0.03613919 0.9826891 0.4314417 0.8618571 0.4314417 0.8618571 0.03613919 0.9826891 0.03613919 0.9826891 0.4314417 0.8618571 0.4314417 0.8618571 0.03613919 0.9826891 0.03613919 0.9826891 0.03613919 0.01603174 0.03613919 0.01603174 0.4314417 0.01603174 0.4314417 0.8618571 0.03613919 0.9826891 0.03613919 0.9826891 0.03613919 0.8618571 0.03613919 0.8618571 0.03613919 0.8618571 0.4314417 0.5188685 0.9082446 0.5188685 0.9082446 0.424144 0.9761345 0.8618571 0.03613919 0.8618571 0.03613919 0.8618571 0.4314417</float_array>
                //      <technique_common>
                //        <accessor source="#Cylinder-mesh-map-0-array" count="456" stride="2">
                //          <param name="S" type="float"/>
                //          <param name="T" type="float"/>
                //        </accessor>
                //      </technique_common>
                //    </source>
                //    <vertices id="Cylinder-mesh-vertices">
                //      <input semantic="POSITION" source="#Cylinder-mesh-positions"/>
                //    </vertices>
                //    <triangles material="Material_004-material" count="152">
                //      <input semantic="VERTEX" source="#Cylinder-mesh-vertices" offset="0"/>
                //      <input semantic="NORMAL" source="#Cylinder-mesh-normals" offset="1"/>
                //      <input semantic="TEXCOORD" source="#Cylinder-mesh-map-0" offset="2" set="0"/>
                //      <p>2 0 0 66 0 1 3 0 2 3 1 3 4 1 4 2 1 5 5 2 6 6 2 7 4 2 8 7 3 9 8 3 10 6 3 11 9 4 12 10 4 13 8 4 14 11 5 15 12 5 16 10 5 17 13 6 18 9 6 19 5 6 20 13 7 21 14 7 22 12 7 23 15 8 24 0 8 25 14 8 26 8 9 27 19 9 28 6 9 29 23 10 30 30 10 31 22 10 32 14 11 33 22 11 34 12 11 35 2 12 36 18 12 37 17 12 38 10 13 39 20 13 40 8 13 41 0 14 42 23 14 43 14 14 44 2 15 45 16 15 46 0 15 47 6 16 48 18 16 49 4 16 50 10 17 51 22 17 52 21 17 53 27 18 54 29 18 55 31 18 56 21 19 57 28 19 58 20 19 59 19 20 60 26 20 61 18 20 62 16 21 63 25 21 64 24 21 65 23 22 66 24 22 67 31 22 68 22 23 69 29 23 70 21 23 71 20 24 72 27 24 73 19 24 74 17 25 75 26 25 76 25 25 77 32 26 78 71 26 79 68 26 80 35 27 81 36 27 82 34 27 83 36 28 84 39 28 85 38 28 86 39 29 87 40 29 88 38 29 89 41 30 90 42 30 91 40 30 92 42 31 93 45 31 94 44 31 95 45 32 96 41 32 97 37 32 98 45 33 99 46 33 100 44 33 101 47 34 102 32 34 103 46 34 104 38 35 105 52 35 106 51 35 107 54 36 108 63 36 109 62 36 110 46 37 111 54 37 112 44 37 113 34 38 114 50 38 115 49 38 116 42 39 117 52 39 118 40 39 119 32 40 120 55 40 121 46 40 122 34 41 123 48 41 124 32 41 125 38 42 126 50 42 127 36 42 128 42 43 129 54 43 130 53 43 131 59 44 132 61 44 133 63 44 134 53 45 135 60 45 136 52 45 137 51 46 138 58 46 139 50 46 140 48 47 141 57 47 142 56 47 143 48 48 144 63 48 145 55 48 146 54 49 147 61 49 148 53 49 149 52 50 150 59 50 151 51 50 152 50 51 153 57 51 154 49 51 155 68 52 156 79 52 157 76 52 158 66 53 159 75 53 160 67 53 161 32 54 162 69 54 163 34 54 164 1 55 165 66 55 166 67 55 167 34 56 168 70 56 169 35 56 170 33 57 171 70 57 172 71 57 173 0 58 174 67 58 175 64 58 176 0 59 177 65 59 178 2 59 179 75 60 180 73 60 181 72 60 182 76 61 183 78 61 184 77 61 185 69 62 186 76 62 187 77 62 188 64 63 189 75 63 190 72 63 191 65 64 192 72 64 193 73 64 194 69 65 195 78 65 196 70 65 197 70 66 198 79 66 199 71 66 200 65 67 201 74 67 202 66 67 203 2 68 204 65 68 205 66 68 206 3 69 207 5 69 208 4 69 209 5 70 210 7 70 211 6 70 212 7 3 213 9 3 214 8 3 215 9 71 216 11 71 217 10 71 218 11 72 219 13 72 220 12 72 221 5 73 222 3 73 223 1 73 224 1 74 225 15 74 226 13 74 227 13 75 228 11 75 229 9 75 230 9 76 231 7 76 232 5 76 233 5 77 234 1 77 235 13 77 236 13 78 237 15 78 238 14 78 239 15 8 240 1 8 241 0 8 242 8 79 243 20 79 244 19 79 245 23 80 246 31 80 247 30 80 248 14 81 249 23 81 250 22 81 251 2 82 252 4 82 253 18 82 254 10 83 255 21 83 256 20 83 257 0 84 258 16 84 259 23 84 260 2 85 261 17 85 262 16 85 263 6 86 264 19 86 265 18 86 266 10 87 267 12 87 268 22 87 269 31 18 270 24 18 271 25 18 272 25 18 273 26 18 274 27 18 275 27 18 276 28 18 277 29 18 278 29 18 279 30 18 280 31 18 281 31 18 282 25 18 283 27 18 284 21 88 285 29 88 286 28 88 287 19 89 288 27 89 289 26 89 290 16 90 291 17 90 292 25 90 293 23 91 294 16 91 295 24 91 296 22 92 297 30 92 298 29 92 299 20 93 300 28 93 301 27 93 302 17 94 303 18 94 304 26 94 305 32 95 306 33 95 307 71 95 308 35 96 309 37 96 310 36 96 311 36 97 312 37 97 313 39 97 314 39 98 315 41 98 316 40 98 317 41 99 318 43 99 319 42 99 320 42 100 321 43 100 322 45 100 323 37 101 324 35 101 325 33 101 326 33 102 327 47 102 328 45 102 329 45 103 330 43 103 331 41 103 332 41 104 333 39 104 334 37 104 335 37 105 336 33 105 337 45 105 338 45 106 339 47 106 340 46 106 341 47 107 342 33 107 343 32 107 344 38 108 345 40 108 346 52 108 347 54 109 348 55 109 349 63 109 350 46 110 351 55 110 352 54 110 353 34 111 354 36 111 355 50 111 356 42 112 357 53 112 358 52 112 359 32 113 360 48 113 361 55 113 362 34 114 363 49 114 364 48 114 365 38 115 366 51 115 367 50 115 368 42 116 369 44 116 370 54 116 371 63 117 372 56 117 373 57 117 374 57 118 375 58 118 376 59 118 377 59 119 378 60 119 379 61 119 380 61 120 381 62 120 382 63 120 383 63 121 384 57 121 385 59 121 386 53 122 387 61 122 388 60 122 389 51 123 390 59 123 391 58 123 392 48 124 393 49 124 394 57 124 395 48 125 396 56 125 397 63 125 398 54 126 399 62 126 400 61 126 401 52 127 402 60 127 403 59 127 404 50 128 405 58 128 406 57 128 407 68 129 408 71 129 409 79 129 410 66 130 411 74 130 412 75 130 413 32 131 414 68 131 415 69 131 416 1 132 417 3 132 418 66 132 419 34 133 420 69 133 421 70 133 422 33 134 423 35 134 424 70 134 425 0 135 426 1 135 427 67 135 428 0 136 429 64 136 430 65 136 431 75 137 432 74 137 433 73 137 434 76 138 435 79 138 436 78 138 437 69 139 438 68 139 439 76 139 440 64 140 441 67 140 442 75 140 443 65 141 444 64 141 445 72 141 446 69 142 447 77 142 448 78 142 449 70 143 450 78 143 451 79 143 452 65 67 453 73 67 454 74 67 455</p>
                //    </triangles>
                //  </mesh>
                //</geometry>
                
                String geometryId = "#" + element.getAttribute("id");
                Element mesh = (Element) element.getElementsByTagName("mesh").item(0);
                 
                // TODO check attribute "<id>-positions"
                Element sourcePositions = (Element) mesh.getElementsByTagName("source").item(0);
                Element floatArrayElemPositions = (Element) sourcePositions.getElementsByTagName("float_array").item(0);

                Element sourceNormals = (Element) mesh.getElementsByTagName("source").item(1);
                Element floatArrayElemNormals = (Element) sourceNormals.getElementsByTagName("float_array").item(0);

                Element stNormals = (Element) mesh.getElementsByTagName("source").item(2);
                Element floatArrayElemSts = (Element) stNormals.getElementsByTagName("float_array").item(0);
                
                int countPositions = Integer.parseInt(floatArrayElemPositions.getAttribute("count"));
                int countNormals = Integer.parseInt(floatArrayElemNormals.getAttribute("count"));
                int countSts = Integer.parseInt(floatArrayElemSts.getAttribute("count"));
                
                String floatArrayPositions = floatArrayElemPositions.getTextContent();
                String floatArrayNormals = floatArrayElemNormals.getTextContent();
                String floatArraySts = floatArrayElemSts.getTextContent();
                
                Geometry geometry = new Geometry(geometryId, null
                        , countPositions / 3, floatArrayPositions
                        , countSts / 2, floatArraySts
                        , countNormals / 3, floatArrayNormals);
                
                collada.getLibraries().addGeometry(geometry);
                
                NodeList trianglesList = mesh.getElementsByTagName("triangles");
                for (int t = 0; t < trianglesList.getLength(); t++) {
                    Element triangles = (Element) trianglesList.item(t); // mesh.getElementsByTagName("triangles").item(0);
                    Element pElem = (Element) triangles.getElementsByTagName("p").item(0);
                    int trianglesCount = Integer.parseInt(triangles.getAttribute("count"));
                    String p = pElem.getTextContent();

                    String materialId = "#" + triangles.getAttribute("material");
                    Material material = collada.getLibraries().getMaterials().get(materialId);

                    geometry.addTriangles(trianglesCount, p, material);
                }
            }
        }        
    }

    private static void parseControllers(Node node) {
        NodeList childNodes = node.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node child = childNodes.item(i);
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                //System.out.println("controllers child nodes: " + child.getNodeName()); // + " content:" + child.getTextContent());
                Element element = (Element) child;
                
                //<controller id="flag_loop-morph" name="flag_loop-morph">
                //  <morph source="#flag_loop-mesh" method="NORMALIZED">
                //    <source id="flag_loop-targets">
                //      <IDREF_array id="flag_loop-targets-array" count="198">flag_loop-mesh_morph_frame2 flag_loop-mesh_morph_frame3 flag_loop-mesh_morph_frame4 flag_loop-mesh_morph_frame5 flag_loop-mesh_morph_frame6 flag_loop-mesh_morph_frame7 flag_loop-mesh_morph_frame8 flag_loop-mesh_morph_frame9 flag_loop-mesh_morph_frame10 flag_loop-mesh_morph_frame11 flag_loop-mesh_morph_frame12 flag_loop-mesh_morph_frame13 flag_loop-mesh_morph_frame14 flag_loop-mesh_morph_frame15 flag_loop-mesh_morph_frame16 flag_loop-mesh_morph_frame17 flag_loop-mesh_morph_frame18 flag_loop-mesh_morph_frame19 flag_loop-mesh_morph_frame20 flag_loop-mesh_morph_frame21 flag_loop-mesh_morph_frame22 flag_loop-mesh_morph_frame23 flag_loop-mesh_morph_frame24 flag_loop-mesh_morph_frame25 flag_loop-mesh_morph_frame26 flag_loop-mesh_morph_frame27 flag_loop-mesh_morph_frame28 flag_loop-mesh_morph_frame29 flag_loop-mesh_morph_frame30 flag_loop-mesh_morph_frame31 flag_loop-mesh_morph_frame32 flag_loop-mesh_morph_frame33 flag_loop-mesh_morph_frame34 flag_loop-mesh_morph_frame35 flag_loop-mesh_morph_frame36 flag_loop-mesh_morph_frame37 flag_loop-mesh_morph_frame38 flag_loop-mesh_morph_frame39 flag_loop-mesh_morph_frame40 flag_loop-mesh_morph_frame41 flag_loop-mesh_morph_frame42 flag_loop-mesh_morph_frame43 flag_loop-mesh_morph_frame44 flag_loop-mesh_morph_frame45 flag_loop-mesh_morph_frame46 flag_loop-mesh_morph_frame47 flag_loop-mesh_morph_frame48 flag_loop-mesh_morph_frame49 flag_loop-mesh_morph_frame50 flag_loop-mesh_morph_frame51 flag_loop-mesh_morph_frame52 flag_loop-mesh_morph_frame53 flag_loop-mesh_morph_frame54 flag_loop-mesh_morph_frame55 flag_loop-mesh_morph_frame56 flag_loop-mesh_morph_frame57 flag_loop-mesh_morph_frame58 flag_loop-mesh_morph_frame59 flag_loop-mesh_morph_frame60 flag_loop-mesh_morph_frame61 flag_loop-mesh_morph_frame62 flag_loop-mesh_morph_frame63 flag_loop-mesh_morph_frame64 flag_loop-mesh_morph_frame65 flag_loop-mesh_morph_frame66 flag_loop-mesh_morph_frame67 flag_loop-mesh_morph_frame68 flag_loop-mesh_morph_frame69 flag_loop-mesh_morph_frame70 flag_loop-mesh_morph_frame71 flag_loop-mesh_morph_frame72 flag_loop-mesh_morph_frame73 flag_loop-mesh_morph_frame74 flag_loop-mesh_morph_frame75 flag_loop-mesh_morph_frame76 flag_loop-mesh_morph_frame77 flag_loop-mesh_morph_frame78 flag_loop-mesh_morph_frame79 flag_loop-mesh_morph_frame80 flag_loop-mesh_morph_frame81 flag_loop-mesh_morph_frame82 flag_loop-mesh_morph_frame83 flag_loop-mesh_morph_frame84 flag_loop-mesh_morph_frame85 flag_loop-mesh_morph_frame86 flag_loop-mesh_morph_frame87 flag_loop-mesh_morph_frame88 flag_loop-mesh_morph_frame89 flag_loop-mesh_morph_frame90 flag_loop-mesh_morph_frame91 flag_loop-mesh_morph_frame92 flag_loop-mesh_morph_frame93 flag_loop-mesh_morph_frame94 flag_loop-mesh_morph_frame95 flag_loop-mesh_morph_frame96 flag_loop-mesh_morph_frame97 flag_loop-mesh_morph_frame98 flag_loop-mesh_morph_frame99 flag_loop-mesh_morph_frame100 flag_loop-mesh_morph_frame101 flag_loop-mesh_morph_frame102 flag_loop-mesh_morph_frame103 flag_loop-mesh_morph_frame104 flag_loop-mesh_morph_frame105 flag_loop-mesh_morph_frame106 flag_loop-mesh_morph_frame107 flag_loop-mesh_morph_frame108 flag_loop-mesh_morph_frame109 flag_loop-mesh_morph_frame110 flag_loop-mesh_morph_frame111 flag_loop-mesh_morph_frame112 flag_loop-mesh_morph_frame113 flag_loop-mesh_morph_frame114 flag_loop-mesh_morph_frame115 flag_loop-mesh_morph_frame116 flag_loop-mesh_morph_frame117 flag_loop-mesh_morph_frame118 flag_loop-mesh_morph_frame119 flag_loop-mesh_morph_frame120 flag_loop-mesh_morph_frame121 flag_loop-mesh_morph_frame122 flag_loop-mesh_morph_frame123 flag_loop-mesh_morph_frame124 flag_loop-mesh_morph_frame125 flag_loop-mesh_morph_frame126 flag_loop-mesh_morph_frame127 flag_loop-mesh_morph_frame128 flag_loop-mesh_morph_frame129 flag_loop-mesh_morph_frame130 flag_loop-mesh_morph_frame131 flag_loop-mesh_morph_frame132 flag_loop-mesh_morph_frame133 flag_loop-mesh_morph_frame134 flag_loop-mesh_morph_frame135 flag_loop-mesh_morph_frame136 flag_loop-mesh_morph_frame137 flag_loop-mesh_morph_frame138 flag_loop-mesh_morph_frame139 flag_loop-mesh_morph_frame140 flag_loop-mesh_morph_frame141 flag_loop-mesh_morph_frame142 flag_loop-mesh_morph_frame143 flag_loop-mesh_morph_frame144 flag_loop-mesh_morph_frame145 flag_loop-mesh_morph_frame146 flag_loop-mesh_morph_frame147 flag_loop-mesh_morph_frame148 flag_loop-mesh_morph_frame149 flag_loop-mesh_morph_frame150 flag_loop-mesh_morph_frame151 flag_loop-mesh_morph_frame152 flag_loop-mesh_morph_frame153 flag_loop-mesh_morph_frame154 flag_loop-mesh_morph_frame155 flag_loop-mesh_morph_frame156 flag_loop-mesh_morph_frame157 flag_loop-mesh_morph_frame158 flag_loop-mesh_morph_frame159 flag_loop-mesh_morph_frame160 flag_loop-mesh_morph_frame161 flag_loop-mesh_morph_frame162 flag_loop-mesh_morph_frame163 flag_loop-mesh_morph_frame164 flag_loop-mesh_morph_frame165 flag_loop-mesh_morph_frame166 flag_loop-mesh_morph_frame167 flag_loop-mesh_morph_frame168 flag_loop-mesh_morph_frame169 flag_loop-mesh_morph_frame170 flag_loop-mesh_morph_frame171 flag_loop-mesh_morph_frame172 flag_loop-mesh_morph_frame173 flag_loop-mesh_morph_frame174 flag_loop-mesh_morph_frame175 flag_loop-mesh_morph_frame176 flag_loop-mesh_morph_frame177 flag_loop-mesh_morph_frame178 flag_loop-mesh_morph_frame179 flag_loop-mesh_morph_frame180 flag_loop-mesh_morph_frame181 flag_loop-mesh_morph_frame182 flag_loop-mesh_morph_frame183 flag_loop-mesh_morph_frame184 flag_loop-mesh_morph_frame185 flag_loop-mesh_morph_frame186 flag_loop-mesh_morph_frame187 flag_loop-mesh_morph_frame188 flag_loop-mesh_morph_frame189 flag_loop-mesh_morph_frame190 flag_loop-mesh_morph_frame191 flag_loop-mesh_morph_frame192 flag_loop-mesh_morph_frame193 flag_loop-mesh_morph_frame194 flag_loop-mesh_morph_frame195 flag_loop-mesh_morph_frame196 flag_loop-mesh_morph_frame197 flag_loop-mesh_morph_frame198 flag_loop-mesh_morph_frame199</IDREF_array>
                //      <technique_common>
                //        <accessor source="#flag_loop-targets-array" count="198" stride="1">
                //          <param name="IDREF" type="IDREF"/>
                //        </accessor>
                //      </technique_common>
                //    </source>
                //    <source id="flag_loop-weights">
                //      <float_array id="flag_loop-weights-array" count="198">0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 1 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0</float_array>
                //      <technique_common>
                //        <accessor source="#flag_loop-weights-array" count="198" stride="1">
                //          <param name="MORPH_WEIGHT" type="float"/>
                //        </accessor>
                //      </technique_common>
                //    </source>
                //    <targets>
                //      <input semantic="MORPH_TARGET" source="#flag_loop-targets"/>
                //      <input semantic="MORPH_WEIGHT" source="#flag_loop-weights"/>
                //    </targets>
                //  </morph>
                //</controller>

                // TODO get morph animation correctly later
                
                String controllerId = "#" + element.getAttribute("id");
                Element morphElement = (Element) element.getElementsByTagName("morph").item(0);
                Element idrefArrayElement = (Element) morphElement.getElementsByTagName("IDREF_array").item(0);
                int framesCount = Integer.parseInt(idrefArrayElement.getAttribute("count"));
                String idrefArray = idrefArrayElement.getTextContent();
                Morph morph = new Morph(controllerId, null);
                morph.setAnimationData(framesCount, idrefArray, collada);
                collada.getLibraries().addMorph(morph);
            }
        }        
    }
    
    private static void parseAnimations(Node node) {
        NodeList childNodes = node.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node child = childNodes.item(i);
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                //System.out.println("animations child nodes: " + child.getNodeName()); // + " content:" + child.getTextContent());
                Element element = (Element) child;
                
                //<animation id="action_container-Camera" name="Camera">
                //  <animation id="Camera_CameraAction_transform" name="Camera">
                //    <source id="Camera_CameraAction_transform-input">
                //      <float_array id="Camera_CameraAction_transform-input-array" count="200">0.04166662 0.08333331 0.125 0.1666666 0.2083333 0.25 0.2916666 0.3333333 0.375 0.4166666 0.4583333 0.5 0.5416667 0.5833333 0.625 0.6666667 0.7083333 0.75 0.7916667 0.8333333 0.875 0.9166667 0.9583333 1 1.041667 1.083333 1.125 1.166667 1.208333 1.25 1.291667 1.333333 1.375 1.416667 1.458333 1.5 1.541667 1.583333 1.625 1.666667 1.708333 1.75 1.791667 1.833333 1.875 1.916667 1.958333 2 2.041667 2.083333 2.125 2.166667 2.208333 2.25 2.291667 2.333333 2.375 2.416667 2.458333 2.5 2.541667 2.583333 2.625 2.666667 2.708333 2.75 2.791667 2.833333 2.875 2.916667 2.958333 3 3.041667 3.083333 3.125 3.166667 3.208333 3.25 3.291667 3.333333 3.375 3.416667 3.458333 3.5 3.541667 3.583333 3.625 3.666667 3.708333 3.75 3.791667 3.833333 3.875 3.916667 3.958333 4 4.041666 4.083333 4.125 4.166666 4.208333 4.25 4.291666 4.333333 4.375 4.416666 4.458333 4.5 4.541666 4.583333 4.625 4.666666 4.708333 4.75 4.791666 4.833333 4.875 4.916666 4.958333 5 5.041666 5.083333 5.125 5.166666 5.208333 5.25 5.291666 5.333333 5.375 5.416666 5.458333 5.5 5.541666 5.583333 5.625 5.666666 5.708333 5.75 5.791666 5.833333 5.875 5.916666 5.958333 6 6.041666 6.083333 6.125 6.166666 6.208333 6.25 6.291666 6.333333 6.375 6.416666 6.458333 6.5 6.541666 6.583333 6.625 6.666666 6.708333 6.75 6.791666 6.833333 6.875 6.916666 6.958333 7 7.041666 7.083333 7.125 7.166666 7.208333 7.25 7.291666 7.333333 7.375 7.416666 7.458333 7.5 7.541666 7.583333 7.625 7.666666 7.708333 7.75 7.791666 7.833333 7.875 7.916666 7.958333 8 8.041667 8.083333 8.125 8.166667 8.208333 8.25 8.291667 8.333333</float_array>
                //      <technique_common>
                //        <accessor source="#Camera_CameraAction_transform-input-array" count="200" stride="1">
                //          <param name="TIME" type="float"/>
                //        </accessor>
                //      </technique_common>
                //    </source>
                //    <source id="Camera_CameraAction_transform-output">
                //      <float_array id="Camera_CameraAction_transform-output-array" count="3200">0.6906523 -0.3134656 0.6517198 7.39145 0.723187 0.2993634 -0.6224003 -6.93988 1.49012e-8 0.9011775 0.4334503 4.924514 0 0 0 1 0.6896682 -0.3139527 0.6525271 7.39869 0.7241256 0.2990133 -0.6214767 -6.927697 1.49012e-8 0.9011241 0.4335612 4.924528 0 0 0 1 0.6867604 -0.3153893 0.6548968 7.419906 0.7268839 0.29798 -0.6187469 -6.891748 -1.49012e-8 0.9009647 0.4338923 4.924623 0 0 0 1 0.6819813 -0.3177342 0.6587462 7.454341 0.7313697 0.2962781 -0.6142619 -6.832932 1.49012e-8 0.900702 0.4344374 4.924883 0 0 0 1 0.6753658 -0.3209407 0.6639865 7.501241 0.737483 0.2939083 -0.6080598 -6.75215 1.49012e-8 0.9003415 0.4351839 4.925389 0 0 0 1 0.666935 -0.3249562 0.6705231 7.559851 0.7451159 0.2908603 -0.6001688 -6.6503 2.98023e-8 0.8998909 0.436115 4.926223 0 0 0 1 0.656699 -0.3297223 0.6782548 7.629414 0.7541528 0.2871146 -0.5906088 -6.528281 1.49012e-8 0.89936 0.4372088 4.927467 0 0 0 1 0.6446609 -0.3351738 0.6870741 7.709176 0.7644688 0.2826452 -0.5793956 -6.386993 1.49012e-8 0.8987603 0.4384402 4.929204 0 0 0 1 0.6308193 -0.3412387 0.6968668 7.798381 0.7759297 0.2774219 -0.5665421 -6.227337 -1.49012e-8 0.8981054 0.4397804 4.931515 0 0 0 1 0.6151726 -0.3478372 0.7075111 7.896274 0.7883925 0.2714129 -0.5520619 -6.05021 -1.49012e-8 0.8974097 0.441198 4.934482 0 0 0 1 0.5977229 -0.3548814 0.7188787 8.002098 0.8017029 0.2645877 -0.5359719 -5.856513 -1.49012e-8 0.8966897 0.4426596 4.938188 0 0 0 1 0.5784783 -0.3622757 0.7308345 8.115101 0.8156978 0.2569194 -0.5182949 -5.647145 2.98023e-8 0.8959625 0.4441298 4.942714 0 0 0 1 0.5574585 -0.3699169 0.7432372 8.234522 0.8302048 0.2483885 -0.4990622 -5.423006 0 0.8952456 0.4455731 4.948143 0 0 0 1 0.5346957 -0.3776959 0.7559407 8.359612 0.8450447 0.2389843 -0.4783158 -5.184995 0 0.894557 0.4469538 4.954556 0 0 0 1 0.5102398 -0.3854986 0.7687953 8.48961 0.8600321 0.2287086 -0.4561108 -4.934011 0 0.8939146 0.4482375 4.962036 0 0 0 1 0.4841594 -0.3932086 0.78165 8.623764 0.8749799 0.2175772 -0.4325165 -4.670954 -1.49012e-8 0.8933349 0.4493916 4.970664 0 0 0 1 0.4565437 -0.4007099 0.7943547 8.761316 0.889701 0.2056214 -0.4076175 -4.396723 0 0.8928334 0.4503871 4.980523 0 0 0 1 0.4275038 -0.4078899 0.8067628 8.901514 0.9040136 0.1928892 -0.3815143 -4.112218 0 0.8924233 0.4511989 4.991694 0 0 0 1 0.3971717 -0.4146435 0.818734 9.0436 0.9177444 0.179445 -0.354323 -3.818339 0 0.8921157 0.4518073 5.004261 0 0 0 1 0.365699 -0.4208759 0.8301372 9.186819 0.9307333 0.1653684 -0.3261733 -3.515985 -1.49012e-8 0.8919174 0.4521983 5.018304 0 0 0 1 0.3332548 -0.4265063 0.8408529 9.330416 0.9428368 0.1507528 -0.2972076 -3.206054 0 0.891833 0.4523648 5.033906 0 0 0 1 0.3000219 -0.4314701 0.8507763 9.473635 0.9539323 0.135702 -0.2675782 -2.889447 -1.49012e-8 0.8918623 0.4523068 5.051149 0 0 0 1 0.2661926 -0.4357221 0.8598185 9.615721 0.9639199 0.1203275 -0.2374444 -2.567063 -7.45058e-9 0.892002 0.4520315 5.070116 0 0 0 1 0.2319642 -0.4392368 0.8679076 9.755918 0.9727243 0.1047442 -0.2069687 -2.239802 0 0.8922442 0.4515531 5.090887 0 0 0 1 0.1975333 -0.4420087 0.8749908 9.893471 0.9802962 0.08906638 -0.1763139 -1.908563 0 0.8925779 0.450893 5.113544 0 0 0 1 0.1630919 -0.4440525 0.8810326 10.02762 0.9866109 0.07340419 -0.1456393 -1.574246 -7.45058e-9 0.892989 0.4500786 5.138171 0 0 0 1 0.1288223 -0.4454005 0.8860154 10.15762 0.9916677 0.0578596 -0.1150975 -1.237749 0 0.89346 0.4491428 5.164849 0 0 0 1 0.09489337 -0.4461012 0.8899376 10.28271 0.9954875 0.04252394 -0.08483198 -0.8999729 -3.72529e-9 0.8939717 0.4481234 5.193661 0 0 0 1 0.061458 -0.4462166 0.8928122 10.40213 0.9981097 0.02747551 -0.05497437 -0.5618181 0 0.8945031 0.4470617 5.224687 0 0 0 1 0.02864993 -0.4458186 0.8946647 10.51514 0.9995895 0.01277792 -0.02564261 -0.2241814 0 0.895032 0.4460017 5.258011 0 0 0 1 -0.003416382 -0.444987 0.8955306 10.62096 0.9999942 -0.001520254 0.003059492 0.1120367 0 0.8955359 0.4449895 5.293714 0 0 0 1 -0.03464689 -0.4438054 0.8954532 10.71885 0.9993997 -0.01538571 0.03104331 0.4459376 0 0.8959911 0.444072 5.331879 0 0 0 1 -0.06496701 -0.4423601 0.8944812 10.80806 0.9978875 -0.02879965 0.0582348 0.7766185 0 0.896375 0.4432966 5.372586 0 0 0 1 -0.09432176 -0.4407368 0.8926671 10.88782 0.9955418 -0.04175724 0.08457498 1.103183 0 0.8966646 0.4427105 5.415919 0 0 0 1 -0.1226734 -0.4390196 0.8900635 10.95738 0.9924471 -0.0542659 0.1100181 1.424732 -3.72529e-9 0.8968372 0.4423607 5.46196 0 0 0 1 -0.1500001 -0.4372889 0.8867233 11.01599 0.9886859 -0.06634401 0.1345307 1.740362 0 0.8968706 0.442293 5.51079 0 0 0 1 -0.1762948 -0.4356213 0.8826971 11.06289 0.9843374 -0.07801975 0.158091 2.049179 0 0.8967424 0.4425528 5.562492 0 0 0 1 -0.201562 -0.4340883 0.8780319 11.09733 0.9794759 -0.08932909 0.1806863 2.35028 0 0.8964306 0.4431843 5.617147 0 0 0 1 -0.2258173 -0.4327569 0.8727704 11.11854 0.9741697 -0.1003151 0.2023124 2.642767 0 0.895912 0.4442316 5.674838 0 0 0 1 -0.2490848 -0.4316891 0.8669494 11.12578 0.9684817 -0.1110266 0.2229716 2.92574 -7.45058e-9 0.8951634 0.4457381 5.735647 0 0 0 1 -0.2712421 -0.4310803 0.8605797 11.12573 0.9625112 -0.1214813 0.2425171 3.19848 7.45058e-9 0.8940984 0.4478705 5.805242 0 0 0 1 -0.2921777 -0.4310698 0.8537043 11.12534 0.9563641 -0.1316956 0.2608142 3.460994 0 0.8926563 0.4507381 5.888697 0 0 0 1 -0.3119432 -0.4316269 0.8463981 11.12427 0.9501008 -0.1417145 0.2778949 3.713468 7.45058e-9 0.8908508 0.4542959 5.9852 0 0 0 1 -0.330595 -0.4327164 0.8387274 11.1222 0.9437727 -0.1515766 0.2937986 3.956089 1.49012e-8 0.8886963 0.4584965 6.093941 0 0 0 1 -0.3481926 -0.4342993 0.8307503 11.11879 0.9374231 -0.1613143 0.3085705 4.189044 0 0.8862064 0.4632906 6.214105 0 0 0 1 -0.3647971 -0.4363343 0.8225178 11.1137 0.931087 -0.1709545 0.32226 4.412521 -1.49012e-8 0.8833951 0.4686289 6.344884 0 0 0 1 -0.3804708 -0.4387789 0.8140732 11.10659 0.924793 -0.1805188 0.3349193 4.626704 -1.49012e-8 0.8802761 0.4744618 6.485465 0 0 0 1 -0.3952757 -0.44159 0.8054535 11.09713 0.9185625 -0.1900249 0.3466026 4.831783 0 0.8768631 0.4807402 6.635036 0 0 0 1 -0.4092729 -0.4447251 0.7966902 11.08499 0.9124121 -0.1994865 0.3573645 5.027943 1.49012e-8 0.8731693 0.4874169 6.792786 0 0 0 1 -0.4225227 -0.4481418 0.7878093 11.06982 0.9063523 -0.2089145 0.3672604 5.215371 0 0.8692086 0.4944457 6.957903 0 0 0 1 -0.4350837 -0.4517997 0.778832 11.0513 0.90039 -0.2183173 0.3763448 5.394254 0 0.864994 0.5017822 7.129577 0 0 0 1 -0.4470127 -0.4556585 0.769776 11.02908 0.8945276 -0.2277014 0.3846719 5.564779 1.49012e-8 0.8605391 0.5093845 7.306993 0 0 0 1 -0.4583648 -0.4596801 0.7606549 11.00284 0.8887643 -0.2370721 0.3922946 5.727133 0 0.8558568 0.5172127 7.489344 0 0 0 1 -0.4691929 -0.4638279 0.7514797 10.97223 0.8830957 -0.246434 0.3992647 5.881502 0 0.8509606 0.5252295 7.675815 0 0 0 1 -0.4795485 -0.4680663 0.7422582 10.93692 0.8775154 -0.2557909 0.4056325 6.028075 0 0.8458635 0.5333995 7.865596 0 0 0 1 -0.4894809 -0.4723614 0.732996 10.89657 0.872014 -0.265147 0.411447 6.167037 -1.49012e-8 0.8405782 0.5416902 8.057874 0 0 0 1 -0.4990379 -0.4766809 0.7236965 10.85085 0.8665802 -0.2745064 0.4167553 6.298574 0 0.8351178 0.5500712 8.251839 0 0 0 1 -0.5082654 -0.480993 0.7143613 10.79942 0.8612005 -0.2838736 0.4216035 6.422875 0 0.8294946 0.5585146 8.446678 0 0 0 1 -0.5172085 -0.4852676 0.7049899 10.74195 0.8558595 -0.2932544 0.4260358 6.540127 0 0.8237215 0.5669945 8.641581 0 0 0 1 -0.5259104 -0.4894751 0.6955806 10.6781 0.8505399 -0.3026549 0.4300952 6.650515 0 0.8178106 0.5754876 8.835735 0 0 0 1 -0.5344138 -0.4935866 0.6861299 10.60753 0.8452231 -0.3120827 0.4338231 6.754227 1.49012e-8 0.8117737 0.5839721 9.028331 0 0 0 1 -0.5427598 -0.4975736 0.6766331 10.52991 0.839888 -0.3215464 0.4372598 6.851448 1.49012e-8 0.8056231 0.5924285 9.218554 0 0 0 1 -0.5509896 -0.5014077 0.6670839 10.44491 0.8345122 -0.3310561 0.4404444 6.942369 -1.49012e-8 0.7993699 0.6008393 9.405594 0 0 0 1 -0.5591432 -0.5050603 0.6574747 10.35218 0.8290712 -0.3406234 0.4434149 7.027173 1.49012e-8 0.7930256 0.6091882 9.588639 0 0 0 1 -0.5672607 -0.5085027 0.6477965 10.2514 0.8235383 -0.3502613 0.4462082 7.106048 0 0.7866017 0.6174609 9.766879 0 0 0 1 -0.5753822 -0.5117049 0.6380388 10.14222 0.8178847 -0.3599846 0.4488605 7.179181 0 0.7801085 0.6256443 9.939501 0 0 0 1 -0.5835474 -0.514636 0.6281896 10.02431 0.8120791 -0.3698094 0.4514073 7.246758 0 0.7735572 0.6337264 10.10569 0 0 0 1 -0.5917969 -0.5172636 0.6182352 9.897331 0.8060871 -0.3797543 0.4538836 7.308969 1.49012e-8 0.7669584 0.6416968 10.26464 0 0 0 1 -0.6001713 -0.5195531 0.6081603 9.760956 0.7998716 -0.3898387 0.4563238 7.365996 0 0.7603225 0.6495457 10.41554 0 0 0 1 -0.6087124 -0.5214672 0.5979475 9.614841 0.793391 -0.4000846 0.4587625 7.418032 0 0.7536606 0.6572639 10.55758 0 0 0 1 -0.6174621 -0.5229658 0.5875775 9.458654 0.7866008 -0.4105152 0.4612338 7.465256 0 0.7469832 0.6648429 10.68994 0 0 0 1 -0.6264638 -0.5240049 0.5770285 9.29206 0.7794505 -0.4211558 0.4637722 7.507862 -1.49012e-8 0.7403016 0.6722749 10.81181 0 0 0 1 -0.6357625 -0.5245355 0.566276 9.114719 0.7718848 -0.4320334 0.4664129 7.546034 0 0.7336276 0.6795516 10.92239 0 0 0 1 -0.6454033 -0.5245041 0.5552929 8.9263 0.763842 -0.4431762 0.469191 7.579957 -1.49012e-8 0.7269734 0.6866656 11.02085 0 0 0 1 -0.6554336 -0.5238495 0.5440483 8.726463 0.7552528 -0.4546141 0.4721433 7.609821 -1.49012e-8 0.7203526 0.6936081 11.10639 0 0 0 1 -0.6659015 -0.5225039 0.5325081 8.514875 0.7460396 -0.4663776 0.4753071 7.635811 -1.49012e-8 0.7137798 0.7003701 11.1782 0 0 0 1 -0.6768565 -0.5203905 0.5206333 8.291201 0.736115 -0.4784982 0.4787215 7.658116 0 0.7072718 0.7069417 11.23547 0 0 0 1 -0.6883487 -0.5174219 0.5083805 8.055101 0.72538 -0.491007 0.4824272 7.67692 1.49012e-8 0.7008471 0.7133114 11.27738 0 0 0 1 -0.7004282 -0.5134992 0.4957005 7.806244 0.7137229 -0.5039341 0.486467 7.692411 -2.98023e-8 0.6945279 0.7194657 11.30311 0 0 0 1 -0.7131454 -0.508509 0.4825372 7.544291 0.7010161 -0.5173075 0.4908863 7.704779 -1.49012e-8 0.6883397 0.7253884 11.31188 0 0 0 1 -0.7268287 -0.5022587 0.4684616 7.264108 0.6868188 -0.5315171 0.4957514 7.715374 -2.98023e-8 0.6820746 0.7312826 11.30758 0 0 0 1 -0.7417574 -0.4945089 0.4530529 6.961584 0.6706684 -0.5469255 0.5010753 7.72544 -1.49012e-8 0.6755245 0.7373375 11.29497 0 0 0 1 -0.757895 -0.4850487 0.4362603 6.637917 0.6523767 -0.5635026 0.5068229 7.73499 0 0.6687245 0.7435102 11.27449 0 0 0 1 -0.775184 -0.4736443 0.4180321 6.294309 0.6317356 -0.5811949 0.5129549 7.744037 0 0.6617202 0.7497509 11.24658 0 0 0 1 -0.7935402 -0.4600409 0.3983169 5.931959 0.6085179 -0.5999182 0.5194268 7.752595 -1.49012e-8 0.654569 0.7560022 11.21167 0 0 0 1 -0.8128434 -0.4439673 0.3770658 5.552066 0.5824823 -0.6195483 0.5261884 7.760678 -1.49012e-8 0.647343 0.7621989 11.17021 0 0 0 1 -0.8329289 -0.4251434 0.3542352 5.155833 0.5533801 -0.6399114 0.5331827 7.7683 0 0.6401299 0.7682666 11.12262 0 0 0 1 -0.8535779 -0.4032912 0.3297894 4.744458 0.5209654 -0.6607742 0.5403449 7.775473 0 0.6330352 0.774123 11.06935 0 0 0 1 -0.8745092 -0.3781499 0.3037042 4.319142 0.4850088 -0.6818341 0.5476027 7.782212 -1.49012e-8 0.6261828 0.7796764 11.01084 0 0 0 1 -0.8953725 -0.3494979 0.2759699 3.881084 0.4453181 -0.7027131 0.5548749 7.788529 0 0.6197141 0.7848276 10.94751 0 0 0 1 -0.9157449 -0.317178 0.2465957 3.431485 0.4017603 -0.7229539 0.5620733 7.794438 0 0.6137881 0.7894707 10.87981 0 0 0 1 -0.9351355 -0.2811274 0.2156131 2.971545 0.3542903 -0.7420248 0.5691024 7.799955 0 0.6085775 0.7934945 10.80818 0 0 0 1 -0.9529968 -0.2414103 0.1830796 2.502463 0.3029804 -0.759334 0.57586 7.805091 1.49012e-8 0.6042623 0.7967855 10.73304 0 0 0 1 -0.9687481 -0.1982466 0.1490821 2.025441 0.2480468 -0.7742534 0.582241 7.80986 -7.45058e-9 0.6010241 0.799231 10.65485 0 0 0 1 -0.9818093 -0.1520331 0.1137387 1.541677 0.1898698 -0.7861571 0.5881382 7.814277 1.49012e-8 0.5990351 0.8007228 10.57403 0 0 0 1 -0.9916445 -0.103351 0.07720044 1.052373 0.1290014 -0.7944683 0.5934464 7.818353 0 0.5984467 0.8011625 10.49103 0 0 0 1 -0.9978095 -0.05295288 0.03965053 0.5587275 0.06615265 -0.7987117 0.5980664 7.822104 0 0.5993794 0.800465 10.40628 0 0 0 1 -0.9999977 -0.001728056 0.001302503 0.06194103 0.002163953 -0.7985626 0.6019078 7.825541 1.16415e-10 0.6019092 0.7985646 10.32022 0 0 0 1 -0.9980733 0.04935247 -0.03760367 -0.4367859 -0.06204597 -0.7938854 0.6048937 7.828681 -1.86265e-9 0.6060613 0.7954179 10.23328 0 0 0 1 -0.9920883 0.0993051 -0.07680739 -0.9362535 -0.1255423 -0.7847506 0.6069643 7.831535 3.72529e-9 0.6118047 0.7910088 10.1459 0 0 0 1 -0.9822762 0.1472055 -0.1160346 -1.435261 -0.1874392 -0.771431 0.6080797 7.834118 -7.45058e-9 0.6190516 0.7853503 10.05853 0 0 0 1 -0.9690263 0.1922527 -0.155006 -1.93261 -0.2469574 -0.7543727 0.6082219 7.836443 7.45058e-9 0.627663 0.7784851 9.97159 0 0 0 1 -0.9528426 0.2338157 -0.1934461 -2.427099 -0.3034652 -0.7341517 0.6073962 7.838523 0 0.6374571 0.7704858 9.885528 0 0 0 1 -0.9342952 0.271458 -0.2310913 -2.917529 -0.3565005 -0.7114208 0.6056302 7.840373 0 0.6482214 0.7614518 9.800776 0 0 0 1 -0.9139744 0.3049402 -0.2676981 -3.402698 -0.4057719 -0.6868577 0.6029723 7.842004 -1.49012e-8 0.6597256 0.7515065 9.717773 0 0 0 1 -0.8924508 0.3342044 -0.3030496 -3.881409 -0.4511449 -0.6611202 0.5994901 7.843432 0 0.6717348 0.7407917 9.636956 0 0 0 1 -0.8702465 0.3593457 -0.3369595 -4.352459 -0.4926166 -0.6348128 0.5952659 7.844671 0 0.68402 0.7294632 9.558763 0 0 0 1 -0.8478178 0.3805792 -0.3692755 -4.814649 -0.5302875 -0.6084659 0.5903935 7.845732 1.49012e-8 0.6963683 0.7176846 9.483631 0 0 0 1 -0.8255479 0.3982049 -0.3998793 -5.266779 -0.5643321 -0.5825245 0.5849739 7.846631 1.49012e-8 0.7085887 0.7056217 9.411997 0 0 0 1 -0.8037466 0.4125765 -0.4286866 -5.707649 -0.594972 -0.5573489 0.5791119 7.847381 -1.49012e-8 0.7205157 0.6934386 9.344297 0 0 0 1 -0.7826556 0.4240742 -0.4556438 -6.136059 -0.622455 -0.5332177 0.5729124 7.847993 1.49012e-8 0.7320109 0.681293 9.280971 0 0 0 1 -0.7624575 0.4330842 -0.4807251 -6.55081 -0.6470382 -0.5103381 0.5664773 7.848485 1.49012e-8 0.7429624 0.6693331 9.222453 0 0 0 1 -0.7432845 0.4399827 -0.5039281 -6.950698 -0.6689756 -0.4888554 0.5599037 7.848867 1.49012e-8 0.7532833 0.6576962 9.169183 0 0 0 1 -0.7252271 0.4451261 -0.5252699 -7.334528 -0.6885098 -0.4688641 0.5532819 7.849155 -1.49012e-8 0.7629084 0.6465067 9.121596 0 0 0 1 -0.7083445 0.4488437 -0.544782 -7.701096 -0.7058669 -0.4504191 0.5466942 7.849361 0 0.7717913 0.6358758 9.080132 0 0 0 1 -0.6926706 0.4514347 -0.562507 -8.049205 -0.7212541 -0.4335443 0.5402147 7.849498 -1.49012e-8 0.7799013 0.6259025 9.045225 0 0 0 1 -0.6782218 0.4531668 -0.5784938 -8.377653 -0.7348573 -0.4182413 0.5339093 7.849583 1.49012e-8 0.7872192 0.6166733 9.017315 0 0 0 1 -0.6650019 0.4542768 -0.5927944 -8.68524 -0.7468417 -0.4044966 0.5278352 7.849626 1.49012e-8 0.793735 0.6082638 8.996838 0 0 0 1 -0.653006 0.4549721 -0.6054614 -8.970766 -0.7573528 -0.3922868 0.522042 7.849641 1.49012e-8 0.7994444 0.60074 8.98423 0 0 0 1 -0.6422251 0.4554329 -0.6165451 -9.233032 -0.7665161 -0.3815842 0.516572 7.849644 1.49012e-8 0.8043472 0.5941596 8.979931 0 0 0 1 -0.630638 0.4570653 -0.6272058 -9.477064 -0.7760772 -0.3714099 0.5096655 7.813645 0 0.8081745 0.5889431 8.979931 0 0 0 1 -0.6162366 0.4609909 -0.6385452 -9.708897 -0.7875612 -0.3607078 0.4996372 7.708091 -1.49012e-8 0.8107882 0.5853398 8.979931 0 0 0 1 -0.5989834 0.4669683 -0.6505071 -9.928843 -0.8007614 -0.3493004 0.4865906 7.536641 -1.49012e-8 0.8123606 0.5831553 8.979931 0 0 0 1 -0.5787918 0.4747742 -0.6630155 -10.13722 -0.8154755 -0.3369757 0.4705819 7.302957 0 0.8130417 0.5822054 8.979931 0 0 0 1 -0.5555389 0.4841866 -0.6759734 -10.33433 -0.8314906 -0.3234967 0.4516341 7.010699 0 0.8129658 0.5823115 8.979931 0 0 0 1 -0.5290794 0.494969 -0.6892611 -10.52049 -0.8485724 -0.30861 0.4297498 6.663528 -1.49012e-8 0.8122596 0.5832962 8.979931 0 0 0 1 -0.4992598 0.506857 -0.7027344 -10.69602 -0.8664523 -0.2920569 0.4049236 6.265105 0 0.811048 0.5849797 8.979931 0 0 0 1 -0.4659355 0.5195457 -0.7162237 -10.86123 -0.8848188 -0.2735868 0.3771552 5.819092 0 0.8094581 0.5871775 8.979931 0 0 0 1 -0.4289891 0.5326802 -0.7295343 -11.01643 -0.9033098 -0.2529741 0.3464617 5.329148 -1.49012e-8 0.8076236 0.5896984 8.979931 0 0 0 1 -0.3883538 0.5458515 -0.7424469 -11.16193 -0.9215105 -0.2300392 0.3128908 4.798935 1.49012e-8 0.8056848 0.5923444 8.979931 0 0 0 1 -0.3440378 0.5585973 -0.7547233 -11.29805 -0.9389558 -0.2046726 0.2765341 4.232114 0 0.8037899 0.5949132 8.979931 0 0 0 1 -0.2961487 0.5704118 -0.7661113 -11.42509 -0.9551419 -0.1768603 0.2375383 3.632344 0 0.8020915 0.5972011 8.979931 0 0 0 1 -0.2449165 0.5807657 -0.7763551 -11.54338 -0.9695442 -0.1467072 0.196115 3.003289 0 0.8007423 0.599009 8.979931 0 0 0 1 -0.190709 0.5891353 -0.7852067 -11.65322 -0.9816467 -0.114454 0.1525457 2.348607 -7.45058e-9 0.7998873 0.6001501 8.979931 0 0 0 1 -0.134037 0.5950399 -0.7924403 -11.75494 -0.9909763 -0.08048362 0.1071835 1.671959 0 0.7996562 0.6004583 8.979931 0 0 0 1 -0.07554465 0.5980828 -0.7978657 -11.84883 -0.9971424 -0.04531144 0.06044722 0.9770083 3.72529e-9 0.8001523 0.5997968 8.979931 0 0 0 1 -0.0159831 0.5979911 -0.8013433 -11.93521 -0.9998723 -0.009558972 0.01280959 0.2674136 0 0.8014458 0.5980675 8.979931 0 0 0 1 0.04382914 0.5946437 -0.8027938 -12.0144 -0.9990391 0.02608779 -0.03521961 -0.4531634 1.86265e-9 0.8035659 0.5952157 8.979931 0 0 0 1 0.103057 0.5880864 -0.8022054 -12.08671 -0.9946755 0.06093084 -0.08311542 -1.181062 7.45058e-9 0.8064997 0.5912346 8.979931 0 0 0 1 0.1609017 0.578527 -0.7996357 -12.15244 -0.9869704 0.09431489 -0.1303613 -1.912621 0 0.8101922 0.5861644 8.979931 0 0 0 1 0.2166485 0.5663118 -0.7952071 -12.21193 -0.9762497 0.1256755 -0.1764717 -2.64418 -7.45058e-9 0.814553 0.5800892 8.979931 0 0 0 1 0.2697029 0.5518913 -0.7890984 -12.26547 -0.9629437 0.1545747 -0.2210121 -3.372079 -7.45058e-9 0.8194648 0.5731295 8.979931 0 0 0 1 0.3196102 0.5357764 -0.7815325 -12.31338 -0.9475491 0.1807184 -0.2636125 -4.092658 0 0.8247936 0.5654339 8.979931 0 0 0 1 0.3660606 0.5184959 -0.7727623 -12.35597 -0.9305911 0.2039574 -0.3039765 -4.802251 -1.49012e-8 0.8303995 0.5571684 8.979931 0 0 0 1 0.4088805 0.5005617 -0.7630562 -12.39356 -0.9125881 0.2242741 -0.3418835 -5.497203 -1.49012e-8 0.8361453 0.5485078 8.979931 0 0 0 1 0.4480131 0.4824403 -0.7526857 -12.42645 -0.8940271 0.2417596 -0.3771844 -6.173849 1.49012e-8 0.8419048 0.5396261 8.979931 0 0 0 1 0.4834965 0.4645374 -0.7419138 -12.45497 -0.8753463 0.2565866 -0.4097952 -6.82853 0 0.8475661 0.5306898 8.979931 0 0 0 1 0.5154381 0.4471908 -0.7309883 -12.47942 -0.8569268 0.2689835 -0.4396867 -7.457586 0 0.8530347 0.5218542 8.979931 0 0 0 1 0.5439924 0.4306701 -0.7201358 -12.50011 -0.8390902 0.2792087 -0.4668728 -8.057355 0 0.8582341 0.5132584 8.979931 0 0 0 1 0.5693401 0.4151829 -0.7095597 -12.51737 -0.8221021 0.2875315 -0.4913998 -8.62418 -1.49012e-8 0.8631042 0.5050259 8.979931 0 0 0 1 0.5916724 0.4008836 -0.6994399 -12.5315 -0.8061786 0.2942173 -0.5133345 -9.15439 0 0.8675992 0.4972639 8.979931 0 0 0 1 0.6111783 0.3878826 -0.6899335 -12.54281 -0.791493 0.2995168 -0.5327556 -9.644333 1.49012e-8 0.8716862 0.4900645 8.979931 0 0 0 1 0.6280351 0.3762575 -0.6811771 -12.55162 -0.7781851 0.303659 -0.5497447 -10.09035 1.49012e-8 0.8753408 0.4835065 8.979931 0 0 0 1 0.6424007 0.3660618 -0.6732905 -12.55824 -0.766369 0.3068475 -0.5643787 -10.48877 0 0.8785463 0.4776574 8.979931 0 0 0 1 0.6544091 0.3573342 -0.6663792 -12.56299 -0.7561408 0.3092582 -0.5767241 -10.83594 -1.49012e-8 0.8812898 0.4725763 8.979931 0 0 0 1 0.6641661 0.3501056 -0.6605374 -12.56617 -0.7475851 0.3110392 -0.5868315 -11.1282 2.98023e-8 0.8835614 0.4683154 8.979931 0 0 0 1 0.6717458 0.3444062 -0.6558522 -12.56809 -0.7407818 0.3123098 -0.594731 -11.36188 0 0.8853514 0.4649226 8.979931 0 0 0 1 0.6771867 0.340271 -0.6524062 -12.56909 -0.7358112 0.3131605 -0.6004269 -11.53333 0 0.8866488 0.4624434 8.979931 0 0 0 1 0.6804891 0.3377447 -0.6502793 -12.56945 -0.7327582 0.3136527 -0.6038936 -11.63889 -1.49012e-8 0.8874405 0.4609224 8.979931 0 0 0 1 0.6816079 0.3368866 -0.6495522 -12.5695 -0.7317176 0.3138158 -0.6050694 -11.67489 0 0.8877088 0.4604052 8.979931 0 0 0 1 0.6824026 0.3368297 -0.6487467 -12.5327 -0.7309765 0.3144471 -0.605637 -11.66616 -1.49012e-8 0.887507 0.4607941 8.972454 0 0 0 1 0.6847457 0.3366531 -0.6463653 -12.42479 -0.7287822 0.3163109 -0.6073089 -11.64056 0 0.8869116 0.4619392 8.950529 0 0 0 1 0.6885927 0.3363336 -0.6424327 -12.2495 -0.7251483 0.3193786 -0.6100469 -11.59898 0 0.8859328 0.4638135 8.914917 0 0 0 1 0.6939192 0.3358307 -0.6369411 -12.0106 -0.7200529 0.323642 -0.6138239 -11.54231 -1.49012e-8 0.8845754 0.4663972 8.86638 0 0 0 1 0.7007175 0.3350845 -0.6298519 -11.71181 -0.7134389 0.3291095 -0.6186209 -11.47143 0 0.8828393 0.4696751 8.805675 0 0 0 1 0.7089936 0.3340152 -0.6210973 -11.35687 -0.7052149 0.3358049 -0.6244253 -11.38723 0 0.8807207 0.473636 8.733564 0 0 0 1 0.7187645 0.3325191 -0.6105805 -10.94955 -0.6952537 0.3437637 -0.6312281 -11.29061 -2.98023e-8 0.8782126 0.4782703 8.650808 0 0 0 1 0.7300538 0.3304656 -0.5981755 -10.49356 -0.6833898 0.3530308 -0.6390208 -11.18245 0 0.8753067 0.4835682 8.558167 0 0 0 1 0.7428875 0.3276909 -0.5837267 -9.992667 -0.6694163 0.3636563 -0.6477931 -11.06363 0 0.8719935 0.4895173 8.456402 0 0 0 1 0.7572882 0.3239929 -0.5670478 -9.450603 -0.6530808 0.3756901 -0.6575276 -10.93504 1.49012e-8 0.8682659 0.4960993 8.346272 0 0 0 1 0.7732669 0.3191241 -0.5479215 -8.871112 -0.6340806 0.3891747 -0.6681951 -10.79758 1.49012e-8 0.8641196 0.5032864 8.228539 0 0 0 1 0.7908118 0.3127851 -0.5261011 -8.257936 -0.6120596 0.404134 -0.679749 -10.65213 0 0.8595587 0.5110369 8.10396 0 0 0 1 0.8098735 0.3046178 -0.5013114 -7.61482 -0.5866048 0.420559 -0.6921164 -10.49957 0 0.8545983 0.5192898 7.9733 0 0 0 1 0.8303465 0.2942027 -0.4732542 -6.945504 -0.5572475 0.4383872 -0.7051893 -10.3408 0 0.8492712 0.527957 7.837317 0 0 0 1 0.8520437 0.2810604 -0.4416182 -6.253731 -0.5234708 0.4574769 -0.7188138 -10.1767 1.49012e-8 0.843635 0.536917 7.696772 0 0 0 1 0.8746666 0.2646632 -0.4060934 -5.543247 -0.4847252 0.4775739 -0.7327788 -10.00816 0 0.8377808 0.5460068 7.552424 0 0 0 1 0.8977722 0.2444613 -0.3663929 -4.817791 -0.4404601 0.4982757 -0.746804 -9.836076 -1.49012e-8 0.8318413 0.5550135 7.405035 0 0 0 1 0.9207405 0.2199302 -0.3222853 -4.081107 -0.3901758 0.5189933 -0.7605319 -9.661325 0 0.8260004 0.5636695 7.255365 0 0 0 1 0.9427505 0.1906451 -0.2736349 -3.336939 -0.3334991 0.5389243 -0.7735236 -9.484798 0 0.8204967 0.5716512 7.104174 0 0 0 1 0.9627805 0.1563823 -0.2204498 -2.589026 -0.2702841 0.5570502 -0.7852653 -9.307384 -7.45058e-9 0.8156223 0.5785847 6.952222 0 0 0 1 0.9796466 0.1172387 -0.1629343 -1.841115 -0.2007299 0.5721743 -0.7951881 -9.129969 0 0.8117091 0.5840619 6.800271 0 0 0 1 0.992095 0.07374627 -0.1015337 -1.096945 -0.1254895 0.5830235 -0.8027054 -8.953442 -3.72529e-9 0.8091014 0.5876691 6.64908 0 0 0 1 0.9989533 0.02694211 -0.03696309 -0.3602624 -0.04573999 0.588411 -0.8072672 -8.77869 1.86265e-9 0.808113 0.5890274 6.49941 0 0 0 1 0.9993215 -0.02165219 0.02979731 0.3651943 0.03683337 0.5874428 -0.808427 -8.606603 0 0.808976 0.5878416 6.352021 0 0 0 1 0.9927546 -0.07016733 0.09754506 1.075679 0.1201603 0.5797167 -0.8059095 -8.438066 3.72529e-9 0.8117913 0.5839477 6.207673 0 0 0 1 0.9793832 -0.1166312 0.1649418 1.767449 0.2020115 0.5654462 -0.7996637 -8.273969 0 0.8164973 0.5773493 6.067128 0 0 0 1 0.9599205 -0.1592599 0.2306273 2.436767 0.2802725 0.5454579 -0.789888 -8.115198 -1.49012e-8 0.8228681 0.5682324 5.931145 0 0 0 1 0.9355518 -0.1967096 0.2933397 3.079885 0.3531896 0.5210574 -0.7770175 -7.962642 0 0.8305447 0.5569519 5.800484 0 0 0 1 0.9077439 -0.2282196 0.3520182 3.693059 0.4195247 0.4938087 -0.7616771 -7.817188 0 0.8390881 0.5439956 5.675907 0 0 0 1 0.8780366 -0.2536208 0.4058672 4.272551 0.4785934 0.4652976 -0.7446116 -7.679725 0 0.8480416 0.5299296 5.558173 0 0 0 1 0.8478714 -0.273237 0.4543738 4.814613 0.5302018 0.4369465 -0.7266111 -7.55114 0 0.8569826 0.5153452 5.448043 0 0 0 1 0.8184846 -0.2877298 0.4972871 5.315508 0.5745284 0.4099056 -0.7084451 -7.432321 0 0.865557 0.5008104 5.346277 0 0 0 1 0.7908665 -0.2979393 0.5345676 5.771492 0.6119888 0.3850237 -0.6908158 -7.324155 1.49012e-8 0.8734923 0.4868378 5.253636 0 0 0 1 0.7657688 -0.3047521 0.5663252 6.17882 0.6431159 0.3628734 -0.6743329 -7.227531 0 0.8805959 0.473868 5.17088 0 0 0 1 0.7437417 -0.30901 0.5927573 6.533751 0.6684672 0.3438068 -0.6595063 -7.143338 0 0.8867412 0.4622665 5.09877 0 0 0 1 0.7251806 -0.3114556 0.6140916 6.832542 0.6885588 0.3280207 -0.6467528 -7.07246 0 0.8918507 0.4523297 5.038065 0 0 0 1 0.7103754 -0.3127068 0.6305404 7.071451 0.7038229 0.3156181 -0.6364106 -7.015788 0 0.8958792 0.4442976 4.989527 0 0 0 1 0.6995533 -0.3132481 0.6422623 7.246731 0.7145805 0.3066607 -0.6287559 -6.974209 1.49012e-8 0.8987964 0.4383664 4.953916 0 0 0 1 0.6929125 -0.3134294 0.6493337 7.354649 0.7210216 0.3012103 -0.6240193 -6.94861 0 0.9005744 0.4347018 4.93199 0 0 0 1 0.6906523 -0.3134656 0.6517198 7.39145 0.723187 0.2993634 -0.6224003 -6.93988 1.49012e-8 0.9011775 0.4334503 4.924514 0 0 0 1</float_array>
                //      <technique_common>
                //        <accessor source="#Camera_CameraAction_transform-output-array" count="200" stride="16">
                //          <param name="TRANSFORM" type="float4x4"/>
                //        </accessor>
                //      </technique_common>
                //    </source>
                //    <source id="Camera_CameraAction_transform-interpolation">
                //      <Name_array id="Camera_CameraAction_transform-interpolation-array" count="200">LINEAR LINEAR LINEAR LINEAR LINEAR LINEAR LINEAR LINEAR LINEAR LINEAR LINEAR LINEAR LINEAR LINEAR LINEAR LINEAR LINEAR LINEAR LINEAR LINEAR LINEAR LINEAR LINEAR LINEAR LINEAR LINEAR LINEAR LINEAR LINEAR LINEAR LINEAR LINEAR LINEAR LINEAR LINEAR LINEAR LINEAR LINEAR LINEAR LINEAR LINEAR LINEAR LINEAR LINEAR LINEAR LINEAR LINEAR LINEAR LINEAR LINEAR LINEAR LINEAR LINEAR LINEAR LINEAR LINEAR LINEAR LINEAR LINEAR LINEAR LINEAR LINEAR LINEAR LINEAR LINEAR LINEAR LINEAR LINEAR LINEAR LINEAR LINEAR LINEAR LINEAR LINEAR LINEAR LINEAR LINEAR LINEAR LINEAR LINEAR LINEAR LINEAR LINEAR LINEAR LINEAR LINEAR LINEAR LINEAR LINEAR LINEAR LINEAR LINEAR LINEAR LINEAR LINEAR LINEAR LINEAR LINEAR LINEAR LINEAR LINEAR LINEAR LINEAR LINEAR LINEAR LINEAR LINEAR LINEAR LINEAR LINEAR LINEAR LINEAR LINEAR LINEAR LINEAR LINEAR LINEAR LINEAR LINEAR LINEAR LINEAR LINEAR LINEAR LINEAR LINEAR LINEAR LINEAR LINEAR LINEAR LINEAR LINEAR LINEAR LINEAR LINEAR LINEAR LINEAR LINEAR LINEAR LINEAR LINEAR LINEAR LINEAR LINEAR LINEAR LINEAR LINEAR LINEAR LINEAR LINEAR LINEAR LINEAR LINEAR LINEAR LINEAR LINEAR LINEAR LINEAR LINEAR LINEAR LINEAR LINEAR LINEAR LINEAR LINEAR LINEAR LINEAR LINEAR LINEAR LINEAR LINEAR LINEAR LINEAR LINEAR LINEAR LINEAR LINEAR LINEAR LINEAR LINEAR LINEAR LINEAR LINEAR LINEAR LINEAR LINEAR LINEAR LINEAR LINEAR LINEAR LINEAR LINEAR LINEAR LINEAR LINEAR LINEAR LINEAR LINEAR LINEAR LINEAR LINEAR</Name_array>
                //      <technique_common>
                //        <accessor source="#Camera_CameraAction_transform-interpolation-array" count="200" stride="1">
                //          <param name="INTERPOLATION" type="name"/>
                //        </accessor>
                //      </technique_common>
                //    </source>
                //    <sampler id="Camera_CameraAction_transform-sampler">
                //      <input semantic="INPUT" source="#Camera_CameraAction_transform-input"/>
                //      <input semantic="OUTPUT" source="#Camera_CameraAction_transform-output"/>
                //      <input semantic="INTERPOLATION" source="#Camera_CameraAction_transform-interpolation"/>
                //    </sampler>
                //    <channel source="#Camera_CameraAction_transform-sampler" target="Camera/transform"/>
                //  </animation>
                //</animation>
                

                String animationId = element.getAttribute("name");
                Element animationElement = (Element) element.getElementsByTagName("animation").item(0);

                // TODO: blender exported morph target animation includes animation library ?
                if (animationElement == null) {
                    continue;
                }

                Element source0 = (Element) animationElement.getElementsByTagName("source").item(0);
                
                
                Element source1 = (Element) animationElement.getElementsByTagName("source").item(1); // TODO check attribute "<id>..._transform-output"
                Element floatArrayElem0 = (Element) source0.getElementsByTagName("float_array").item(0);
                Element floatArrayElem1 = (Element) source1.getElementsByTagName("float_array").item(0);
                int framesCount = Integer.parseInt(floatArrayElem0.getAttribute("count"));
                String floatArray = floatArrayElem1.getTextContent();
                Animation animation = new Animation(animationId, null);
                
    boolean invertMatrix = false;
    // TODO
    if (animationId.toLowerCase().contains("camera")) {
        invertMatrix = true;
    }
                
                animation.setAnimationData(framesCount, floatArray, invertMatrix);
                collada.getLibraries().addAnimation(animation);
            }
        }        
    }
    
    private static void parseVisualScenes(Node node) {
        NodeList childNodes = node.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node child = childNodes.item(i);
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                //System.out.println("viual scenes child nodes: " + child.getNodeName()); // + " content:" + child.getTextContent());
                Element element = (Element) child;
                
                //<visual_scene id="Scene" name="Scene">
                //  <node id="Arwing_SNES_Vert_004" name="Arwing_SNES_Vert.004" type="NODE">
                //    <matrix sid="transform">-1.96334e-9 7.05811e-9 0.0890626 -136.5463 0.0449159 0.01483961 -6.58904e-10 0.008006454 -0.01483961 0.0449159 -1.37777e-8 0.1066261 0 0 0 1</matrix>
                //    <instance_geometry url="#Arwing_SNES_Vert_006-mesh" name="Arwing_SNES_Vert.004">
                //      <bind_material>
                //        <technique_common>
                //          <instance_material symbol="Material_002-material" target="#Material_002-material"/>
                //          <instance_material symbol="Material_001-material" target="#Material_001-material"/>
                //          <instance_material symbol="Material_003-material" target="#Material_003-material"/>
                //        </technique_common>
                //      </bind_material>
                //    </instance_geometry>
                //  </node>
                //  <node id="Arwing_SNES_Vert_002" name="Arwing_SNES_Vert.002" type="NODE">
                //    <matrix sid="transform">-0.0019072 9.26077e-4 0.0889731 -136.9236 0.02583818 -0.03958199 0.00342379 0.0480004 0.03957781 0.02588554 0.002052285 -0.1355872 0 0 0 1</matrix>
                //    <instance_geometry url="#Arwing_SNES_Vert_004-mesh" name="Arwing_SNES_Vert.002">
                //      <bind_material>
                //        <technique_common>
                //          <instance_material symbol="Material_002-material" target="#Material_002-material"/>
                //          <instance_material symbol="Material_001-material" target="#Material_001-material"/>
                //          <instance_material symbol="Material_003-material" target="#Material_003-material"/>
                //        </technique_common>
                //      </bind_material>
                //    </instance_geometry>
                //  </node>
                //  <node id="Torus" name="Torus" type="NODE">
                //    <matrix sid="transform">7.54979e-8 0 1 -0.01172042 0 1 0 0 -1 0 7.54979e-8 0 0 0 0 1</matrix>
                //    <instance_geometry url="#Torus-mesh" name="Torus">
                //      <bind_material>
                //        <technique_common>
                //          <instance_material symbol="Material_006-material" target="#Material_006-material">
                //            <bind_vertex_input semantic="UVMap" input_semantic="TEXCOORD" input_set="0"/>
                //          </instance_material>
                //          <instance_material symbol="Material_009-material" target="#Material_009-material">
                //            <bind_vertex_input semantic="UVMap" input_semantic="TEXCOORD" input_set="0"/>
                //          </instance_material>
                //        </technique_common>
                //      </bind_material>
                //    </instance_geometry>
                //  </node>
                //  <node id="Arwing_SNES_Vert_001" name="Arwing_SNES_Vert.001" type="NODE">
                //    <matrix sid="transform">-2.06772e-9 7.70677e-9 0.0890626 -96.96961 0.04730383 3.36874e-16 3.89305e-9 -1.67802e-6 0 0.04730383 -1.45101e-8 -0.01426983 0 0 0 1</matrix>
                //    <instance_geometry url="#Arwing_SNES_Vert_001-mesh" name="Arwing_SNES_Vert.001">
                //      <bind_material>
                //        <technique_common>
                //          <instance_material symbol="Material_002-material" target="#Material_002-material"/>
                //          <instance_material symbol="Material_001-material" target="#Material_001-material"/>
                //          <instance_material symbol="Material_003-material" target="#Material_003-material"/>
                //        </technique_common>
                //      </bind_material>
                //    </instance_geometry>
                //  </node>
                //  <node id="Camera" name="Camera" type="NODE">
                //    <matrix sid="transform">0.001146308 -0.005560433 0.999984 -55.89851 0.9999993 6.37397e-6 -0.00114629 -0.04708183 4.54747e-13 0.9999846 0.005560436 0.214107 0 0 0 1</matrix>
                //    <instance_camera url="#Camera-camera"/>
                //  </node>
                //  <node id="Light" name="Light" type="NODE">
                //    <matrix sid="transform">-0.2908646 -0.7711008 0.5663932 4.076245 0.9551712 -0.1998834 0.2183912 1.005454 -0.05518906 0.6045247 0.7946723 5.903862 0 0 0 1</matrix>
                //    <instance_light url="#Light-light"/>
                //  </node>
                //</visual_scene>

                String visualSceneId = "#" + element.getAttribute("id");
                VisualScene visualScene = new VisualScene(visualSceneId);

                parseVisualSceneNode(element, visualScene);
                
                collada.getLibraries().addVisualScene(visualScene);
            }
        }        
    }
    
    private static void parseVisualSceneNode(Element element, collada.Node parent) {
        
        // Node: element.getElementsByTagName(name) return all decendent elements,
        //       not necessary the only children.
        
        NodeList nodes = element.getChildNodes();

        for (int n = 0; n < nodes.getLength(); n++) {
            
            // child node is not 'node'
            if (!(nodes.item(n) instanceof Element)
                || !((Element) nodes.item(n)).getNodeName().equals("node")) {
                
                continue;
            }

            Element childNodeElement = (Element) nodes.item(n);
            String childNodeId = childNodeElement.getAttribute("name");

            collada.Node childNode = new collada.Node(childNodeId, parent);

            Element matrix = (Element) childNodeElement.getElementsByTagName("matrix").item(0);
            String matrixStrData = matrix.getTextContent();

        boolean invertMatrix = false;
        // TODO
        if (childNodeId.toLowerCase().contains("camera")) {
            invertMatrix = true;
        }

            childNode.setLocalTransform(matrixStrData, invertMatrix);

            // TODO get transform animations correctly later
            Animation animation = collada.getLibraries().getAnimations().get(childNodeId);
            if (animation != null) {
                childNode.addChild(animation);
                childNode = animation;
            }
            
            // TODO get morph animation correctly later
            Morph morph = collada.getLibraries().getMorphs().get("#" + childNodeId + "-morph");

            Element instanceGeometry = (Element) childNodeElement.getElementsByTagName("instance_geometry").item(0);
            Element instanceCamera = (Element) childNodeElement.getElementsByTagName("instance_camera").item(0);
            // TODO light, etc

            // TODO morph + instanceGeometry ?
            if (morph != null) {
                childNode.addChild(morph);
                childNode = morph;
                parent.addChild(childNode);
            }
            else if (instanceGeometry != null) {
                String instanceGeometryId = instanceGeometry.getAttribute("url");
                Geometry geometry = collada.getLibraries().getGeometries().get(instanceGeometryId);
                childNode.addChild(geometry);
                parent.addChild(childNode);
            }
            else if (instanceCamera != null) {
                String instanceCameraId = instanceCamera.getAttribute("url");
                Camera camera = collada.getLibraries().getCameras().get(instanceCameraId);
                childNode.addChild(camera);
                parent.addChild(childNode);
            }
            
            // parse child nodes of this node
            parseVisualSceneNode(childNodeElement, childNode);
        }
    }

    private static void parseScene(Node node) {
        NodeList childNodes = node.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node child = childNodes.item(i);
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                System.out.println("Scene child nodes: " + child.getNodeName()); // + " content:" + child.getTextContent());
                Element element = (Element) child;
                
                //<scene>
                //  <instance_visual_scene url="#Scene"/>
                //</scene>
            
                String instanceGeometryId = element.getAttribute("url");
                VisualScene visualScene = collada.getLibraries().getVisualScenes().get(instanceGeometryId);
                collada.setScene(visualScene);
            }
        }        
    }
    
}
