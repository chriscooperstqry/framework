package nz.co.cjc.base.features.categoriesandlistings.logic;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import java.util.List;

import nz.co.cjc.base.BuildConfig;
import nz.co.cjc.base.features.categoriesandlistings.events.CategoryEvent;
import nz.co.cjc.base.features.categoriesandlistings.models.CategoryData;
import nz.co.cjc.base.features.categoriesandlistings.providers.contract.CategoriesAndListingsProvider;
import nz.co.cjc.base.framework.eventbus.providers.contracts.EventBusProvider;
import nz.co.cjc.base.framework.statesaver.providers.contract.StateSaverProvider;
import nz.co.cjc.base.framework.strings.providers.contracts.StringsProvider;
import nz.co.cjc.base.robolectric.RobolectricBuildConfig;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isEmptyOrNullString;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

/**
 * Created by Chris Cooper on 8/05/16.
 * <p>
 * Tests for categories view logic
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(sdk = RobolectricBuildConfig.ROBOLECTRIC_SDK, constants = BuildConfig.class)
public class CategoriesViewLogicTest {

    @Spy
    TestCategoriesAndListingsProvider mCategoriesAndListingsProvider;

    @Mock
    private StringsProvider mStringsProvider;

    @Mock
    EventBusProvider mEventBusProvider;

    @Mock
    StateSaverProvider mStateSaverProvider;

    @Mock
    CategoriesViewLogic.ViewLogicDelegate mDelegate;

    @Captor
    ArgumentCaptor<List<CategoryData>> mCategoryDataListCaptor;

    @Captor
    ArgumentCaptor<CategoryEvent> mCategoryEventCaptor;

    @Captor
    ArgumentCaptor<String> mStringCaptor;

    @Captor
    ArgumentCaptor<CategoriesAndListingsProvider.CategoriesRequestDelegate> mCategoriesRequestDelegateCaptor;

    @Rule
    public ExpectedException mExpectedException = ExpectedException.none();

    private CategoriesViewLogic mViewLogic;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        mViewLogic = new CategoriesViewLogic(
                mStringsProvider,
                mCategoriesAndListingsProvider,
                mEventBusProvider,
                mStateSaverProvider);

    }

    @Test
    public void testInitViewLogicNullDelegate() {

        //Run
        mViewLogic.initViewLogic(null, new Bundle(), null);

        //Verify
        verifyZeroInteractions(mDelegate);
    }

    @Test
    public void testInitViewLogicNoSavedInstance() {

        //Run
        mViewLogic.initViewLogic(mDelegate, new Bundle(), null);

        //Verify
        verifyZeroInteractions(mStateSaverProvider);
    }

    @Test
    public void testInitViewLogicSavedInstance() {

        //Setup
        Bundle args = new Bundle();
        args.putParcelable("Category.Data", generateCategoryDataWithSubcategories().get(0));

        Bundle state = new Bundle();
        when(mStateSaverProvider.getInt("State.Int", state, -1)).thenReturn(5);

        //Run
        mViewLogic.initViewLogic(mDelegate, args, state);

        //Verify
        verify(mStateSaverProvider).getInt("State.Int", state, -1);
        verify(mDelegate).setSelectedItem(5);
    }

    @Test
    public void testSavedCategoryData() {

        //Setup
        Bundle bundle = new Bundle();
        bundle.putParcelable("Category.Data", generateCategoryDataWithSubcategories().get(0));

        //Run
        mViewLogic.initViewLogic(mDelegate, bundle, null);

        //Verify
        verify(mDelegate).populateScreen(mCategoryDataListCaptor.capture());
        verify(mDelegate).setSelectedItem(anyInt());
        assertThat(mCategoryDataListCaptor.getValue().size(), is(4));

        verifyZeroInteractions(mCategoriesAndListingsProvider);
    }

    @Test
    public void testFetchDataSuccess() {

        //Setup
        mCategoriesAndListingsProvider.requestAction = TestCategoriesAndListingsProvider.RequestAction.Success;
        List<CategoryData> resultData = generateCategoryDataWithSubcategories();
        mCategoriesAndListingsProvider.requestResult = resultData;

        //Run
        mViewLogic.initViewLogic(mDelegate, new Bundle(), null);

        //Verify
        verify(mDelegate).showProgressBar();
        verify(mDelegate).hideErrorView();
        verify(mCategoriesAndListingsProvider).getCategoriesData(mStringCaptor.capture(), mCategoriesRequestDelegateCaptor.capture());
        verify(mDelegate).populateScreen(resultData);
        verify(mDelegate).hideProgressBar();
        verify(mDelegate, never()).showErrorView();
        assertThat(mStringCaptor.getValue(), is(isEmptyOrNullString()));
    }

    @Test
    public void testFetchDataFailed() {

        //Setup
        mCategoriesAndListingsProvider.requestAction = TestCategoriesAndListingsProvider.RequestAction.Failed;

        //Run
        mViewLogic.initViewLogic(mDelegate, new Bundle(), null);

        //Verify
        verify(mDelegate).showProgressBar();
        verify(mDelegate).hideErrorView();
        verify(mCategoriesAndListingsProvider).getCategoriesData(mStringCaptor.capture(), mCategoriesRequestDelegateCaptor.capture());
        verify(mDelegate,never()).populateScreen(anyListOf(CategoryData.class));
        verify(mDelegate).hideProgressBar();
        verify(mDelegate).showErrorView();
        assertThat(mStringCaptor.getValue(), is(isEmptyOrNullString()));

    }

    @Test
    public void testListItemSelectedWithSubcategories() {

        //Setup
        mCategoriesAndListingsProvider.requestAction = TestCategoriesAndListingsProvider.RequestAction.Success;
        List<CategoryData> resultData = generateCategoryDataWithSubcategories();
        mCategoriesAndListingsProvider.requestResult = resultData;
        CategoryData dataAtPosOne = resultData.get(1);

        //Run
        mViewLogic.initViewLogic(mDelegate, new Bundle(), null);
        mViewLogic.listItemSelected(1);

        //Verify
        verify(mEventBusProvider).postEvent(mCategoryEventCaptor.capture());
        verify(mDelegate, never()).setSelectedItem(anyInt());
        assertThat(mCategoryEventCaptor.getValue().getEventType(), is(CategoryEvent.EventType.CategorySelected));
        assertThat(mCategoryEventCaptor.getValue().getBundle().getParcelable("Category.Data"), Matchers.<Parcelable>is(dataAtPosOne));
    }

    @Test
    public void testListItemSelectedWithNoSubcategories() {

        //Setup
        mCategoriesAndListingsProvider.requestAction = TestCategoriesAndListingsProvider.RequestAction.Success;
        List<CategoryData> resultData = generateCategoryDataWithNoSubcategories();
        mCategoriesAndListingsProvider.requestResult = resultData;
        CategoryData dataAtPosOne = resultData.get(1);

        //Run
        mViewLogic.initViewLogic(mDelegate, new Bundle(), null);
        mViewLogic.listItemSelected(1);

        //Verify
        verify(mEventBusProvider).postEvent(mCategoryEventCaptor.capture());
        verify(mDelegate).setSelectedItem(1);
        assertThat(mCategoryEventCaptor.getValue().getEventType(), is(CategoryEvent.EventType.CategorySelected));
        assertThat(mCategoryEventCaptor.getValue().getBundle().getParcelable("Category.Data"), Matchers.<Parcelable>is(dataAtPosOne));
    }

    @Test
    public void testScreenResumed() {

        //Run
        mViewLogic.initViewLogic(mDelegate, new Bundle(), null);
        mViewLogic.screenResumed();

        //Verify
        verify(mEventBusProvider).subscribe(mViewLogic);
        verify(mEventBusProvider).postEvent(mCategoryEventCaptor.capture());
        assertThat(mCategoryEventCaptor.getValue().getEventType(), is(CategoryEvent.EventType.CategoryLayoutReady));
        assertThat(mCategoryEventCaptor.getValue().getBundle(), is(nullValue()));
    }

    @Test
    public void testScreenPaused() {

        //Run
        mViewLogic.initViewLogic(mDelegate, new Bundle(), null);
        mViewLogic.screenPaused();

        //Verify
        verify(mEventBusProvider).unsubscribe(mViewLogic);
    }

    @Test
    public void testOnEventClearCategorySelection() {
        //Setup
        CategoryEvent categoryEvent = new CategoryEvent(null, CategoryEvent.EventType.ClearCategorySelection, null);

        //Run
        mViewLogic.initViewLogic(mDelegate, new Bundle(), null);
        mViewLogic.onEvent(categoryEvent);

        //Verify
        verify(mDelegate).setSelectedItem(-1);
    }

    @Test
    public void testSaveInstanceState() {

        //Setup
        Bundle outState = new Bundle();
        mCategoriesAndListingsProvider.requestAction = TestCategoriesAndListingsProvider.RequestAction.Success;
        mCategoriesAndListingsProvider.requestResult = generateCategoryDataWithSubcategories();

        //Run
        mViewLogic.initViewLogic(mDelegate, new Bundle(), null);
        mViewLogic.listItemSelected(3);
        mViewLogic.onSaveInstanceState(outState);

        //Verify
        verify(mStateSaverProvider).saveInt("State.Int",3, outState);
    }

    @Test
    public void testOnErrorViewClick() {

        //Setup`
        mCategoriesAndListingsProvider.requestAction = TestCategoriesAndListingsProvider.RequestAction.Failed;

        //Run
        mViewLogic.initViewLogic(mDelegate, new Bundle(), null);
        reset(mCategoriesAndListingsProvider);
        mViewLogic.onErrorViewClick();

        //Verify
        verify(mCategoriesAndListingsProvider).getCategoriesData(mStringCaptor.capture(), mCategoriesRequestDelegateCaptor.capture());
    }



    /**
     * Test class to simulate the CategoriesAndListingsProvider.
     */
    private static class TestCategoriesAndListingsProvider implements CategoriesAndListingsProvider {

        public enum RequestAction {
            None,
            Success,
            Failed
        }

        public RequestAction requestAction;
        public List<CategoryData> requestResult;

        public TestCategoriesAndListingsProvider() {
            requestAction = RequestAction.None;
        }

        @Override
        public void getCategoriesData(@Nullable String categoryNumber, @NonNull CategoriesRequestDelegate requestDelegate) {

            switch (requestAction) {
                case Success:
                    requestDelegate.requestSuccess(requestResult);
                    break;
                case Failed:
                    requestDelegate.requestFailed();
                    break;
            }
        }

        @Override
        public void getListingsData(@Nullable String categoryNumber, @NonNull ListingsRequestDelegate requestDelegate) {

        }

    }


    private List<CategoryData> generateCategoryDataWithNoSubcategories() {
        return parseJson(SAMPLE_CATEGORY_DATA_WITH_NO_SUBCATEGORIES);
    }

    private List<CategoryData> generateCategoryDataWithSubcategories() {
        return parseJson(SAMPLE_CATEGORY_DATA_WITH_SUBCATEGORIES);
    }

    private List<CategoryData> parseJson(String input) {
        try {
            JsonObject rootObject = new JsonParser().parse(input).getAsJsonObject();
            JsonArray subcategories = rootObject.getAsJsonArray(CategoriesAndListingsProvider.SUBCATEGORIES);

            return new Gson().fromJson(subcategories, new TypeToken<List<CategoryData>>() {
            }.getType());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static final String SAMPLE_CATEGORY_DATA_WITH_NO_SUBCATEGORIES = "{\n" +
            "  \"Name\": \"Jetskis\",\n" +
            "  \"Number\": \"0001-0348-2948-\",\n" +
            "  \"Path\": \"\\/Trade-Me-Motors\\/Boats-marine\\/Jetskis\",\n" +
            "  \"Subcategories\": [\n" +
            "    {\n" +
            "      \"Name\": \"Kawasaki\",\n" +
            "      \"Number\": \"0001-0348-2948-8556-\",\n" +
            "      \"Path\": \"\\/Trade-Me-Motors\\/Boats-marine\\/Jetskis\\/Kawasaki\",\n" +
            "      \"HasClassifieds\": true\n" +
            "    },\n" +
            "    {\n" +
            "      \"Name\": \"Seadoo\",\n" +
            "      \"Number\": \"0001-0348-2948-8554-\",\n" +
            "      \"Path\": \"\\/Trade-Me-Motors\\/Boats-marine\\/Jetskis\\/Seadoo\",\n" +
            "      \"HasClassifieds\": true\n" +
            "    },\n" +
            "    {\n" +
            "      \"Name\": \"Yamaha\",\n" +
            "      \"Number\": \"0001-0348-2948-8555-\",\n" +
            "      \"Path\": \"\\/Trade-Me-Motors\\/Boats-marine\\/Jetskis\\/Yamaha\",\n" +
            "      \"HasClassifieds\": true\n" +
            "    },\n" +
            "    {\n" +
            "      \"Name\": \"Other jetskis\",\n" +
            "      \"Number\": \"0001-0348-2948-2949-\",\n" +
            "      \"Path\": \"\\/Trade-Me-Motors\\/Boats-marine\\/Jetskis\\/Other-jetskis\",\n" +
            "      \"HasClassifieds\": true\n" +
            "    },\n" +
            "    {\n" +
            "      \"Name\": \"Parts\",\n" +
            "      \"Number\": \"0001-0348-2948-8553-\",\n" +
            "      \"Path\": \"\\/Trade-Me-Motors\\/Boats-marine\\/Jetskis\\/Parts\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"Name\": \"Accessories\",\n" +
            "      \"Number\": \"0001-0348-2948-2950-\",\n" +
            "      \"Path\": \"\\/Trade-Me-Motors\\/Boats-marine\\/Jetskis\\/Accessories\"\n" +
            "    }\n" +
            "  ],\n" +
            "  \"HasClassifieds\": true\n" +
            "}";

    private static final String SAMPLE_CATEGORY_DATA_WITH_SUBCATEGORIES = "{\n" +
            "  \"Name\": \"Boats & marine\",\n" +
            "  \"Number\": \"0001-0348-\",\n" +
            "  \"Path\": \"\\/Trade-Me-Motors\\/Boats-marine\",\n" +
            "  \"Subcategories\": [\n" +
            "    {\n" +
            "      \"Name\": \"Dinghies & rowboats\",\n" +
            "      \"Number\": \"0001-0348-1261-\",\n" +
            "      \"Path\": \"\\/Trade-Me-Motors\\/Boats-marine\\/Dinghies-rowboats\",\n" +
            "      \"Subcategories\": [\n" +
            "        {\n" +
            "          \"Name\": \"Aluminium\",\n" +
            "          \"Number\": \"0001-0348-1261-1287-\",\n" +
            "          \"Path\": \"\\/Trade-Me-Motors\\/Boats-marine\\/Dinghies-rowboats\\/Aluminium\",\n" +
            "          \"HasClassifieds\": true\n" +
            "        },\n" +
            "        {\n" +
            "          \"Name\": \"Fibreglass\",\n" +
            "          \"Number\": \"0001-0348-1261-2971-\",\n" +
            "          \"Path\": \"\\/Trade-Me-Motors\\/Boats-marine\\/Dinghies-rowboats\\/Fibreglass\",\n" +
            "          \"HasClassifieds\": true\n" +
            "        },\n" +
            "        {\n" +
            "          \"Name\": \"Inflatable\",\n" +
            "          \"Number\": \"0001-0348-1261-1288-\",\n" +
            "          \"Path\": \"\\/Trade-Me-Motors\\/Boats-marine\\/Dinghies-rowboats\\/Inflatable\",\n" +
            "          \"HasClassifieds\": true\n" +
            "        },\n" +
            "        {\n" +
            "          \"Name\": \"Wood\",\n" +
            "          \"Number\": \"0001-0348-1261-1289-\",\n" +
            "          \"Path\": \"\\/Trade-Me-Motors\\/Boats-marine\\/Dinghies-rowboats\\/Wood\",\n" +
            "          \"HasClassifieds\": true\n" +
            "        }\n" +
            "      ],\n" +
            "      \"HasClassifieds\": true\n" +
            "    },\n" +
            "    {\n" +
            "      \"Name\": \"Jetskis\",\n" +
            "      \"Number\": \"0001-0348-2948-\",\n" +
            "      \"Path\": \"\\/Trade-Me-Motors\\/Boats-marine\\/Jetskis\",\n" +
            "      \"Subcategories\": [\n" +
            "        {\n" +
            "          \"Name\": \"Kawasaki\",\n" +
            "          \"Number\": \"0001-0348-2948-8556-\",\n" +
            "          \"Path\": \"\\/Trade-Me-Motors\\/Boats-marine\\/Jetskis\\/Kawasaki\",\n" +
            "          \"HasClassifieds\": true\n" +
            "        },\n" +
            "        {\n" +
            "          \"Name\": \"Seadoo\",\n" +
            "          \"Number\": \"0001-0348-2948-8554-\",\n" +
            "          \"Path\": \"\\/Trade-Me-Motors\\/Boats-marine\\/Jetskis\\/Seadoo\",\n" +
            "          \"HasClassifieds\": true\n" +
            "        },\n" +
            "        {\n" +
            "          \"Name\": \"Yamaha\",\n" +
            "          \"Number\": \"0001-0348-2948-8555-\",\n" +
            "          \"Path\": \"\\/Trade-Me-Motors\\/Boats-marine\\/Jetskis\\/Yamaha\",\n" +
            "          \"HasClassifieds\": true\n" +
            "        },\n" +
            "        {\n" +
            "          \"Name\": \"Other jetskis\",\n" +
            "          \"Number\": \"0001-0348-2948-2949-\",\n" +
            "          \"Path\": \"\\/Trade-Me-Motors\\/Boats-marine\\/Jetskis\\/Other-jetskis\",\n" +
            "          \"HasClassifieds\": true\n" +
            "        },\n" +
            "        {\n" +
            "          \"Name\": \"Parts\",\n" +
            "          \"Number\": \"0001-0348-2948-8553-\",\n" +
            "          \"Path\": \"\\/Trade-Me-Motors\\/Boats-marine\\/Jetskis\\/Parts\"\n" +
            "        },\n" +
            "        {\n" +
            "          \"Name\": \"Accessories\",\n" +
            "          \"Number\": \"0001-0348-2948-2950-\",\n" +
            "          \"Path\": \"\\/Trade-Me-Motors\\/Boats-marine\\/Jetskis\\/Accessories\"\n" +
            "        }\n" +
            "      ],\n" +
            "      \"HasClassifieds\": true\n" +
            "    },\n" +
            "    {\n" +
            "      \"Name\": \"Marina berths\",\n" +
            "      \"Number\": \"0001-0348-6987-\",\n" +
            "      \"Path\": \"\\/Trade-Me-Motors\\/Boats-marine\\/Marina-berths\",\n" +
            "      \"HasClassifieds\": true\n" +
            "    },\n" +
            "    {\n" +
            "      \"Name\": \"Motorboats\",\n" +
            "      \"Number\": \"0001-0348-0030-\",\n" +
            "      \"Path\": \"\\/Trade-Me-Motors\\/Boats-marine\\/Motorboats\",\n" +
            "      \"HasClassifieds\": true\n" +
            "    },\n" +
            "    {\n" +
            "      \"Name\": \"Parts & accessories\",\n" +
            "      \"Number\": \"0001-0348-3935-\",\n" +
            "      \"Path\": \"\\/Trade-Me-Motors\\/Boats-marine\\/Parts-accessories\",\n" +
            "      \"Subcategories\": [\n" +
            "        {\n" +
            "          \"Name\": \"Anchors\",\n" +
            "          \"Number\": \"0001-0348-3935-3937-\",\n" +
            "          \"Path\": \"\\/Trade-Me-Motors\\/Boats-marine\\/Parts-accessories\\/Anchors\"\n" +
            "        },\n" +
            "        {\n" +
            "          \"Name\": \"Blocks, shackles & deck hardware\",\n" +
            "          \"Number\": \"0001-0348-3935-9873-\",\n" +
            "          \"Path\": \"\\/Trade-Me-Motors\\/Boats-marine\\/Parts-accessories\\/Blocks-shackles-deck-hardware\"\n" +
            "        },\n" +
            "        {\n" +
            "          \"Name\": \"Boat trailers\",\n" +
            "          \"Number\": \"0001-0348-3935-8552-\",\n" +
            "          \"Path\": \"\\/Trade-Me-Motors\\/Boats-marine\\/Parts-accessories\\/Boat-trailers\",\n" +
            "          \"HasClassifieds\": true\n" +
            "        },\n" +
            "        {\n" +
            "          \"Name\": \"Bolts, anodes & fittings\",\n" +
            "          \"Number\": \"0001-0348-3935-9874-\",\n" +
            "          \"Path\": \"\\/Trade-Me-Motors\\/Boats-marine\\/Parts-accessories\\/Bolts-anodes-fittings\"\n" +
            "        },\n" +
            "        {\n" +
            "          \"Name\": \"Buoys & fenders\",\n" +
            "          \"Number\": \"0001-0348-3935-4152-\",\n" +
            "          \"Path\": \"\\/Trade-Me-Motors\\/Boats-marine\\/Parts-accessories\\/Buoys-fenders\"\n" +
            "        },\n" +
            "        {\n" +
            "          \"Name\": \"Category 10006\",\n" +
            "          \"Number\": \"0001-0348-3935-10006-\",\n" +
            "          \"Path\": \"\\/Trade-Me-Motors\\/Boats-marine\\/Parts-accessories\\/Category-10006\"\n" +
            "        },\n" +
            "        {\n" +
            "          \"Name\": \"Engine parts\",\n" +
            "          \"Number\": \"0001-0348-3935-7278-\",\n" +
            "          \"Path\": \"\\/Trade-Me-Motors\\/Boats-marine\\/Parts-accessories\\/Engine-parts\"\n" +
            "        },\n" +
            "        {\n" +
            "          \"Name\": \"Fish finders\",\n" +
            "          \"Number\": \"0001-0348-3935-8551-\",\n" +
            "          \"Path\": \"\\/Trade-Me-Motors\\/Boats-marine\\/Parts-accessories\\/Fish-finders\"\n" +
            "        },\n" +
            "        {\n" +
            "          \"Name\": \"Lighting\",\n" +
            "          \"Number\": \"0001-0348-3935-9875-\",\n" +
            "          \"Path\": \"\\/Trade-Me-Motors\\/Boats-marine\\/Parts-accessories\\/Lighting\"\n" +
            "        },\n" +
            "        {\n" +
            "          \"Name\": \"Manuals\",\n" +
            "          \"Number\": \"0001-0348-3935-3992-\",\n" +
            "          \"Path\": \"\\/Trade-Me-Motors\\/Boats-marine\\/Parts-accessories\\/Manuals\"\n" +
            "        },\n" +
            "        {\n" +
            "          \"Name\": \"Navigation devices\",\n" +
            "          \"Number\": \"0001-0348-3935-4154-\",\n" +
            "          \"Path\": \"\\/Trade-Me-Motors\\/Boats-marine\\/Parts-accessories\\/Navigation-devices\"\n" +
            "        },\n" +
            "        {\n" +
            "          \"Name\": \"Outboards\",\n" +
            "          \"Number\": \"0001-0348-3935-3938-\",\n" +
            "          \"Path\": \"\\/Trade-Me-Motors\\/Boats-marine\\/Parts-accessories\\/Outboards\"\n" +
            "        },\n" +
            "        {\n" +
            "          \"Name\": \"Paints, glues & resins\",\n" +
            "          \"Number\": \"0001-0348-3935-9876-\",\n" +
            "          \"Path\": \"\\/Trade-Me-Motors\\/Boats-marine\\/Parts-accessories\\/Paints-glues-resins\"\n" +
            "        },\n" +
            "        {\n" +
            "          \"Name\": \"Propellers\",\n" +
            "          \"Number\": \"0001-0348-3935-3939-\",\n" +
            "          \"Path\": \"\\/Trade-Me-Motors\\/Boats-marine\\/Parts-accessories\\/Propellers\"\n" +
            "        },\n" +
            "        {\n" +
            "          \"Name\": \"Pumps & tanks\",\n" +
            "          \"Number\": \"0001-0348-3935-3941-\",\n" +
            "          \"Path\": \"\\/Trade-Me-Motors\\/Boats-marine\\/Parts-accessories\\/Pumps-tanks\"\n" +
            "        },\n" +
            "        {\n" +
            "          \"Name\": \"Radio & electronics\",\n" +
            "          \"Number\": \"0001-0348-3935-3942-\",\n" +
            "          \"Path\": \"\\/Trade-Me-Motors\\/Boats-marine\\/Parts-accessories\\/Radio-electronics\"\n" +
            "        },\n" +
            "        {\n" +
            "          \"Name\": \"Rope\",\n" +
            "          \"Number\": \"0001-0348-3935-3943-\",\n" +
            "          \"Path\": \"\\/Trade-Me-Motors\\/Boats-marine\\/Parts-accessories\\/Rope\"\n" +
            "        },\n" +
            "        {\n" +
            "          \"Name\": \"Safety\",\n" +
            "          \"Number\": \"0001-0348-3935-3993-\",\n" +
            "          \"Path\": \"\\/Trade-Me-Motors\\/Boats-marine\\/Parts-accessories\\/Safety\"\n" +
            "        },\n" +
            "        {\n" +
            "          \"Name\": \"Sails\",\n" +
            "          \"Number\": \"0001-0348-3935-9877-\",\n" +
            "          \"Path\": \"\\/Trade-Me-Motors\\/Boats-marine\\/Parts-accessories\\/Sails\"\n" +
            "        },\n" +
            "        {\n" +
            "          \"Name\": \"Seating & covers\",\n" +
            "          \"Number\": \"0001-0348-3935-3944-\",\n" +
            "          \"Path\": \"\\/Trade-Me-Motors\\/Boats-marine\\/Parts-accessories\\/Seating-covers\"\n" +
            "        },\n" +
            "        {\n" +
            "          \"Name\": \"Trailer parts\",\n" +
            "          \"Number\": \"0001-0348-3935-9878-\",\n" +
            "          \"Path\": \"\\/Trade-Me-Motors\\/Boats-marine\\/Parts-accessories\\/Trailer-parts\"\n" +
            "        },\n" +
            "        {\n" +
            "          \"Name\": \"Winches\",\n" +
            "          \"Number\": \"0001-0348-3935-8550-\",\n" +
            "          \"Path\": \"\\/Trade-Me-Motors\\/Boats-marine\\/Parts-accessories\\/Winches\"\n" +
            "        },\n" +
            "        {\n" +
            "          \"Name\": \"Other\",\n" +
            "          \"Number\": \"0001-0348-3935-3936-\",\n" +
            "          \"Path\": \"\\/Trade-Me-Motors\\/Boats-marine\\/Parts-accessories\\/Other\"\n" +
            "        }\n" +
            "      ]\n" +
            "    },\n" +
            "    {\n" +
            "      \"Name\": \"Yachts\",\n" +
            "      \"Number\": \"0001-0348-0031-\",\n" +
            "      \"Path\": \"\\/Trade-Me-Motors\\/Boats-marine\\/Yachts\",\n" +
            "      \"Subcategories\": [\n" +
            "        {\n" +
            "          \"Name\": \"Centreboard\",\n" +
            "          \"Number\": \"0001-0348-0031-2179-\",\n" +
            "          \"Path\": \"\\/Trade-Me-Motors\\/Boats-marine\\/Yachts\\/Centreboard\",\n" +
            "          \"HasClassifieds\": true\n" +
            "        },\n" +
            "        {\n" +
            "          \"Name\": \"Keeler\",\n" +
            "          \"Number\": \"0001-0348-0031-2211-\",\n" +
            "          \"Path\": \"\\/Trade-Me-Motors\\/Boats-marine\\/Yachts\\/Keeler\",\n" +
            "          \"HasClassifieds\": true\n" +
            "        },\n" +
            "        {\n" +
            "          \"Name\": \"Multihull\",\n" +
            "          \"Number\": \"0001-0348-0031-2183-\",\n" +
            "          \"Path\": \"\\/Trade-Me-Motors\\/Boats-marine\\/Yachts\\/Multihull\",\n" +
            "          \"HasClassifieds\": true\n" +
            "        },\n" +
            "        {\n" +
            "          \"Name\": \"Trailer-sailer\",\n" +
            "          \"Number\": \"0001-0348-0031-1271-\",\n" +
            "          \"Path\": \"\\/Trade-Me-Motors\\/Boats-marine\\/Yachts\\/Trailersailer\",\n" +
            "          \"HasClassifieds\": true\n" +
            "        },\n" +
            "        {\n" +
            "          \"Name\": \"Other yachts\",\n" +
            "          \"Number\": \"0001-0348-0031-2221-\",\n" +
            "          \"Path\": \"\\/Trade-Me-Motors\\/Boats-marine\\/Yachts\\/Other-yachts\",\n" +
            "          \"HasClassifieds\": true\n" +
            "        }\n" +
            "      ],\n" +
            "      \"HasClassifieds\": true\n" +
            "    }\n" +
            "  ],\n" +
            "  \"HasClassifieds\": true\n" +
            "}";

}