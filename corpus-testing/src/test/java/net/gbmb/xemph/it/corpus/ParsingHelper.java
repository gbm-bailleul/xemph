/*
 * Copyright 2017 Guillaume Bailleul.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

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

    protected static Collection<Object[]> data (File root, List<String> skipList) {
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
