package org.nybatis.core.db.session.controller;

import org.nybatis.core.db.session.executor.util.DbUtils;
import org.nybatis.core.db.session.executor.util.QueryParameter;
import org.nybatis.core.log.NLogger;
import org.nybatis.core.model.NMap;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * @author nayasis@gmail.com
 * @since 2015-08-20
 */
public class DbUtilsTest {

    @Test
    public void getNrowParam() {

        SampleVo vo = getSampleVo();

        vo.children.add( getSampleVo() );
        vo.children.add( getSampleVo() );
        vo.children.add( getSampleVo() );
        vo.children.add( getSampleVo() );

        NMap nMap = new QueryParameter( vo );

        NLogger.debug( nMap );

        assertEquals(
                nMap.toJson(),
                "{name=nayasis, age=39, firstChild={name=firstChild, age=5, firstChild=null, children=[]}, children=[{name=nayasis, age=39, firstChild={name=firstChild, age=5, firstChild=null, children=[]}, children=[]}, {name=nayasis, age=39, firstChild={name=firstChild, age=5, firstChild=null, children=[]}, children=[]}, {name=nayasis, age=39, firstChild={name=firstChild, age=5, firstChild=null, children=[]}, children=[]}, {name=nayasis, age=39, firstChild={name=firstChild, age=5, firstChild=null, children=[]}, children=[]}]}"
        );

    }

    private SampleVo getSampleVo() {

        SampleVo vo = new SampleVo();

        vo.name = "nayasis";
        vo.age  = 39;

        SampleVo child = new SampleVo();

        child.name = "firstChild";
        child.age  = 5;

        vo.firstChild = child;

        return vo;

    }

}
