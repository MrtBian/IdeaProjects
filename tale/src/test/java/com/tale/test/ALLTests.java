package com.tale.test;

import com.blade.test.BladeApplication;
import com.tale.Application;
import com.tale.test.service.OptionsServiceTest;
import com.tale.test.utils.MapCacheTest;
import com.tale.test.utils.TaleUtilsTest;
import junit.framework.TestCase;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * @author biezhi
 * @date 2018/6/3
 */
@RunWith(BladeTestRunner.class)
@BladeApplication(Application.class)
@Suite.SuiteClasses(
        {OptionsServiceTest.class,
                MapCacheTest.class,
                TaleUtilsTest.class}
)
public abstract class ALLTests {

}
