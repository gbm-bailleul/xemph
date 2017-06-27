package net.gbmb.xemph.it.corpus;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by Guillaume Bailleul on 27/06/2017.
 */
public class ParsingHelper {

    protected static Collection<Object[]> data (File root, List<String> skipList) throws Exception {
        Collection<File> files = FileUtils.listFiles(root,new String []{"PDF","pdf"},true);
        Collection<Object[]> result = new ArrayList<>(files.size());

        for (File file: files) {
            if (skipList.contains(file.getName()))
                continue; // skip file in skip list
            result.add(new Object []{file});
        }
        return result;
    }

}
