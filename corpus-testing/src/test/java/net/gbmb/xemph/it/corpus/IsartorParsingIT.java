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

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@RunWith(value = Parameterized.class)
public class IsartorParsingIT extends AbstractParsing {


    private static File root = new File("target/suites/isartor/Isartor testsuite/PDFA-1b");


    public IsartorParsingIT (File target) {
        super(target);
    }


    @Parameterized.Parameters(name="{index}: {0}")
    public static Collection<Object[]> data () throws Exception {
        return ParsingHelper.data(root,getSkipFile());
    }

    public static List<String> getSkipFile () {
        List<String> ret = new ArrayList<>();
        ret.add("isartor-6-1-3-t02-fail-a.pdf"); // encrypted PDF
        ret.add("isartor-6-7-2-t01-fail-a.pdf"); // no xmp in PDF
        return ret;
    }


}
