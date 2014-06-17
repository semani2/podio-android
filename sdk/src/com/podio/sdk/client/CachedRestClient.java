/*
 *  Copyright (C) 2014 Copyright Citrix Systems, Inc.
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy of 
 *  this software and associated documentation files (the "Software"), to deal in 
 *  the Software without restriction, including without limitation the rights to 
 *  use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies 
 *  of the Software, and to permit persons to whom the Software is furnished to 
 *  do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all 
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR 
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, 
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE 
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER 
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, 
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE 
 *  SOFTWARE.
 */

package com.podio.sdk.client;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.net.Uri;

import com.podio.sdk.PodioParser;
import com.podio.sdk.RestClient;
import com.podio.sdk.RestClientDelegate;
import com.podio.sdk.client.cache.CacheClient;
import com.podio.sdk.internal.request.RestOperation;

/**
 * A RestClient that, when requesting data, returns content from a local
 * database first, before passing the request on to the parent,
 * {@link HttpRestClient}, implementation. For any other operation (basically,
 * pushing or deleting data) the request is relayed directly to the parent
 * implementation.
 * 
 * @author László Urszuly
 */
public class CachedRestClient extends HttpRestClient {
    private final CacheClient cacheClient;
    private final List<RestRequest<?>> delegatedRequests;

    /**
     * Creates a new <code>CachedRestClient</code>. This implementation will
     * return any cached content that matches the request criteria immediately
     * and after that do a corresponding network operation. When the network
     * call is finished, the cache is updated and the new result is also
     * returned (through the same callback).
     * 
     * @param context
     *        The context in which to operate on the database and network files.
     * @param authority
     *        The content authority, this authority will apply to both the
     *        database and the network Uri.
     * @param networkDelegate
     *        The {@link RestClientDelegate} implementation that will perform
     *        the HTTP requests.
     * @param cacheDelegate
     *        The {@link RestClientDelegate} implementation that will perform
     *        the SQLite queries.
     * @param queueCapacity
     *        The number of pending request this {@link RestClient} will keep in
     *        its queue.
     */
    public CachedRestClient(Context context, String authority, RestClientDelegate networkDelegate,
            CacheClient cacheClient, int queueCapacity) {

        super(context, authority, networkDelegate, queueCapacity);

        if (cacheClient == null) {
            throw new NullPointerException("The cacheDelegate must not be null");
        }

        this.delegatedRequests = new ArrayList<RestRequest<?>>();
        this.cacheClient = cacheClient;
    }

    /**
     * Performs a custom rest request flow, by - generally speaking - allowing
     * all requests to be handled by the super network client implementation
     * first. When the super implementation delivers a result, that result is
     * stored by this implementation in a local database. The stored data is
     * then requested immediately after and returned to the caller.
     * <p>
     * One exception from the above flow is the GET rest requests, which
     * actually return the cached content first and then re-posts the same
     * request to be handled by the network client as well according to the
     * above pattern.
     * 
     * @see com.podio.sdk.client.HttpRestClient#handleRequest(com.podio.sdk.client.RestRequest)
     */
    @Override
    protected <T> RestResult<T> handleRequest(RestRequest<T> restRequest) {
        RestOperation operation = restRequest.getOperation();
        PodioParser<? extends T> parser = restRequest.getParser();

        Uri uri = restRequest.getFilter().buildUri("content", getAuthority());
        String cacheKey = uri.toString();

        RestResult<T> result;
        if (operation == RestOperation.GET //
                && !delegatedRequests.contains(restRequest)) {

            // Query the locally cached data first and then queue the
            // request again for the super implementation to act upon.

            delegatedRequests.add(restRequest);
            super.enqueue(restRequest);

            byte[] data = cacheClient.load(cacheKey);
            if (data != null) {
                T responseItem = parser.parseToItem(new String(data));
                return RestResult.success(responseItem);
            } else {
                return RestResult.failure();
            }
        } else {
            delegatedRequests.remove(restRequest);
            result = super.handleRequest(restRequest);

            if (result.isSuccess()) {
                if (operation == RestOperation.GET) {
                    String json = parser.parseToJson(result.item());

                    cacheClient.save(cacheKey, json != null ? json.getBytes() : null);
                } else if (operation != RestOperation.AUTHORIZE) {
                    cacheClient.delete(cacheKey);
                }
            }

            return result;
        }
    }

}
