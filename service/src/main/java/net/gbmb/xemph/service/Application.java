package net.gbmb.xemph.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import net.gbmb.xemph.Packet;
import net.gbmb.xemph.xml.XmpReader;
import org.apache.commons.io.IOUtils;
import org.apache.pdfbox.io.RandomAccessFile;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.*;
import org.springframework.boot.autoconfigure.*;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.*;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.xml.stream.XMLStreamException;
import java.io.*;
import java.util.UUID;

@Controller
@EnableAutoConfiguration
public class Application {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Bean
    public ObjectMapper objectMapper () {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS,false);
        return mapper;
    }

    @Value("${storage.dir:/tmp}")
    private File storageDir;

    @Value("${working.dir:/tmp}")
    private File workingDir;

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
    ExtractionResult extract (
            HttpServletRequest request,
            @RequestParam(name = "store", defaultValue = "false") boolean store,
            @RequestParam(name = "key", defaultValue = "<NOKEY>") String key
    ) throws IOException {
        String identifier = UUID.randomUUID().toString();
        logger.info("Starting request with internal id '{}' for key '{}'",identifier,key);
        byte [] xmp = extract(request.getInputStream());
        if (store)
            storeRaw(identifier,xmp);
        // parse
        ExtractionResult result =  new ExtractionResult();
        result.setIdentifier(identifier);
        try {
            XmpReader xmpReader = new XmpReader();
            Packet packet = xmpReader.parse(new ByteArrayInputStream(xmp));
            // store if needed
            result.setPacket(packet);
        } catch (XMLStreamException e) {
            result.setFailingException(e);
        }
        if (store) store(identifier,result);
        return result;
    }

    private byte [] extract (InputStream pdfFile) throws IOException {
        // copy in tmp (TODO very ugly)
        File tmp = File.createTempFile("xemph-","-service",workingDir);
        try (FileOutputStream fos = new FileOutputStream(tmp)) {
            IOUtils.copy(pdfFile, fos);
            fos.close();
            // parse PDF
            RandomAccessFile raf = new RandomAccessFile(tmp,"r");
            PDFParser parser = new PDFParser(raf);
            parser.parse();
            // extract packet
            PDDocument document = parser.getPDDocument();
            InputStream xmp = document.getDocumentCatalog().getMetadata().exportXMPMetadata();
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            IOUtils.copy(xmp,bos);
            bos.close();
            document.close();
            return bos.toByteArray();
        } finally {
            tmp.delete();
        }
    }


    private void store (String identifier,ExtractionResult result) throws IOException {
        File resultDir = new File(storageDir,identifier);
        if (!resultDir.exists()) {
            if (!resultDir.mkdir()) {
                throw new IOException("Failed to create target directory: "+resultDir.getAbsolutePath());
            }
        }
        try (FileOutputStream res = new FileOutputStream(new File(resultDir,"result.json"))) {
            objectMapper().writeValue(res,result);
            logger.info("Stored result for {}",identifier);
        }
    }

    private void storeRaw (String  identifier,byte [] content) throws IOException {
        File resultDir = new File(storageDir,identifier);
        if (!resultDir.exists()) {
            if (!resultDir.mkdir()) {
                throw new IOException("Failed to create target directory: "+resultDir.getAbsolutePath());
            }
        }
        try (FileOutputStream raw = new FileOutputStream(new File(resultDir,"raw.bin"))) {
            IOUtils.write(content,raw);
            logger.info("Stored xmp block for {}",identifier);
        }

    }

}