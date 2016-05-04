package nz.co.cjc.base.framework.utils;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.annotation.Config;

import nz.co.cjc.base.robolectric.RobolectricApplicationTestRunner;
import nz.co.cjc.base.robolectric.RobolectricBuildConfig;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * Created by Chris Cooper on 4/05/16.
 *
 * Tests for string utils class
 */
@RunWith(RobolectricApplicationTestRunner.class)
@Config(sdk = RobolectricBuildConfig.ROBOLECTRIC_SDK, constants = RobolectricBuildConfig.class)
public class StringUtilsTest {
    @Test
    public void testIsEmptyNullInput() {

        //Setup
        String input = null;

        //Run
        boolean result = StringUtils.isEmpty(input);

        //Verify
        assertThat(result, is(true));
    }

    @Test
    public void testIsEmptyEmptyInput() {

        //Setup
        String input = "";

        //Run
        boolean result = StringUtils.isEmpty(input);

        //Verify
        assertThat(result, is(true));
    }

    @Test
    public void testIsEmptyNonEmptyInput() {
        //Setup
        String input = "hello";
        //Run
        boolean result = StringUtils.isEmpty(input);
        //Verify
        assertThat(result, is(false));
    }

    @Test
    public void testEmptyIfNullWithNullInput() {

        //Setup
        String input = null;

        //Run
        String result = StringUtils.emptyIfNull(input);

        //Verify
        assertThat(result, is(""));
    }

    @Test
    public void testEmptyIfNullWithNormalInput() {

        //Setup
        String input = "hello";

        //Run
        String result = StringUtils.emptyIfNull(input);

        //Verify
        assertThat(result, is(input));
    }

    @Test
    public void testGetDateSuffixWithFirstSecondThird() {

        //Setup
        String inputFirst = "21";
        String inputSecond = "22";
        String inputThird = "23";

        //Run
        String resultFirst = StringUtils.getDateSuffix(inputFirst);
        String resultSecond = StringUtils.getDateSuffix(inputSecond);
        String resultThird = StringUtils.getDateSuffix(inputThird);

        //Verify
        assertThat(resultFirst, is("st"));
        assertThat(resultSecond, is("nd"));
        assertThat(resultThird, is("rd"));

    }

    @Test
    public void testGetDateSuffixWithElevenTwelveThirteen() {
        //Setup
        String inputEleven = "11";
        String inputTwelve = "12";
        String inputThirteen = "13";
        //Run
        String resultEleven = StringUtils.getDateSuffix(inputEleven);
        String resultTwelve = StringUtils.getDateSuffix(inputTwelve);
        String resultThirteen = StringUtils.getDateSuffix(inputThirteen);
        //Verify
        assertThat(resultEleven, is("th"));
        assertThat(resultTwelve, is("th"));
        assertThat(resultThirteen, is("th"));
    }

    @Test
    public void testGetDateSuffixWithZeroSevenEightNine() {
        //Setup
        String inputZero = "0";
        String inputSeven = "7";
        String inputEight = "8";
        String inputNine = "9";

        //Run
        String resultZero = StringUtils.getDateSuffix(inputZero);
        String resultSeven = StringUtils.getDateSuffix(inputSeven);
        String resultEight = StringUtils.getDateSuffix(inputEight);
        String resultNine = StringUtils.getDateSuffix(inputNine);

        //Verify
        assertThat(resultZero, is("th"));
        assertThat(resultSeven, is("th"));
        assertThat(resultEight, is("th"));
        assertThat(resultNine, is("th"));
    }

}