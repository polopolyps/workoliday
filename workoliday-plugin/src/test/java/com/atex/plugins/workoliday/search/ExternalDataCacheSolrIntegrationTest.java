package com.atex.plugins.workoliday.search;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.solr.client.solrj.SolrQuery;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.inject.Inject;
import com.polopoly.cm.ContentId;
import com.polopoly.cm.ContentReference;
import com.polopoly.cm.ExternalContentId;
import com.polopoly.cm.client.CMException;
import com.polopoly.cm.collections.ContentList;
import com.polopoly.cm.collections.ContentListSimple;
import com.polopoly.cm.collections.ContentListUtil;
import com.polopoly.cm.policy.PolicyCMServer;
import com.polopoly.management.ServiceNotAvailableException;
import com.polopoly.search.solr.PostFilteredSolrSearchClient;
import com.polopoly.search.solr.SearchResult;
import com.polopoly.search.solr.SearchResultPage;
import com.polopoly.testbase.ImportTestContent;
import com.polopoly.testbase.TestBaseRunner;

/**
 * Test for checking that the SOLR mapping for the External Data Cache Content is working.
 *
 */
@RunWith(TestBaseRunner.class)
@ImportTestContent
public class ExternalDataCacheSolrIntegrationTest {

    private static final String CLASS = ExternalDataCacheSolrIntegrationTest.class.getSimpleName();
    private static final String EXTERNAL_DATA_CACHE_CONTENT_1 = CLASS + ".1";
    private static final String EXTERNAL_DATA_CACHE_CONTENT_2 = CLASS + ".2";
    private static final String EXTERNAL_DATA_CACHE_CONTENT_3 = CLASS + ".3";
    private static final Logger LOG = Logger.getLogger(ExternalDataCacheSolrIntegrationTest.class.getName());
    
    @Inject
    private PolicyCMServer cmServer;
    
    @Inject
    private PostFilteredSolrSearchClient internalSearchClient;
   
    /**
     * Tests if the external data cache contents are searchable in the internal index by name.
     */
    @Test
    public void testInternalSolrMapping() throws Exception {
        List<ContentId> contentIds = new ArrayList<ContentId>();
        contentIds.add(cmServer.findContentIdByExternalId(new ExternalContentId(EXTERNAL_DATA_CACHE_CONTENT_1)));
        contentIds.add(cmServer.findContentIdByExternalId(new ExternalContentId(EXTERNAL_DATA_CACHE_CONTENT_2)));
        contentIds.add(cmServer.findContentIdByExternalId(new ExternalContentId(EXTERNAL_DATA_CACHE_CONTENT_3)));
        ContentList toVerify = ContentListUtil.unmodifiableContentList(new ContentListSimple(contentIds));
        
        verifyInIndex(toVerify);
    }
    
    protected int getExpectedNumberOfHits() {
        return 3;
    }

    protected SolrQuery getSolrQuery() {
        SolrQuery query = new SolrQuery(CLASS);
        return query;
    }
    
    private void verifyInIndex(ContentList toVerify) throws CMException {
        ContentList searchResult = getSourceContentList();
        assertEquals("Search result must correspond in size to provided ContentList", toVerify.size(), searchResult.size());
        ListIterator<ContentReference> iterator = toVerify.getListIterator();
        while (iterator.hasNext()) {
            assertTrue(
                    "Search result must contain all element in list to verify",
                    ContentListUtil.indexOf(iterator.next()
                            .getReferredContentId(), searchResult) >= 0);
        }
    }    
    
    /**
     * Performs a search and constructs a ContentList from a search result.
     * @return ContentList representing result of search.
     */
    private ContentList getSourceContentList() {
        ContentList sourceContentList = ContentListUtil.EMPTY_CONTENT_LIST;
        SearchResultPage searchResult = getSearchResult();
        if (searchResult != null) {
            List<ContentId> contentIds = searchResult.getHits();
            sourceContentList = ContentListUtil.unmodifiableContentList(new ContentListSimple(contentIds));
        }

        return sourceContentList;
    }

    /**
     * Performs search using the internal search client, does not use cache.
     * @return SearchResultPage representing the result.
     */
    private SearchResultPage getSearchResult() {
        SearchResultPage result = null;
        SearchResult searchResult = null;
        try {            
            SolrQuery query = getSolrQuery();
            searchResult = internalSearchClient.search(query, getExpectedNumberOfHits());
            result = searchResult.getPage(0);
        } catch (ServiceNotAvailableException e) {
            LOG.log(Level.WARNING, "Unable to perform search, Solr search service is unavailable", e);
        } catch (Exception e) {
            LOG.log(Level.WARNING, "Unable to perform search", e);
        }
        return result;
    }
}
