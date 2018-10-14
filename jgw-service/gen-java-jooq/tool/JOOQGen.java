package tool;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jooq.util.jaxb.Configuration;



public class JOOQGen {

    public static void main(String[] args) throws Exception {
        List<String> lstDbConf = new ArrayList<>(Arrays.asList(
                "/home/tam/Desktop/MHH/kobishop-jgw-service/jgw-service/gen-java-jooq/tool/conf/kobishop_conf.xml"
        ));

        lstDbConf.stream().forEach(path -> {
            try {
                InputStream in;
                in = new FileInputStream(path);
                Configuration conf = org.jooq.util.GenerationTool.load(in);
                org.jooq.util.GenerationTool.generate(conf);
            } catch (Exception ex) {
                Logger.getLogger(JOOQGen.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
    }
}
