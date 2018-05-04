package net.gbmb.xemph.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import net.gbmb.xemph.Packet;
import net.gbmb.xemph.xml.XmpReader;
import org.apache.pdfbox.io.IOUtils;
import org.apache.pdfbox.io.RandomAccessFile;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.springframework.boot.*;
import org.springframework.boot.autoconfigure.*;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.*;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.xml.stream.XMLStreamException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

@Controller
@EnableAutoConfiguration
public class Application {

    @Bean
    public ObjectMapper objectMapper () {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS,false);
        return mapper;
    }

    @RequestMapping("/")
    @ResponseBody
    String home() {
        return "Hello World!";
    }

    public static void main(String[] args) throws Exception {
        SpringApplication.run(Application.class, args);
    }

    @RequestMapping(
            path="/extract",
            consumes = "application/octet-stream",
            produces = "application/json",
            method = RequestMethod.POST)
    @ResponseBody
    ExtractionResult extract (HttpServletRequest request) throws IOException {
        // copy in tmp (TODO very ugly)
        File tmp = File.createTempFile("xemph-","-service");
        FileOutputStream fos = new FileOutputStream(tmp);
        IOUtils.copy(request.getInputStream(),fos);
        IOUtils.closeQuietly(fos);
        // parse PDF
        RandomAccessFile raf = new RandomAccessFile(tmp,"r");
        PDFParser parser = new PDFParser(raf);
        parser.parse();
        // extract packet
        PDDocument document = parser.getPDDocument();
        InputStream xmp = document.getDocumentCatalog().getMetadata().exportXMPMetadata();
        XmpReader xmpReader = new XmpReader();
        try {
            Packet packet = xmpReader.parse(xmp);
            return new ExtractionResult(packet);
        } catch (XMLStreamException e) {
            return new ExtractionResult(e);
        }
    }
}