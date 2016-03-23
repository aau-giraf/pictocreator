package dk.aau.cs.giraf.pictocreator;

import static org.junit.Assert.*;

import org.junit.Test;

public class ResultFunctionTest {
    @Test
    public void testGetRequestCode_CalculatingRCFromRF_ReturnRequestCode() {
        assertEquals(2, ResultFunction.LOADPICTOINFO.getRequestCode());
    }

    @Test
    public void testFromRequestCode_CalculatingRFFromRC_ReturnResultFunction() {
        assertEquals(ResultFunction.LOADPICTOINFO, ResultFunction.fromRequestCode(2));
    }
}
