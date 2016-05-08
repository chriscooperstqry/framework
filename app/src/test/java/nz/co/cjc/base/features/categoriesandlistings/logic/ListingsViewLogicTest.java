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

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import java.util.ArrayList;
import java.util.List;

import nz.co.cjc.base.features.categoriesandlistings.events.CategoryEvent;
import nz.co.cjc.base.features.categoriesandlistings.events.ListingsEvent;
import nz.co.cjc.base.features.categoriesandlistings.models.ListingData;
import nz.co.cjc.base.features.categoriesandlistings.providers.contract.CategoriesAndListingsProvider;
import nz.co.cjc.base.framework.eventbus.providers.contracts.EventBusProvider;
import nz.co.cjc.base.framework.statesaver.providers.contract.StateSaverProvider;
import nz.co.cjc.base.framework.strings.providers.contracts.StringsProvider;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

/**
 * Created by Chris Cooper on 8/05/16.
 * <p/>
 * Tests for listings view logic
 */
public class ListingsViewLogicTest {

    @Spy
    TestCategoriesAndListingsProvider mCategoriesAndListingsProvider;

    @Mock
    private StringsProvider mStringsProvider;

    @Mock
    EventBusProvider mEventBusProvider;

    @Mock
    StateSaverProvider mStateSaverProvider;

    @Mock
    ListingsViewLogic.ViewLogicDelegate mDelegate;

    @Captor
    ArgumentCaptor<List<ListingData>> mListingsDataListCaptor;

    @Captor
    ArgumentCaptor<CategoryEvent> mCategoryEventCaptor;

    @Captor
    ArgumentCaptor<String> mStringCaptor;

    @Captor
    ArgumentCaptor<CategoriesAndListingsProvider.ListingsRequestDelegate> mCategoriesRequestDelegateCaptor;

    private ListingsViewLogic mViewLogic;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        mViewLogic = new ListingsViewLogic(
                mStringsProvider,
                mCategoriesAndListingsProvider,
                mEventBusProvider,
                mStateSaverProvider);

    }

    @Test
    public void testInitViewLogicNullDelegate() {

        //Run
        mViewLogic.initViewLogic(null, null);

        //Verify
        verifyZeroInteractions(mDelegate);
    }

    @Test
    public void testInitViewLogicNoSavedInstance() {

        //Run
        mViewLogic.initViewLogic(mDelegate, null);

        //Verify
        verify(mDelegate, never()).populateScreen(anyListOf(ListingData.class));
    }

    @Test
    public void testInitViewLogicSavedInstance() {

        //Setup
        Bundle state = new Bundle();
        ArrayList<ListingData> mListingItems = (ArrayList<ListingData>) generateListingData();
        Mockito.doReturn(mListingItems).when(mStateSaverProvider).getParcelableArrayList("State.Parcelable.Array.List", state);

        //Run
        mViewLogic.initViewLogic(mDelegate, new Bundle());

        //Verify
        verify(mDelegate, never()).populateScreen(mListingItems);
    }

    @Test
    public void testOnEventUpdateListingsSuccess() {
        //Setup

        ListingsEvent event = new ListingsEvent(null, ListingsEvent.EventType.UpdateListings, "8");

        mCategoriesAndListingsProvider.requestAction = TestCategoriesAndListingsProvider.RequestAction.Success;
        List<ListingData> resultData = generateListingData();
        mCategoriesAndListingsProvider.requestResult = resultData;

        //Run
        mViewLogic.initViewLogic(mDelegate, null);
        mViewLogic.onEvent(event);

        //Verify
        verify(mCategoriesAndListingsProvider).getListingsData(mStringCaptor.capture(), mCategoriesRequestDelegateCaptor.capture());
        verify(mDelegate).hideMessage();
        verify(mDelegate).populateScreen(resultData);

        assertThat(mStringCaptor.getValue(), is("8"));
    }

    @Test
    public void testOnEventUpdateListingsFail() {
        //Setup
        ListingsEvent event = new ListingsEvent(null, ListingsEvent.EventType.UpdateListings, "8");
        mCategoriesAndListingsProvider.requestAction = TestCategoriesAndListingsProvider.RequestAction.Failed;

        //Run
        mViewLogic.initViewLogic(mDelegate, null);
        mViewLogic.onEvent(event);

        //Verify
        verify(mCategoriesAndListingsProvider).getListingsData(mStringCaptor.capture(), mCategoriesRequestDelegateCaptor.capture());
        verify(mDelegate).hideMessage();
        verify(mDelegate, never()).populateScreen(anyListOf(ListingData.class));

        assertThat(mStringCaptor.getValue(), is("8"));
    }

    @Test
    public void testOnEventUpdateListingsNoCategoryNumber() {
        //Setup
        ListingsEvent event = new ListingsEvent(null, ListingsEvent.EventType.UpdateListings, "");

        //Run
        mViewLogic.initViewLogic(mDelegate, null);
        mViewLogic.onEvent(event);

        //Verify
        verify(mCategoriesAndListingsProvider, never()).getListingsData(mStringCaptor.capture(), mCategoriesRequestDelegateCaptor.capture());
        verify(mDelegate).showMessage();
        verify(mDelegate).populateScreen(mListingsDataListCaptor.capture());

        assertThat(mListingsDataListCaptor.getValue().size(), is(0));
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
    public void testOnSaveInstanceState() {

        //Setup
        ListingsEvent event = new ListingsEvent(null, ListingsEvent.EventType.UpdateListings, "8");
        mCategoriesAndListingsProvider.requestAction = TestCategoriesAndListingsProvider.RequestAction.Success;
        List<ListingData> resultData = generateListingData();
        mCategoriesAndListingsProvider.requestResult = resultData;
        Bundle state = new Bundle();

        //Run
        mViewLogic.initViewLogic(mDelegate, null);
        mViewLogic.onEvent(event);
        mViewLogic.onSaveInstanceState(state);

        //Verify
        verify(mStateSaverProvider).saveParcelableArrayList("State.Parcelable.Array.List", (ArrayList<? extends Parcelable>) resultData, state);
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
        public List<ListingData> requestResult;

        public TestCategoriesAndListingsProvider() {
            requestAction = RequestAction.None;
        }

        @Override
        public void getCategoriesData(@Nullable String categoryNumber, @NonNull CategoriesRequestDelegate requestDelegate) {


        }

        @Override
        public void getListingsData(@Nullable String categoryNumber, @NonNull ListingsRequestDelegate requestDelegate) {
            switch (requestAction) {
                case Success:
                    requestDelegate.requestSuccess(requestResult);
                    break;
                case Failed:
                    requestDelegate.requestFailed();
                    break;
            }
        }
    }

    private List<ListingData> generateListingData() {
        return parseJson(SAMPLE_LISTINGS);
    }


    private List<ListingData> parseJson(String input) {

        try {
            JsonObject rootObject = new JsonParser().parse(input).getAsJsonObject();
            JsonArray listings = rootObject.getAsJsonArray(CategoriesAndListingsProvider.LIST);

            return new Gson().fromJson(listings, new TypeToken<List<ListingData>>() {
            }.getType());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static final String SAMPLE_LISTINGS = "{\n" +
            "  \"TotalCount\": 9,\n" +
            "  \"Page\": 1,\n" +
            "  \"PageSize\": 9,\n" +
            "  \"List\": [\n" +
            "    {\n" +
            "      \"ListingId\": 4623161,\n" +
            "      \"Title\": \"Dinghy Ding Ding\",\n" +
            "      \"Category\": \"0001-0348-1261-1289-\",\n" +
            "      \"StartPrice\": 4000.0000,\n" +
            "      \"BuyNowPrice\": 4500.0000,\n" +
            "      \"StartDate\": \"\\/Date(1462143835320)\\/\",\n" +
            "      \"EndDate\": \"\\/Date(1462748635320)\\/\",\n" +
            "      \"ListingLength\": null,\n" +
            "      \"IsFeatured\": true,\n" +
            "      \"HasGallery\": true,\n" +
            "      \"IsBold\": true,\n" +
            "      \"AsAt\": \"\\/Date(1462679871012)\\/\",\n" +
            "      \"CategoryPath\": \"\\/Trade-Me-Motors\\/Boats-marine\\/Dinghies-rowboats\\/Wood\",\n" +
            "      \"PictureHref\": \"https:\\/\\/images.tmsandbox.co.nz\\/photoserver\\/med\\/1109685.jpg\",\n" +
            "      \"IsNew\": true,\n" +
            "      \"Region\": \"Wellington\",\n" +
            "      \"Suburb\": \"Wellington City\",\n" +
            "      \"HasBuyNow\": true,\n" +
            "      \"NoteDate\": \"\\/Date(0)\\/\",\n" +
            "      \"PriceDisplay\": \"$4,000\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"ListingId\": 4632216,\n" +
            "      \"Title\": \"iululkullj\",\n" +
            "      \"Category\": \"0001-0348-2948-8554-\",\n" +
            "      \"StartPrice\": 1.0000,\n" +
            "      \"StartDate\": \"\\/Date(1462599047450)\\/\",\n" +
            "      \"EndDate\": \"\\/Date(1462771847450)\\/\",\n" +
            "      \"ListingLength\": null,\n" +
            "      \"AsAt\": \"\\/Date(1462679871012)\\/\",\n" +
            "      \"CategoryPath\": \"\\/Trade-Me-Motors\\/Boats-marine\\/Jetskis\\/Seadoo\",\n" +
            "      \"IsNew\": true,\n" +
            "      \"Region\": \"Northland\",\n" +
            "      \"Suburb\": \"Whangarei\",\n" +
            "      \"NoteDate\": \"\\/Date(0)\\/\",\n" +
            "      \"Subtitle\": \"ewrterwt\",\n" +
            "      \"PriceDisplay\": \"$1.00\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"ListingId\": 4623688,\n" +
            "      \"Title\": \"Luxury Motorboat\",\n" +
            "      \"Category\": \"0001-0348-0030-\",\n" +
            "      \"StartPrice\": 400000.0000,\n" +
            "      \"BuyNowPrice\": 515000.0000,\n" +
            "      \"StartDate\": \"\\/Date(1462166794333)\\/\",\n" +
            "      \"EndDate\": \"\\/Date(1463030794333)\\/\",\n" +
            "      \"ListingLength\": null,\n" +
            "      \"HasGallery\": true,\n" +
            "      \"AsAt\": \"\\/Date(1462679871012)\\/\",\n" +
            "      \"CategoryPath\": \"\\/Trade-Me-Motors\\/Boats-marine\\/Motorboats\",\n" +
            "      \"PictureHref\": \"https:\\/\\/images.tmsandbox.co.nz\\/photoserver\\/med\\/894561.jpg\",\n" +
            "      \"IsNew\": true,\n" +
            "      \"Region\": \"Marlborough\",\n" +
            "      \"Suburb\": \"Marlborough Sounds\",\n" +
            "      \"HasReserve\": true,\n" +
            "      \"HasBuyNow\": true,\n" +
            "      \"NoteDate\": \"\\/Date(0)\\/\",\n" +
            "      \"ReserveState\": 2,\n" +
            "      \"PriceDisplay\": \"$400,000\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"ListingId\": 4625459,\n" +
            "      \"Title\": \"3S Danforth Galvanised Sand Anchor- 1.4kg (3lb)\",\n" +
            "      \"Category\": \"0001-0348-3935-3937-\",\n" +
            "      \"StartPrice\": 0,\n" +
            "      \"BuyNowPrice\": 17.9900,\n" +
            "      \"StartDate\": \"\\/Date(1462243376213)\\/\",\n" +
            "      \"EndDate\": \"\\/Date(1462848176213)\\/\",\n" +
            "      \"ListingLength\": null,\n" +
            "      \"HasGallery\": true,\n" +
            "      \"AsAt\": \"\\/Date(1462679871012)\\/\",\n" +
            "      \"CategoryPath\": \"\\/Trade-Me-Motors\\/Boats-marine\\/Parts-accessories\\/Anchors\",\n" +
            "      \"PictureHref\": \"https:\\/\\/images.tmsandbox.co.nz\\/photoserver\\/med\\/1521976.jpg\",\n" +
            "      \"IsNew\": true,\n" +
            "      \"Region\": \"Canterbury\",\n" +
            "      \"Suburb\": \"Christchurch City\",\n" +
            "      \"HasBuyNow\": true,\n" +
            "      \"NoteDate\": \"\\/Date(0)\\/\",\n" +
            "      \"ReserveState\": 3,\n" +
            "      \"Subtitle\": \", Was $24.99 now $17.99\",\n" +
            "      \"IsBuyNowOnly\": true,\n" +
            "      \"PriceDisplay\": \"$17.99 per item\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"ListingId\": 4625460,\n" +
            "      \"Title\": \"6S Danforth Galvanised Sand Anchor- 2.7kg (6lb)\",\n" +
            "      \"Category\": \"0001-0348-3935-3937-\",\n" +
            "      \"StartPrice\": 0,\n" +
            "      \"BuyNowPrice\": 27.9900,\n" +
            "      \"StartDate\": \"\\/Date(1462243377040)\\/\",\n" +
            "      \"EndDate\": \"\\/Date(1462848177040)\\/\",\n" +
            "      \"ListingLength\": null,\n" +
            "      \"HasGallery\": true,\n" +
            "      \"AsAt\": \"\\/Date(1462679871012)\\/\",\n" +
            "      \"CategoryPath\": \"\\/Trade-Me-Motors\\/Boats-marine\\/Parts-accessories\\/Anchors\",\n" +
            "      \"PictureHref\": \"https:\\/\\/images.tmsandbox.co.nz\\/photoserver\\/med\\/1521976.jpg\",\n" +
            "      \"IsNew\": true,\n" +
            "      \"Region\": \"Canterbury\",\n" +
            "      \"Suburb\": \"Christchurch City\",\n" +
            "      \"HasBuyNow\": true,\n" +
            "      \"NoteDate\": \"\\/Date(0)\\/\",\n" +
            "      \"ReserveState\": 3,\n" +
            "      \"Subtitle\": \", Was $39.99 now $27.99\",\n" +
            "      \"IsBuyNowOnly\": true,\n" +
            "      \"PriceDisplay\": \"$27.99 per item\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"ListingId\": 4625461,\n" +
            "      \"Title\": \"8S Danforth Galvanised Sand Anchor- 3.6kg (8lb)\",\n" +
            "      \"Category\": \"0001-0348-3935-3937-\",\n" +
            "      \"StartPrice\": 0,\n" +
            "      \"BuyNowPrice\": 37.9900,\n" +
            "      \"StartDate\": \"\\/Date(1462243377290)\\/\",\n" +
            "      \"EndDate\": \"\\/Date(1462848177290)\\/\",\n" +
            "      \"ListingLength\": null,\n" +
            "      \"HasGallery\": true,\n" +
            "      \"AsAt\": \"\\/Date(1462679871012)\\/\",\n" +
            "      \"CategoryPath\": \"\\/Trade-Me-Motors\\/Boats-marine\\/Parts-accessories\\/Anchors\",\n" +
            "      \"PictureHref\": \"https:\\/\\/images.tmsandbox.co.nz\\/photoserver\\/med\\/1521976.jpg\",\n" +
            "      \"IsNew\": true,\n" +
            "      \"Region\": \"Canterbury\",\n" +
            "      \"Suburb\": \"Christchurch City\",\n" +
            "      \"HasBuyNow\": true,\n" +
            "      \"NoteDate\": \"\\/Date(0)\\/\",\n" +
            "      \"ReserveState\": 3,\n" +
            "      \"Subtitle\": \", Was $49.95 now $37.99\",\n" +
            "      \"IsBuyNowOnly\": true,\n" +
            "      \"PriceDisplay\": \"$37.99 per item\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"ListingId\": 4625462,\n" +
            "      \"Title\": \"13S Danforth Galvanised Sand Anchor- 5.9kg (13lb)\",\n" +
            "      \"Category\": \"0001-0348-3935-3937-\",\n" +
            "      \"StartPrice\": 0,\n" +
            "      \"BuyNowPrice\": 69.9900,\n" +
            "      \"StartDate\": \"\\/Date(1462243381633)\\/\",\n" +
            "      \"EndDate\": \"\\/Date(1462848181633)\\/\",\n" +
            "      \"ListingLength\": null,\n" +
            "      \"HasGallery\": true,\n" +
            "      \"AsAt\": \"\\/Date(1462679871012)\\/\",\n" +
            "      \"CategoryPath\": \"\\/Trade-Me-Motors\\/Boats-marine\\/Parts-accessories\\/Anchors\",\n" +
            "      \"PictureHref\": \"https:\\/\\/images.tmsandbox.co.nz\\/photoserver\\/med\\/1521976.jpg\",\n" +
            "      \"IsNew\": true,\n" +
            "      \"Region\": \"Canterbury\",\n" +
            "      \"Suburb\": \"Christchurch City\",\n" +
            "      \"HasBuyNow\": true,\n" +
            "      \"NoteDate\": \"\\/Date(0)\\/\",\n" +
            "      \"ReserveState\": 3,\n" +
            "      \"Subtitle\": \", Was $89.95 now $69.99\",\n" +
            "      \"IsBuyNowOnly\": true,\n" +
            "      \"PriceDisplay\": \"$69.99 per item\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"ListingId\": 4625463,\n" +
            "      \"Title\": \"Danforth Galvanised Sand Anchor 4s- 1.8kg\",\n" +
            "      \"Category\": \"0001-0348-3935-3937-\",\n" +
            "      \"StartPrice\": 0,\n" +
            "      \"BuyNowPrice\": 19.9900,\n" +
            "      \"StartDate\": \"\\/Date(1462243381837)\\/\",\n" +
            "      \"EndDate\": \"\\/Date(1462848181837)\\/\",\n" +
            "      \"ListingLength\": null,\n" +
            "      \"HasGallery\": true,\n" +
            "      \"AsAt\": \"\\/Date(1462679871012)\\/\",\n" +
            "      \"CategoryPath\": \"\\/Trade-Me-Motors\\/Boats-marine\\/Parts-accessories\\/Anchors\",\n" +
            "      \"PictureHref\": \"https:\\/\\/images.tmsandbox.co.nz\\/photoserver\\/med\\/1521983.jpg\",\n" +
            "      \"IsNew\": true,\n" +
            "      \"Region\": \"Canterbury\",\n" +
            "      \"Suburb\": \"Christchurch City\",\n" +
            "      \"HasBuyNow\": true,\n" +
            "      \"NoteDate\": \"\\/Date(0)\\/\",\n" +
            "      \"ReserveState\": 3,\n" +
            "      \"Subtitle\": \", Was $27.99 now $19.99\",\n" +
            "      \"IsBuyNowOnly\": true,\n" +
            "      \"PriceDisplay\": \"$19.99 per item\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"ListingId\": 4625464,\n" +
            "      \"Title\": \"10S Danforth Galvanised Sand Anchor- 4.5kg (10lb)\",\n" +
            "      \"Category\": \"0001-0348-3935-3937-\",\n" +
            "      \"StartPrice\": 0,\n" +
            "      \"BuyNowPrice\": 49.9900,\n" +
            "      \"StartDate\": \"\\/Date(1462243382117)\\/\",\n" +
            "      \"EndDate\": \"\\/Date(1462848182117)\\/\",\n" +
            "      \"ListingLength\": null,\n" +
            "      \"HasGallery\": true,\n" +
            "      \"AsAt\": \"\\/Date(1462679871012)\\/\",\n" +
            "      \"CategoryPath\": \"\\/Trade-Me-Motors\\/Boats-marine\\/Parts-accessories\\/Anchors\",\n" +
            "      \"PictureHref\": \"https:\\/\\/images.tmsandbox.co.nz\\/photoserver\\/med\\/1521976.jpg\",\n" +
            "      \"IsNew\": true,\n" +
            "      \"Region\": \"Canterbury\",\n" +
            "      \"Suburb\": \"Christchurch City\",\n" +
            "      \"HasBuyNow\": true,\n" +
            "      \"NoteDate\": \"\\/Date(0)\\/\",\n" +
            "      \"ReserveState\": 3,\n" +
            "      \"Subtitle\": \", Was $69.95 now $49.99\",\n" +
            "      \"IsBuyNowOnly\": true,\n" +
            "      \"PriceDisplay\": \"$49.99 per item\"\n" +
            "    }\n" +
            "  ],\n" +
            "  \"DidYouMean\": \"\",\n" +
            "  \"FoundCategories\": [\n" +
            "    {\n" +
            "      \"Count\": 1,\n" +
            "      \"Category\": \"0001-0348-1261-\",\n" +
            "      \"Name\": \"Dinghies & rowboats\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"Count\": 1,\n" +
            "      \"Category\": \"0001-0348-2948-\",\n" +
            "      \"Name\": \"Jetskis\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"Count\": 1,\n" +
            "      \"Category\": \"0001-0348-0030-\",\n" +
            "      \"Name\": \"Motorboats\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"Count\": 6,\n" +
            "      \"Category\": \"0001-0348-3935-\",\n" +
            "      \"Name\": \"Parts & accessories\"\n" +
            "    }\n" +
            "  ]\n" +
            "}";


}