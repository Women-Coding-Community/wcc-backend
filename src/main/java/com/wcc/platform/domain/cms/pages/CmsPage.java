package com.wcc.platform.domain.cms.pages;

public class CmsPage<T> {

    private final PageMetadata metadata;
    private final PageData<T> data;

    public CmsPage(PageMetadata metadata, PageData<T> data) {
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
