package org.bombayneurosciences.bna_2023.CustomPageUtils



/**
 * Interface definition for a page provider.
 */
interface PageProvider {
    /**
     * Get the total number of pages available.
     *
     * @return The total number of pages.
     */
    val pageCount: Int

    /**
     * Update the contents of the given page.
     *
     * @param page  The page object to update.
     * @param index The index of the page to be updated.
     */
    fun updatePage(page: CurlPage?, index: Int)

    fun updatePage(page: CurlPage?, width: Int, height: Int, index: Int)
}
