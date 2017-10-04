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

/**
 * Created by Guillaume Bailleul on 26/06/2017.
 */
@RunWith(value = Parameterized.class)
public class BfoParsingIT extends AbstractParsing {

    private static File root = new File("target/suites/bfo/pdfa-testsuite-master");

    public BfoParsingIT(File target) {
        super(target);
    }


    @Parameterized.Parameters(name="{index}: {0}")
    public static Collection<Object[]> data () throws Exception {
         Collection<Object[]> result = new ArrayList<>(1);
        result.add(new Object[] {new File("target/suites/bfo/pdfa-testsuite-master/pdfa2-6-3-3-bfo-t01-fail.pdf")});
        return result;
    }

    public static List<String> getSkipFile () {
        List<String> ret = new ArrayList<>();
        return ret;
    }


}
