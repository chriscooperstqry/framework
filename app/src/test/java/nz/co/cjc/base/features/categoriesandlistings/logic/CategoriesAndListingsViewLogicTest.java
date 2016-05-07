package nz.co.cjc.base.features.categoriesandlistings.logic;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import java.util.List;

import nz.co.cjc.base.BuildConfig;
import nz.co.cjc.base.R;
import nz.co.cjc.base.features.categoriesandlistings.events.CategoryEvent;
import nz.co.cjc.base.features.categoriesandlistings.events.ListingsEvent;
import nz.co.cjc.base.features.categoriesandlistings.models.CategoryData;
import nz.co.cjc.base.features.categoriesandlistings.providers.contract.CategoriesAndListingsProvider;
import nz.co.cjc.base.features.categoriesandlistings.ui.CategoriesFragment;
import nz.co.cjc.base.features.categoriesandlistings.ui.ListingsFragment;
import nz.co.cjc.base.features.listingsstack.providers.contract.ListingsStackProvider;
import nz.co.cjc.base.framework.eventbus.providers.contracts.EventBusProvider;
import nz.co.cjc.base.framework.strings.providers.contracts.StringsProvider;
import nz.co.cjc.base.robolectric.RobolectricBuildConfig;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

/**
 * Created by Chris Cooper on 7/05/16.
 * <p>
 * Tests suite for categories and listings view logic
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(sdk = RobolectricBuildConfig.ROBOLECTRIC_SDK, constants = BuildConfig.class)
public class CategoriesAndListingsViewLogicTest {

    @Mock
    Context mApplicationContext;

    @Mock
    private StringsProvider mStringsProvider;

    @Mock
    EventBusProvider mEventBusProvider;

    @Mock
    ListingsStackProvider mListingsStackProvider;

    @Mock
    CategoriesAndListingsViewLogic.ViewLogicDelegate mDelegate;

    //    @Captor
//    ArgumentCaptor<Intent> mIntentCaptor;
//
//    @Captor
//    ArgumentCaptor<Runnable> mRunnableCaptor;
//
//    @Captor
//    ArgumentCaptor<Integer> mIntegerCaptor;
//
    @Captor
    ArgumentCaptor<ListingsEvent> mListingsEventCaptor;

    @Captor
    ArgumentCaptor<CategoryEvent> mCategoryEventCaptor;

    @Captor
    ArgumentCaptor<CategoryData> mCategoryDataCaptor;

    @Captor
    ArgumentCaptor<Fragment> mFragmentCaptor;

    @Captor
    ArgumentCaptor<Integer> mIntegerCaptor;

    @Captor
    ArgumentCaptor<Boolean> mBooleanCaptor;

    @Rule
    public ExpectedException mExpectedException = ExpectedException.none();

    private CategoriesAndListingsViewLogic mViewLogic;


    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        mViewLogic = new CategoriesAndListingsViewLogic(
                mStringsProvider,
                mEventBusProvider,
                mListingsStackProvider);

    }

    @Test
    public void testInitViewLogicNullDelegate() {

        //Run
        mViewLogic.initViewLogic(null, null);

        //Verify
        verifyZeroInteractions(mDelegate);
    }

    @Test
    public void testInitViewLogicNullSavedInstanceState() {

        //Run
        mViewLogic.initViewLogic(mDelegate, null);

        //Verify
        verify(mListingsStackProvider).addListing(mCategoryDataCaptor.capture());
        verify(mDelegate, times(2)).presentFragment(mFragmentCaptor.capture(), mIntegerCaptor.capture(), mBooleanCaptor.capture());

        assertThat(mCategoryDataCaptor.getValue().getName(), is(""));
        assertThat(mCategoryDataCaptor.getValue().getNumber(), is(""));
        assertThat(mCategoryDataCaptor.getValue().getPath(), is(""));
        assertThat(mCategoryDataCaptor.getValue().getSubCategories().size(), is(0));

        List<Fragment> capturedFragments = mFragmentCaptor.getAllValues();
        assertThat(capturedFragments.get(0), instanceOf(CategoriesFragment.class));
        assertThat(capturedFragments.get(1), instanceOf(ListingsFragment.class));

        List<Integer> capturedInts = mIntegerCaptor.getAllValues();
        assertThat(capturedInts.get(0), is(R.id.categories_container));
        assertThat(capturedInts.get(1), is(R.id.listings_container));

        List<Boolean> capturedBools = mBooleanCaptor.getAllValues();
        assertThat(capturedBools.get(0), is(false));
        assertThat(capturedBools.get(1), is(false));

    }

    @Test
    public void testInitViewLogicNotNullSavedInstanceState() {

        //Run
        mViewLogic.initViewLogic(mDelegate, new Bundle());

        //Verify
        verify(mListingsStackProvider, never()).addListing(any(CategoryData.class));
        verify(mDelegate, never()).presentFragment(any(Fragment.class), anyInt(), anyBoolean());

    }

    @Test
    public void testScreenResumed() {

        //Run
        mViewLogic.initViewLogic(mDelegate, null);
        mViewLogic.screenResumed();

        //Verify
        verify(mEventBusProvider).subscribe(mViewLogic);
    }

    @Test
    public void testScreenPaused() {

        //Run
        mViewLogic.initViewLogic(mDelegate, null);
        mViewLogic.screenPaused();

        //Verify
        verify(mEventBusProvider).unsubscribe(mViewLogic);
    }

    @Test
    public void testOnEventCategorySelectedNullBundle() {

        //Setup
        CategoryEvent categoryEvent = new CategoryEvent(null, CategoryEvent.EventType.CategorySelected, null);

        mExpectedException.expect(IllegalArgumentException.class);
        mExpectedException.expectMessage("Must provide category data");

        //Run
        mViewLogic.initViewLogic(mDelegate, null);
        mViewLogic.onEvent(categoryEvent);
    }

    @Test
    public void testOnEventCategorySelectedNoCategoryData() {

        //Setup
        CategoryEvent categoryEvent = new CategoryEvent(null, CategoryEvent.EventType.CategorySelected, new Bundle());

        mExpectedException.expect(IllegalArgumentException.class);
        mExpectedException.expectMessage("Must provide category data");

        //Run
        mViewLogic.initViewLogic(mDelegate, null);
        mViewLogic.onEvent(categoryEvent);
    }

    @Test
    public void testOnEventCategorySelectedWithSubcategories() {

        //Setup
        Bundle bundle = new Bundle();
        CategoryData categoryData = generateCategoryDataWithSubcategories().get(0);
        bundle.putParcelable(CategoriesViewLogic.CATEGORY_DATA, categoryData);
        CategoryEvent categoryEvent = new CategoryEvent(null, CategoryEvent.EventType.CategorySelected, bundle);

        //Run
        mViewLogic.initViewLogic(mDelegate, null);
        reset(mDelegate);
        when(mDelegate.getToolbarTitle()).thenReturn("Title");
        mViewLogic.onEvent(categoryEvent);

        //Verify
        verify(mDelegate).getToolbarTitle();
        verify(mDelegate).updateToolbarText("Title > Dinghies & rowboats");
        verify(mListingsStackProvider).addListing(categoryData);
        verify(mEventBusProvider).postEvent(mListingsEventCaptor.capture());
        verify(mDelegate).presentFragment(mFragmentCaptor.capture(), mIntegerCaptor.capture(), mBooleanCaptor.capture());
        verify(mDelegate, never()).closeSlidingPanel();

        assertThat(mListingsEventCaptor.getValue().getEventType(), is(ListingsEvent.EventType.UpdateListings));
        assertThat(mListingsEventCaptor.getValue().getCategoryNumber(), is("0001-0348-1261-"));

        assertThat(mFragmentCaptor.getValue(), instanceOf(CategoriesFragment.class));
        assertThat(mIntegerCaptor.getValue(), is(R.id.categories_container));
        assertThat(mBooleanCaptor.getValue(), is(true));
    }

    @Test
    public void testOnEventCategorySelectedWithNoSubcategories() {

        //Setup
        Bundle bundleWithSubcategories = new Bundle();
        CategoryData categoryDataWithSubcategories = generateCategoryDataWithSubcategories().get(0);
        bundleWithSubcategories.putParcelable(CategoriesViewLogic.CATEGORY_DATA, categoryDataWithSubcategories);
        CategoryEvent categoryEventWithSubcategories = new CategoryEvent(null, CategoryEvent.EventType.CategorySelected, bundleWithSubcategories);

        Bundle bundleWithNoSubcategories = new Bundle();
        CategoryData categoryDataWithNoSubcategories = generateCategoryDataWithNoSubcategories().get(0);
        bundleWithNoSubcategories.putParcelable(CategoriesViewLogic.CATEGORY_DATA, categoryDataWithNoSubcategories);
        CategoryEvent categoryEventWithNoSubcategories = new CategoryEvent(null, CategoryEvent.EventType.CategorySelected, bundleWithNoSubcategories);

        when(mListingsStackProvider.getTopListing()).thenReturn(categoryDataWithSubcategories);

        //Run
        mViewLogic.initViewLogic(mDelegate, null);
        mViewLogic.onEvent(categoryEventWithSubcategories);
        reset(mDelegate, mEventBusProvider);
        when(mDelegate.getToolbarTitle()).thenReturn("Title");
        mViewLogic.onEvent(categoryEventWithNoSubcategories);


        //Verify
        verify(mDelegate).getToolbarTitle();
        verify(mDelegate).updateToolbarText("Title > Kawasaki");
        verify(mListingsStackProvider).addListing(categoryDataWithNoSubcategories);
        verify(mEventBusProvider).postEvent(mListingsEventCaptor.capture());
        verify(mDelegate, never()).presentFragment(any(Fragment.class), anyInt(), anyBoolean());
        verify(mDelegate).closeSlidingPanel();

        assertThat(mListingsEventCaptor.getValue().getEventType(), is(ListingsEvent.EventType.UpdateListings));
        assertThat(mListingsEventCaptor.getValue().getCategoryNumber(), is("0001-0348-2948-8556-"));

    }

    @Test
    public void testOnEventCategorySelectedWithNoSubcategoriesTwice() {

        //Setup
        Bundle bundleWithSubcategories = new Bundle();
        CategoryData categoryDataWithSubcategories = generateCategoryDataWithSubcategories().get(0);
        bundleWithSubcategories.putParcelable(CategoriesViewLogic.CATEGORY_DATA, categoryDataWithSubcategories);
        CategoryEvent categoryEventWithSubcategories = new CategoryEvent(null, CategoryEvent.EventType.CategorySelected, bundleWithSubcategories);

        Bundle bundleWithNoSubcategories = new Bundle();
        CategoryData categoryDataWithNoSubcategories = generateCategoryDataWithNoSubcategories().get(0);
        bundleWithNoSubcategories.putParcelable(CategoriesViewLogic.CATEGORY_DATA, categoryDataWithNoSubcategories);
        CategoryEvent categoryEventWithNoSubcategories = new CategoryEvent(null, CategoryEvent.EventType.CategorySelected, bundleWithNoSubcategories);

        when(mListingsStackProvider.getTopListing()).thenReturn(categoryDataWithSubcategories);
        when(mListingsStackProvider.isViewingEmptyRootSubcategory()).thenReturn(true);
//        when(mListingsStackProvider.isEndOfSubcategory()).thenReturn(true);
//        when(mListingsStackProvider.size()).thenReturn(2);

        //Run
        mViewLogic.initViewLogic(mDelegate, null);
        mViewLogic.onEvent(categoryEventWithSubcategories);
        reset(mDelegate, mEventBusProvider);
        when(mDelegate.getToolbarTitle()).thenReturn("Title > Boats");
        mViewLogic.onEvent(categoryEventWithNoSubcategories);

        //Verify
        verify(mDelegate, times(2)).getToolbarTitle();
        verify(mDelegate).updateToolbarText("Title > Boats > Kawasaki");
        verify(mListingsStackProvider).removeListing();
        verify(mListingsStackProvider).addListing(categoryDataWithNoSubcategories);
        verify(mEventBusProvider).postEvent(mListingsEventCaptor.capture());
        verify(mDelegate, never()).presentFragment(any(Fragment.class), anyInt(), anyBoolean());
        verify(mDelegate).closeSlidingPanel();

        assertThat(mListingsEventCaptor.getValue().getEventType(), is(ListingsEvent.EventType.UpdateListings));
        assertThat(mListingsEventCaptor.getValue().getCategoryNumber(), is("0001-0348-2948-8556-"));

    }

    @Test
    public void testOnEventCategoryLayoutReady() {

        //Setup
        CategoryEvent categoryEvent = new CategoryEvent(null, CategoryEvent.EventType.CategoryLayoutReady, new Bundle());

        //Run
        mViewLogic.initViewLogic(mDelegate, null);
        mViewLogic.onEvent(categoryEvent);

        //Verify
        verify(mDelegate).setSlidingPanelScrollableView();
    }

    @Test
    public void testOnBackPressedEndOfSubcategory() {

        //Setup
        when(mListingsStackProvider.isViewingEmptyRootSubcategory()).thenReturn(true);
        when(mListingsStackProvider.isListingsEmpty()).thenReturn(true);
        //Run
        mViewLogic.initViewLogic(mDelegate, null);
        boolean result = mViewLogic.onBackPressed();

        //Verify
        verify(mEventBusProvider).postEvent(mCategoryEventCaptor.capture());
        assertThat(mCategoryEventCaptor.getValue().getEventType(), is(CategoryEvent.EventType.ClearCategorySelection));
        assertThat(result, is(false));
    }

    @Test
    public void testOnBackPressedNotEndOfSubcategory() {

        //Setup
        when(mListingsStackProvider.isViewingEmptyRootSubcategory()).thenReturn(false);
        when(mListingsStackProvider.isListingsEmpty()).thenReturn(true);
        //Run
        mViewLogic.initViewLogic(mDelegate, null);
        boolean result = mViewLogic.onBackPressed();

        //Verify
        verify(mEventBusProvider,never()).postEvent(any(CategoryEvent.class));
        assertThat(result, is(true));
    }

    @Test
    public void testOnBackPressedRemoveListing() {

        //Setup
        when(mListingsStackProvider.isListingsEmpty()).thenReturn(true);

        //Run
        mViewLogic.initViewLogic(mDelegate, null);
        mViewLogic.onBackPressed();

        //Verify
        verify(mListingsStackProvider).removeListing();
    }

    @Test
    public void testOnBackPressedUpdateToolbarAndListings() {

        //Setup
        CategoryData categoryDataWithSubcategories = generateCategoryDataWithSubcategories().get(0);
        when(mListingsStackProvider.getTopListing()).thenReturn(categoryDataWithSubcategories);
        when(mListingsStackProvider.isListingsEmpty()).thenReturn(false);
        when(mDelegate.getToolbarTitle()).thenReturn("Title > Books");

        //Run
        mViewLogic.initViewLogic(mDelegate, null);
        mViewLogic.onBackPressed();

        //Verify
        verify(mEventBusProvider).postEvent(mListingsEventCaptor.capture());
        verify(mDelegate).setSlidingPanelScrollableView();
        verify(mDelegate).updateToolbarText("Title");

        assertThat(mListingsEventCaptor.getValue().getEventType(), is(ListingsEvent.EventType.UpdateListings));
        assertThat(mListingsEventCaptor.getValue().getCategoryNumber(), is("0001-0348-1261-"));
    }

    @Test
    public void testOnBackPressedSetSlidingPanelView() {

        //Setup
        when(mListingsStackProvider.isListingsEmpty()).thenReturn(true);

        //Run
        mViewLogic.initViewLogic(mDelegate, null);
        mViewLogic.onBackPressed();

        //Verify
        verify(mDelegate).setSlidingPanelScrollableView();
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