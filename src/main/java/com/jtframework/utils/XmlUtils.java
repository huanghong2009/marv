package com.jtframework.utils;


import org.jdom2.Document;
import org.jdom2.JDOMException;
import com.alibaba.fastjson.JSONObject;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by alany on 2018/7/10.
 */
public class XmlUtils {
    /**
     * xml 转成json
     * @param xmlStr
     * @return
     * @throws JDOMException
     * @throws IOException
     */
    public static JSONObject xml2Json(String xmlStr) throws JDOMException, IOException {
        if (BaseUtils.isBlank(xmlStr)) {
            return null;
        }
        xmlStr = xmlStr.replaceAll("\\\n", "");
        byte[] xml = xmlStr.getBytes("UTF-8");
        JSONObject json = new JSONObject();
        InputStream is = new ByteArrayInputStream(xml);
        SAXBuilder sb = new SAXBuilder();
        Document doc = sb.build(is);
        Element root = doc.getRootElement();
        json.put(root.getName(), iterateElement(root));

        return json;
    }

    private static JSONObject iterateElement(Element element) {
        List<Element> node = element.getChildren();
        JSONObject obj = new JSONObject();
        List list = null;
        for (Element child : node) {
            list = new LinkedList();
            String text = child.getTextTrim();
            if (BaseUtils.isBlank((text))) {
                if (child.getChildren().size() == 0) {
                    continue;
                }
                if (obj.containsKey(child.getName())) {
                    list = (List) obj.get(child.getName());
                }
                list.add(iterateElement(child)); //遍历child的子节点
                obj.put(child.getName(), list);
            } else {
                if (obj.containsKey(child.getName())) {
                    Object value = obj.get(child.getName());
                    try {
                        list = (List) value;
                    } catch (ClassCastException e) {
                        list.add(value);
                    }
                }
                if (child.getChildren().size() == 0) { //child无子节点时直接设置text
                    obj.put(child.getName(), text);
                } else {
                    list.add(text);
                    obj.put(child.getName(), list);
                }
            }
        }
        return obj;
    }

    public static void main(String[] args) throws JDOMException, IOException {
        String xml = "{\"xml\":{\"ToUserName\":\"wwc29725ce638815e8\",\"AgentID\":\"3010040\",\"Encrypt\":\"gcKKUiK8ytvXEoV7Pm7nXu7jYkGuk8yfMTpRKau2j51Uc9aaqkEf2JOmAfOn6URHsPvhlfpLs+79FfOaF3uhcyPugVPu3uRbh0kbe25CsAHa5vVi3+HsARb3mtG8Hw+Bsy7XIi8E+r/6wV64C8klticcnYYw/aMezyUKFVLZAbTWntCoxySDNtBHbEppb+KAxBZHNYrNUFmN4BaA2W32uVT5/4RzbDObZAWGaHMcdBgqNckyh16i8M+3EGNOQCHfgpe4ILXeSatephNyD4lTODsKy/rF9biXsFNu4eXc6cd1DBrGTkdFzve6k+q2GsuROhM2GhD+5Nr+ViAzAMWccI0pp3L2yKup/16GPRFQMXZTJ4NxwNNXDcrJuqTI//sYvIKIkph9MaHHcU/K0iA3i/ZHhWs2QBH5mLIeOJDVXvpKDyb6t/iIBV2RhER0TJ+M9BhksVTV59VlHMm4+FsSJAMQmSv/yYc3Dvu7UaM2QBfWZjBJetRU9BRp+l0XaimbmdObslc7RTmoS1njVXaH3ZON+vutKC3+R9rcHlAYAE2vdJNIwPFreh+gsQZ9+B73XXJ2j3Gzgp6//GMMdT5kw33mqRVc2IkbOloEakZXDF06rPEfRS1uHZxUfQmivcq+C7lmF2ZNgFndFHmfjBPN2re7sFye9+PuH9EdjUMMHmULreUP/xrxDAW+dn4+IKnO8WSxXSRM4FAkpLf9NUS4EP7POf74R60M8HFwL43NjQ0kr6L4iiQBN47N5S1+B1iij2Nt7ogIjRYJHffRAoC2XJLL7Gd3L0BynqRh217fUDP7GW9goJO7XcBwSWSG685kuJsJ3erR38FTMA5F1hMsR2p73rF+O2wiJdNX7Ej+FsKvsr1Hk3b7iT3agvdVRXnbpNnkL1TvqrrRiiJPlq/e+BWlzZhVNnurrZx9g+vs4qATD29fS5rZuaH6LKDxxDfnEPvCacMHCdBw4fvzh2UWMN8wv9d9V1uHtKn/J2Ol16FuriGlVBj7w4HBES6T8wf7/kkXMIiGwJ3UFVDwqWVzdKCVb+4zY1d4h6C0UCA1ktIY7OmqsHUdgrPqDB4RGw+IThrEjp2o3x2AMPw5IxleGpId1SI4UXEjEX78wLyH2nVShvkfFhzaGSnT/AK9Ykpgffgiue5N8t0ShGqGHFoRzOiIvQ+7Ub4C0NV5FpQaj9e3WjgMmi574m5Nobo9weZdMTTS74t8eSoFB868hgb2cNPrsiCyAGL2eu4XcUZNJXqztj5MW7HAgcoD/0ZdohkOZ0cQSzppQMOIAgYUZntYJ+ViUleHkj/061tU89O5YTq54iHbtLSoH8t1yP+f/Ek/MxHoOvN4msmiDCFktF7dtqpGXCNdFMinR9Jxy38vXmYU9gTMrn+C47N4789Cmir4/1Z4Vww4ISRP6OZ7+yRScFDYg0vXA+olzCKwfYmViSbWG4XAKsJMVeFcl0OUXDq8An6nAluW4wO/6gb4XpsbK5He9OjQEddljQNuKlSLROAnEhycuJLRXc095OOsOroGeG5dU94A4KpWCmGWuKYgKjzhaRjQOG8WyMAMJpf11Gi9keTirod1bPPZxQ7eN6xL4SD1Xqbc7G4cw3OtGeXa30JJ1hysYDYUaNuH66M4CqACOZZf/nmG/nQBd1iy/Uj0GtXYuYNBDGOniMuCRHcKoC/rocB36nDGk3TrUCYjBzEMXnKgPgC55zaMHzB+SoPRyBP88WLGQBSAm4W06VLJqpdnktFCIf2HHYkqV4ia8cR15s72NxV/djojZMO4F6z9v6RXVUEhAifVvrhQuNsapPOaeclBLWBwd/m9V8BmANq+jU1jgMwwAeEY/kXmFQ26aJTBtKwT046DGqejOZmi8A==\"}}";
        System.out.println(XmlUtils.xml2Json(xml));
    }
}