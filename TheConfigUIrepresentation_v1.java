// Working and tested as on 16-NOV
// Coined the name for this datastructure visualization
//
//
// Multi-Level(3H)--non-Binary(B+)--Multi-Type-Leaf(MTL)
// 3H_B+MTL
//
// History
// 17-NOV-2017     Renamed class name from IllustrateDOMParsevXX to 'TheConfigUIrepresentation_v1'

import java.io.File;
import java.util.LinkedHashMap;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;

import org.apache.ecs.xhtml.button;
import org.apache.ecs.xhtml.div;
import org.w3c.dom.*;

public class TheConfigUIrepresentation_v1 {
    static LinkedHashMap<String, StringBuffer> lhm_tabs = new LinkedHashMap<String, StringBuffer>();
    static LinkedHashMap<String, StringBuffer> lhm_accs_for_tab = new LinkedHashMap<String, StringBuffer>();
    static LinkedHashMap<String, StringBuffer> lhm_repeatSingle_for_acc = new LinkedHashMap<String, StringBuffer>();
    static LinkedHashMap<String, StringBuffer> lhm_repeatSections_for_acc = new LinkedHashMap<String, StringBuffer>();
    static LinkedHashMap<String, StringBuffer> lhm_explict_bold_inside_acc = new LinkedHashMap<String, StringBuffer>();
    static LinkedHashMap<String, StringBuffer> lhm_explict_line_demark_inside_acc = new LinkedHashMap<String, StringBuffer>();
    static LinkedHashMap<String, StringBuffer> lhm_config_elements = new LinkedHashMap<String, StringBuffer>();

    static boolean viewOnlyGUIHierarchy = false; // toggle this to false if you need to view the 'config-parameter-names' under every GUI group

    public static void main(String[] args) throws Exception {
        //Get Document Builder
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();

        //Build Document
        Document document = builder.parse(new File("C:\\users\\amiraj2\\Desktop\\configurations.xml"));

        //Normalize the XML Structure; It's just too important !!
        document.getDocumentElement().normalize();

        //Here comes the root node
        Element root = document.getDocumentElement();
      ////  System.out.println(root.getNodeName());

        visitChildNodesOfRoot(document);

       //// System.out.println("============================");
    }

    /**
     * @param rootDocument
     */
    private static void visitChildNodesOfRoot(Document rootDocument) {

        div d=new div();
        Element root = rootDocument.getDocumentElement();

        // process all tabs
        NodeList nListTabs = rootDocument.getElementsByTagName("paramGroupTab");
        int size = nListTabs.getLength();
        if (size == 0) return;
        for (int idx1 = 0; idx1 < nListTabs.getLength(); idx1++) {
            ////System.out.println(nListTabs.item(idx1).getAttributes().getNamedItem("name"));
            Node n=nListTabs.item(idx1);
            NamedNodeMap attr = n.getAttributes();
            //System.out.println(attr);
            if (null != attr)  //making sure the attributes section isn't empty
            {
                Node p = attr.getNamedItem("name");  //node p now contains the value stored in name attribute

                d.addElement(generateTabButton(p));
            }

            visitChildUIElementsUnder_paramGroupTab(nListTabs, idx1);  //  SHOULD GO synonymous WITH "buildHTMLforTabPanels()"

        }
        System.out.println("MARKUP for tabs in a div tag:     "+d);
    }

    /**
     * @param nListTabs
     * @param idx1
     */
    private static void visitChildUIElementsUnder_paramGroupTab(NodeList nListTabs, int idx1) {
        Node node = nListTabs.item(idx1);
        if (node.getNodeType() == Node.ELEMENT_NODE) {
            Element e = (Element) node;

            // processConfigParamsAtGivenElement(e); , NOT required provided there are no stray config elements

            // process all accordions
            NodeList nListAccordions = e.getElementsByTagName("paramGroupAccordion");
            int size = nListAccordions.getLength();
            if (size == 0) return;
            for (int idx2 = 0; idx2 < size; idx2++) {
              /////  System.out.println("           " + nListAccordions.item(idx2).getAttributes().getNamedItem("name"));
                visitChildUIElementsUnder_paramGroupAccordion(nListAccordions, idx2);   //  SHOULD GO synonymous WITH "buildAccordions()"
            }
        }
    }


    /**
     * @param nListAccordions
     * @param idx1
     */
    private static void visitChildUIElementsUnder_paramGroupAccordion(NodeList nListAccordions, int idx1) {
        Node node = nListAccordions.item(idx1);
        if (node.getNodeType() == Node.ELEMENT_NODE) {
            Element e = (Element) node;

           // // System.out.println("  write logic to process configElements here ");
            // provided NO child repeat s
            if (e.getElementsByTagName("paramGroupRepeatSection").getLength() == 0) {
                processConfigParamsAtGivenElement(e);
            }

            // process all repeat-singles
            NodeList nListRepSingles = e.getElementsByTagName("paramGroupRepeatSingle");
            for (int j = 0; j < nListRepSingles.getLength(); j++) {
               //// System.out.println("           -- single config element repeat --       " + nListRepSingles.item(j).getAttributes().getNamedItem("name"));
            }

            // process all repeat-sections
            NodeList nListRepSections = e.getElementsByTagName("paramGroupRepeatSection");
            int size = nListRepSections.getLength();
            if (size == 0) return;
            for (int idx2 = 0; idx2 < size; idx2++) {
               ///// System.out.println("                                   " + nListRepSections.item(idx2).getAttributes().getNamedItem("name"));
                visitChildUIElementsUnder_paramGroupRepeatSection(nListRepSections, idx2);   //  SHOULD GO synonymous WITH "buildRepeats()"
               ///// System.out.println("          ---------------------------------------             ");

            }
        }
    }

    /**
     * @param nListRepSections
     * @param idx1
     */
    private static void visitChildUIElementsUnder_paramGroupRepeatSection(NodeList nListRepSections, int idx1) {
        Node node = nListRepSections.item(idx1);
        if (node.getNodeType() == Node.ELEMENT_NODE) {
            Element e = (Element) node;

            //System.out.println("  write logic to process configElements here ");
            processConfigParamsAtGivenElement(e);

            // process all line demark
            NodeList nListLineDemarks = e.getElementsByTagName("paramGroupLineDemark");
            for (int idx2 = 0; idx2 < nListLineDemarks.getLength(); idx2++) {
              /////  System.out.println("                                       ** ===  EXPLICIT LINE DEMARK === ** " + nListLineDemarks.item(idx2).getAttributes().getNamedItem("name"));
            }

            // process all bold sections
            NodeList nListBolds = e.getElementsByTagName("paramGroupBold");

            int size = nListBolds.getLength();
            boolean paramsInsideBoldFound = false;
            if (size == 0) {
                return;
            } else {
                paramsInsideBoldFound = true;
            }
            for (int i = 0; i < size; i++) {
          /////      System.out.println("                                       **      EXPLICIT BOLD            ** " + nListBolds.item(i).getAttributes().getNamedItem("name"));
            }
        }
    }

    /**
     * @param e
     */
    private static void processConfigParamsAtGivenElement(Element e) {

        NodeList nListConfigElements = e.getElementsByTagName("element");   // THIS CAN BE param when telepresence gives it
        for (int elemItr = 0; elemItr < nListConfigElements.getLength(); elemItr++) {
            if (!viewOnlyGUIHierarchy) {
              /////  System.out.println("___process_config_element :: " + nListConfigElements.item(elemItr).getAttributes().getNamedItem("name"));
                String configParamType = nListConfigElements.item(elemItr).getAttributes().getNamedItem("type").getNodeValue();
                String html_mark = "";
                switch (configParamType) {
                    case "String":

                        // TBD --
                        html_mark = "<config_element_UI_markup_considering_its_typeString>";
                        break;

                    case "Integer":

                        // TBD --
                        html_mark = "<config_element_UI_markup_considering_its_typeInteger>";

                        break;

                    case "Literal":


                        // TBD --
                        html_mark = "<config_element_UI_markup_considering_its_typeLiteral>";
                        break;
                }

            }


        }
    }

    /**
     *
     * @param e
     * @param paramGroupTagName
     */
    private static void processConfigParamsAtGivenElementConsideringGUIHierarchy(Element e, String paramGroupTagName,String labelName) {

        NodeList nListConfigElements = e.getElementsByTagName("element");   // THIS CAN BE param when telepresence gives it
        for (int elemItr = 0; elemItr < nListConfigElements.getLength(); elemItr++) {
            if (!viewOnlyGUIHierarchy) {
               ////// System.out.println("___process_config_element :: " + nListConfigElements.item(elemItr).getAttributes().getNamedItem("name"));
            }


        }
    }
    public static button generateTabButton(Node p)
    {
        button b=new button();

        b.setTagText(p.getNodeValue());
        b.setID(p.getNodeValue());
        b.setType("Button");
        b.setOnClick("javascript:openTab('"+p.getNodeValue()+"')");
        //d.addElement(b); //add the button generated to a div tag
        return b;

    }

} // end of class definition
