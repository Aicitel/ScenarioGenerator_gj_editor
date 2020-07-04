package amadeus.controller;

import amadeus.core.XmlGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.w3c.dom.Document;

import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.OutputStream;
import java.util.Arrays;

@Controller
@RequestMapping(value = "/generate")
public class GenerateController {

    private static final Logger logger = LoggerFactory.getLogger(GenerateController.class);

    @RequestMapping(value = "/amount", method = RequestMethod.POST)
    public void amount(
            HttpServletResponse response,
            @RequestParam("file") MultipartFile file,
            String targetAvatarName,
            Double width, Double height,
            Integer lineCount, Integer columnCount,
            Double random
    ) throws Exception{

        response.setContentType("application/octet-stream;charset=utf-8");
        response.setHeader("Content-Disposition", "attachment;filename=" + file.getName() + "_amount.xml");
        response.flushBuffer();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document root = builder.parse(file.getInputStream());
        Document lightXml = builder.parse("src/main/resources/templates/light.xml");
//        XmlGenerator xmlGenerator = new XmlGenerator(false,
//                targetAvatarName, Arrays.asList(lineDistance, columnDistance),
//                lineCount, columnCount, random);
        XmlGenerator xmlGenerator = new XmlGenerator(false,
                targetAvatarName, Arrays.asList(width, height),
                lineCount, columnCount, random);
        xmlGenerator.init(root.getDocumentElement());
        OutputStream outputStream = response.getOutputStream();
        xmlGenerator.generateXml(root.getDocumentElement(), lightXml.getDocumentElement(),
                outputStream, null);
        outputStream.flush();
        outputStream.close();
    }

}
