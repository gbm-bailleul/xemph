package net.gbmb.xemph.it.corpus;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by Guillaume Bailleul on 26/06/2017.
 */
@RunWith(value = Parameterized.class)
public class BfoParsingIT extends AbstractParsing {


    // TODO
    private static File root = new File("target/suites/bfo/pdfa-testsuite-master");


    public BfoParsingIT(File target) {
        super(target);
    }


    @Parameterized.Parameters(name="{index}: {0}")
    public static Collection<Object[]> data () throws Exception {
        return ParsingHelper.data(root,getSkipFile());
    }

    public static List<String> getSkipFile () {
        List ret = new ArrayList();
  //      ret.add("isartor-6-1-3-t02-fail-a.pdf"); // encrypted PDF
    //    ret.add("isartor-6-7-2-t01-fail-a.pdf"); // no xmp in PDF
        return ret;
    }


}
