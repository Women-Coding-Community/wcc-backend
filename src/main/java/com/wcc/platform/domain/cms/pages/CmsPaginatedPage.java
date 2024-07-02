package com.wcc.platform.domain.cms.pages;

/** Generic CMS Paginated Page. */
public class CmsPaginatedPage<T> {

  private final PageMetadata metadata;
  private final PageData<T> data;

  public CmsPaginatedPage(PageMetadata metadata, PageData<T> data) {
    this.metadata = metadata;
    this.data = data;
  }

  public PageMetadata getMetadata() {
    return metadata;
  }

  public PageData<T> getData() {
    return data;
  }
}
