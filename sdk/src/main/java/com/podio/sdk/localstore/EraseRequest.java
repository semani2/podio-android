
package com.podio.sdk.localstore;

import android.util.LruCache;

import java.io.File;
import java.util.concurrent.Callable;

/**
 * A specific {@link com.podio.sdk.localstore.LocalStoreRequest LocalStoreRequest} implementation,
 * targeting the "destroy store" operation. This implementation clears the memory cache and wipes
 * all files from the disk cache that belongs to this very store, leaving other stores intact. If
 * the disk store isn't prepared yet, the disk write request will block until the disk store is
 * ready. This class also removes the actual store sub directory from the file system.
 *
 */
final class EraseRequest extends LocalStoreRequest<Void> {

    /**
     * Evicts all entries in the given memory cache.
     *
     * @param memoryStore
     *         The in-memory cache to clear.
     */
    private static void destroyMemoryStore(LruCache<Object, Object> memoryStore) {
        if (memoryStore != null) {
            memoryStore.evictAll();
        }
    }

    /**
     * Recursively removes all files in the given directory and tries to remove the directory as
     * well.
     *
     * @param diskStore
     *         The disk cache to clear.
     */
    private static void destroyDiskStore(File diskStore) {
        if (isWritableDirectory(diskStore)) {
            File[] files = diskStore.listFiles();

            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        destroyDiskStore(file);
                    } else if (file.isFile()) {
                        file.delete();
                    }
                }
            }
        }
    }

    /**
     * Creates a new Request for destroying the local store. The request will not deliver anything.
     *
     * @param storeEnabler
     *         The callback that will provide the memory and disk stores.
     */
    EraseRequest(final RuntimeStoreEnabler storeEnabler) {
        super(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                destroyMemoryStore(storeEnabler.getMemoryStore());

                // We have to synchronize at this point as the destroyDiskStore method is recursive
                // and we would otherwise cause a deadlock if synchronizing on the same lock inside
                // the method itself.
                synchronized (storeEnabler.getDiskStoreLock()) {
                    destroyDiskStore(storeEnabler.getDiskStore());
                }
                return null;
            }
        });
    }

}
